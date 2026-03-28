# 🚀 End-to-End CI/CD Pipeline for Product-Service (Spring Boot + Docker + Kubernetes)

## 📌 Overview

This document describes the complete implementation of a **CI/CD pipeline** for a Spring Boot microservice (`product-service`) using:

* GitHub (Source Code Management)
* Jenkins (CI/CD Automation)
* Maven (Build Tool)
* Docker (Containerization)
* DockerHub (Image Registry)
* Kubernetes (Deployment Platform)

---

# 🧱 Architecture Flow

```
Developer → GitHub → Jenkins → Maven Build → Docker Build → DockerHub → Kubernetes Cluster
```

---

# ⚙️ Step 1: Application Setup

* Developed a **Spring Boot microservice** (`product-service`)
* Ensured proper package structure:

  ```
  com.vnukala.productservice
  ```
* Fixed test issues:

  * Resolved package mismatch
  * Ensured `@SpringBootTest` works correctly

---

# 📦 Step 2: Version Control (GitHub)

### Initialize Repository

```bash
git init
git add .
git commit -m "initial commit"
git branch -M main
git remote add origin https://github.com/vnukala84/product-service.git
git push -u origin main
```

---

# 🔧 Step 3: Jenkins Setup

### Installed Tools:

* Jenkins
* Maven
* JDK 17
* Docker
* kubectl

### Configured in Jenkins:

* **Global Tool Configuration**

  * Maven → `Maven3`
  * JDK → `JDK17`

---

# 🔄 Step 4: Jenkins Pipeline (CI)

### Created `Jenkinsfile`

Pipeline stages:

* Checkout
* Build (Maven)
* Test
* Package

### Key Fixes:

* Removed duplicate `git` checkout
* Fixed branch issue (`main` vs `master`)

---

# 🐳 Step 5: Dockerization

### Dockerfile

```dockerfile
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Build Image

```bash
docker build -t product-service:latest .
```

---

# 📤 Step 6: Push to DockerHub

### Login

```bash
docker login
```

### Tag & Push

```bash
docker tag product-service:latest venkat8430/product-service:latest
docker push venkat8430/product-service:latest
```

---

# ☸️ Step 7: Kubernetes Deployment

### Deployment YAML

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: product-service
spec:
  replicas: 2
  selector:
    matchLabels:
      app: product-service
  template:
    metadata:
      labels:
        app: product-service
    spec:
      containers:
      - name: product-service
        image: venkat8430/product-service:latest
        ports:
        - containerPort: 8080
```

---

### Service YAML

```yaml
apiVersion: v1
kind: Service
metadata:
  name: product-service
spec:
  type: NodePort
  selector:
    app: product-service
  ports:
    - port: 80
      targetPort: 8080
      nodePort: 30007
```

---

### Deploy to Cluster

```bash
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
```

---

# 🔐 Step 8: Jenkins Credentials Setup

### DockerHub Credentials

* ID: `docker-creds`
* Type: Username/Password

### Kubernetes Credentials

* ID: `kubeconfig`
* Type: Secret File

---

# 🚀 Step 9: Full CI/CD Pipeline

### Final Jenkinsfile Features

* Maven Build
* Docker Build
* Docker Push
* Kubernetes Deployment
* Dynamic image tagging using `BUILD_NUMBER`

---

### Deployment Strategy

```bash
kubectl set image deployment/product-service \
product-service=venkat8430/product-service:<BUILD_NUMBER>
```

---

# 🔁 Pipeline Execution Flow

1. Developer pushes code to GitHub
2. Jenkins triggers pipeline
3. Maven builds application
4. Docker image is created
5. Image pushed to DockerHub
6. Kubernetes deployment updated
7. Rolling update happens with zero downtime

---

# 🔍 Verification

### Check Pods

```bash
kubectl get pods
```

### Check Service

```bash
kubectl get svc
```

### Access Application

```
http://<node-ip>:30007
```

---

# 🧠 Key Learnings

* Fixed Git branch mismatch (`main` vs `master`)
* Debugged Jenkins pipeline issues
* Resolved Spring Boot test configuration error
* Handled Docker permission issues
* Migrated from deprecated Docker base image
* Implemented Kubernetes rolling updates

---

# 🎯 Outcome

✅ Fully automated CI/CD pipeline
✅ Zero-downtime Kubernetes deployment
✅ Versioned Docker images
✅ Production-like DevOps setup

---

# 🚀 Next Enhancements

* Helm charts for deployment
* ArgoCD (GitOps)
* Automated rollback
* Monitoring (Prometheus + Grafana)
* Logging (ELK stack)

---

## 👨‍💻 Author

Venkat Nukala
DevOps Engineer | Kubernetes | CI/CD | Cloud

