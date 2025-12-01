.PHONY: help run build clean test check analyze format format-fix ci install setup setup-husky check-setup kill-port

# Colors for output
GREEN  := $(shell tput -Txterm setaf 2)
YELLOW := $(shell tput -Txterm setaf 3)
RESET  := $(shell tput -Txterm sgr0)

# Gradle configuration cache flag
# Use --no-configuration-cache for tasks incompatible with configuration cache
# (e.g., when Jmix plugin captures Project objects)
# Override: make analyze-full NO_CCACHE=1
NO_CCACHE ?= 0
GRADLE_CCACHE_FLAG := $(if $(filter 1,$(NO_CCACHE)),--no-configuration-cache,)

# Default target
.DEFAULT_GOAL := help

##@ Help

help: ## Show this help message
	@echo "$(GREEN)Available commands:$(RESET)"
	@echo ""
	@awk 'BEGIN {FS = ":.*##"; printf "\nUsage:\n  make $(YELLOW)<target>$(RESET)\n"} /^[a-zA-Z_-]+:.*?##/ { printf "  $(YELLOW)%-20s$(RESET) %s\n", $$1, $$2 } /^##@/ { printf "\n$(GREEN)%s$(RESET)\n", substr($$0, 5) } ' $(MAKEFILE_LIST)

##@ Setup

setup: ## Setup project: check requirements, install dependencies, configure git hooks
	@echo "$(GREEN)Setting up project...$(RESET)"
	@echo ""
	@echo "$(YELLOW)Checking and installing requirements...$(RESET)"
	@echo ""
	@# Check Java
	@if ! command -v java >/dev/null 2>&1; then \
		echo "$(YELLOW)⚠️  Java not found. Attempting to install...$(RESET)"; \
		if command -v brew >/dev/null 2>&1; then \
			echo "$(YELLOW)Installing Java 21 via Homebrew...$(RESET)"; \
			brew install openjdk@21 || { \
				echo "$(YELLOW)⚠️  Failed to install Java via Homebrew$(RESET)"; \
				echo "$(YELLOW)   Please install Java 21 manually:$(RESET)"; \
				echo "$(YELLOW)   https://adoptium.net/ or brew install openjdk@21$(RESET)"; \
				exit 1; \
			}; \
			echo "$(GREEN)✅ Java installed via Homebrew$(RESET)"; \
		else \
			echo "$(YELLOW)⚠️  Java not found and no package manager available$(RESET)"; \
			echo "$(YELLOW)   Please install Java 21 manually:$(RESET)"; \
			echo "$(YELLOW)   https://adoptium.net/$(RESET)"; \
			exit 1; \
		fi; \
	fi
	@JAVA_VERSION=$$(java -version 2>&1 | head -n 1 | awk -F'"' '{print $$2}' | awk -F'.' '{if ($$1 == 1) print $$2; else print $$1}'); \
	if [ -z "$$JAVA_VERSION" ] || [ "$$JAVA_VERSION" -lt 21 ] 2>/dev/null; then \
		echo "$(YELLOW)⚠️  Java version check failed or version < 21$(RESET)"; \
		echo "$(YELLOW)   Detected version: $$JAVA_VERSION (Java 21+ required)$(RESET)"; \
		echo "$(YELLOW)   Please install Java 21: https://adoptium.net/$(RESET)"; \
		exit 1; \
	else \
		echo "$(GREEN)✅ Java $$JAVA_VERSION found$(RESET)"; \
	fi
	@# Check Gradle wrapper
	@if [ ! -f "./gradlew" ]; then \
		echo "$(YELLOW)⚠️  gradlew not found$(RESET)"; \
		exit 1; \
	fi
	@chmod +x ./gradlew 2>/dev/null || true
	@echo "$(GREEN)✅ Gradle wrapper found$(RESET)"
	@# Check Node.js (Gradle plugin will install if needed, but verify)
	@if ! command -v node >/dev/null 2>&1; then \
		echo "$(YELLOW)⚠️  Node.js not found in PATH$(RESET)"; \
		echo "$(YELLOW)   Gradle Node plugin will install Node.js 24.11.0 automatically$(RESET)"; \
	else \
		NODE_VERSION=$$(node --version 2>/dev/null | sed 's/v//' | cut -d'.' -f1); \
		echo "$(GREEN)✅ Node.js found (version: $$(node --version 2>/dev/null || echo 'unknown'))$(RESET)"; \
	fi
	@echo ""
	@echo "$(YELLOW)Installing Gradle dependencies (this may take a while on first run)...$(RESET)"
	@./gradlew --version --console=plain >/dev/null 2>&1 || true
	@./gradlew tasks --console=plain >/dev/null 2>&1 || true
	@echo "$(GREEN)✅ Gradle dependencies ready$(RESET)"
	@echo ""
	@echo "$(YELLOW)Installing npm dependencies (Node.js will be installed if needed)...$(RESET)"
	@./gradlew npmInstall --console=plain || { \
		echo "$(YELLOW)⚠️  npm install failed, retrying...$(RESET)"; \
		./gradlew npmInstall --console=plain || { \
			echo "$(YELLOW)⚠️  npm install failed again$(RESET)"; \
			echo "$(YELLOW)   This may be normal if Node.js is being installed$(RESET)"; \
		}; \
	}
	@echo "$(GREEN)✅ npm dependencies installed$(RESET)"
	@echo ""
	@echo "$(YELLOW)Verifying installations...$(RESET)"
	@./gradlew --version --console=plain | head -1 || true
	@if command -v node >/dev/null 2>&1; then \
		echo "$(GREEN)✅ Node.js: $$(node --version 2>/dev/null || echo 'installed by Gradle')$(RESET)"; \
	fi
	@echo ""
	@echo "$(YELLOW)Setting up Git hooks...$(RESET)"
	@$(MAKE) setup-husky 2>/dev/null || echo "$(YELLOW)⚠️  Husky setup skipped (this is OK)$(RESET)"
	@echo ""
	@echo "$(YELLOW)Compiling project (first time, this may take a while)...$(RESET)"
	@./gradlew compileJava --console=plain || { \
		echo "$(YELLOW)⚠️  Compilation failed$(RESET)"; \
		echo "$(YELLOW)   Run 'make build' to see detailed errors$(RESET)"; \
		exit 0; \
	}
	@echo "$(GREEN)✅ Project compiled successfully$(RESET)"
	@echo ""
	@echo "$(GREEN)🎉 Setup completed!$(RESET)"
	@echo ""
	@echo "$(GREEN)Next steps:$(RESET)"
	@echo "  • Run application: $(YELLOW)make run$(RESET)"
	@echo "  • Run tests: $(YELLOW)make test$(RESET)"
	@echo "  • Check code quality: $(YELLOW)make analyze$(RESET)"

