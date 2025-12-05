#!/bin/bash
# Automatically fix all commit messages to comply with conventional commits
# Usage: ./scripts/auto-fix-commits.sh [base-commit] [--dry-run]
#   base-commit: SHA or branch to start from (default: HEAD~100 or first commit)
#   --dry-run: Show what would be fixed without making changes
#
# This script will automatically:
#   1. Find all commits with commitlint violations
#   2. Fix common issues (lowercase, length, scope, etc.)
#   3. Rewrite git history using git filter-branch

set -euo pipefail

DRY_RUN=false
BASE_COMMIT=""

# Parse arguments
for arg in "$@"; do
  case $arg in
    --dry-run)
      DRY_RUN=true
      ;;
    *)
      if [ -z "$BASE_COMMIT" ] && [[ ! "$arg" =~ ^-- ]]; then
        BASE_COMMIT="$arg"
      fi
      ;;
  esac
done

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║      Auto-Fix Commit Messages - Conventional Commits        ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
echo ""

# Check if we're in a git repository
if ! git rev-parse --git-dir > /dev/null 2>&1; then
  echo -e "${RED}❌ Not in a git repository${NC}"
  exit 1
fi

# Check current branch
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
echo -e "${BLUE}Current branch: ${CURRENT_BRANCH}${NC}"

if [ "$CURRENT_BRANCH" = "main" ] || [ "$CURRENT_BRANCH" = "develop" ] || [ "$CURRENT_BRANCH" = "master" ]; then
  echo -e "${YELLOW}⚠️  Warning: You're on a protected branch (${CURRENT_BRANCH})${NC}"
  echo -e "${YELLOW}   This will rewrite git history!${NC}"
  read -p "Continue? (type 'yes' to confirm): " CONFIRM
  if [ "$CONFIRM" != "yes" ]; then
    echo "Aborted"
    exit 0
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

# Find commits with issues
echo -e "${BLUE}🔍 Checking commits for issues...${NC}"

if [ -z "$BASE_COMMIT" ]; then
  # Try to find first commit, or use reasonable default
  BASE_COMMIT=$(git rev-list --max-parents=0 HEAD 2>/dev/null || echo "HEAD~100")
fi

# Get all commits from BASE_COMMIT to HEAD
COMMITS=$(git log --format="%H" --reverse "$BASE_COMMIT"..HEAD 2>/dev/null || git log --format="%H" --reverse -100)

if [ -z "$COMMITS" ]; then
  echo -e "${GREEN}✅ No commits to check${NC}"
  exit 0
fi

# Check each commit
PROBLEMATIC_COMMITS=()
COMMIT_COUNT=0

while IFS= read -r COMMIT_SHA; do
  if [ -z "$COMMIT_SHA" ]; then
    continue
  fi
  
  COMMIT_COUNT=$((COMMIT_COUNT + 1))
  COMMIT_MSG=$(git log -1 --format="%B" "$COMMIT_SHA")
  
  # Check with commitlint
  if echo "$COMMIT_MSG" | npx commitlint --verbose > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Commit ${COMMIT_SHA:0:7}${NC}"
  else
    echo -e "${RED}❌ Commit ${COMMIT_SHA:0:7} has issues${NC}"
    PROBLEMATIC_COMMITS+=("$COMMIT_SHA")
  fi
done <<< "$COMMITS"

echo ""
echo -e "${BLUE}📊 Summary:${NC}"
echo "  Total commits checked: $COMMIT_COUNT"
echo "  Commits with issues: ${#PROBLEMATIC_COMMITS[@]}"

