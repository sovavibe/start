#!/bin/bash
# Interactive fix commit messages with AI assistance
# Usage: 
#   Interactive mode: ./scripts/interactive-fix-commits.sh [-n N] [-s SHA]
#   AI Agent mode: 
#     1. List commits: ./scripts/interactive-fix-commits.sh --list [-n N] [-s SHA]
#     2. Fix commit: ./scripts/interactive-fix-commits.sh --fix-commit SHA "message"
#   AI mode (step-by-step): ./scripts/interactive-fix-commits.sh --ai-mode [-n N] [-s SHA]
#
# Options:
#   -n N: Fix last N commits (default: 10)
#   -s SHA: Start from specific commit SHA
#   --ai-mode: Agent mode - show commit info step-by-step for AI analysis
#   --fix-commit SHA "message": Apply fixed message to specific commit (for AI agent)
#   --list: List all problematic commits with full info (for AI agent)
#
# SAFETY: This script ONLY modifies commit messages, NEVER touches files.
# Uses: git rebase -i with 'reword' (changes only messages)
#       git commit --amend --message (changes only messages)
#
# AI Agent Workflow:
#   1. Get list of problematic commits:
#      ./scripts/interactive-fix-commits.sh --list -n 10
#   2. For each commit, analyze and fix:
#      ./scripts/interactive-fix-commits.sh --fix-commit <SHA> "<fixed message>"
#   3. Or use --ai-mode for step-by-step processing

set -euo pipefail

NUM_COMMITS=10
START_COMMIT=""
AI_MODE=false
FIX_COMMIT_SHA=""
FIX_COMMIT_MSG=""
LIST_MODE=false

# Parse arguments
while [[ $# -gt 0 ]]; do
  case $1 in
    -n)
      NUM_COMMITS="$2"
      shift 2
      ;;
    -s)
      START_COMMIT="$2"
      shift 2
      ;;
    --ai-mode)
      AI_MODE=true
      shift
      ;;
    --fix-commit)
      FIX_COMMIT_SHA="$2"
      FIX_COMMIT_MSG="$3"
      shift 3
      ;;
    --list)
      LIST_MODE=true
      shift
      ;;
    *)
      echo "Unknown option: $1"
      echo "Usage: $0 [-n N] [-s SHA] [--ai-mode] [--fix-commit SHA \"message\"] [--list]"
      exit 1
      ;;
  esac
done

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
CYAN='\033[0;36m'
NC='\033[0m'

echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║     Interactive Fix Commits - AI Assisted (SAFE MODE)      ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
echo ""

# Check if we're in a git repository
if ! git rev-parse --git-dir > /dev/null 2>&1; then
  echo -e "${RED}❌ Not in a git repository${NC}"
  exit 1
fi

# Check if working directory is clean (skip for --fix-commit and --list modes)
if [ "$FIX_COMMIT_SHA" = "" ] && [ "$LIST_MODE" = false ]; then
  if ! git diff-index --quiet HEAD -- 2>/dev/null; then
    echo -e "${YELLOW}⚠️  Warning: Working directory has uncommitted changes${NC}"
    echo -e "${YELLOW}   Please commit or stash them before running this script${NC}"
    exit 1
  fi
fi

# Handle --fix-commit mode (for AI agent)
if [ -n "$FIX_COMMIT_SHA" ] && [ -n "$FIX_COMMIT_MSG" ]; then
  if ! git rev-parse --verify "$FIX_COMMIT_SHA" > /dev/null 2>&1; then
    echo -e "${RED}❌ Invalid commit SHA: ${FIX_COMMIT_SHA}${NC}"
    exit 1
  fi
  
  # Validate message
  if ! echo "$FIX_COMMIT_MSG" | npx commitlint --verbose > /dev/null 2>&1; then
    echo -e "${RED}❌ Invalid commit message format${NC}"
    echo "$FIX_COMMIT_MSG" | npx commitlint --verbose 2>&1 || true
    exit 1
  fi
  
  # Apply fix
  COMMIT_PARENT=$(git rev-parse "$FIX_COMMIT_SHA^" 2>/dev/null || echo "")
  if [ -z "$COMMIT_PARENT" ]; then
    echo -e "${RED}❌ Cannot find parent commit${NC}"
    exit 1
  fi
  
  # Create sequence editor to mark commit for reword
  COMMIT_SHA_SHORT="${FIX_COMMIT_SHA:0:7}"
  TEMP_SEQUENCE_EDITOR=$(mktemp)
  cat > "$TEMP_SEQUENCE_EDITOR" << SCRIPT
