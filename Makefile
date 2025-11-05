.DEFAULT_GOAL := help
.SILENT = true
SHELL = bash

.PHONY: help
help: ## show help
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

.PHONY: format
format: ## format the code as per the spotless configuration
	@mvn spotless:apply

.PHONY: dockup
dockup: ## docker up shortcut
	docker compose up -d

.PHONY: dockclean
dockclean: ## docker down + clean volumes shortcut
	docker compose down -v

.PHONY: test
test: ## run test
	@mvn test

.PHONY: health
health: ## check application health endpoint
	@echo "Checking health..."
	@curl -s http://localhost:$(PORT)/actuator/health | jq '.' || echo "Health endpoint not available"

