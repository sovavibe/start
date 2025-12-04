#!/bin/bash
# Disable Automatic Analysis in SonarCloud project
# This script requires SONAR_TOKEN environment variable

set -e

PROJECT_KEY="sovavibe_start"
ORGANIZATION="sovavibe"

if [ -z "$SONAR_TOKEN" ]; then
    echo "❌ SONAR_TOKEN environment variable is not set"
    echo "   Get token from: https://sonarcloud.io/account/security"
    exit 1
fi

echo "🔧 Disabling Automatic Analysis for project: $PROJECT_KEY"
echo ""

# Note: SonarCloud API doesn't have direct endpoint to disable Automatic Analysis
# This must be done manually in project settings:
# 1. Go to: https://sonarcloud.io/project/settings?project=$PROJECT_KEY
# 2. Navigate to: Administration → Analysis Method
# 3. Turn OFF "SonarCloud Automatic Analysis"

echo "⚠️  Automatic Analysis cannot be disabled via API"
echo "   Please disable it manually:"
echo "   1. Go to: https://sonarcloud.io/project/settings?project=$PROJECT_KEY"
echo "   2. Navigate to: Administration → Analysis Method"
echo "   3. Turn OFF 'SonarCloud Automatic Analysis'"
echo ""
echo "✅ After disabling, CI/CD analysis will work correctly"
