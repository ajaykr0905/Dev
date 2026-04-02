# Contributing to Dev

Thank you for your interest in contributing. This guide covers the setup, workflow, and standards for each module.

## Getting Started

1. Fork this repository
2. Clone your fork: `git clone https://github.com/<your-username>/Dev.git`
3. Create a branch: `git checkout -b fix/description-of-change`
4. Make your changes
5. Push and open a Pull Request

## Module-Specific Setup

### 01 - Spring Boot Application

**Prerequisites:** Java 21+, Maven 3.9+, Docker

```bash
cd 01-advanced-java-spring/templates/spring-boot-starter

# Start database
docker compose up db -d

# Run tests
mvn verify

# Run application
mvn spring-boot:run
```

**Code standards:**
- Follow existing package structure (`controller/`, `service/`, `domain/`, `repository/`, `exception/`)
- Use Reactor types (`Mono<T>`, `Flux<T>`) for all service methods
- Write `StepVerifier` tests for every new service method
- Use Lombok annotations (`@Data`, `@Builder`, `@RequiredArgsConstructor`)

### 02 - CI/CD Pipelines

- Workflow files live in `pipelines/github-actions/`
- Test changes by pushing to a feature branch (the workflow triggers on `push` and `pull_request`)
- Validate YAML syntax before committing

### 03 - Kubernetes / Helm

**Prerequisites:** Helm 3, kubectl

```bash
cd 03-kubernetes-orchestration/helm-charts/spring-boot-app

# Lint the chart
helm lint .

# Dry-run a template render
helm template test . --debug
```

- Every `values.yaml` key that controls a resource must have a corresponding template
- Use `_helpers.tpl` for shared labels and naming

### 04 - Terraform

**Prerequisites:** Terraform 1.5+

```bash
cd 04-aws-cloud-infrastructure/terraform/modules/vpc

terraform init
terraform validate
terraform fmt -check
```

- Run `terraform fmt` before committing
- Run `terraform validate` to catch syntax errors
- Use `count` or `for_each` with proper conditionals to avoid plan failures

## Pull Request Guidelines

- One logical change per PR
- Include a clear description of what changed and why
- Reference the issue number if applicable
- Ensure no secrets, credentials, or `.env` files are committed

## Reporting Issues

Open a GitHub issue with:
- Module affected (01/02/03/04)
- Steps to reproduce
- Expected vs actual behavior