#!/bin/bash
sed -i '' 's/^pick ${COMMIT_SHA_SHORT}/reword ${COMMIT_SHA_SHORT}/' "\$1"
SCRIPT
  chmod +x "$TEMP_SEQUENCE_EDITOR"
  
  # Create editor script
  TEMP_MSG_FILE=$(mktemp)
  echo "$FIX_COMMIT_MSG" > "$TEMP_MSG_FILE"
  
  TEMP_EDITOR=$(mktemp)
  cat > "$TEMP_EDITOR" << SCRIPT
#!/bin/bash
cp "${TEMP_MSG_FILE}" "\$1"
SCRIPT
  chmod +x "$TEMP_EDITOR"
  
  export GIT_SEQUENCE_EDITOR="$TEMP_SEQUENCE_EDITOR"
  export GIT_EDITOR="$TEMP_EDITOR"
  
  if git rebase -i "$COMMIT_PARENT" > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Commit ${FIX_COMMIT_SHA:0:7} message updated${NC}"
  else
    if [ -d ".git/rebase-merge" ] || [ -d ".git/rebase-apply" ]; then
      git commit --amend --message "$FIX_COMMIT_MSG" > /dev/null 2>&1 || true
      git rebase --continue > /dev/null 2>&1 || true
    fi
  fi
  
  rm -f "$TEMP_SEQUENCE_EDITOR" "$TEMP_MSG_FILE" "$TEMP_EDITOR"
  unset GIT_SEQUENCE_EDITOR
  unset GIT_EDITOR
  exit 0
fi

# Check current branch
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
echo -e "${BLUE}Current branch: ${CURRENT_BRANCH}${NC}"

# Skip confirmation for --list, --fix-commit, and --ai-mode (agent modes)
if [ "$LIST_MODE" = false ] && [ -z "$FIX_COMMIT_SHA" ] && [ "$AI_MODE" = false ]; then
  if [ "$CURRENT_BRANCH" = "main" ] || [ "$CURRENT_BRANCH" = "develop" ] || [ "$CURRENT_BRANCH" = "master" ]; then
    echo -e "${YELLOW}⚠️  Warning: You're on a protected branch (${CURRENT_BRANCH})${NC}"
    echo -e "${YELLOW}   This will rewrite git history (only commit messages)${NC}"
    read -p "Continue? (yes/no): " CONFIRM
    if [ "$CONFIRM" != "yes" ]; then
      echo "Aborted"
      exit 0
    fi
  fi
fi

# Check if commitlint is available
if ! command -v npx &> /dev/null; then
  echo -e "${RED}❌ npx is not installed${NC}"
  exit 1
fi

# Install dependencies if needed
if [ ! -d "node_modules" ]; then
  echo -e "${YELLOW}Installing dependencies...${NC}"
  npm install --legacy-peer-deps
fi

# Create backup branch
BACKUP_BRANCH="backup-before-fix-$(date +%Y%m%d-%H%M%S)"
git branch "$BACKUP_BRANCH" > /dev/null 2>&1 || true
echo -e "${GREEN}✅ Created backup branch: ${BACKUP_BRANCH}${NC}"
echo -e "${CYAN}   To restore: git reset --hard ${BACKUP_BRANCH}${NC}"
echo ""

# Get commits to check
if [ -n "$START_COMMIT" ]; then
  if ! git rev-parse --verify "$START_COMMIT" > /dev/null 2>&1; then
    echo -e "${RED}❌ Invalid commit SHA: ${START_COMMIT}${NC}"
    exit 1
  fi
  BASE_COMMIT=$(git rev-parse "$START_COMMIT^" 2>/dev/null || echo "$START_COMMIT")
  COMMITS=$(git log --format="%H" --reverse "$BASE_COMMIT"..HEAD 2>/dev/null || echo "")
else
  BASE_COMMIT=$(git rev-parse "HEAD~${NUM_COMMITS}" 2>/dev/null || git rev-list --max-parents=0 HEAD 2>/dev/null || echo "HEAD")
  COMMITS=$(git log --format="%H" --reverse -n "$NUM_COMMITS" 2>/dev/null || echo "")
fi

if [ -z "$COMMITS" ]; then
  echo -e "${GREEN}✅ No commits to check${NC}"
  exit 0
fi

# Check each commit and collect problematic ones
echo -e "${BLUE}🔍 Checking commits for issues...${NC}"
PROBLEMATIC_COMMITS=()
COMMIT_INFO=()

