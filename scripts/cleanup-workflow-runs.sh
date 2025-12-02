#!/bin/bash
# Script to clean up old GitHub Actions workflow runs
# Requires: gh CLI (https://cli.github.com/)
# Usage: ./scripts/cleanup-workflow-runs.sh [workflow-name] [keep-count]

set -euo pipefail

WORKFLOW_NAME="${1:-CI/CD Pipeline}"
KEEP_COUNT="${2:-10}"

if ! command -v gh &> /dev/null; then
    echo "❌ GitHub CLI (gh) is not installed"
    echo "Install from: https://cli.github.com/"
    exit 1
fi

if ! gh auth status &> /dev/null; then
    echo "❌ Not authenticated with GitHub CLI"
    echo "Run: gh auth login"
    exit 1
fi

echo "🧹 Cleaning up old workflow runs for: $WORKFLOW_NAME"
echo "📊 Keeping latest $KEEP_COUNT runs"

# Get workflow ID
WORKFLOW_ID=$(gh workflow list --json name,id --jq ".[] | select(.name == \"$WORKFLOW_NAME\") | .id")

if [ -z "$WORKFLOW_ID" ]; then
    echo "❌ Workflow '$WORKFLOW_NAME' not found"
    exit 1
fi

echo "🔍 Found workflow ID: $WORKFLOW_ID"

# Get all runs (excluding the ones to keep)
RUNS=$(gh run list --workflow="$WORKFLOW_ID" --json databaseId,status,conclusion --limit 1000 --jq ".[$KEEP_COUNT:] | .[] | select(.status == \"completed\") | .databaseId")

if [ -z "$RUNS" ]; then
    echo "✅ No old runs to clean up"
    exit 0
fi

RUN_COUNT=$(echo "$RUNS" | wc -l | tr -d ' ')
echo "🗑️  Found $RUN_COUNT old runs to delete"

# Delete runs
DELETED=0
for RUN_ID in $RUNS; do
    if gh run delete "$RUN_ID" --confirm 2>/dev/null; then
        DELETED=$((DELETED + 1))
        echo "  ✅ Deleted run $RUN_ID"
    else
        echo "  ⚠️  Failed to delete run $RUN_ID"
    fi
done

echo "✨ Cleanup complete: deleted $DELETED runs"

