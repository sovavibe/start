# CI/CD Setup Guide

This guide explains how to set up CI/CD for the Start project, including required secrets and configuration.

## GitHub Secrets

The CI/CD pipeline requires the following secrets to be configured in GitHub repository settings:

### Required Secrets

| Secret Name | Purpose | How to Obtain |
|-------------|---------|---------------|
| `SONAR_TOKEN` | SonarCloud authentication | [SonarCloud Token](https://sonarcloud.io/account/security) |
| `DOCKER_HUB_TOKEN` | Docker Hub authentication | [Docker Hub Access Token](https://hub.docker.com/settings/security) |

### Optional Secrets

| Secret Name | Purpose | When Needed |
|-------------|---------|-------------|
| `GITHUB_TOKEN` | GitHub API access | Auto-provided by GitHub Actions |

## Setting Up Secrets

### 1. SonarCloud Token

1. Go to [SonarCloud](https://sonarcloud.io/)
2. Sign in with your GitHub account
3. Navigate to: **My Account** → **Security** → **Generate Token**
4. Copy the token
5. In GitHub repository: **Settings** → **Secrets and variables** → **Actions** → **New repository secret**
6. Name: `SONAR_TOKEN`
7. Value: Paste the token
8. Click **Add secret**

### 2. Docker Hub Token

1. Go to [Docker Hub](https://hub.docker.com/)
2. Sign in
3. Navigate to: **Account Settings** → **Security** → **New Access Token**
4. Create token with **Read & Write** permissions
5. Copy the token (it won't be shown again)
6. In GitHub repository: **Settings** → **Secrets and variables** → **Actions** → **New repository secret**
7. Name: `DOCKER_HUB_TOKEN`
8. Value: Paste the token
9. Click **Add secret**

## SonarCloud Setup

### 1. Create Project

1. Go to [SonarCloud](https://sonarcloud.io/)
2. Click **+** → **Analyze new project**
3. Select **GitHub** → **Select repository**
4. Choose your repository
5. Project key will be: `sovavibe_start` (or your org/repo)
6. Click **Set Up**

### 2. Configure Project

1. In SonarCloud project settings, verify:
   - **Project Key**: `sovavibe_start`
   - **Organization**: `sovavibe`
   - **Language**: Java
   - **Repository**: `sovavibe/start`

2. **GitHub Integration**:
   - Go to project settings → **GitHub** section
   - Verify repository is connected
   - Enable auto-scan if desired

### 3. Quality Gate

The quality gate is automatically configured. It will:
- Block PRs if quality gate fails
- Require coverage thresholds (85%/75%/90%)
- Enforce complexity limits (≤10)

### 4. Verify Connection

**Quick Check:**
```bash
./scripts/check-sonarcloud.sh
```

**Manual Check:**
1. Open: https://sonarcloud.io/project/overview?id=sovavibe_start
2. Verify project exists and shows analysis
3. Check quality gate status

See [SonarCloud Verification Guide](ci-cd/SONARCLOUD_VERIFICATION.md) for detailed verification steps.

## Docker Hub Setup

### 1. Create Repository

1. Go to [Docker Hub](https://hub.docker.com/)
2. Click **Create Repository**
3. Name: `start`
4. Visibility: **Public** (for open source) or **Private**
5. Click **Create**

### 2. Update Workflow

The workflow uses `DOCKER_HUB_USERNAME` environment variable:
- Default: `sovavibe`
- Update in `.github/workflows/ci.yml` if different

## GitHub Actions Setup

### 1. Enable Actions

1. Go to repository **Settings** → **Actions** → **General**
2. Ensure **Allow all actions and reusable workflows** is enabled
3. Save

### 2. Branch Protection

Set up branch protection for `main` and `develop`:

1. Go to **Settings** → **Branches**
2. Add rule for `main`:
   - ✅ Require a pull request before merging
   - ✅ Require status checks to pass
   - ✅ Require branches to be up to date
   - ✅ Select: `format-check`, `code-quality`, `test`, `build`, `security`
   - ✅ Require SonarCloud quality gate to pass (if available)

3. Repeat for `develop` branch

### 3. Environments

Create production environment for releases:

1. Go to **Settings** → **Environments**
2. Click **New environment**
3. Name: `production`
4. Add protection rules if needed
5. Save

## Verification

### 1. Test CI/CD

1. Create a test branch:
   ```bash
   git checkout -b test/ci-setup
   ```

2. Make a small change (e.g., update README)

3. Commit and push:
   ```bash
   git add .
   git commit -m "test: verify CI/CD setup"
   git push origin test/ci-setup
   ```

4. Create a Pull Request

5. Verify all checks pass:
   - ✅ Format Check
   - ✅ Code Quality
   - ✅ Tests
   - ✅ Build
   - ✅ Security
   - ✅ SonarCloud Quality Gate

### 2. Test Release

1. Create a test tag:
   ```bash
   git tag v0.0.1-test
   git push origin v0.0.1-test
   ```

2. Verify release workflow runs:
   - Docker image built and pushed
   - GitHub Release created

3. Delete test tag:
   ```bash
   git tag -d v0.0.1-test
   git push origin :refs/tags/v0.0.1-test
   ```

## Troubleshooting

### SonarCloud Issues

**Problem**: SonarCloud scan fails with authentication error

**Solution**:
1. Verify `SONAR_TOKEN` secret is set correctly
2. Check token hasn't expired
3. Verify project key matches in `config/sonar-project.properties`

**Problem**: Quality gate not blocking PRs

**Solution**:
1. Check SonarCloud project settings
2. Verify GitHub integration is enabled
3. Check branch protection rules include SonarCloud status

### Docker Hub Issues

**Problem**: Docker build fails with authentication error

**Solution**:
1. Verify `DOCKER_HUB_TOKEN` secret is set
2. Check token has **Read & Write** permissions
3. Verify `DOCKER_HUB_USERNAME` matches your Docker Hub username

**Problem**: Image not pushed

**Solution**:
1. Check workflow runs on `main` or `develop` branches
2. Verify `docker-build` job dependencies pass
3. Check Docker Hub repository exists

### GitHub Actions Issues

**Problem**: Workflow not running

**Solution**:
1. Check Actions are enabled in repository settings
2. Verify workflow file syntax is correct
3. Check branch protection doesn't block workflows

**Problem**: Status checks not appearing

**Solution**:
1. Wait for workflow to complete
2. Check workflow has proper job names
3. Verify branch protection includes correct check names

## Best Practices

### Security

1. **Never commit secrets** to repository
2. **Rotate tokens regularly** (every 90 days)
3. **Use least privilege** for tokens
4. **Review access logs** regularly

### Maintenance

1. **Monitor workflow runs** for failures
2. **Update dependencies** regularly (Dependabot helps)
3. **Review quality metrics** in SonarCloud
4. **Keep workflows updated** with latest action versions

### Performance

1. **Use caching** (already configured for Gradle)
2. **Parallel jobs** (already configured)
3. **Fail-fast** (already configured)
4. **Clean up artifacts** (retention configured)

## References

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [SonarCloud Documentation](https://docs.sonarcloud.io/)
- [Docker Hub Documentation](https://docs.docker.com/docker-hub/)
- [GitHub Secrets](https://docs.github.com/en/actions/security-guides/encrypted-secrets)

## Related Documentation

- [CI/CD Documentation](CI_CD.md) - Detailed pipeline documentation
- [Local Development Guide](LOCAL_DEVELOPMENT.md) - Local setup
- [Contributing Guide](../CONTRIBUTING.md) - Contribution process

