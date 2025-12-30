# 02 - DevOps & CI/CD

## Purpose

DevOps learning materials, CI/CD pipeline templates, and automation scripts. Master these skills to automate deployments and enable faster delivery.

---

## Learning Objectives

1. Master CI/CD Pipelines - GitHub Actions, Jenkins, GitLab CI
2. Implement GitOps - ArgoCD, Flux, declarative deployments
3. Automate Infrastructure - Terraform, Ansible
4. Secure Pipelines - Secret management, SAST/DAST
5. Monitor & Alert - Pipeline observability

---

## Directory Structure

```
02-devops-cicd/
├── pipelines/
│   ├── github-actions/
│   │   ├── java-spring-boot.yml
│   │   ├── node-typescript.yml
│   │   └── python-ml-pipeline.yml
│   ├── jenkins/
│   └── gitlab-ci/
├── infrastructure/
│   ├── terraform/
│   │   ├── aws-eks-cluster/
│   │   ├── aws-rds-postgres/
│   │   └── modules/
│   └── ansible/
├── gitops/
│   ├── argocd/
│   └── flux/
└── security/
    ├── secret-management/
    └── sast-dast/
```

---

## GitHub Actions Pipeline

```yaml
name: Spring Boot CI/CD Pipeline

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

env:
  JAVA_VERSION: '21'
  REGISTRY: ghcr.io
  IMAGE_NAME: ${{ github.repository }}

jobs:
  test:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_DB: testdb
          POSTGRES_USER: testuser
          POSTGRES_PASSWORD: testpass
        ports:
          - 5432:5432
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'
          cache: 'maven'
      
      - name: Run tests
        run: ./mvnw verify

  build-and-push:
    runs-on: ubuntu-latest
    needs: test
    if: github.ref == 'refs/heads/main'
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Build and push
        uses: docker/build-push-action@v5
        with:
          context: .
          push: true
          tags: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}:${{ github.sha }}

  deploy:
    runs-on: ubuntu-latest
    needs: build-and-push
    
    steps:
      - name: Deploy to Kubernetes
        run: |
          helm upgrade --install app ./helm \
            --set image.tag=${{ github.sha }} \
            --wait
```

---

## Terraform EKS Module

```hcl
variable "cluster_name" {
  type = string
}

variable "cluster_version" {
  type    = string
  default = "1.28"
}

variable "vpc_id" {
  type = string
}

variable "subnet_ids" {
  type = list(string)
}

resource "aws_eks_cluster" "main" {
  name     = var.cluster_name
  version  = var.cluster_version
  role_arn = aws_iam_role.cluster.arn

  vpc_config {
    subnet_ids              = var.subnet_ids
    endpoint_private_access = true
    endpoint_public_access  = true
  }

  enabled_cluster_log_types = ["api", "audit", "authenticator"]
}

resource "aws_eks_node_group" "main" {
  cluster_name    = aws_eks_cluster.main.name
  node_group_name = "${var.cluster_name}-nodes"
  node_role_arn   = aws_iam_role.node.arn
  subnet_ids      = var.subnet_ids

  scaling_config {
    desired_size = 3
    max_size     = 10
    min_size     = 1
  }
}

output "cluster_endpoint" {
  value = aws_eks_cluster.main.endpoint
}
```

---

## ArgoCD Application

```yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: spring-boot-api
  namespace: argocd
spec:
  project: default
  
  source:
    repoURL: https://github.com/your-org/your-repo.git
    targetRevision: HEAD
    path: k8s/overlays/production
  
  destination:
    server: https://kubernetes.default.svc
    namespace: production
  
  syncPolicy:
    automated:
      prune: true
      selfHeal: true
    syncOptions:
      - CreateNamespace=true
```

---

## Revenue Projects

### CI/CD Pipeline Implementation
**$5,000 - $15,000**
- GitHub Actions or GitLab CI setup
- Docker containerization
- Kubernetes deployment
- Environment management

### Infrastructure Automation Package
**$10,000 - $25,000**
- Terraform modules for AWS
- EKS cluster setup
- RDS, ElastiCache, S3 configuration
- IAM roles and policies

### DevOps Retainer Service
**$3,000 - $8,000/month**
- On-call DevOps support
- Pipeline maintenance
- Infrastructure updates
- Security patching
