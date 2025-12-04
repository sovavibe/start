#!/bin/bash
# Check SonarCloud project connection and status

set -e

echo "🔍 Checking SonarCloud project connection..."
echo ""

PROJECT_KEY="sovavibe_start"
ORGANIZATION="sovavibe"
SONAR_URL="https://sonarcloud.io"

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Check if SONAR_TOKEN is set in GitHub
echo "1. Checking GitHub Secrets..."
if gh secret list | grep -q "SONAR_TOKEN"; then
    echo -e "${GREEN}✅ SONAR_TOKEN secret is configured in GitHub${NC}"
    SECRET_DATE=$(gh secret list | grep SONAR_TOKEN | awk '{print $2}')
    echo "   Last updated: $SECRET_DATE"
else
    echo -e "${RED}❌ SONAR_TOKEN secret is NOT configured${NC}"
    echo "   Go to: https://github.com/sovavibe/start/settings/secrets/actions"
    echo "   Add secret: SONAR_TOKEN"
fi
echo ""

# Check project configuration
echo "2. Checking Project Configuration..."
PROJECT_KEY_LINE=$(grep -E "property.*sonar.projectKey" build.gradle 2>/dev/null | head -1)
ORGANIZATION_LINE=$(grep -E "property.*sonar.organization" build.gradle 2>/dev/null | head -1)

if echo "$PROJECT_KEY_LINE" | grep -q "sovavibe_start"; then
    echo -e "${GREEN}✅ Project key configured: sovavibe_start${NC}"
elif [ -n "$PROJECT_KEY_LINE" ]; then
    PROJECT_KEY_CONFIG=$(echo "$PROJECT_KEY_LINE" | grep -o "'[^']*'" | tr -d "'" | head -1)
    echo -e "${YELLOW}⚠️  Project key in build.gradle: $PROJECT_KEY_CONFIG${NC}"
else
    echo -e "${RED}❌ Project key not found in build.gradle${NC}"
fi

if echo "$ORGANIZATION_LINE" | grep -q "sovavibe"; then
    echo -e "${GREEN}✅ Organization configured: sovavibe${NC}"
elif [ -n "$ORGANIZATION_LINE" ]; then
    ORGANIZATION_CONFIG=$(echo "$ORGANIZATION_LINE" | grep -o "'[^']*'" | tr -d "'" | head -1)
    echo -e "${YELLOW}⚠️  Organization in build.gradle: $ORGANIZATION_CONFIG${NC}"
else
    echo -e "${RED}❌ Organization not found in build.gradle${NC}"
fi
echo ""

# Instructions for manual check
echo "3. Manual Verification Steps:"
echo ""
echo "   a) Check SonarCloud project exists:"
echo "      ${SONAR_URL}/project/overview?id=${PROJECT_KEY}"
echo ""
echo "   b) Verify GitHub integration:"
echo "      - Go to: ${SONAR_URL}/project/settings?project=${PROJECT_KEY}"
echo "      - Check 'GitHub' section"
echo "      - Verify repository is connected"
echo ""
echo "   c) Check quality gate status:"
echo "      - Go to: ${SONAR_URL}/dashboard?id=${PROJECT_KEY}"
echo "      - Verify quality gate is configured"
echo ""
echo "   d) Test via PR:"
echo "      - Create a test PR"
echo "      - Check if SonarCloud analysis appears in PR checks"
echo ""

# Check if we can access SonarCloud API (requires token)
echo "4. API Check (requires SONAR_TOKEN):"
echo ""
echo "   To check via API, you need SONAR_TOKEN:"
echo "   curl -u \${SONAR_TOKEN}: \"${SONAR_URL}/api/project_analyses/search?project=${PROJECT_KEY}\""
echo ""
echo "   Or use SonarCloud web interface to verify project exists."
echo ""

# Summary
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "📋 Summary:"
echo ""
echo "   Project Key: ${PROJECT_KEY}"
echo "   Organization: ${ORGANIZATION}"
echo "   SonarCloud URL: ${SONAR_URL}/project/overview?id=${PROJECT_KEY}"
echo ""
echo "   Next steps:"
echo "   1. Open SonarCloud project: ${SONAR_URL}/project/overview?id=${PROJECT_KEY}"
echo "   2. Verify project exists and is connected to GitHub"
echo "   3. Check quality gate is configured"
echo "   4. Test by creating a PR (SonarCloud analysis should run)"
echo ""

