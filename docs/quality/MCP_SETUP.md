# MCP SonarQube Setup

**Note**: MCP SonarQube tools work only with **SonarCloud**, not local SonarQube Server.

For local SonarQube Server, use:
- **IntelliJ IDEA SonarLint** (Connected Mode) - see [IDE_SETUP.md](IDE_SETUP.md)
- **SonarQube Web UI** - http://localhost:9000
- **SonarQube API** - direct API calls

## Local SonarQube Configuration

### Token for IntelliJ IDEA

Your token: `sqa_859a5b51815dbbc44d0f2a20d1b13c9182664655`

**Use this token in IntelliJ IDEA:**
1. Settings → Tools → SonarQube for IDE → Connections
2. Add connection:
   - Server URL: `http://localhost:9000`
   - Token: `sqa_859a5b51815dbbc44d0f2a20d1b13c9182664655`

### Project Configuration

- **Project Key**: `sovavibe`
- **Server URL**: `http://localhost:9000`
- **Quality Profile**: `Start Custom` (with `java:S117` disabled)

## MCP SonarQube (SonarCloud Only)

If you want to use MCP tools, you need **SonarCloud** (not local server):

1. Create SonarCloud account: https://sonarcloud.io
2. Create project in SonarCloud
3. Configure MCP server in Cursor settings:
   ```json
   {
     "mcpServers": {
       "sonarqube": {
         "url": "https://sonarcloud.io",
         "token": "your_sonarcloud_token"
       }
     }
   }
   ```

## Current Setup

- ✅ Local SonarQube Server running
- ✅ Project created: `sovavibe`
- ✅ Quality profile configured
- ✅ Token generated: `sqa_859a5b51815dbbc44d0f2a20d1b13c9182664655`

**Next step**: Configure IntelliJ IDEA (see [IDE_SETUP.md](IDE_SETUP.md))

