# SonarCloud Verification Guide

This guide explains how to verify that the SonarCloud project `sovavibe_start` is properly connected and configured.

## Quick Check

Run the verification script:

```bash
./scripts/check-sonarcloud.sh
```

## Manual Verification

### 1. Check Project Exists

**Via Web Interface:**
1. Go to: https://sonarcloud.io/project/overview?id=sovavibe_start
2. If project exists, you'll see the project dashboard
3. If you see "Project not found", the project needs to be created

**Project Details:**
- **Project Key**: `sovavibe_start`
- **Organization**: `sovavibe`
- **URL**: https://sonarcloud.io/project/overview?id=sovavibe_start

### 2. Verify GitHub Integration

1. Go to: https://sonarcloud.io/project/settings?project=sovavibe_start
2. Navigate to **GitHub** section
3. Verify:
   - ✅ Repository is connected: `sovavibe/start`
   - ✅ Auto-scan is enabled (if desired)
   - ✅ Quality gate is configured

### 3. Check Quality Gate

1. Go to: https://sonarcloud.io/dashboard?id=sovavibe_start
2. Verify:
   - ✅ Quality gate status is visible
   - ✅ Coverage thresholds are set (85%/75%/90%)
   - ✅ Complexity limits are configured (≤10)

### 4. Verify GitHub Secret

**Check in GitHub:**
1. Go to: https://github.com/sovavibe/start/settings/secrets/actions
2. Verify `SONAR_TOKEN` exists
3. Check last updated date

**Via CLI:**
```bash
gh secret list | grep SONAR_TOKEN
```

### 5. Test Integration

**Create a Test PR:**
1. Create a test branch:
   ```bash
   git checkout -b test/sonarcloud-verification
   git commit --allow-empty -m "test: verify SonarCloud integration"
   git push origin test/sonarcloud-verification
   ```

2. Create a Pull Request

3. Check PR status:
   - Look for "SonarCloud Code Analysis" check
   - Verify it runs and completes
   - Check quality gate status in PR

**Expected Result:**
- ✅ SonarCloud analysis appears in PR checks
- ✅ Quality gate status is shown
- ✅ Coverage report is available
- ✅ Issues (if any) are reported

## API Verification

If you have `SONAR_TOKEN`, you can verify via API:

```bash
# Get SONAR_TOKEN from GitHub secrets (for local testing)
# Note: Secrets are not accessible via CLI for security reasons

# Check project exists
curl -u "${SONAR_TOKEN}:" \
  "https://sonarcloud.io/api/project_analyses/search?project=sovavibe_start"

# Get project status
curl -u "${SONAR_TOKEN}:" \
  "https://sonarcloud.io/api/qualitygates/project_status?projectKey=sovavibe_start"
```

## Troubleshooting

### Project Not Found

**Problem**: Project `sovavibe_start` doesn't exist in SonarCloud

**Solution**:
1. Go to [SonarCloud](https://sonarcloud.io/)
2. Sign in with GitHub
3. Click **+** → **Analyze new project**
4. Select **GitHub** → **Select repository**
5. Choose `sovavibe/start`
6. Project key will be auto-generated: `sovavibe_start`
7. Click **Set Up**

### GitHub Integration Not Working

**Problem**: SonarCloud doesn't analyze PRs

**Solution**:
1. Go to SonarCloud project settings
2. Check **GitHub** section
3. Verify repository is connected
4. Enable **Auto-scan** if needed
5. Check GitHub App permissions

### Quality Gate Not Blocking PRs

**Problem**: Quality gate fails but doesn't block merge

**Solution**:
1. Check SonarCloud project settings
2. Verify quality gate is configured
3. Check GitHub branch protection rules
4. Ensure SonarCloud status check is required

### Token Issues

**Problem**: Authentication errors in CI/CD

**Solution**:
1. Verify `SONAR_TOKEN` is set in GitHub secrets
2. Check token hasn't expired
3. Regenerate token if needed:
   - Go to: https://sonarcloud.io/account/security
   - Generate new token
   - Update GitHub secret

## Configuration Verification

### Project Key

Verify in `build.gradle`:
```groovy
property 'sonar.projectKey', 'sovavibe_start'
```

### Organization

Verify in `build.gradle`:
```groovy
property 'sonar.organization', 'sovavibe'
```

### Host URL

Verify in `build.gradle`:
```groovy
property 'sonar.host.url', 'https://sonarcloud.io'
```

## Expected Behavior

### In CI/CD

When CI/CD runs, SonarCloud step should:
1. ✅ Authenticate using `SONAR_TOKEN`
2. ✅ Upload analysis to SonarCloud
3. ✅ Report quality gate status
4. ✅ Show results in GitHub PR

### In Pull Requests

When PR is created:
1. ✅ SonarCloud analysis runs automatically
2. ✅ Quality gate status appears in PR checks
3. ✅ Coverage report is available
4. ✅ Issues are reported (if any)

### Quality Gate

Quality gate should enforce:
- ✅ Coverage: 85% instructions, 75% branches, 90% lines
- ✅ Complexity: ≤10 per method
- ✅ Duplication: <3%
- ✅ No new issues (configurable)

## Verification Checklist

- [ ] Project exists in SonarCloud: https://sonarcloud.io/project/overview?id=sovavibe_start
- [ ] GitHub integration is enabled
- [ ] `SONAR_TOKEN` secret is configured in GitHub
- [ ] Project key matches: `sovavibe_start`
- [ ] Organization matches: `sovavibe`
- [ ] Quality gate is configured
- [ ] Coverage thresholds are set
- [ ] Test PR shows SonarCloud analysis
- [ ] Quality gate blocks merge on failure

## Quick Links

- **Project Dashboard**: https://sonarcloud.io/project/overview?id=sovavibe_start
- **Project Settings**: https://sonarcloud.io/project/settings?project=sovavibe_start
- **Quality Gate**: https://sonarcloud.io/dashboard?id=sovavibe_start
- **GitHub Secrets**: https://github.com/sovavibe/start/settings/secrets/actions

## Related Documentation

- [CI/CD Setup Guide](CI_CD_SETUP.md) - SonarCloud setup instructions
- [Quality Gates Documentation](../quality/QUALITY_GATES.md) - Quality gate details
- [CI/CD Documentation](CI_CD.md) - Pipeline documentation


