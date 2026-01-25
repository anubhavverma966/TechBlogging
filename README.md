📌 Cloud Architecture & Deployment

This project is deployed entirely on Microsoft Azure using a container-based, cloud-native architecture designed to stay within free-tier limits while remaining production-realistic.

⚙️ Deployment Flow

1. Code is pushed to GitHub.
2. GitHub Actions pipeline builds the Spring Boot Docker image.
3. The image is pushed to Azure Container Registry.
4. Azure Container Apps deploys a new revision automatically.
5. Traffic is routed to the latest revision.
6. The application scales to zero when idle.
   
🔐 Secrets Management

Application secrets such as database credentials and OAuth client secrets are not stored in source control or container images.
Instead:
- Secrets are stored in Azure Key Vault
- Azure Container Apps uses system-assigned managed identity
- The identity is granted Key Vault Secrets User
- Secrets are injected as environment variables
- Spring Boot reads them in the prod profile

This mirrors enterprise-grade security practices used in real production systems.

📦 Why Azure Container Apps?

The project originally ran on Azure App Service Free tier, but was migrated to Azure Container Apps to improve cost control and reliability.
Container Apps was chosen because it provides:
- Scale-to-zero for idle workloads
- Fine-grained CPU and memory limits
- Revision-based deployments
- Native container execution model
- Lower idle cost than App Service Free tier
- Better suitability for Java workloads

💸 Cost Optimization Strategy

This project is intentionally engineered for near-zero cost operation:
- Container resources capped at:
  - 0.25 vCPU
  - 0.5 GiB RAM
- Maximum replicas limited to 1
- Scale-to-zero enabled
- No VNET integration
- Azure Database for MySQL free tier
- Key Vault free tier usage
- Minimal logging and telemetry

These guardrails ensure the application can stay online for portfolio purposes without unexpected billing.

🔄 CI/CD Pipeline

A fully automated GitHub Actions pipeline handles deployments:
- Docker image build
- Push to Azure Container Registry
- Deployment to Azure Container Apps
  
This eliminates manual releases and enables rapid iteration with production-style workflows.

🛡️ Security Highlights

- JWT-based authentication with refresh token rotation
- OAuth2 login using Microsoft Entra ID and Google
- Role-based access control
- Secrets stored in Key Vault
- No credentials committed to GitHub
- HTTPS enforced by platform ingress

🎯 Design Goals

This architecture was built with three goals:
1. Learning real Azure production patterns
2. Staying within free-tier limits
3. Creating a strong portfolio project
   
It demonstrates:
- Cloud-native deployment
- Containerization
- CI/CD automation
- Managed identity usage
- Secure secret handling
- Cost-aware cloud design
