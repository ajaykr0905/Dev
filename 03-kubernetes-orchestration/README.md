# 03 - Kubernetes & Container Orchestration

## Purpose

Kubernetes learning resources, cluster configurations, Helm charts, and production patterns. Master these skills for enterprise container orchestration and CKA certification.

---

## Learning Objectives

1. Master Kubernetes Core - Pods, Services, Deployments, StatefulSets
2. Implement Production Patterns - Ingress, auto-scaling, resource management
3. Deploy Service Mesh - Istio, traffic management, observability
4. Secure Clusters - RBAC, Network Policies, Pod Security
5. Pass CKA Certification

---

## Directory Structure

```
03-kubernetes-orchestration/
├── clusters/
│   ├── local-dev/
│   ├── staging/
│   └── production/
├── helm-charts/
│   ├── spring-boot-app/
│   ├── full-stack-app/
│   └── monitoring-stack/
├── manifests/
│   ├── core/
│   ├── networking/
│   └── security/
├── service-mesh/
│   ├── istio/
│   └── linkerd/
└── cka-prep/
    └── practice-scenarios/
```

---

## Production Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: spring-boot-api
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  selector:
    matchLabels:
      app: api
  template:
    metadata:
      labels:
        app: api
      annotations:
        prometheus.io/scrape: "true"
        prometheus.io/port: "8080"
    spec:
      containers:
        - name: api
          image: ghcr.io/your-org/api:v1.2.3
          ports:
            - containerPort: 8080
          resources:
            requests:
              cpu: "250m"
              memory: "512Mi"
            limits:
              cpu: "1000m"
              memory: "1Gi"
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 30
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 10
```

---

## HPA Configuration

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: api-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: spring-boot-api
  minReplicas: 3
  maxReplicas: 20
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
    - type: Resource
      resource:
        name: memory
        target:
          type: Utilization
          averageUtilization: 80
  behavior:
    scaleDown:
      stabilizationWindowSeconds: 300
    scaleUp:
      stabilizationWindowSeconds: 0
```

---

## Istio Traffic Management

```yaml
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: api-vs
spec:
  hosts:
    - api.example.com
  http:
    - route:
        - destination:
            host: api-service
            subset: stable
          weight: 90
        - destination:
            host: api-service
            subset: canary
          weight: 10
      retries:
        attempts: 3
        perTryTimeout: 5s
```

---

## Helm Chart Values

```yaml
replicaCount: 3

image:
  repository: ghcr.io/your-org/api
  tag: latest

service:
  type: ClusterIP
  port: 80

ingress:
  enabled: true
  hosts:
    - host: api.example.com
      paths:
        - path: /
          pathType: Prefix

resources:
  requests:
    cpu: 250m
    memory: 512Mi
  limits:
    cpu: 1000m
    memory: 1Gi

autoscaling:
  enabled: true
  minReplicas: 3
  maxReplicas: 20
  targetCPUUtilizationPercentage: 70
```

---

## CKA Exam Focus

| Domain | Weight |
|--------|--------|
| Cluster Architecture | 25% |
| Workloads | 15% |
| Services & Networking | 20% |
| Storage | 10% |
| Troubleshooting | 30% |

---

## Revenue Projects

### Kubernetes Migration Service
**$15,000 - $40,000**
- Assess current infrastructure
- Design K8s architecture
- Containerize applications
- Deploy to EKS/GKE/AKS

### Kubernetes Managed Service
**$5,000 - $15,000/month**
- Cluster management
- Security patching
- Cost optimization
- Incident response

### Service Mesh Implementation
**$20,000 - $50,000**
- Istio/Linkerd deployment
- mTLS implementation
- Traffic management
- Observability dashboards