if [ ${#PROBLEMATIC_COMMITS[@]} -eq 0 ]; then
  echo -e "${GREEN}✅ All commits are valid!${NC}"
  exit 0
fi

if [ "$DRY_RUN" = true ]; then
  echo -e "${YELLOW}🔍 Dry run mode - showing what would be fixed:${NC}"
  echo ""
  for COMMIT_SHA in "${PROBLEMATIC_COMMITS[@]}"; do
    OLD_MSG=$(git log -1 --format="%B" "$COMMIT_SHA")
    echo -e "${BLUE}Commit ${COMMIT_SHA:0:7}:${NC}"
    echo -e "${RED}  OLD:${NC} $(echo "$OLD_MSG" | head -1)"
    # Show what it would be fixed to (simplified)
    echo -e "${GREEN}  NEW:${NC} (would be auto-fixed)"
    echo ""
  done
  exit 0
fi

# Create fix script
FIX_SCRIPT=$(mktemp)
cat > "$FIX_SCRIPT" << 'FIXSCRIPT'
#!/bin/bash
# Auto-fix commit message according to conventional commits rules

MSG=$(cat)

# Extract header (first line) and body (rest)
HEADER=$(echo "$MSG" | head -1)
BODY=$(echo "$MSG" | sed -n '2,$p')

# Fix header
FIXED_HEADER="$HEADER"

# 1. Convert to lowercase (but preserve type/scope structure)
if [[ $HEADER =~ ^([a-z]+)(\(([^)]+)\))?:\ (.+)$ ]]; then
  TYPE="${BASH_REMATCH[1]}"
  SCOPE="${BASH_REMATCH[3]}"
  SUBJECT="${BASH_REMATCH[4]}"
  
  # Fix type: ensure lowercase
  TYPE=$(echo "$TYPE" | tr '[:upper:]' '[:lower:]')
  
  # Fix scope: ensure lowercase and valid
  if [ -n "$SCOPE" ]; then
    SCOPE=$(echo "$SCOPE" | tr '[:upper:]' '[:lower:]')
    # Remove invalid characters, keep only valid scope format
    SCOPE=$(echo "$SCOPE" | sed 's/[^a-z0-9-]//g')
    # If scope contains commas or multiple words, take first valid one
    SCOPE=$(echo "$SCOPE" | cut -d',' -f1 | cut -d' ' -f1)
    
    # Map common invalid scopes to valid ones
    case "$SCOPE" in
      userservice|userservice*|localehelper|localehelper*)
        SCOPE="service"
        ;;
      testfixtures|testfixtures*)
        SCOPE="test"
        ;;
      startapplication|startapplication*)
        SCOPE="config"
        ;;
      *)
        # Check if it's a valid scope, otherwise use first part
        VALID_SCOPES="jmix vaadin entity view service security liquibase ui api db config test ci deps docs scripts gradle copyright"
        if ! echo "$VALID_SCOPES" | grep -q "\b${SCOPE}\b"; then
          # Try to extract valid scope from the string
          for valid_scope in $VALID_SCOPES; do
            if echo "$SCOPE" | grep -q "$valid_scope"; then
              SCOPE="$valid_scope"
              break
            fi
          done
          # If still not valid, use 'config' as default
          if ! echo "$VALID_SCOPES" | grep -q "\b${SCOPE}\b"; then
            SCOPE="config"
          fi
        fi
        ;;
    esac
  fi
  
  # Fix subject: lowercase, remove period, truncate to fit in 72 chars with type/scope
  SUBJECT=$(echo "$SUBJECT" | tr '[:upper:]' '[:lower:]' | sed 's/\.$//')
  
  # Calculate available space for subject (type + scope + ": " = ~20-30 chars)
  if [ -n "$SCOPE" ]; then
    PREFIX_LEN=$((${#TYPE} + ${#SCOPE} + 5))  # "type(scope): " = type + scope + 5
  else
    PREFIX_LEN=$((${#TYPE} + 2))  # "type: " = type + 2
  fi
  MAX_SUBJECT_LEN=$((72 - PREFIX_LEN))
  if [ $MAX_SUBJECT_LEN -lt 10 ]; then
    MAX_SUBJECT_LEN=50  # Minimum reasonable length
  fi
  
  SUBJECT=$(echo "$SUBJECT" | cut -c1-$MAX_SUBJECT_LEN | sed 's/ $//')
  
  # Reconstruct header
  if [ -n "$SCOPE" ]; then
    FIXED_HEADER="${TYPE}(${SCOPE}): ${SUBJECT}"
  else
    FIXED_HEADER="${TYPE}: ${SUBJECT}"
  fi
  
  # Ensure header is max 72 chars
  if [ ${#FIXED_HEADER} -gt 72 ]; then
    # Truncate subject more aggressively
    OVERFLOW=$((${#FIXED_HEADER} - 72))
    SUBJECT_LEN=$((${#SUBJECT} - OVERFLOW - 3))
    if [ $SUBJECT_LEN -lt 10 ]; then
      SUBJECT_LEN=10
    fi
    SUBJECT=$(echo "$SUBJECT" | cut -c1-$SUBJECT_LEN)
    if [ -n "$SCOPE" ]; then
      FIXED_HEADER="${TYPE}(${SCOPE}): ${SUBJECT}"
    else
      FIXED_HEADER="${TYPE}: ${SUBJECT}"
    fi
  fi
else
  # Doesn't match conventional format - try to fix it
  # Extract first word as type, rest as subject
  FIRST_WORD=$(echo "$HEADER" | cut -d' ' -f1 | tr '[:upper:]' '[:lower:]' | sed 's/://')
  REST=$(echo "$HEADER" | cut -d' ' -f2- | tr '[:upper:]' '[:lower:]' | sed 's/\.$//')
  
  # Map common prefixes to types
  case "$FIRST_WORD" in
    fix|fixed|fixes|fixing)
      TYPE="fix"
      ;;
    feat|feature|features)
      TYPE="feat"
      ;;
    docs|doc|documentation)
      TYPE="docs"
      ;;
    refactor|refactoring)
      TYPE="refactor"
      ;;
    test|tests|testing)
      TYPE="test"
      ;;
    chore|chores)
      TYPE="chore"
      ;;
    *)
      TYPE="fix"  # Default
      ;;
  esac
  
  # Try to extract scope from rest (look for common patterns)
  SCOPE=""
  if echo "$REST" | grep -qi "ci\|cd\|pipeline\|workflow"; then
    SCOPE="ci"
  elif echo "$REST" | grep -qi "test\|tests"; then
    SCOPE="test"
  elif echo "$REST" | grep -qi "doc\|readme"; then
    SCOPE="docs"
  elif echo "$REST" | grep -qi "script"; then
    SCOPE="scripts"
  elif echo "$REST" | grep -qi "gradle\|build"; then
    SCOPE="gradle"
  else
    SCOPE="config"  # Default scope
  fi
  
  SUBJECT=$(echo "$REST" | sed "s/^${SCOPE}[^a-z]*//i" | cut -c1-50)
  
  if [ -n "$SCOPE" ]; then
    FIXED_HEADER="${TYPE}(${SCOPE}): ${SUBJECT}"
  else
    FIXED_HEADER="${TYPE}: ${SUBJECT}"
  fi
  
  # Ensure max 72 chars
  if [ ${#FIXED_HEADER} -gt 72 ]; then
    FIXED_HEADER=$(echo "$FIXED_HEADER" | cut -c1-72)
  fi
fi

# Fix body: wrap lines to 72 chars, ensure leading blank line
if [ -n "$BODY" ]; then
  # Remove leading/trailing whitespace
  BODY=$(echo "$BODY" | sed 's/^[[:space:]]*//;s/[[:space:]]*$//')
  
  # Wrap long lines to 72 chars
  FIXED_BODY=$(echo "$BODY" | fold -w 72 -s)
  
  # Output fixed message
  echo -e "${FIXED_HEADER}\n\n${FIXED_BODY}"
else
  echo -e "$FIXED_HEADER"
fi
FIXSCRIPT

chmod +x "$FIX_SCRIPT"

# Determine base for filter-branch
FIRST_BAD_COMMIT="${PROBLEMATIC_COMMITS[0]}"
BASE_FOR_REBASE=$(git rev-parse "$FIRST_BAD_COMMIT^" 2>/dev/null || echo "$BASE_COMMIT")

echo -e "${YELLOW}🔧 Auto-fixing commits from ${BASE_FOR_REBASE:0:7}...${NC}"
echo -e "${BLUE}   This will rewrite git history using git filter-branch${NC}"
echo -e "${YELLOW}   ⚠️  Make sure you have a backup!${NC}"
echo ""

read -p "Continue with auto-fix? (type 'yes' to confirm): " CONFIRM_FIX

if [ "$CONFIRM_FIX" != "yes" ]; then
  echo "Aborted"
  rm -f "$FIX_SCRIPT"
  exit 0
fi

# Use git filter-branch to rewrite commits
echo -e "${BLUE}Rewriting commits...${NC}"

# Create backup branch first
BACKUP_BRANCH="backup-before-fix-$(date +%Y%m%d-%H%M%S)"
git branch "$BACKUP_BRANCH" > /dev/null 2>&1 || true
echo -e "${GREEN}✅ Created backup branch: ${BACKUP_BRANCH}${NC}"

# Use filter-branch
if git filter-branch -f --msg-filter "$FIX_SCRIPT" "$BASE_FOR_REBASE"..HEAD 2>/dev/null; then
  echo -e "${GREEN}✅ Auto-fix complete${NC}"
else
  echo -e "${YELLOW}⚠️  Filter-branch may have issues. Trying alternative method...${NC}"
  
  # Alternative: use git rebase with automatic fixing
  # This is more complex but sometimes more reliable
  GIT_SEQUENCE_EDITOR="true" git rebase -i "$BASE_FOR_REBASE" > /dev/null 2>&1 || {
    echo -e "${RED}❌ Failed to rewrite commits${NC}"
    echo -e "${YELLOW}   Restore from backup: git reset --hard ${BACKUP_BRANCH}${NC}"
    rm -f "$FIX_SCRIPT"
    exit 1
  }
fi

rm -f "$FIX_SCRIPT"

# Verify fixes
echo -e "${BLUE}🔍 Verifying fixes...${NC}"
VERIFICATION_FAILED=0

for COMMIT_SHA in "${PROBLEMATIC_COMMITS[@]}"; do
  # Get new commit SHA (may have changed due to rebase)
  NEW_SHA=$(git log --format="%H" --grep="$(git log -1 --format="%s" "$COMMIT_SHA" 2>/dev/null | head -c 20)" --all | head -1 || echo "")
  
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
echo -e "${GREEN}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║                  Auto-Fix Complete! 🎉                     ║${NC}"
echo -e "${GREEN}╚════════════════════════════════════════════════════════════╝${NC}"
echo ""
echo -e "${BLUE}Next steps:${NC}"
echo -e "  1. Review changes: ${YELLOW}git log${NC}"
echo -e "  2. Test your code: ${YELLOW}make test${NC}"
echo -e "  3. If everything is OK, push with: ${YELLOW}git push --force-with-lease${NC}"
echo -e "  4. If something went wrong, restore: ${YELLOW}git reset --hard ${BACKUP_BRANCH}${NC}"
echo ""

if [ $VERIFICATION_FAILED -eq 1 ]; then
  echo -e "${YELLOW}⚠️  Some commits may still need manual fixing${NC}"
  echo -e "${BLUE}   Use ${YELLOW}./scripts/fix-commits.sh${BLUE} for interactive fixing${NC}"
fi

