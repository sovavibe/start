#!/bin/bash
# Check if Automatic Analysis is enabled in SonarCloud
# This script helps diagnose SonarCloud configuration issues

set -e

PROJECT_KEY="sovavibe_start"
ORGANIZATION="sovavibe"

if [ -z "$SONAR_TOKEN" ]; then
    echo "❌ SONAR_TOKEN environment variable is not set"
    exit 1
fi

echo "🔍 Checking SonarCloud Automatic Analysis status..."
echo ""

# Try to get project settings (may not be available via API)
# The best way is to check if CI analysis works
echo "To check Automatic Analysis status:"
echo "1. Go to: https://sonarcloud.io/project/settings?project=$PROJECT_KEY"
echo "2. Navigate to: Administration → Analysis Method"
echo "3. Check if 'SonarCloud Automatic Analysis' is ON or OFF"
echo ""
echo "If Automatic Analysis is ON:"
echo "  - ⚠️  WARNING: CI/CD SonarCloud scan will FAIL due to conflict"
echo "  - Automatic Analysis and CI analysis cannot run simultaneously"
echo "  - CI/CD pipeline will be blocked until Automatic Analysis is disabled"
echo "  - Action required: Disable Automatic Analysis in project settings"
echo ""
echo "If Automatic Analysis is OFF:"
echo "  - ✅ CI/CD SonarCloud scan will run as required (blocks on failure)"
echo "  - This is the recommended configuration"
echo "  - Quality gate will properly block merge on failures"
