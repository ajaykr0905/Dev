# 04 - AWS Cloud Infrastructure

## Purpose

AWS learning resources, Terraform modules, and production-ready cloud architectures. Master these skills for enterprise cloud infrastructure and AWS certifications.

---

## Learning Objectives

1. Master Core AWS Services - EC2, EKS, RDS, S3, Lambda
2. Implement IaC - Terraform, AWS CDK
3. Design Scalable Architectures - Multi-AZ, auto-scaling, DR
4. Secure AWS Environments - IAM, VPC, KMS
5. Optimize Costs - Reserved instances, Spot, right-sizing

---

## Directory Structure

```
04-aws-cloud-infrastructure/
├── terraform/
│   ├── modules/
│   │   ├── vpc/
│   │   ├── eks/
│   │   ├── rds/
│   │   └── s3/
│   └── environments/
│       ├── dev/
│       ├── staging/
│       └── production/
├── architectures/
│   ├── microservices/
│   ├── serverless/
│   └── data-pipeline/
└── cost-optimization/
    └── scripts/
```

---

## VPC Module

```hcl
variable "name" {
  type = string
}

variable "cidr" {
  type    = string
  default = "10.0.0.0/16"
}

variable "azs" {
  type = list(string)
}

resource "aws_vpc" "main" {
  cidr_block           = var.cidr
  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = {
    Name = "${var.name}-vpc"
  }
}

resource "aws_subnet" "public" {
  count                   = length(var.azs)
  vpc_id                  = aws_vpc.main.id
  cidr_block              = cidrsubnet(var.cidr, 8, count.index)
  availability_zone       = var.azs[count.index]
  map_public_ip_on_launch = true

  tags = {
    Name = "${var.name}-public-${var.azs[count.index]}"
  }
}

resource "aws_subnet" "private" {
  count             = length(var.azs)
  vpc_id            = aws_vpc.main.id
  cidr_block        = cidrsubnet(var.cidr, 8, count.index + 100)
  availability_zone = var.azs[count.index]

  tags = {
    Name = "${var.name}-private-${var.azs[count.index]}"
  }
}

output "vpc_id" {
  value = aws_vpc.main.id
}

output "public_subnet_ids" {
  value = aws_subnet.public[*].id
}

output "private_subnet_ids" {
  value = aws_subnet.private[*].id
}
```

---

## Production Environment

```hcl
terraform {
  backend "s3" {
    bucket         = "company-terraform-state"
    key            = "production/terraform.tfstate"
    region         = "us-east-1"
    encrypt        = true
    dynamodb_table = "terraform-lock"
  }
}

module "vpc" {
  source = "../../modules/vpc"
  name   = "prod"
  cidr   = "10.0.0.0/16"
  azs    = ["us-east-1a", "us-east-1b", "us-east-1c"]
}

module "eks" {
  source       = "../../modules/eks"
  cluster_name = "prod-cluster"
  vpc_id       = module.vpc.vpc_id
  subnet_ids   = module.vpc.private_subnet_ids
}

module "rds" {
  source         = "../../modules/rds"
  identifier     = "prod-db"
  engine         = "postgres"
  instance_class = "db.t3.medium"
  vpc_id         = module.vpc.vpc_id
  subnet_ids     = module.vpc.private_subnet_ids
}
```

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                         AWS Cloud                            │
├─────────────────────────────────────────────────────────────┤
│  Route 53 ──► CloudFront ──► Application LB                 │
│                                    │                         │
│  ┌─────────────────────────────────┼───────────────────┐    │
│  │                    EKS Cluster  │                   │    │
│  │  ┌─────────┐  ┌─────────┐  ┌─────────┐             │    │
│  │  │ API GW  │  │ Users   │  │ Orders  │             │    │
│  │  │ Service │  │ Service │  │ Service │             │    │
│  │  └────┬────┘  └────┬────┘  └────┬────┘             │    │
│  └───────┼────────────┼────────────┼──────────────────┘    │
│          │            │            │                        │
│  ┌───────▼───┐  ┌─────▼─────┐  ┌──▼───────┐                │
│  │ RDS       │  │ ElastiCache│  │ S3       │                │
│  │ (Aurora)  │  │ (Redis)    │  │          │                │
│  └───────────┘  └───────────┘  └──────────┘                │
└─────────────────────────────────────────────────────────────┘
```

---

## Cost Optimization

| Strategy | Savings |
|----------|---------|
| Spot Instances | 60-90% |
| Reserved Instances | 30-60% |
| Right-sizing | 20-40% |
| S3 Intelligent-Tiering | 20-30% |

---

## Certifications Path

| Certification | Salary Impact |
|---------------|---------------|
| Solutions Architect Associate | +$15-25k |
| Developer Associate | +$10-15k |
| DevOps Engineer Professional | +$20-30k |

---

## Revenue Projects

### AWS Infrastructure Setup
**$10,000 - $30,000**
- VPC design and implementation
- EKS cluster setup
- RDS/Aurora configuration
- Security baseline

### AWS Cost Optimization Audit
**$5,000 - $15,000**
- Current spend analysis
- Reserved Instance recommendations
- Right-sizing recommendations
- Implementation support

### AWS Migration Service
**$25,000 - $75,000**
- Migration assessment
- Architecture design
- Phased migration
- Cutover support
