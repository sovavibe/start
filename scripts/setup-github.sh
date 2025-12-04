#!/bin/bash
# Setup GitHub repository for open source project
# This script configures branch protection, topics, and other GitHub settings

set -e

echo "🔧 Setting up GitHub repository..."

# Get repository owner and name
REPO=$(gh repo view --json nameWithOwner -q .nameWithOwner)
echo "Repository: $REPO"

# 1. Update repository description and topics
echo ""
echo "📝 Updating repository metadata..."
gh repo edit "$REPO" \
  --description "Production-ready Jmix application template demonstrating Vibe Coding philosophy: strict quality gates, minimal suppressions, and senior-level code standards" \
  --add-topic "jmix" \
  --add-topic "vaadin" \
  --add-topic "spring-boot" \
  --add-topic "java" \
  --add-topic "postgresql" \
  --add-topic "quality-gates" \
  --add-topic "code-quality" \
  --add-topic "open-source" \
  --add-topic "vibe-coding" \
  --enable-issues \
  --enable-discussions \
  --enable-wiki

echo "✅ Repository metadata updated"

# 2. Create production environment (if not exists)
echo ""
echo "🌍 Setting up environments..."
if ! gh api "repos/$REPO/environments/production" &>/dev/null; then
  gh api "repos/$REPO/environments/production" --method PUT \
    --field wait_timer=0 \
    --field reviewers=null \
    --field deployment_branch_policy=null
  echo "✅ Production environment created"
else
  echo "✅ Production environment already exists"
fi

# 3. Branch protection setup instructions
echo ""
echo "🔒 Branch Protection Setup"
echo "=========================="
echo ""
echo "Branch protection must be configured manually via GitHub web interface:"
echo ""
echo "1. Go to: https://github.com/$REPO/settings/branches"
echo ""
echo "2. For 'main' branch, add rule with:"
echo "   - Require a pull request before merging"
echo "   - Require approvals: 1"
echo "   - Require status checks: format-check, code-quality, test, build, security"
echo "   - Require branches to be up to date"
echo "   - Do not allow force pushes"
echo "   - Do not allow deletions"
echo ""
echo "3. For 'develop' branch (if exists), add similar rule"
echo ""
echo "See .github/BRANCH_PROTECTION.md for detailed instructions"
echo ""

# 4. Verify secrets
echo ""
echo "🔐 Checking required secrets..."
SECRETS=$(gh secret list)
if echo "$SECRETS" | grep -q "SONAR_TOKEN"; then
  echo "✅ SONAR_TOKEN is configured"
else
  echo "⚠️  SONAR_TOKEN is missing - required for SonarCloud"
fi

if echo "$SECRETS" | grep -q "DOCKER_HUB_TOKEN"; then
  echo "✅ DOCKER_HUB_TOKEN is configured"
else
  echo "⚠️  DOCKER_HUB_TOKEN is missing - required for Docker builds"
fi

# 5. Summary
echo ""
echo "✅ GitHub setup complete!"
echo ""
echo "Next steps:"
echo "1. Configure branch protection (see instructions above)"
echo "2. Verify SonarCloud project is connected"
echo "3. Test CI/CD by creating a PR"
echo ""

