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
	@echo "  • Setup .env file: $(YELLOW)make setup-env$(RESET)"
	@echo "  • Start PostgreSQL: $(YELLOW)make postgres-up$(RESET) (or use Docker Compose: $(YELLOW)make docker-up$(RESET))"
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

run: ## Run the application (auto-starts PostgreSQL if needed)
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
	@# Check if .env file exists and create if needed
	@if [ ! -f ".env" ]; then \
		echo "$(YELLOW)⚠️  .env file not found. Creating from .env.example...$(RESET)"; \
		if [ -f ".env.example" ]; then \
			cp .env.example .env; \
			echo "$(GREEN)✅ Created .env file$(RESET)"; \
		else \
			echo "$(YELLOW)⚠️  .env.example not found. Creating basic .env file...$(RESET)"; \
			echo "# PostgreSQL database configuration" > .env; \
			echo "MAIN_DATASOURCE_URL=jdbc:postgresql://localhost:5432/start" >> .env; \
			echo "MAIN_DATASOURCE_USERNAME=start" >> .env; \
			echo "MAIN_DATASOURCE_PASSWORD=start" >> .env; \
			echo "$(GREEN)✅ Created basic .env file$(RESET)"; \
		fi; \
		echo ""; \
	fi
	@# Check PostgreSQL and start if needed
	@PGHOST=$$(grep MAIN_DATASOURCE_URL .env 2>/dev/null | cut -d'/' -f3 | cut -d':' -f1 | head -1 || echo "localhost"); \
	PGPORT=$$(grep MAIN_DATASOURCE_URL .env 2>/dev/null | cut -d':' -f3 | cut -d'/' -f1 | head -1 || echo "5432"); \
	POSTGRES_RUNNING=0; \
	if command -v pg_isready >/dev/null 2>&1; then \
		if pg_isready -h "$$PGHOST" -p "$$PGPORT" >/dev/null 2>&1; then \
			POSTGRES_RUNNING=1; \
		fi; \
	elif docker ps --format '{{.Names}}' 2>/dev/null | grep -qE "postgres|start-postgres"; then \
		POSTGRES_RUNNING=1; \
	elif lsof -ti:$$PGPORT >/dev/null 2>&1; then \
		POSTGRES_RUNNING=1; \
	fi; \
	if [ "$$POSTGRES_RUNNING" -eq 0 ]; then \
		echo "$(YELLOW)⚠️  PostgreSQL not running at $$PGHOST:$$PGPORT$(RESET)"; \
		echo "$(YELLOW)   Starting PostgreSQL automatically...$(RESET)"; \
		echo ""; \
		$(MAKE) postgres-up >/dev/null 2>&1 || { \
			echo "$(YELLOW)⚠️  Failed to start PostgreSQL automatically$(RESET)"; \
			echo "$(YELLOW)   Please start PostgreSQL manually: $(YELLOW)make postgres-up$(RESET)"; \
			echo ""; \
		}; \
		echo "$(YELLOW)   Waiting for PostgreSQL to be ready...$(RESET)"; \
		sleep 5; \
		if command -v pg_isready >/dev/null 2>&1; then \
			for i in 1 2 3 4 5; do \
				if pg_isready -h "$$PGHOST" -p "$$PGPORT" >/dev/null 2>&1; then \
					echo "$(GREEN)✅ PostgreSQL is ready$(RESET)"; \
					break; \
				fi; \
				if [ $$i -eq 5 ]; then \
					echo "$(YELLOW)⚠️  PostgreSQL may still be starting. Continuing anyway...$(RESET)"; \
				else \
					sleep 2; \
				fi; \
			done; \
		else \
			echo "$(YELLOW)⚠️  pg_isready not found, skipping readiness check$(RESET)"; \
		fi; \
		echo ""; \
	else \
		echo "$(GREEN)✅ PostgreSQL is running$(RESET)"; \
	fi
	@echo "$(GREEN)Starting application...$(RESET)"
	./gradlew bootRun

build: ## Build the project
	@echo "$(GREEN)Building project...$(RESET)"
	./gradlew build

