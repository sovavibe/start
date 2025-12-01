# Проверка GitHub Actions

## Быстрая проверка через GitHub CLI

### 1. Установка GitHub CLI

```bash
# macOS
brew install gh

# Linux
# См. https://cli.github.com/manual/installation
```

### 2. Авторизация

```bash
gh auth login
```

### 3. Основные команды

```bash
# Список всех workflow runs
gh run list

# Просмотр последнего run
gh run view

# Просмотр в браузере
gh run view --web

# Просмотр конкретного workflow
gh workflow list
gh workflow view ci.yml

# Просмотр логов в реальном времени
gh run watch

# Перезапуск failed run
gh run rerun <run-id>

# Просмотр статуса конкретного run
gh run view <run-id>
```

### 4. Фильтрация

```bash
# Только failed runs
gh run list --status failure

# Только конкретный workflow
gh run list --workflow=ci.yml

# Последние 10 runs
gh run list --limit 10
```

### 5. Проверка через веб-интерфейс

```bash
# Открыть Actions в браузере
gh browse --repo . -- /actions
```

## Проверка локально перед push

```bash
# Проверка форматирования
make format-check

# Проверка качества кода
make analyze-full

# Запуск тестов
make test

# Полная проверка CI
make ci
```

## Полезные алиасы (добавить в ~/.zshrc или ~/.bashrc)

```bash
alias gh-runs='gh run list'
alias gh-run-view='gh run view --web'
alias gh-run-watch='gh run watch'
alias gh-workflows='gh workflow list'
```

