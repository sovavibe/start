#!/bin/bash
# Create PR with auto-generated description from git diff
# Usage: ./scripts/create-pr.sh [base-branch] [title]

set -e

BASE_BRANCH="${1:-main}"
TITLE="${2:-}"

# Get current branch
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)

if [ "$CURRENT_BRANCH" = "main" ] || [ "$CURRENT_BRANCH" = "develop" ] || [ "$CURRENT_BRANCH" = "master" ]; then
  echo "❌ Cannot create PR from protected branch: $CURRENT_BRANCH"
  exit 1
fi

# Generate PR description from diff
echo "📝 Analyzing changes and generating PR description..."

# Get statistics
STATS=$(git diff --shortstat "$BASE_BRANCH".."$CURRENT_BRANCH" 2>/dev/null || echo "")
COMMIT_COUNT=$(git rev-list --count "$BASE_BRANCH".."$CURRENT_BRANCH" 2>/dev/null || echo "0")

# Get changed files by category
JAVA_FILES=$(git diff --name-only "$BASE_BRANCH".."$CURRENT_BRANCH" 2>/dev/null | grep -E '\.java$' || true)
TEST_FILES=$(git diff --name-only "$BASE_BRANCH".."$CURRENT_BRANCH" 2>/dev/null | grep -E '(Test|IT)\.java$' || true)
CONFIG_FILES=$(git diff --name-only "$BASE_BRANCH".."$CURRENT_BRANCH" 2>/dev/null | grep -E '\.(gradle|properties|xml|yml|yaml|json)$' || true)
DOC_FILES=$(git diff --name-only "$BASE_BRANCH".."$CURRENT_BRANCH" 2>/dev/null | grep -E '\.(md|txt)$' || true)
OTHER_FILES=$(git diff --name-only "$BASE_BRANCH".."$CURRENT_BRANCH" 2>/dev/null | grep -vE '\.(java|gradle|properties|xml|yml|yaml|json|md|txt)$' || true)

# Count files
JAVA_COUNT=$(echo "$JAVA_FILES" | grep -c . || echo "0")
TEST_COUNT=$(echo "$TEST_FILES" | grep -c . || echo "0")
CONFIG_COUNT=$(echo "$CONFIG_FILES" | grep -c . || echo "0")
DOC_COUNT=$(echo "$DOC_FILES" | grep -c . || echo "0")
OTHER_COUNT=$(echo "$OTHER_FILES" | grep -c . || echo "0")

# Get commit messages (first line only, max 5)
COMMITS=$(git log --format="%s" "$BASE_BRANCH".."$CURRENT_BRANCH" 2>/dev/null | head -5 || true)

# Detect PR type from branch name or commits
if echo "$CURRENT_BRANCH" | grep -qE '^feat/'; then
  PR_TYPE="feat"
elif echo "$CURRENT_BRANCH" | grep -qE '^fix/'; then
  PR_TYPE="fix"
elif echo "$CURRENT_BRANCH" | grep -qE '^refactor/'; then
  PR_TYPE="refactor"
elif echo "$CURRENT_BRANCH" | grep -qE '^docs/'; then
  PR_TYPE="docs"
elif echo "$CURRENT_BRANCH" | grep -qE '^test/'; then
  PR_TYPE="test"
else
  PR_TYPE="chore"
fi

# Build description using temp file
TEMP_DESC=$(mktemp)
trap "rm -f $TEMP_DESC" EXIT

