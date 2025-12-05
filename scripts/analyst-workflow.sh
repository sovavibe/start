#!/bin/bash
# Automated workflow for Analysts - Zero knowledge required!
# Just run this script and follow the prompts

set -e

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║     Analyst Workflow - Automated from Start to PR         ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
echo ""

# Step 1: Setup
echo -e "${YELLOW}Step 1: Setting up project...${NC}"
if ! make check-setup 2>/dev/null; then
    echo -e "${YELLOW}Project not set up. Running setup...${NC}"
    make setup
fi
echo -e "${GREEN}✅ Setup complete${NC}"
echo ""

# Step 2: Get hypothesis/idea
echo -e "${YELLOW}Step 2: What are you analyzing?${NC}"
read -p "Enter your hypothesis/idea (what needs to be done): " HYPOTHESIS
echo ""

# Step 3: Get type
echo -e "${YELLOW}Step 3: What type of change is this?${NC}"
echo "1) feat - New feature"
echo "2) fix - Bug fix"
echo "3) docs - Documentation"
echo "4) refactor - Code refactoring"
echo "5) test - Tests"
echo "6) chore - Maintenance"
read -p "Choose (1-6): " TYPE_CHOICE

case $TYPE_CHOICE in
    1) TYPE="feat" ;;
    2) TYPE="fix" ;;
    3) TYPE="docs" ;;
    4) TYPE="refactor" ;;
    5) TYPE="test" ;;
    6) TYPE="chore" ;;
    *) TYPE="feat" ;;
esac
echo ""

# Step 4: Get scope
echo -e "${YELLOW}Step 4: What area does this affect?${NC}"
echo "Common scopes: jmix, vaadin, service, entity, view, config, ci, test"
read -p "Enter scope (or press Enter for 'config'): " SCOPE
SCOPE=${SCOPE:-config}
echo ""

# Step 5: Generate branch name
echo -e "${YELLOW}Step 5: Generating branch name...${NC}"
BRANCH_DESC=$(echo "$HYPOTHESIS" | tr '[:upper:]' '[:lower:]' | sed 's/[^a-z0-9]/-/g' | sed 's/--*/-/g' | sed 's/^-\|-$//g' | cut -c1-50)
BRANCH_NAME="${TYPE}/${SCOPE}-${BRANCH_DESC}"
echo -e "${GREEN}Branch name: ${BRANCH_NAME}${NC}"
echo ""

# Step 6: Create branch
echo -e "${YELLOW}Step 6: Creating branch...${NC}"
git checkout -b "$BRANCH_NAME" 2>/dev/null || git checkout "$BRANCH_NAME"
echo -e "${GREEN}✅ Branch created${NC}"
echo ""

# Step 7: Analysis commands
echo -e "${YELLOW}Step 7: Running analysis...${NC}"
echo -e "${BLUE}Checking code quality baseline...${NC}"
make analyze-full || echo -e "${YELLOW}⚠️  Quality check completed with warnings${NC}"
echo ""

echo -e "${BLUE}Checking test coverage...${NC}"
make coverage 2>/dev/null || echo -e "${YELLOW}⚠️  Coverage check skipped (Docker may be required)${NC}"
echo ""

# Step 8: Summary
echo -e "${GREEN}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║                    Analysis Complete!                      ║${NC}"
echo -e "${GREEN}╚════════════════════════════════════════════════════════════╝${NC}"
echo ""
echo -e "${BLUE}Summary:${NC}"
echo "  Hypothesis: $HYPOTHESIS"
echo "  Type: $TYPE"
echo "  Scope: $SCOPE"
echo "  Branch: $BRANCH_NAME"
echo ""

# Step 9: Ask about changes
echo -e "${YELLOW}Step 8: Did you make any code/documentation changes?${NC}"
read -p "Have changes been made? (y/n): " HAS_CHANGES

if [ "$HAS_CHANGES" = "y" ] || [ "$HAS_CHANGES" = "Y" ]; then
    echo ""
    echo -e "${YELLOW}Step 9: Formatting and checking code...${NC}"
    make format || echo -e "${YELLOW}⚠️  Formatting skipped${NC}"
    make analyze-full || echo -e "${YELLOW}⚠️  Quality check completed${NC}"
    echo ""
    
    echo -e "${YELLOW}Step 10: Committing changes...${NC}"
    git add -A
    COMMIT_MSG="${TYPE}(${SCOPE}): ${HYPOTHESIS}"
    git commit -m "$COMMIT_MSG" || echo -e "${YELLOW}⚠️  No changes to commit${NC}"
    echo ""
    
    echo -e "${YELLOW}Step 11: Pushing branch...${NC}"
    git push -u origin "$BRANCH_NAME" || {
        echo -e "${RED}❌ Push failed. Please push manually:${NC}"
        echo -e "${YELLOW}   git push -u origin $BRANCH_NAME${NC}"
        exit 1
    }
    echo -e "${GREEN}✅ Branch pushed${NC}"
    echo ""
    
    echo -e "${YELLOW}Step 12: Creating Pull Request...${NC}"
    ./scripts/create-pr.sh || {
        echo -e "${YELLOW}⚠️  Auto PR creation failed. Create manually:${NC}"
        echo -e "${BLUE}   gh pr create --title \"$COMMIT_MSG\"${NC}"
    }
    echo ""
fi

echo -e "${GREEN}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║                      All Done! 🎉                          ║${NC}"
echo -e "${GREEN}╚════════════════════════════════════════════════════════════╝${NC}"
echo ""
echo -e "${BLUE}Next steps:${NC}"
echo "  • PR will be automatically reviewed"
echo "  • SpotBugs review will be added automatically"
echo "  • Wait for CI/CD checks to pass"
echo "  • Team Lead will assign reviewer"
echo ""