##@ Development

check-setup: ## Check if project is set up correctly
	@SETUP_NEEDED=0; \
	if ! command -v java >/dev/null 2>&1; then \
		SETUP_NEEDED=1; \
	elif [ ! -f "./gradlew" ]; then \
		SETUP_NEEDED=1; \
	else \
		JAVA_VERSION=$$(java -version 2>&1 | head -n 1 | awk -F'"' '{print $$2}' | awk -F'.' '{if ($$1 == 1) print $$2; else print $$1}'); \
		if [ -z "$$JAVA_VERSION" ] || [ "$$JAVA_VERSION" -lt 21 ] 2>/dev/null; then \
			SETUP_NEEDED=1; \
		fi; \
	fi; \
	if [ "$$SETUP_NEEDED" -eq 1 ]; then \
		echo "$(YELLOW)⚠️  Project setup incomplete or missing dependencies$(RESET)"; \
		echo "$(YELLOW)   Run 'make setup' to install and configure everything$(RESET)"; \
		exit 1; \
	fi

run: ## Run the application (auto-runs setup if needed)
	@SETUP_NEEDED=0; \
	if ! command -v java >/dev/null 2>&1; then \
		SETUP_NEEDED=1; \
	elif [ ! -f "./gradlew" ]; then \
		SETUP_NEEDED=1; \
	else \
		JAVA_VERSION=$$(java -version 2>&1 | head -n 1 | awk -F'"' '{print $$2}' | awk -F'.' '{if ($$1 == 1) print $$2; else print $$1}'); \
		if [ -z "$$JAVA_VERSION" ] || [ "$$JAVA_VERSION" -lt 21 ] 2>/dev/null; then \
			SETUP_NEEDED=1; \
		fi; \
	fi; \
	if [ "$$SETUP_NEEDED" -eq 1 ]; then \
		echo "$(YELLOW)⚠️  Project setup incomplete. Running setup...$(RESET)"; \
		echo ""; \
		$(MAKE) setup || { \
			echo "$(YELLOW)⚠️  Setup failed. Please run 'make setup' manually$(RESET)"; \
			exit 1; \
		}; \
		echo ""; \
	fi
	@echo "$(GREEN)Starting application...$(RESET)"
	./gradlew bootRun

