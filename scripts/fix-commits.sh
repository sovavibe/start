#!/bin/bash
# Fix commit messages to comply with conventional commits
# Usage: ./scripts/fix-commits.sh [base-commit] [--dry-run]
#   base-commit: SHA or branch to start from (default: HEAD~50 or first commit)
#   --dry-run: Show what would be fixed without making changes
#
# This script will:
#   1. Find all commits with commitlint violations
#   2. Start interactive rebase to fix them
#   3. You'll need to edit each commit message manually

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
echo -e "${BLUE}║          Fix Commit Messages - Conventional Commits       ║${NC}"
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
  echo -e "${YELLOW}   This will rewrite git history. Make sure you know what you're doing!${NC}"
  read -p "Continue? (yes/no): " CONFIRM
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
  # Find the first commit (or use a reasonable default)
  BASE_COMMIT=$(git rev-list --max-parents=0 HEAD 2>/dev/null || echo "HEAD~50")
fi

# Get all commits from BASE_COMMIT to HEAD
COMMITS=$(git log --format="%H" --reverse "$BASE_COMMIT"..HEAD 2>/dev/null || git log --format="%H" --reverse -50)

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
    # Show the issue
    echo "$COMMIT_MSG" | npx commitlint --verbose 2>&1 | head -5 || true
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
  echo -e "${YELLOW}🔍 Dry run mode - no changes will be made${NC}"
  echo ""
  echo "Problematic commits:"
  for COMMIT_SHA in "${PROBLEMATIC_COMMITS[@]}"; do
    echo "  - ${COMMIT_SHA:0:7}: $(git log -1 --format="%s" "$COMMIT_SHA")"
  done
  exit 0
fi

# Fix commits using interactive rebase
echo -e "${YELLOW}📝 Starting interactive rebase to fix commits...${NC}"
echo -e "${BLUE}   You'll need to edit each commit message manually${NC}"
echo -e "${BLUE}   Change 'pick' to 'reword' (or 'r') for commits you want to fix${NC}"
echo ""

FIRST_BAD_COMMIT="${PROBLEMATIC_COMMITS[0]}"
BASE_FOR_REBASE=$(git rev-parse "$FIRST_BAD_COMMIT^" 2>/dev/null || echo "$BASE_COMMIT")

echo -e "${BLUE}Rebase will start from: ${BASE_FOR_REBASE:0:7}${NC}"
echo -e "${YELLOW}Problematic commits that need fixing:${NC}"
for COMMIT_SHA in "${PROBLEMATIC_COMMITS[@]}"; do
  COMMIT_SUBJECT=$(git log -1 --format="%s" "$COMMIT_SHA")
  echo -e "  ${RED}❌${NC} ${COMMIT_SHA:0:7}: ${COMMIT_SUBJECT}"
done
echo ""

read -p "Continue with interactive rebase? (yes/no): " CONFIRM_REBASE

if [ "$CONFIRM_REBASE" != "yes" ]; then
  echo "Aborted"
  exit 0
fi

echo -e "${BLUE}Starting interactive rebase...${NC}"
echo -e "${YELLOW}Instructions:${NC}"
echo "  1. In the editor, change 'pick' to 'reword' (or 'r') for commits to fix"
echo "  2. Save and close the editor"
echo "  3. For each commit marked 'reword', edit the message"
echo "  4. Follow conventional commits format: type(scope): description"
echo "  5. Rules: lowercase, max 72 chars header, max 72 chars body lines"
echo ""

git rebase -i "$BASE_FOR_REBASE" || {
  echo -e "${YELLOW}⚠️  Rebase paused or aborted${NC}"
  echo -e "${BLUE}   To continue: git rebase --continue${NC}"
  echo -e "${BLUE}   To abort: git rebase --abort${NC}"
  exit 1
}

echo -e "${GREEN}✅ Interactive rebase complete${NC}"
echo -e "${YELLOW}⚠️  Please verify commits are fixed:${NC}"
echo -e "${BLUE}   git log${NC}"
echo -e "${BLUE}   npm run commitlint:last${NC}"
echo ""
echo -e "${YELLOW}⚠️  If pushing to remote, use force-with-lease:${NC}"
echo -e "${BLUE}   git push --force-with-lease${NC}"

echo ""
echo -e "${GREEN}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║                    Fix Complete! 🎉                         ║${NC}"
echo -e "${GREEN}╚════════════════════════════════════════════════════════════╝${NC}"

