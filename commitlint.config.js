/**
 * (c) Copyright 2025 Digital Technologies and Platforms LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

export default {
  extends: ['@commitlint/config-conventional'],
  rules: {
    'type-enum': [
      2,
      'always',
      [
        'feat',
        'fix',
        'docs',
        'style',
        'refactor',
        'perf',
        'test',
        'build',
        'ci',
        'chore',
        'revert',
      ],
    ],
    // Scope enum: warning only (structure is more important than specific scope)
    // Allows any scope as long as structure type(scope): description is correct
    'scope-enum': [
      1,
      'always',
      [
        // Framework & UI
        'jmix',
        'vaadin',
        'ui',
        'frontend',
        // Application layers
        'entity',
        'view',
        'service',
        'api',
        // Infrastructure
        'config',
        'db',
        'docker',
        'kubernetes',
        'k8s',
        'helm',
        'observability',
        // Security
        'security',
        'auth',
        // Database
        'liquibase',
        'migration',
        // Quality & Tools
        'test',
        'checkstyle',
        'pmd',
        'spotbugs',
        'sonar',
        'quality',
        'format',
        'lint',
        // CI/CD & DevOps
        'ci',
        'github',
        'workflow',
        // Dependencies & Build
        'deps',
        'gradle',
        'npm',
        'node',
        // Documentation & Scripts
        'docs',
        'scripts',
        'makefile',
        'husky',
        'commitlint',
        // Other
        'copyright',
        'rules',
        'cleanup',
        'logging',
        'metrics',
        'monitoring',
      ],
    ],
    'scope-case': [2, 'always', 'lower-case'],
    // Subject case: warning only (structure is more important than case)
    // Allows technical terms like AvoidFullyQualifiedNames in subject
    'subject-case': [1, 'always', 'lower-case'],
    'subject-empty': [2, 'never'],
    'subject-full-stop': [2, 'never', '.'],
    'type-case': [2, 'always', 'lower-case'],
    'type-empty': [2, 'never'],
    // Header length: warning only (structure is more important than length)
    'header-max-length': [1, 'always', 100],
    'body-leading-blank': [2, 'always'],
    // Body line length: warning only (structure is more important than length)
    'body-max-line-length': [1, 'always', 100],
    // ASCII-only validation is handled by .husky/commit-msg hook (byte-level check)
    // commitlint v19.8.1 doesn't support custom format rules, so we rely on the hook
  },
};
