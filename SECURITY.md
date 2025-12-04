# Security Policy

## Supported Versions

We provide security updates for the following versions:

| Version | Supported          |
| ------- | ------------------ |
| 0.0.1   | :white_check_mark: |
| < 0.0.1 | :x:                |

## Reporting a Vulnerability

We take security vulnerabilities seriously. If you discover a security vulnerability, please follow these steps:

### 1. Do Not Disclose Publicly

**Do not** create a public GitHub issue for security vulnerabilities. This could put users at risk.

### 2. Report Privately

Please report security vulnerabilities by emailing:

**security@digtp.com**

Include the following information:

- **Description**: Clear description of the vulnerability
- **Impact**: Potential impact and severity
- **Steps to Reproduce**: Detailed steps to reproduce the issue
- **Affected Versions**: Which versions are affected
- **Suggested Fix**: If you have a suggested fix (optional)

### 3. Response Timeline

- **Initial Response**: Within 48 hours
- **Status Update**: Within 7 days
- **Resolution**: Depends on severity and complexity

### 4. Disclosure Policy

- We will acknowledge receipt of your report within 48 hours
- We will provide regular updates on the status of the vulnerability
- We will notify you when the vulnerability is fixed
- We will credit you in the security advisory (if you wish)

### 5. What to Report

Please report:

- Authentication and authorization bypasses
- SQL injection vulnerabilities
- Cross-site scripting (XSS)
- Cross-site request forgery (CSRF)
- Remote code execution
- Sensitive data exposure
- Security misconfigurations
- Dependency vulnerabilities with known exploits

### 6. What NOT to Report

Please do not report:

- Issues that require physical access to the system
- Issues that require social engineering
- Denial of service (DoS) attacks
- Issues in dependencies without known exploits (use Dependabot)
- Issues already reported (check existing issues)

## Security Best Practices

### For Users

- Keep dependencies up to date
- Use strong passwords
- Enable security features (HTTPS, etc.)
- Review security advisories regularly

### For Contributors

- Follow secure coding practices
- Review dependencies for vulnerabilities
- Run security scans before submitting PRs
- Report vulnerabilities responsibly

## Security Scanning

This project uses automated security scanning:

- **OWASP Dependency-Check**: Scans dependencies for known vulnerabilities
- **Trivy**: Container and filesystem vulnerability scanning
- **GitHub Security Advisories**: Automated dependency vulnerability alerts
- **Dependabot**: Automated dependency updates

## Security Updates

Security updates are released as:

- **Patch releases**: For critical vulnerabilities
- **Minor releases**: For important security fixes
- **Advisories**: For significant security issues

## Security Contact

For security-related questions or concerns:

- **Email**: security@digtp.com
- **GitHub Security**: Use GitHub's private vulnerability reporting (if enabled)

## Acknowledgments

We appreciate responsible disclosure of security vulnerabilities. Contributors who report valid security issues will be:

- Credited in security advisories (if desired)
- Listed in project acknowledgments
- Thanked in release notes

Thank you for helping keep this project secure!

