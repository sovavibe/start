#!/bin/bash
# Custom entrypoint for SonarQube that runs Quality Profile initialization
# Professional approach: wraps official SonarQube entrypoint and adds initialization
# Based on industry best practices for Docker entrypoint scripts

set -e

# Function to run initialization in background
run_init() {
  # Wait for SonarQube API to be ready
  echo "⏳ Waiting for SonarQube API to be ready..."
  local max_attempts=120
  local attempt=0
  
  while [ $attempt -lt $max_attempts ]; do
    if curl -s -f http://localhost:9000/api/system/status > /dev/null 2>&1; then
      echo "✅ SonarQube API is ready"
      break
    fi
    attempt=$((attempt + 1))
    if [ $attempt -eq $max_attempts ]; then
      echo "⚠️  SonarQube API not ready after $max_attempts attempts, skipping initialization"
      return 1
    fi
    sleep 2
  done
  
  # Wait a bit more for authentication to be ready
  echo "⏳ Waiting for authentication to be available..."
  sleep 10
  
  # Run Quality Profile initialization
  if [ -f /opt/sonarqube/extensions/init-quality-profile.sh ]; then
    echo "🔄 Running Quality Profile initialization..."
    /opt/sonarqube/extensions/init-quality-profile.sh || {
      echo "⚠️  Quality Profile initialization failed, but continuing..."
      return 1
    }
    echo "✅ Quality Profile initialization complete"
  else
    echo "⚠️  Init script not found, skipping initialization"
  fi
}

# Start initialization in background (non-blocking)
run_init &

# Start SonarQube using official entrypoint (foreground, blocks)
# This is the standard way - let official entrypoint handle the process
# Using exec ensures proper signal handling and process management
exec /opt/sonarqube/docker/entrypoint.sh