build: ## Build the project
	@echo "$(GREEN)Building project...$(RESET)"
	./gradlew build

clean: ## Clean build artifacts and kill process on port 8080
	@echo "$(GREEN)Cleaning build artifacts...$(RESET)"
	@$(MAKE) kill-port PORT=8080
	./gradlew clean

install: ## Install npm dependencies
	@echo "$(GREEN)Installing npm dependencies...$(RESET)"
	./gradlew npmInstall

##@ Testing

test: ## Run tests (requires Docker for Testcontainers)
	@echo "$(GREEN)Running tests...$(RESET)"
	@echo "$(YELLOW)Note: Tests require Docker to be running for Testcontainers$(RESET)"
	@if [ ! -f ~/.docker-java.properties ]; then \
		echo "$(YELLOW)⚠️  Warning: ~/.docker-java.properties not found. Creating it with workaround for Docker 29+...$(RESET)"; \
		mkdir -p ~/.docker-java 2>/dev/null || true; \
		echo "api.version=1.44" > ~/.docker-java.properties; \
		echo "$(GREEN)✅ Created ~/.docker-java.properties with api.version=1.44$(RESET)"; \
	fi
	./gradlew test --no-configuration-cache

test-report: test ## Run tests and open coverage report
	@echo "$(GREEN)Opening test coverage report...$(RESET)"
	@if command -v open >/dev/null 2>&1; then \
		open build/reports/tests/test/index.html; \
	elif command -v xdg-open >/dev/null 2>&1; then \
		xdg-open build/reports/tests/test/index.html; \
	else \
		echo "Open manually: build/reports/tests/test/index.html"; \
	fi

coverage: test ## Generate test coverage report
	@echo "$(GREEN)Test coverage report generated at: build/reports/jacoco/test/html/index.html$(RESET)"
	@if command -v open >/dev/null 2>&1; then \
		open build/reports/jacoco/test/html/index.html; \
	elif command -v xdg-open >/dev/null 2>&1; then \
		xdg-open build/reports/jacoco/test/html/index.html; \
	fi

##@ Code Quality - Formatting

format: ## Format code (Java, CSS, YAML)
	@echo "$(GREEN)Formatting code...$(RESET)"
	./gradlew spotlessApply
	./gradlew lintCssFix
	./gradlew lintYamlFix

format-check: ## Check code formatting (without fixing)
	@echo "$(GREEN)Checking code formatting...$(RESET)"
	./gradlew spotlessCheck
	./gradlew lintCss
	./gradlew lintYaml

format-java: ## Format Java code only
	@echo "$(GREEN)Formatting Java code...$(RESET)"
	./gradlew spotlessApply

format-css: ## Format CSS code only
	@echo "$(GREEN)Formatting CSS code...$(RESET)"
	./gradlew lintCssFix

format-yaml: ## Format YAML files only
	@echo "$(GREEN)Formatting YAML files...$(RESET)"
	./gradlew lintYamlFix

##@ Code Quality - Checks

check: format-check ## Run all formatting checks
	@echo "$(GREEN)All formatting checks completed$(RESET)"

lint: check ## Alias for check
	@echo "$(GREEN)Linting completed$(RESET)"

##@ Code Quality - Analysis

analyze: ## Run code quality analysis (Checkstyle, PMD, SpotBugs, SonarLint)
	@echo "$(GREEN)Running code quality analysis...$(RESET)"
	@if [ "$(NO_CCACHE)" = "1" ]; then \
		echo "$(YELLOW)Using --no-configuration-cache flag$(RESET)"; \
	fi
	./gradlew $(GRADLE_CCACHE_FLAG) codeQuality

analyze-full: ## Run all code quality checks (including CSS/YAML linting)
	@echo "$(GREEN)Running full code quality analysis...$(RESET)"
	@if [ "$(NO_CCACHE)" = "1" ]; then \
		echo "$(YELLOW)Using --no-configuration-cache flag$(RESET)"; \
	fi
	./gradlew $(GRADLE_CCACHE_FLAG) codeQualityFull

analyze-reports: analyze ## Open code quality reports
	@echo "$(GREEN)Opening code quality reports...$(RESET)"
	@if command -v open >/dev/null 2>&1; then \
		find build/reports -name "*.html" -type f | head -1 | xargs open; \
	elif command -v xdg-open >/dev/null 2>&1; then \
		find build/reports -name "*.html" -type f | head -1 | xargs xdg-open; \
	else \
		echo "Reports available in: build/reports/"; \
	fi

