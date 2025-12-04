# Branch Protection Setup

This document describes the recommended branch protection rules for the Start project.

## Required Branch Protection Rules

### Main Branch (`main`)

**Protection Rules:**
- ✅ Require a pull request before merging
- ✅ Require approvals: 1 (or more as needed)
- ✅ Dismiss stale pull request approvals when new commits are pushed
- ✅ Require status checks to pass before merging
- ✅ Require branches to be up to date before merging
- ✅ Require conversation resolution before merging
- ✅ Do not allow bypassing the above settings

**Required Status Checks:**
- `format-check` (Format Check)
- `code-quality` (Code Quality)
- `test` (Tests)
- `build` (Build Application)
- `security` (Security Checks)
- `SonarCloud Quality Gate` (if available)

**Restrictions:**
- Allow force pushes: ❌ No
- Allow deletions: ❌ No

### Develop Branch (`develop`)

**Protection Rules:**
- ✅ Require a pull request before merging
- ✅ Require approvals: 1
- ✅ Require status checks to pass before merging
- ✅ Require branches to be up to date before merging

**Required Status Checks:**
- `format-check`
- `code-quality`
- `test`
- `build`
- `security`

## Setting Up Branch Protection

### Using GitHub CLI

```bash
# Protect main branch
gh api repos/:owner/:repo/branches/main/protection \
  --method PUT \
  --field required_status_checks='{"strict":true,"contexts":["format-check","code-quality","test","build","security"]}' \
  --field enforce_admins=true \
  --field required_pull_request_reviews='{"required_approving_review_count":1,"dismiss_stale_reviews":true,"require_code_owner_reviews":false}' \
  --field restrictions=null \
  --field allow_force_pushes=false \
  --field allow_deletions=false

# Protect develop branch
gh api repos/:owner/:repo/branches/develop/protection \
  --method PUT \
  --field required_status_checks='{"strict":true,"contexts":["format-check","code-quality","test","build","security"]}' \
  --field enforce_admins=true \
  --field required_pull_request_reviews='{"required_approving_review_count":1,"dismiss_stale_reviews":true}' \
  --field restrictions=null \
  --field allow_force_pushes=false \
  --field allow_deletions=false
```

### Using GitHub Web Interface

1. Go to repository **Settings** → **Branches**
2. Click **Add rule** or **Edit** for existing branch
3. Configure as described above
4. Save changes

## Verification

After setting up branch protection:

1. Create a test branch
2. Make changes
3. Try to push directly to `main` (should fail)
4. Create a PR (should require checks to pass)

## References

- [GitHub Branch Protection](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches/about-protected-branches)
- [GitHub CLI API](https://cli.github.com/manual/gh_api)

