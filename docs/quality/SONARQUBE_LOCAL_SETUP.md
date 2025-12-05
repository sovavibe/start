# Local SonarQube Server Setup

Guide for setting up a local SonarQube Server for use with IntelliJ IDEA SonarLint in Connected Mode.

## Why Local SonarQube?

- **Team consistency**: All developers use the same quality profile and rules
- **Offline development**: Works without internet connection
- **Custom rules**: Configure project-specific rules without affecting SonarCloud
- **Fast feedback**: Local analysis is faster than SonarCloud

## Prerequisites

- Docker and Docker Compose installed
- PostgreSQL running (via `make postgres-up`)

## Quick Start

### 1. Start SonarQube Server

```bash
make sonarqube-up
```

This will:
- Start PostgreSQL (if not running)
- Create `sonar` database
- Start SonarQube Server on http://localhost:9000

**Wait 30-60 seconds** for SonarQube to fully start.

### 2. Access SonarQube

Open http://localhost:9000 in your browser.

**Default credentials**:
- Username: `admin`
- Password: `admin`

**First login**: You'll be prompted to change the password. Save it securely.

### 3. Create Project

1. Log in to SonarQube
2. Go to **Projects** → **Create Project**
3. Select **Manually**
4. Enter:
   - **Project key**: `sovavibe_start`
   - **Display name**: `Start`
5. Click **Set Up**

### 4. Generate Token

1. Go to **My Account** → **Security**
2. Generate a new token (e.g., `local-dev-token`)
3. **Save the token** - you'll need it for IntelliJ IDEA

### 5. Configure Quality Profile

1. Go to **Quality Profiles** → **Java**
2. Find **Sonar way** profile
3. Click **Copy** to create a custom profile (e.g., `Start Custom`)
4. In the new profile, find rule `java:S117` (Local variable naming convention)
5. Click **Deactivate** to disable it
6. Go to **Projects** → **Administration** → **Quality Profiles**
7. Set your custom profile as default for the project

### 6. Configure IntelliJ IDEA

1. Open **Settings** → **Tools** → **SonarQube for IDE** → **Connections**
2. Click **+** to add a new connection
3. Enter:
   - **Name**: `Local SonarQube`
   - **Server URL**: `http://localhost:9000`
   - **Token**: (paste the token from step 4)
4. Click **Test Connection** → **OK**

### 7. Bind Project to SonarQube

1. Go to **Settings** → **Tools** → **SonarQube for IDE** → **Project Settings**
2. Check **Bind project to SonarQube (Server, Cloud)**
3. Select **Connection**: `Local SonarQube`
4. Enter **Project key**: `sovavibe_start`
5. Click **OK**

### 8. Sync Settings

1. In IntelliJ IDEA, open **SonarQube for IDE** tool window (bottom panel)
2. Click **Sync** or **Update binding**
3. Wait for synchronization to complete

## Usage

### Start/Stop SonarQube

```bash
# Start SonarQube
make sonarqube-up

# Stop SonarQube
make sonarqube-down

# View logs
make sonarqube-logs
```

### Run Analysis

SonarQube will automatically analyze code when:
- Files are saved in IntelliJ IDEA (if auto-analysis is enabled)
- You manually trigger analysis via **SonarQube for IDE** tool window

### Manual Analysis via Gradle

```bash
# Configure build.gradle to use local SonarQube
# Update sonar.properties in build.gradle:
# sonar.host.url=http://localhost:9000
# sonar.login=<your-token>

./gradlew sonar
```

## Configuration

### Update Rules

1. Go to **Quality Profiles** → **Java** → Your profile
2. Find the rule you want to change
3. Click **Activate** or **Deactivate**
4. Changes apply immediately to all connected IDEs

### Update Project Settings

Settings from `config/sonar-project.properties` can be applied via:
- **Project Settings** → **General Settings** in SonarQube web UI
- Or via `./gradlew sonar` (applies properties from `config/sonar-project.properties`)

## Troubleshooting

### SonarQube won't start

**Problem**: Container exits immediately

**Solution**:
```bash
# Check logs
make sonarqube-logs

# Common issues:
# 1. PostgreSQL not running - run: make postgres-up
# 2. Database not created - run: make sonarqube-up (creates DB automatically)
# 3. Port 9000 already in use - stop other service or change port in docker-compose.dev.yml
```

### Can't connect from IntelliJ IDEA

**Problem**: Connection test fails

**Solution**:
1. Verify SonarQube is running: http://localhost:9000
2. Check token is correct (regenerate if needed)
3. Verify server URL: `http://localhost:9000` (not `https://`)
4. Check firewall/network settings

### Rules not syncing

**Problem**: Changes in SonarQube not reflected in IDE

**Solution**:
1. In IntelliJ IDEA: **Settings** → **Tools** → **SonarQube for IDE** → **Project Settings**
2. Click **Update binding** or **Sync**
3. Restart IntelliJ IDEA if needed

## Data Persistence

SonarQube data is stored in Docker volumes:
- `sonarqube_data_dev`: Database and configuration
- `sonarqube_extensions_dev`: Plugins and extensions
- `sonarqube_logs_dev`: Log files

**To reset SonarQube**:
```bash
make sonarqube-down
docker volume rm start_sonarqube_data_dev start_sonarqube_extensions_dev start_sonarqube_logs_dev
make sonarqube-up
```

## Team Setup

For team consistency:

1. **Export Quality Profile**:
   - Go to **Quality Profiles** → Your profile → **Back Up**
   - Share the exported file with the team

2. **Import Quality Profile**:
   - Go to **Quality Profiles** → **Restore Profile**
   - Upload the exported file

3. **Document Setup**:
   - Add SonarQube connection details to team wiki/docs
   - Share token securely (use password manager)

## Comparison: Local vs SonarCloud

| Feature | Local SonarQube | SonarCloud |
|---------|----------------|------------|
| **Setup** | Requires Docker | Automatic |
| **Cost** | Free | Free for open source |
| **Internet** | Not required | Required |
| **Team sync** | Manual (export/import profiles) | Automatic |
| **CI/CD** | Requires self-hosted runner | Integrated |
| **Storage** | Local (Docker volumes) | Cloud |

**Recommendation**: Use **SonarCloud** for production/CI/CD, **Local SonarQube** for development/offline work.

