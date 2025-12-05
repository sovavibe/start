#!/bin/sh
# Init script for SonarQube Quality Profile setup
# This script runs inside SonarQube container after startup
# Automatically configures Quality Profile from config/sonar-project.properties

set -e

SONAR_URL="${SONAR_URL:-http://localhost:9000}"
SONAR_USER="${SONAR_USER:-admin}"
SONAR_PASSWORD="${SONAR_PASSWORD:-admin}"
PROFILE_NAME="Start Custom"
PROJECT_KEY="${SONAR_PROJECT_KEY:-sovavibe}"

# Wait for SonarQube to be ready
echo "⏳ Waiting for SonarQube to be ready..."
for i in $(seq 1 60); do
  if curl -s -f "$SONAR_URL/api/system/status" > /dev/null 2>&1; then
    echo "✅ SonarQube is ready"
    break
  fi
  if [ $i -eq 60 ]; then
    echo "❌ SonarQube is not ready after 60 attempts"
    exit 1
  fi
  sleep 2
done

# Wait for authentication to be available
echo "⏳ Waiting for authentication..."
for i in $(seq 1 30); do
  AUTH_RESPONSE=$(curl -s -u "$SONAR_USER:$SONAR_PASSWORD" "$SONAR_URL/api/authentication/validate" 2>/dev/null || echo "")
  if echo "$AUTH_RESPONSE" | grep -q '"valid":true'; then
    echo "✅ Authentication available"
    break
  fi
  if [ $i -eq 30 ]; then
    echo "⚠️  Authentication not available yet, skipping profile setup"
    exit 0
  fi
  sleep 2
done

# Create project if not exists
PROJECT_EXISTS=$(curl -s -u "$SONAR_USER:$SONAR_PASSWORD" "$SONAR_URL/api/projects/search?projects=$PROJECT_KEY" 2>/dev/null | grep -o "\"key\":\"$PROJECT_KEY\"" || true)
if [ -z "$PROJECT_EXISTS" ]; then
  echo "📦 Creating project: $PROJECT_KEY"
  curl -s -u "$SONAR_USER:$SONAR_PASSWORD" \
    -X POST "$SONAR_URL/api/projects/create" \
    -d "project=$PROJECT_KEY" \
    -d "name=$PROJECT_KEY" > /dev/null 2>&1 || true
fi

# Get or create quality profile
PROFILE_KEY=$(curl -s -u "$SONAR_USER:$SONAR_PASSWORD" "$SONAR_URL/api/qualityprofiles/search?language=java" 2>/dev/null | \
  grep -o "\"key\":\"[^\"]*\"[^}]*\"name\":\"[^\"]*$PROFILE_NAME" | \
  head -1 | \
  grep -o "\"key\":\"[^\"]*" | \
  cut -d'"' -f4)

if [ -z "$PROFILE_KEY" ]; then
  echo "📋 Creating quality profile: $PROFILE_NAME"
  SONAR_WAY_KEY=$(curl -s -u "$SONAR_USER:$SONAR_PASSWORD" "$SONAR_URL/api/qualityprofiles/search?language=java" 2>/dev/null | \
    grep -o '"key":"[^"]*Sonar way[^"]*"' | head -1 | cut -d'"' -f4)
  
  if [ -n "$SONAR_WAY_KEY" ]; then
    curl -s -u "$SONAR_USER:$SONAR_PASSWORD" \
      -X POST "$SONAR_URL/api/qualityprofiles/copy" \
      -d "fromKey=$SONAR_WAY_KEY" \
      -d "toName=$PROFILE_NAME" > /dev/null 2>&1 || true
    
    PROFILE_KEY=$(curl -s -u "$SONAR_USER:$SONAR_PASSWORD" "$SONAR_URL/api/qualityprofiles/search?language=java" 2>/dev/null | \
      grep -o "\"key\":\"[^\"]*\"[^}]*\"name\":\"[^\"]*$PROFILE_NAME" | \
      head -1 | \
      grep -o "\"key\":\"[^\"]*" | \
      cut -d'"' -f4)
  fi
fi

# Apply quality profile to project
if [ -n "$PROFILE_KEY" ]; then
  echo "🔧 Applying quality profile to project..."
  curl -s -u "$SONAR_USER:$SONAR_PASSWORD" \
    -X POST "$SONAR_URL/api/qualityprofiles/add_project" \
    -d "language=java" \
    -d "project=$PROJECT_KEY" \
    -d "qualityProfile=$PROFILE_NAME" > /dev/null 2>&1 || true
fi

# Sync rules from config/sonar-project.properties if available
if [ -f /config/sonar-project.properties ]; then
  echo "🔄 Syncing rules from config/sonar-project.properties..."
  
  RULES_TO_DISABLE=$(grep "^sonar.issue.ignore.multicriteria\." /config/sonar-project.properties 2>/dev/null | \
    grep "\.ruleKey=" | \
    sed 's/.*ruleKey=//' | \
    sort -u)
  
  if [ -n "$RULES_TO_DISABLE" ] && [ -n "$PROFILE_KEY" ]; then
    echo "$RULES_TO_DISABLE" | while IFS= read -r rule; do
      if [ -z "$rule" ]; then
        continue
      fi
      
      # Deactivate rule
      curl -s -u "$SONAR_USER:$SONAR_PASSWORD" \
        -X POST "$SONAR_URL/api/qualityprofiles/deactivate_rule" \
        -d "key=$PROFILE_KEY" \
        -d "rule=$rule" > /dev/null 2>&1 || true
    done
    
    RULE_COUNT=$(echo "$RULES_TO_DISABLE" | wc -l | tr -d ' ')
    echo "✅ Disabled $RULE_COUNT rules from config"
  fi
fi

echo "✅ Quality profile setup complete"

