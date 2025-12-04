# GitHub Repository Setup

This guide helps you set up the GitHub repository for the Start open source project.

## Current Status

### ✅ Already Configured

- **Repository**: Public, issues, discussions, wiki enabled
- **Secrets**: 
  - `SONAR_TOKEN` ✅
  - `DOCKER_HUB_TOKEN` ✅
- **Environments**: 
  - `production` ✅ (for releases)
- **Default Branch**: `main`

### ⚠️ Needs Configuration

- **Branch Protection**: Not configured (see below)
- **Repository Topics**: Can be added/updated
- **SonarCloud Integration**: Needs verification

## Quick Setup

Run the setup script:

```bash
./scripts/setup-github.sh
```

This will:
- Update repository description and topics
- Create production environment (if needed)
- Verify secrets
- Show branch protection setup instructions

## Manual Setup

### 1. Branch Protection

**Important**: Branch protection must be configured via GitHub web interface.

1. Go to: `https://github.com/sovavibe/start/settings/branches`

2. **For `main` branch**:
   - Click **Add rule** or **Edit** existing rule
   - **Branch name pattern**: `main`
   - ✅ **Require a pull request before merging**
     - ✅ Require approvals: `1`
     - ✅ Dismiss stale pull request approvals when new commits are pushed
   - ✅ **Require status checks to pass before merging**
     - ✅ Require branches to be up to date before merging
     - Select required checks:
       - `format-check`
       - `code-quality`
       - `test`
       - `build`
       - `security`
   - ✅ **Require conversation resolution before merging**
   - ❌ **Do not allow force pushes**
   - ❌ **Do not allow deletions**
   - ✅ **Do not allow bypassing the above settings**
   - Click **Create** or **Save changes**

3. **For `develop` branch** (if exists):
   - Similar configuration
   - Require approvals: `1`
   - Same status checks

See [Branch Protection Guide](.github/BRANCH_PROTECTION.md) for details.

### 2. Repository Topics

Update topics via GitHub CLI:

```bash
gh repo edit \
  --add-topic "jmix" \
  --add-topic "vaadin" \
  --add-topic "spring-boot" \
  --add-topic "java" \
  --add-topic "postgresql" \
  --add-topic "quality-gates" \
  --add-topic "code-quality" \
  --add-topic "open-source" \
  --add-topic "vibe-coding"
```

Or via web interface:
1. Go to repository main page
2. Click gear icon (⚙️) next to "About"
3. Add topics in "Topics" field

### 3. SonarCloud Integration

1. Go to [SonarCloud](https://sonarcloud.io/)
2. Sign in with GitHub
3. **Import project**:
   - Select organization: `sovavibe`
   - Select repository: `start`
   - Project key: `sovavibe_start`
4. Verify `SONAR_TOKEN` secret is set in GitHub
5. **Verify connection**:
   ```bash
   ./scripts/check-sonarcloud.sh
   ```
6. Test by creating a PR (SonarCloud analysis should run)

**Detailed verification**: See [SonarCloud Verification Guide](../ci-cd/SONARCLOUD_VERIFICATION.md)

### 4. Repository Settings

Verify these settings in **Settings** → **General**:

- ✅ **Features**:
  - Issues: Enabled
  - Projects: Enabled (optional)
  - Wiki: Enabled (optional)
  - Discussions: Enabled
- ✅ **Pull Requests**:
  - Allow merge commits: Yes
  - Allow squash merging: Yes
  - Allow rebase merging: Yes
- ✅ **Archive this repository**: No

## Verification

### 1. Test Branch Protection

```bash
# Create test branch
git checkout -b test/branch-protection
git commit --allow-empty -m "test: verify branch protection"
git push origin test/branch-protection

# Try to push directly to main (should fail)
git checkout main
git merge test/branch-protection
git push origin main  # Should be blocked
```

### 2. Test CI/CD

1. Create a PR from test branch
2. Verify all status checks appear:
   - format-check
   - code-quality
   - test
   - build
   - security
3. Verify PR cannot be merged until checks pass

### 3. Test Release

```bash
# Create test release tag
git tag v0.0.1-test
git push origin v0.0.1-test

# Verify release workflow runs
# Check GitHub Actions tab

# Clean up
git tag -d v0.0.1-test
git push origin :refs/tags/v0.0.1-test
```

## Required GitHub Secrets

| Secret | Purpose | Status |
|--------|---------|--------|
| `SONAR_TOKEN` | SonarCloud authentication | ✅ Configured |
| `DOCKER_HUB_TOKEN` | Docker Hub authentication | ✅ Configured |
| `GITHUB_TOKEN` | GitHub API access | ✅ Auto-provided |

See [CI/CD Setup Guide](CI_CD_SETUP.md) for details on setting up secrets.

## Repository Features

### Enabled Features

- ✅ **Issues**: For bug reports and feature requests
- ✅ **Discussions**: For questions and community discussions
- ✅ **Wiki**: For additional documentation (optional)
- ✅ **Projects**: For project management (optional)

### GitHub Actions

- ✅ **CI/CD Pipeline**: `.github/workflows/ci.yml`
- ✅ **Release Workflow**: `.github/workflows/release.yml`
- ✅ **Dependabot**: `.github/dependabot.yml`

## Best Practices

### Branch Strategy

- **main**: Production-ready code (protected)
- **develop**: Integration branch (protected, if used)
- **feature/***: Feature branches
- **fix/***: Bug fix branches

### Pull Requests

- All PRs must pass CI/CD checks
- At least 1 approval required
- Branch must be up to date
- Quality gate must pass

### Releases

- Use semantic versioning: `v1.0.0`, `v1.1.0`, etc.
- Create release notes
- Tag triggers release workflow

## Troubleshooting

### Branch Protection Not Working

1. Verify rule is saved in GitHub settings
2. Check required status checks are correct
3. Verify workflows are running
4. Check branch name matches pattern

### CI/CD Not Running

1. Check Actions are enabled in repository settings
2. Verify workflow files are in `.github/workflows/`
3. Check workflow syntax is correct
4. Verify secrets are set

### SonarCloud Not Analyzing

1. Verify `SONAR_TOKEN` secret is set
2. Check SonarCloud project exists
3. Verify project key matches: `sovavibe_start`
4. Check SonarCloud GitHub integration

## References

- [GitHub Branch Protection](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches)
- [GitHub Secrets](https://docs.github.com/en/actions/security-guides/encrypted-secrets)
- [GitHub Environments](https://docs.github.com/en/actions/deployment/targeting-different-environments/using-environments-for-deployment)
- [CI/CD Setup Guide](CI_CD_SETUP.md)

## Related Documentation

- [Branch Protection Guide](.github/BRANCH_PROTECTION.md) - Detailed branch protection setup
- [CI/CD Setup Guide](CI_CD_SETUP.md) - CI/CD configuration
- [Contributing Guide](../CONTRIBUTING.md) - Contribution process

