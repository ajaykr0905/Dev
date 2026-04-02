# Dev - Enterprise Java & Cloud Infrastructure

A modular learning repository and production-ready template collection for building, deploying, and operating Java microservices on Kubernetes and AWS.

## Tech Stack

| Layer | Technologies |
|-------|-------------|
| **Application** | Java 21, Spring Boot 3.2, WebFlux, R2DBC, PostgreSQL, Resilience4j |
| **Containers** | Docker (multi-stage), Docker Compose |
| **CI/CD** | GitHub Actions (build, test, security scan, deploy) |
| **Orchestration** | Kubernetes, Helm 3 |
| **Cloud/IaC** | Terraform (AWS VPC, EKS, RDS) |
| **Observability** | Micrometer, Prometheus, Brave Tracing |

## Repository Structure

```
.
├── 01-advanced-java-spring/       # Reactive Spring Boot microservice
│   └── templates/spring-boot-starter/
│       ├── src/main/java/         # Application code (WebFlux + R2DBC)
│       ├── src/main/resources/    # Config + Flyway migrations
│       ├── src/test/java/         # Unit tests (StepVerifier)
│       ├── Dockerfile             # Multi-stage JRE Alpine build
│       ├── docker-compose.yml     # App + PostgreSQL
│       └── pom.xml                # Maven dependencies
│
├── 02-devops-cicd/                # CI/CD pipeline definitions
│   └── pipelines/github-actions/
│       └── spring-boot-ci.yml     # Build → Test → Scan → Deploy
│
├── 03-kubernetes-orchestration/   # Kubernetes deployment
│   └── helm-charts/spring-boot-app/
│       ├── Chart.yaml
│       ├── values.yaml
│       └── templates/             # Deployment, Service, HPA, Ingress, PDB
│
├── 04-aws-cloud-infrastructure/   # Infrastructure as Code
│   └── terraform/modules/
│       ├── vpc/main.tf            # VPC, subnets, NAT, routing
│       ├── eks/main.tf            # EKS cluster + node groups
│       └── rds/main.tf            # RDS PostgreSQL + Secrets Manager
│
└── CONTRIBUTING.md                # How to contribute
```

## Quick Start

### Prerequisites

- Java 21+
- Maven 3.9+
- Docker & Docker Compose
- Terraform 1.5+ (for infrastructure modules)
- kubectl + Helm 3 (for Kubernetes deployment)

### Run the Spring Boot Starter

```bash
cd 01-advanced-java-spring/templates/spring-boot-starter

# Start PostgreSQL + App
docker compose up -d

# Or run locally (requires PostgreSQL on localhost:5432)
mvn spring-boot:run
```

### Run Tests

```bash
cd 01-advanced-java-spring/templates/spring-boot-starter
mvn verify
```

### Deploy Infrastructure

```bash
cd 04-aws-cloud-infrastructure/terraform/modules/vpc
terraform init
terraform plan -var="name=myapp" -var='azs=["us-east-1a","us-east-1b"]'
```

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/entities` | List all entities |
| `GET` | `/api/entities/active` | List active entities |
| `GET` | `/api/entities/{id}` | Get entity by ID |
| `POST` | `/api/entities` | Create entity |
| `PUT` | `/api/entities/{id}` | Update entity |
| `DELETE` | `/api/entities/{id}` | Delete entity |
| `GET` | `/api/entities/search?q=term` | Search entities |
| `GET` | `/actuator/health` | Health check |
| `GET` | `/actuator/prometheus` | Prometheus metrics |

## License

MIT License - see [LICENSE](LICENSE) for details.
