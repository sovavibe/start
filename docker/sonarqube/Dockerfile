# Custom SonarQube image with automatic Quality Profile initialization
FROM sonarqube:25.11.0.114957-community

# Ensure curl is available (should be in base image, but verify)
USER root
RUN which curl > /dev/null || (apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*)
USER sonarqube

# Copy init script with executable permissions
COPY --chmod=755 docker/sonarqube/init-quality-profile.sh /opt/sonarqube/extensions/init-quality-profile.sh

# Create custom entrypoint that runs init script after SonarQube starts
COPY --chmod=755 docker/sonarqube/docker-entrypoint.sh /opt/sonarqube/docker-entrypoint-custom.sh

# Use custom entrypoint
ENTRYPOINT ["/opt/sonarqube/docker-entrypoint-custom.sh"]