clean: ## Clean build artifacts, logs, and kill process on port 8080
	@clear
	@echo "$(GREEN)Cleaning build artifacts and logs...$(RESET)"
	@$(MAKE) kill-port PORT=8080
	@# Clean logs directory
	@if [ -d "logs" ]; then \
		echo "$(YELLOW)Cleaning logs...$(RESET)"; \
		find logs -type f -name "*.log" -delete 2>/dev/null || true; \
		echo "$(GREEN)✅ Logs cleaned$(RESET)"; \
	fi
	./gradlew clean

install: ## Install npm dependencies
	@echo "$(GREEN)Installing npm dependencies...$(RESET)"
	./gradlew npmInstall

##@ Testing

mutation: ## Run mutation testing (PIT)
	@echo "$(GREEN)Running mutation testing (PIT)...$(RESET)"
	./gradlew pitest --no-daemon

test: ## Run tests (requires Docker for Testcontainers PostgreSQL)
	@echo "$(GREEN)Running tests...$(RESET)"
	@echo "$(YELLOW)Note: Tests require Docker to be running for Testcontainers$(RESET)"
	@if ! docker info >/dev/null 2>&1; then \
		echo "$(YELLOW)⚠️  Docker is not running$(RESET)"; \
		echo "$(YELLOW)   Please start Docker and try again$(RESET)"; \
		exit 1; \
	fi
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

format: ## Format code (Java, CSS, YAML) - uses Palantir Baseline format task (includes copyright headers)
	@echo "$(GREEN)Formatting code...$(RESET)"
	./gradlew format
	./gradlew lintCssFix
	./gradlew lintYamlFix

format-check: ## Check code formatting (without fixing)
	@echo "$(GREEN)Checking code formatting...$(RESET)"
	./gradlew spotlessCheck
	./gradlew lintCss
	./gradlew lintYaml

format-java: ## Format Java code only - uses Palantir Baseline format task (includes copyright headers)
	@echo "$(GREEN)Formatting Java code...$(RESET)"
	./gradlew format

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

##@ Environment Setup

setup-env: ## Create .env file from .env.example
	@echo "$(GREEN)Setting up .env file...$(RESET)"
	@if [ -f ".env" ]; then \
		echo "$(YELLOW)⚠️  .env file already exists$(RESET)"; \
		echo "$(YELLOW)   Skipping creation. To recreate, delete .env and run this command again$(RESET)"; \
	else \
		if [ -f ".env.example" ]; then \
			cp .env.example .env; \
			echo "$(GREEN)✅ Created .env file from .env.example$(RESET)"; \
			echo "$(YELLOW)⚠️  Please update .env with your database credentials$(RESET)"; \
		else \
			echo "$(YELLOW)⚠️  .env.example not found$(RESET)"; \
			echo "$(YELLOW)   Creating basic .env file...$(RESET)"; \
			echo "# PostgreSQL database configuration" > .env; \
			echo "MAIN_DATASOURCE_URL=jdbc:postgresql://localhost:5432/start" >> .env; \
			echo "MAIN_DATASOURCE_USERNAME=start" >> .env; \
			echo "MAIN_DATASOURCE_PASSWORD=start" >> .env; \
			echo "$(GREEN)✅ Created basic .env file$(RESET)"; \
		fi; \
	fi

postgres-up: ## Start PostgreSQL in Docker (for local development)
	@echo "$(GREEN)Starting PostgreSQL container...$(RESET)"
	@if [ ! -f ".env" ]; then \
		echo "$(YELLOW)⚠️  .env file not found. Creating from .env.example...$(RESET)"; \
		$(MAKE) setup-env; \
	fi
	@docker-compose -f docker-compose.dev.yml up -d postgres || { \
		echo "$(YELLOW)⚠️  Failed to start PostgreSQL. Trying with docker-compose.yml...$(RESET)"; \
		docker-compose up -d postgres || { \
			echo "$(YELLOW)⚠️  PostgreSQL container not found in docker-compose files$(RESET)"; \
			echo "$(YELLOW)   Starting standalone PostgreSQL container...$(RESET)"; \
			docker run -d --name start-postgres \
				-e POSTGRES_DB=start \
				-e POSTGRES_USER=start \
				-e POSTGRES_PASSWORD=start \
				-p 5432:5432 \
				postgres:16-alpine || echo "$(YELLOW)⚠️  Container may already exist$(RESET)"; \
		}; \
	}
	@echo "$(GREEN)✅ PostgreSQL should be running on localhost:5432$(RESET)"
	@echo "$(YELLOW)   Waiting for PostgreSQL to be ready...$(RESET)"
	@sleep 3
	@if command -v pg_isready >/dev/null 2>&1; then \
		pg_isready -h localhost -p 5432 && echo "$(GREEN)✅ PostgreSQL is ready$(RESET)" || echo "$(YELLOW)⚠️  PostgreSQL may still be starting$(RESET)"; \
	else \
		echo "$(YELLOW)⚠️  pg_isready not found, skipping readiness check$(RESET)"; \
	fi