while IFS= read -r COMMIT_SHA; do
  if [ -z "$COMMIT_SHA" ]; then
    continue
  fi
  
  COMMIT_MSG=$(git log -1 --format="%B" "$COMMIT_SHA")
  COMMIT_SUBJECT=$(git log -1 --format="%s" "$COMMIT_SHA")
  
  # Check with commitlint
  if echo "$COMMIT_MSG" | npx commitlint --verbose > /dev/null 2>&1; then
    echo -e "${GREEN}✅ ${COMMIT_SHA:0:7}: ${COMMIT_SUBJECT}${NC}"
  else
    echo -e "${RED}❌ ${COMMIT_SHA:0:7}: ${COMMIT_SUBJECT}${NC}"
    PROBLEMATIC_COMMITS+=("$COMMIT_SHA")
    COMMIT_INFO+=("$COMMIT_SHA|$COMMIT_SUBJECT")
  fi
done <<< "$COMMITS"

echo ""
echo -e "${BLUE}📊 Summary:${NC}"
echo "  Total commits checked: $(echo "$COMMITS" | wc -l | tr -d ' ')"
echo "  Commits with issues: ${#PROBLEMATIC_COMMITS[@]}"

if [ ${#PROBLEMATIC_COMMITS[@]} -eq 0 ]; then
  echo -e "${GREEN}✅ All commits are valid!${NC}"
  exit 0
fi

# Handle --list mode (for AI agent)
if [ "$LIST_MODE" = true ]; then
  echo "PROBLEMATIC_COMMITS_START"
  for COMMIT_SHA in "${PROBLEMATIC_COMMITS[@]}"; do
    COMMIT_SUBJECT=$(git log -1 --format="%s" "$COMMIT_SHA")
    COMMIT_MSG=$(git log -1 --format="%B" "$COMMIT_SHA")
    COMMIT_DIFF=$(git show --stat "$COMMIT_SHA" 2>/dev/null | head -20 || echo "")
    COMMIT_FILES=$(git diff-tree --no-commit-id --name-only -r "$COMMIT_SHA" 2>/dev/null | head -10 || echo "")
    
    echo "COMMIT:${COMMIT_SHA}"
    echo "SUBJECT:${COMMIT_SUBJECT}"
    echo "MESSAGE:${COMMIT_MSG}"
    echo "FILES:${COMMIT_FILES}"
    echo "DIFF_STAT:${COMMIT_DIFF}"
    echo "COMMITLINT_ERRORS:"
    echo "$COMMIT_MSG" | npx commitlint --verbose 2>&1 | head -10 || echo "No errors"
    echo "COMMIT_END"
  done
  echo "PROBLEMATIC_COMMITS_END"
  exit 0
fi

echo ""
echo -e "${YELLOW}⚠️  SAFETY REMINDER:${NC}"
echo -e "${CYAN}   This script ONLY modifies commit messages${NC}"
echo -e "${CYAN}   Files in your working directory will NOT be changed${NC}"
echo -e "${CYAN}   You can abort at any time with: git rebase --abort${NC}"
echo ""

read -p "Continue with interactive fix? (yes/no): " CONFIRM_START
if [ "$CONFIRM_START" != "yes" ]; then
  echo "Aborted"
  exit 0
fi

# Start interactive rebase - process commits one by one
FIRST_BAD_COMMIT="${PROBLEMATIC_COMMITS[0]}"
BASE_FOR_REBASE=$(git rev-parse "$FIRST_BAD_COMMIT^" 2>/dev/null || echo "$BASE_COMMIT")

echo ""
echo -e "${BLUE}Starting interactive rebase from: ${BASE_FOR_REBASE:0:7}${NC}"
echo -e "${CYAN}We'll process ${#PROBLEMATIC_COMMITS[@]} commit(s) one by one${NC}"
echo ""

# Process each problematic commit sequentially
for i in "${!PROBLEMATIC_COMMITS[@]}"; do
  COMMIT_SHA="${PROBLEMATIC_COMMITS[$i]}"
  COMMIT_SUBJECT=$(git log -1 --format="%s" "$COMMIT_SHA")
  COMMIT_MSG=$(git log -1 --format="%B" "$COMMIT_SHA")
  COMMIT_DIFF=$(git show --stat "$COMMIT_SHA" 2>/dev/null || echo "")
  
  echo ""
  echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
  echo -e "${BLUE}║  Commit $((i+1))/${#PROBLEMATIC_COMMITS[@]}: ${COMMIT_SHA:0:7}                    ║${NC}"
  echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
  echo ""
  echo -e "${CYAN}Current message:${NC}"
  echo -e "${YELLOW}${COMMIT_SUBJECT}${NC}"
  if [ -n "$COMMIT_MSG" ] && [ "$COMMIT_MSG" != "$COMMIT_SUBJECT" ]; then
    echo ""
    echo -e "${CYAN}Body:${NC}"
    echo "$COMMIT_MSG" | head -10
  fi
  echo ""
  echo -e "${CYAN}Files changed (for reference only - files are NOT modified):${NC}"
  echo "$COMMIT_DIFF" | head -20
  echo ""
  
  # Show commitlint errors
  echo -e "${RED}Commitlint errors:${NC}"
  echo "$COMMIT_MSG" | npx commitlint --verbose 2>&1 | head -10 || true
  echo ""
  
  echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
  echo -e "${CYAN}Instructions:${NC}"
  echo "  1. Copy the commit message above"
  echo "  2. Ask AI in chat to fix it according to Conventional Commits rules"
  echo "  3. Paste the fixed message below"
  echo ""
  echo -e "${CYAN}Rules reminder:${NC}"
  echo "  - Format: type(scope): description"
  echo "  - Types: feat, fix, docs, refactor, test, chore, etc."
  echo "  - Scopes: jmix, vaadin, entity, view, service, ci, etc."
  echo "  - Lowercase, no period, max 72 chars header"
  echo ""
  echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
  echo ""
  
  # Get fixed message from user (or AI agent in ai-mode)
  FIXED_MSG=""
  if [ "$AI_MODE" = true ]; then
    # In AI mode, output structured info and wait for input via environment or file
    echo "AI_MODE_COMMIT_INFO_START"
    echo "SHA:${COMMIT_SHA}"
    echo "SUBJECT:${COMMIT_SUBJECT}"
    echo "MESSAGE:${COMMIT_MSG}"
    echo "FILES:$(git diff-tree --no-commit-id --name-only -r "$COMMIT_SHA" 2>/dev/null | head -10 || echo "")"
    echo "DIFF_STAT:${COMMIT_DIFF}"
    echo "COMMITLINT_ERRORS:"
    echo "$COMMIT_MSG" | npx commitlint --verbose 2>&1 | head -10 || echo "No errors"
    echo "AI_MODE_COMMIT_INFO_END"
    echo ""
    echo -e "${CYAN}AI Agent: Analyze the commit above and provide fixed message${NC}"
    echo -e "${CYAN}Then call: $0 --fix-commit ${COMMIT_SHA} \"<fixed message>\"${NC}"
    echo ""
    # In AI mode, we don't wait for input - agent will call --fix-commit separately
    continue
  fi
  
  while true; do
    echo -e "${CYAN}Enter fixed commit message (or 'skip' to skip, 'abort' to cancel):${NC}"
    read -r FIXED_MSG
    
    if [ "$FIXED_MSG" = "abort" ]; then
      echo -e "${YELLOW}Aborting rebase...${NC}"
      git rebase --abort 2>/dev/null || true
      echo -e "${CYAN}To restore original state: git reset --hard ${BACKUP_BRANCH}${NC}"
      exit 1
    fi
    
    if [ "$FIXED_MSG" = "skip" ]; then
      echo -e "${YELLOW}Skipping this commit...${NC}"
      break
    fi
    
    if [ -z "$FIXED_MSG" ]; then
      echo -e "${RED}Empty message. Please enter a message or 'skip' or 'abort'${NC}"
      continue
    fi
    
    # Validate with commitlint
    if echo "$FIXED_MSG" | npx commitlint --verbose > /dev/null 2>&1; then
      echo -e "${GREEN}✅ Message is valid!${NC}"
      break
    else
      echo -e "${RED}❌ Message is invalid. Commitlint errors:${NC}"
      echo "$FIXED_MSG" | npx commitlint --verbose 2>&1 | head -10 || true
      echo ""
      echo -e "${YELLOW}Please fix the message and try again${NC}"
    fi
  done
  
  # Apply the fix using interactive rebase
  if [ "$FIXED_MSG" != "skip" ] && [ -n "$FIXED_MSG" ]; then
    echo -e "${BLUE}Applying fix to commit ${COMMIT_SHA:0:7}...${NC}"
    
    # Find the parent of this commit to start rebase from
    COMMIT_PARENT=$(git rev-parse "$COMMIT_SHA^" 2>/dev/null || echo "")
    
    if [ -z "$COMMIT_PARENT" ]; then
      echo -e "${RED}❌ Cannot find parent commit. Skipping...${NC}"
      continue
    fi
    
    # Create a script that will mark this commit for reword and use our fixed message
    COMMIT_SHA_SHORT="${COMMIT_SHA:0:7}"
    TEMP_MSG_FILE=$(mktemp)
    echo "$FIXED_MSG" > "$TEMP_MSG_FILE"
    
    # Create sequence editor script
    TEMP_SEQUENCE_EDITOR=$(mktemp)
    cat > "$TEMP_SEQUENCE_EDITOR" << SCRIPT
#!/bin/bash
sed -i '' 's/^pick ${COMMIT_SHA_SHORT}/reword ${COMMIT_SHA_SHORT}/' "\$1"
SCRIPT
    chmod +x "$TEMP_SEQUENCE_EDITOR"
    
    # Create editor script that uses our fixed message
    TEMP_EDITOR=$(mktemp)
    cat > "$TEMP_EDITOR" << SCRIPT
#!/bin/bash
cp "${TEMP_MSG_FILE}" "\$1"
SCRIPT
    chmod +x "$TEMP_EDITOR"
    
    # Set environment variables
    export GIT_SEQUENCE_EDITOR="$TEMP_SEQUENCE_EDITOR"
    export GIT_EDITOR="$TEMP_EDITOR"
    
    # Start rebase - this will mark the commit for reword and use our editor
    if git rebase -i "$COMMIT_PARENT" > /dev/null 2>&1; then
      echo -e "${GREEN}✅ Commit message updated${NC}"
    else
      # Check if we're in rebase state (rebase might have paused)
      if [ -d ".git/rebase-merge" ] || [ -d ".git/rebase-apply" ]; then
        # We're in rebase - the editor should have been called, but if not, apply manually
        if git log -1 --format="%s" HEAD 2>/dev/null | grep -q "$COMMIT_SUBJECT"; then
          # Current HEAD is our commit, apply the message
          git commit --amend --message "$FIXED_MSG" > /dev/null 2>&1 || true
        fi
        # Continue rebase
        git rebase --continue > /dev/null 2>&1 || {
          echo -e "${YELLOW}⚠️  Rebase may need manual intervention${NC}"
          echo -e "${CYAN}Current commit message: $(git log -1 --format='%s' HEAD 2>/dev/null || echo 'unknown')${NC}"
          echo -e "${CYAN}To continue: git rebase --continue${NC}"
          echo -e "${CYAN}To abort: git rebase --abort${NC}"
        }
      else
        echo -e "${GREEN}✅ Commit message updated${NC}"
      fi
    fi
    
    # Cleanup
    rm -f "$TEMP_SEQUENCE_EDITOR" "$TEMP_MSG_FILE" "$TEMP_EDITOR"
    unset GIT_SEQUENCE_EDITOR
    unset GIT_EDITOR
  fi
done

# Final check
echo ""
echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║                    Fix Complete! 🎉                         ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
echo ""

# Verify fixes
echo -e "${BLUE}🔍 Verifying fixes...${NC}"
VERIFICATION_FAILED=0

for COMMIT_SHA in "${PROBLEMATIC_COMMITS[@]}"; do
  # Try to find the commit (SHA may have changed after rebase)
  NEW_SHA=$(git log --format="%H" --grep="$(echo "$COMMIT_SHA" | cut -c1-7)" --all | head -1 || echo "")
  
  if [ -n "$NEW_SHA" ]; then
    NEW_MSG=$(git log -1 --format="%B" "$NEW_SHA" 2>/dev/null || echo "")
    if echo "$NEW_MSG" | npx commitlint --verbose > /dev/null 2>&1; then
      echo -e "${GREEN}✅ Commit ${NEW_SHA:0:7} is now valid${NC}"
    else
      echo -e "${YELLOW}⚠️  Commit ${NEW_SHA:0:7} still has issues (may need manual fix)${NC}"
      VERIFICATION_FAILED=1
    fi
  fi
done

echo ""
echo -e "${BLUE}Next steps:${NC}"
echo -e "  1. Review changes: ${YELLOW}git log${NC}"
echo -e "  2. If everything is OK, push with: ${YELLOW}git push --force-with-lease${NC}"
echo -e "  3. If something went wrong, restore: ${YELLOW}git reset --hard ${BACKUP_BRANCH}${NC}"
echo ""

if [ $VERIFICATION_FAILED -eq 1 ]; then
  echo -e "${YELLOW}⚠️  Some commits may still need manual fixing${NC}"
fi

echo -e "${GREEN}✅ Interactive fix complete!${NC}"