{
  echo "## Description"
  echo ""
  
  # Add summary from commits if available
  if [ -n "$COMMITS" ]; then
    echo "### Summary"
    echo ""
    echo "$COMMITS" | while IFS= read -r commit; do
      if [ -n "$commit" ]; then
        echo "- ${commit}"
      fi
    done
    echo ""
  fi
  
  echo "## Type"
  echo ""
  echo "- [x] \`$PR_TYPE\`"
  echo ""
  echo "## Changes Overview"
  echo ""
  
  # Add statistics
  if [ -n "$STATS" ]; then
    echo "**Statistics:** $STATS"
  fi
  
  echo "**Commits:** $COMMIT_COUNT"
  echo "**Files changed:**"
  echo "- Java: $JAVA_COUNT"
  echo "- Tests: $TEST_COUNT"
  echo "- Config: $CONFIG_COUNT"
  echo "- Docs: $DOC_COUNT"
  echo "- Other: $OTHER_COUNT"
  echo ""
  
  # Add Java files
  if [ "$JAVA_COUNT" -gt 0 ]; then
    echo "### Java Files ($JAVA_COUNT)"
    echo ""
    echo "$JAVA_FILES" | while IFS= read -r file; do
      if [ -n "$file" ]; then
        # Get file status (added/modified/deleted)
        STATUS=$(git diff --name-status "$BASE_BRANCH".."$CURRENT_BRANCH" 2>/dev/null | grep "$file" | cut -f1 || echo "M")
        case "$STATUS" in
          A*) ICON="➕" ;;
          D*) ICON="🗑️" ;;
          M*) ICON="✏️" ;;
          R*) ICON="🔄" ;;
          *) ICON="📝" ;;
        esac
        echo "$ICON \`$file\`"
      fi
    done
    echo ""
  fi
  
  # Add test files
  if [ "$TEST_COUNT" -gt 0 ]; then
    echo "### Test Files ($TEST_COUNT)"
    echo ""
    echo "$TEST_FILES" | while IFS= read -r file; do
      if [ -n "$file" ]; then
        echo "✅ \`$file\`"
      fi
    done
    echo ""
  fi
  
  # Add config files
  if [ "$CONFIG_COUNT" -gt 0 ]; then
    echo "### Configuration Files ($CONFIG_COUNT)"
    echo ""
    echo "$CONFIG_FILES" | head -10 | while IFS= read -r file; do
      if [ -n "$file" ]; then
        echo "⚙️ \`$file\`"
      fi
    done
    echo ""
  fi
  
  # Add doc files
  if [ "$DOC_COUNT" -gt 0 ]; then
    echo "### Documentation ($DOC_COUNT)"
    echo ""
    echo "$DOC_FILES" | while IFS= read -r file; do
      if [ -n "$file" ]; then
        echo "📚 \`$file\`"
      fi
    done
    echo ""
  fi
  
  # Add other files (limited)
  if [ "$OTHER_COUNT" -gt 0 ] && [ "$OTHER_COUNT" -le 5 ]; then
    echo "### Other Files"
    echo ""
    echo "$OTHER_FILES" | while IFS= read -r file; do
      if [ -n "$file" ]; then
        echo "📄 \`$file\`"
      fi
    done
    echo ""
  fi
  
  echo "## Related"
  echo ""
  echo "<!-- Link issues: Closes #123, Related to #456 -->"
  echo ""
  echo "## Testing"
  echo ""
  
  # Check if tests were added/modified
  if [ "$TEST_COUNT" -gt 0 ]; then
    echo "- [x] Tests added/updated ($TEST_COUNT files)"
  else
    echo "- [ ] Tests added/updated"
  fi
  
  echo "- [ ] All existing tests pass"
  echo "- [ ] Tested locally"
  echo "- [ ] Tested in CI/CD"
  echo ""
  echo "## Quality"
  echo ""
  echo "- [ ] \`make format\` passed"
  echo "- [ ] \`make analyze-full\` passed"
  echo "- [ ] No new suppressions added (or documented if needed)"
  echo "- [ ] Code follows project conventions"
  echo ""
  echo "## Additional Notes"
  echo ""
  echo "<!-- Add any additional information or context -->"
} > "$TEMP_DESC"

DESCRIPTION=$(cat "$TEMP_DESC")

# Use title from branch name if not provided
if [ -z "$TITLE" ]; then
  # Extract description from branch name (after scope)
  TITLE=$(echo "$CURRENT_BRANCH" | sed -E 's|^[^/]+/[^/]+-||' | sed 's/-/ /g' | sed 's/\b\(.\)/\u\1/g')
  # Add type prefix
  TITLE="$PR_TYPE: $TITLE"
fi

echo "🚀 Creating PR..."
echo "Title: $TITLE"
echo "Base: $BASE_BRANCH"
echo "Head: $CURRENT_BRANCH"
echo ""
echo "📊 Changes: $STATS"
echo "📝 Commits: $COMMIT_COUNT"
echo ""

# Create PR
gh pr create \
  --title "$TITLE" \
  --body "$DESCRIPTION" \
  --base "$BASE_BRANCH" \
  --head "$CURRENT_BRANCH"

echo ""
echo "✅ PR created successfully!"
echo "🔍 SpotBugs review will be added automatically when PR is opened"