postgres-down: ## Stop PostgreSQL container
	@echo "$(GREEN)Stopping PostgreSQL container...$(RESET)"
	@docker-compose -f docker-compose.dev.yml down postgres 2>/dev/null || \
	docker-compose down postgres 2>/dev/null || \
	docker stop start-postgres 2>/dev/null || \
	echo "$(YELLOW)⚠️  No PostgreSQL container found$(RESET)"
	@echo "$(GREEN)✅ PostgreSQL stopped$(RESET)"

postgres-logs: ## View PostgreSQL logs
	@echo "$(GREEN)Viewing PostgreSQL logs...$(RESET)"
	@docker-compose -f docker-compose.dev.yml logs -f postgres 2>/dev/null || \
	docker-compose logs -f postgres 2>/dev/null || \
	docker logs -f start-postgres 2>/dev/null || \
	echo "$(YELLOW)⚠️  No PostgreSQL container found$(RESET)"

##@ DevOps

docker-up: ## Start Docker Compose (app + PostgreSQL)
	@echo "$(GREEN)Starting Docker Compose...$(RESET)"
	@if [ ! -f ".env" ]; then \
		echo "$(YELLOW)⚠️  .env file not found. Creating from .env.example...$(RESET)"; \
		$(MAKE) setup-env; \
	fi
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

health: ## Check application health
	@echo "$(GREEN)Checking application health...$(RESET)"
	@curl -sf http://localhost:8080/actuator/health | jq . || echo "$(YELLOW)Application not responding$(RESET)"

##@ CodeMachine

codemachine-install: ## Install CodeMachine CLI globally
	@echo "$(GREEN)Installing CodeMachine CLI...$(RESET)"
	@if command -v npm >/dev/null 2>&1; then \
		npm install -g codemachine; \
		echo "$(GREEN)✅ CodeMachine CLI installed$(RESET)"; \
		echo "$(YELLOW)Note: You need to configure an AI CLI engine (Claude Code, Cursor CLI, etc.)$(RESET)"; \
	elif command -v bun >/dev/null 2>&1; then \
		bun install -g codemachine; \
		echo "$(GREEN)✅ CodeMachine CLI installed$(RESET)"; \
		echo "$(YELLOW)Note: You need to configure an AI CLI engine (Claude Code, Cursor CLI, etc.)$(RESET)"; \
	else \
		echo "$(YELLOW)⚠️  npm or bun not found$(RESET)"; \
		echo "$(YELLOW)   Please install Node.js first: https://nodejs.org/$(RESET)"; \
		exit 1; \
	fi

codemachine-check: ## Check if CodeMachine CLI is installed
	@if command -v codemachine >/dev/null 2>&1 || command -v cm >/dev/null 2>&1; then \
		echo "$(GREEN)✅ CodeMachine CLI is installed$(RESET)"; \
		codemachine --version 2>/dev/null || cm --version 2>/dev/null || echo "$(YELLOW)Version check failed$(RESET)"; \
	else \
		echo "$(YELLOW)⚠️  CodeMachine CLI not found$(RESET)"; \
		echo "$(YELLOW)   Install with: $(YELLOW)make codemachine-install$(RESET)"; \
		exit 1; \
	fi