##@ CI/CD

ci: ## Run full CI pipeline (format check + analysis + tests + build)
	@echo "$(GREEN)Running CI pipeline...$(RESET)"
	@if [ "$(NO_CCACHE)" = "1" ]; then \
		echo "$(YELLOW)Using --no-configuration-cache flag$(RESET)"; \
	fi
	./gradlew $(GRADLE_CCACHE_FLAG) ciBuild

ci-fast: ## Fast CI checks (format + compile, no tests)
	@echo "$(GREEN)Running fast CI checks...$(RESET)"
	./gradlew spotlessCheck compileJava

##@ Git Hooks

setup-husky: ## Setup Husky git hooks
	@echo "$(GREEN)Setting up Husky git hooks...$(RESET)"
	@if [ ! -d ".husky" ]; then \
		echo "$(YELLOW)Warning: .husky directory not found$(RESET)"; \
		exit 1; \
	fi
	@if command -v npx >/dev/null 2>&1; then \
		npx husky install || echo "$(YELLOW)Husky not installed, installing...$(RESET)"; \
		npx husky add .husky/pre-commit .husky/pre-commit 2>/dev/null || true; \
		chmod +x .husky/pre-commit; \
		echo "$(GREEN)Husky hooks installed$(RESET)"; \
	else \
		echo "$(YELLOW)npx not found. Installing Husky manually...$(RESET)"; \
		cp .husky/pre-commit .git/hooks/pre-commit; \
		chmod +x .git/hooks/pre-commit; \
		echo "$(GREEN)Git hook installed manually$(RESET)"; \
	fi

##@ Utilities

kill-port: ## Kill process on specified port (default: 8080). Usage: make kill-port PORT=8080
	@PORT=$(or $(PORT),8080); \
	if lsof -ti:$$PORT >/dev/null 2>&1; then \
		echo "$(YELLOW)Killing process on port $$PORT...$(RESET)"; \
		lsof -ti:$$PORT | xargs kill -9 2>/dev/null || true; \
		echo "$(GREEN)Process on port $$PORT terminated$(RESET)"; \
	else \
		echo "$(GREEN)No process found on port $$PORT$(RESET)"; \
	fi

dependencies: ## Show dependency versions
	@echo "$(GREEN)Showing dependency versions...$(RESET)"
	./gradlew --no-configuration-cache managedVersions

update-deps: ## Update dependency lock files
	@echo "$(GREEN)Updating dependency lock files...$(RESET)"
	./gradlew --no-configuration-cache updateDependencies

info: ## Show build information
	@echo "$(GREEN)Build information:$(RESET)"
	./gradlew buildInfo

compile: ## Compile Java code only
	@echo "$(GREEN)Compiling Java code...$(RESET)"
	./gradlew compileJava

compile-test: ## Compile test code
	@echo "$(GREEN)Compiling test code...$(RESET)"
	./gradlew compileTestJava

##@ DevOps

docker-up: ## Start Docker Compose (app + PostgreSQL)
	@echo "$(GREEN)Starting Docker Compose...$(RESET)"
	docker-compose up -d

docker-down: ## Stop Docker Compose
	@echo "$(GREEN)Stopping Docker Compose...$(RESET)"
	docker-compose down

docker-logs: ## View application logs
	@echo "$(GREEN)Viewing application logs...$(RESET)"
	docker-compose logs -f app

docker-ps: ## List running containers
	@echo "$(GREEN)Running containers:$(RESET)"
	docker-compose ps

observability-up: ## Start observability stack (Grafana + Loki + OpenTelemetry)
	@echo "$(GREEN)Starting observability stack...$(RESET)"
	docker-compose -f docker-compose.observability.yml up -d

observability-down: ## Stop observability stack
	@echo "$(GREEN)Stopping observability stack...$(RESET)"
	docker-compose -f docker-compose.observability.yml down

check-devops: ## Check DevOps infrastructure status
	@echo "$(GREEN)Checking DevOps infrastructure...$(RESET)"
	@./scripts/check-devops.sh

health: ## Check application health
	@echo "$(GREEN)Checking application health...$(RESET)"
	@curl -sf http://localhost:8080/actuator/health | jq . || echo "$(YELLOW)Application not responding$(RESET)"

test-observability: ## Test observability stack (Grafana + Loki + OpenTelemetry)
	@echo "$(GREEN)Testing observability stack...$(RESET)"
	@./scripts/test-observability.sh

