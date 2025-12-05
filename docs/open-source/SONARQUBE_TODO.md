# SonarQube Integration TODO

Notes for future SonarQube server integration.

## Goal

Centralized quality rules for IDE (SonarLint Connected Mode) and CI/CD (SonarLint Gradle Plugin).

## Current State

- SonarLint works in standalone mode (no server)
- Rules defined in `sonar-project.properties`
- No centralized server sync

## Future Implementation

- [ ] Set up SonarQube server (local or SonarCloud)
- [ ] Create Quality Profile: "Start Custom"
- [ ] Configure framework-specific rule exclusions
- [ ] Enable SonarLint Connected Mode in IDE
- [ ] Update SonarLint Gradle Plugin to use server connection

## Notes

Add implementation notes here as needed.