codemachine-init: codemachine-check ## Initialize CodeMachine workspace
	@echo "$(GREEN)Initializing CodeMachine workspace...$(RESET)"
	@if [ -d ".codemachine" ]; then \
		echo "$(YELLOW)⚠️  .codemachine directory already exists$(RESET)"; \
		echo "$(YELLOW)   CodeMachine is already initialized$(RESET)"; \
	else \
		codemachine init || cm init || { \
			echo "$(YELLOW)⚠️  CodeMachine initialization failed$(RESET)"; \
			echo "$(YELLOW)   Make sure CodeMachine CLI is installed: $(YELLOW)make codemachine-install$(RESET)"; \
			exit 1; \
		}; \
		echo "$(GREEN)✅ CodeMachine workspace initialized$(RESET)"; \
		echo "$(YELLOW)Next steps:$(RESET)"; \
		echo "  1. Add your specifications to .codemachine/inputs/specifications.md"; \
		echo "  2. Configure an AI CLI engine (Claude Code, Cursor CLI, etc.)"; \
		echo "  3. Run: $(YELLOW)codemachine /start$(RESET) or $(YELLOW)cm /start$(RESET)"; \
	fi

codemachine-start: codemachine-check ## Start CodeMachine workflow
	@echo "$(GREEN)Starting CodeMachine workflow...$(RESET)"
	@if [ ! -d ".codemachine" ]; then \
		echo "$(YELLOW)⚠️  CodeMachine not initialized$(RESET)"; \
		echo "$(YELLOW)   Run: $(YELLOW)make codemachine-init$(RESET)"; \
		exit 1; \
	fi
	@codemachine /start || cm /start || { \
		echo "$(YELLOW)⚠️  CodeMachine workflow failed$(RESET)"; \
		echo "$(YELLOW)   Check .codemachine/ directory for logs$(RESET)"; \
		exit 1; \
	}

cursor-cli-install: ## Install Cursor CLI
	@echo "$(GREEN)Installing Cursor CLI...$(RESET)"
	@curl https://cursor.com/install -fsS | bash || { \
		echo "$(YELLOW)⚠️  Cursor CLI installation failed$(RESET)"; \
		echo "$(YELLOW)   Please install manually: https://docs.cursor.com/cli/installation$(RESET)"; \
		exit 1; \
	}
	@echo "$(GREEN)✅ Cursor CLI installed$(RESET)"
	@echo "$(YELLOW)Note: Make sure ~/.local/bin is in your PATH$(RESET)"
	@echo "$(YELLOW)   Add to ~/.zshrc or ~/.bashrc:$(RESET)"
	@echo "$(YELLOW)   export PATH=\"\$$HOME/.local/bin:\$$PATH\"$(RESET)"
	@if ! echo "$$PATH" | grep -q "$(HOME)/.local/bin"; then \
		echo "$(YELLOW)⚠️  ~/.local/bin not in PATH. Adding temporarily...$(RESET)"; \
		export PATH="$$HOME/.local/bin:$$PATH"; \
	fi
	@if command -v cursor-agent >/dev/null 2>&1; then \
		echo "$(GREEN)✅ Cursor CLI is available$(RESET)"; \
		cursor-agent --version 2>/dev/null || echo "$(YELLOW)Version check failed$(RESET)"; \
	else \
		echo "$(YELLOW)⚠️  Cursor CLI not found in PATH$(RESET)"; \
		echo "$(YELLOW)   Please add ~/.local/bin to PATH and reload shell$(RESET)"; \
	fi

cursor-cli-check: ## Check if Cursor CLI is installed
	@if command -v cursor-agent >/dev/null 2>&1; then \
		echo "$(GREEN)✅ Cursor CLI is installed$(RESET)"; \
		cursor-agent --version 2>/dev/null || echo "$(YELLOW)Version check failed$(RESET)"; \
	else \
		echo "$(YELLOW)⚠️  Cursor CLI not found$(RESET)"; \
		echo "$(YELLOW)   Install with: $(YELLOW)make cursor-cli-install$(RESET)"; \
		echo "$(YELLOW)   Or check PATH: echo \$$PATH | grep -q ~/.local/bin$(RESET)"; \
		exit 1; \
	fi

