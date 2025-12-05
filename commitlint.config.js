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

/**
 * Custom rule to check if string contains only ASCII characters
 */
function isASCII(str) {
  return /^[\x00-\x7F]*$/.test(str);
}

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
    'scope-enum': [
      2,
      'always',
      [
        'jmix',
        'vaadin',
        'entity',
        'view',
        'service',
        'security',
        'liquibase',
        'ui',
        'api',
        'db',
        'config',
        'test',
        'ci',
        'deps',
        'docs',
        'scripts',
        'gradle',
        'copyright',
      ],
    ],
    'scope-case': [2, 'always', 'lower-case'],
    'subject-case': [2, 'always', 'lower-case'],
    'subject-empty': [2, 'never'],
    'subject-full-stop': [2, 'never', '.'],
    'type-case': [2, 'always', 'lower-case'],
    'type-empty': [2, 'never'],
    'header-max-length': [2, 'always', 72],
    'body-leading-blank': [2, 'always'],
    'body-max-line-length': [2, 'always', 72],
    'header-format': [
      2,
      'always',
      (parsed) => {
        const { header } = parsed;
        if (!isASCII(header)) {
          return [
            false,
            'header must contain only ASCII characters (no Unicode, emoji, or special characters)',
          ];
        }
        return [true];
      },
    ],
    'body-format': [
      2,
      'always',
      (parsed) => {
        const { body } = parsed;
        if (body && !isASCII(body)) {
          return [
            false,
            'body must contain only ASCII characters (no Unicode, emoji, or special characters)',
          ];
        }
        return [true];
      },
    ],
  },
};
