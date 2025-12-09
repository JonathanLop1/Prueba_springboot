# 🚀 Deployment Guide on Render

This guide will allow you to deploy the **CoopCredit** backend on [Render.com](https://render.com) quickly and for free.

## 📋 Prerequisites

1.  Have an account on [Render.com](https://render.com).
2.  Have this code uploaded to a **GitHub** or **GitLab** repository.

---

## ⚡ Option A: Automatic Deployment (Recommended)

We have created a `render.yaml` file that defines all the necessary infrastructure (Database + 2 Microservices).

1.  In the Render dashboard, go to **Blueprints**.
2.  Click on **New Blueprint Instance**.
3.  Connect your GitHub/GitLab repository.
4.  Render will automatically detect the `render.yaml` file.
5.  Click on **Apply**.

Done! Render will create:
*   A PostgreSQL database.
*   The `risk-central-mock` service.
*   The `credit-application-service` (automatically connected to the DB and the mock).

---

## 🛠️ Option B: Manual Deployment

If you prefer to do it step by step:

### 1. Create Database
1.  **New +** -> **PostgreSQL**.
2.  Name: `coopcredit-db`.
3.  Database: `coopcredit_db`.
4.  User: `coopcredit_user`.
5.  Plan: **Free**.
6.  **IMPORTANT**: Copy the `Internal Connection URL` when it finishes creating.

### 2. Deploy Risk Central Mock
1.  **New +** -> **Web Service**.
2.  Connect your repo.
3.  **Root Directory**: `risk-central-mock-service`.
4.  **Runtime**: Docker.
5.  **Name**: `risk-central-mock`.
6.  **Plan**: Free.
7.  **Environment Variables**:
    *   `SERVER_PORT`: `8080`
8.  Create Web Service.

### 3. Deploy Credit Application Service
1.  **New +** -> **Web Service**.
2.  Connect your repo.
3.  **Root Directory**: `credit-application-service`.
4.  **Runtime**: Docker.
5.  **Name**: `credit-application-service`.
6.  **Plan**: Free.
7.  **Environment Variables**:
    *   `SERVER_PORT`: `8080`
    *   `JWT_SECRET`: (Invent a long and secure key)
    *   `RISK_CENTRAL_URL`: (The URL of the service created in step 2, e.g., `https://risk-central-mock.onrender.com`)
    *   `SPRING_DATASOURCE_URL`: (Paste the `Internal Connection URL` from step 1. **NOTE**: If it starts with `postgres://`, change it to `jdbc:postgresql://` keeping the rest the same).
    *   `SPRING_DATASOURCE_USERNAME`: `coopcredit_user`
    *   `SPRING_DATASOURCE_PASSWORD`: (The DB password).
8.  Create Web Service.

---

## 🔍 Verification

Once deployed, you can test the endpoints using the public URL provided by Render (e.g., `https://credit-application-service.onrender.com`).

**Health Check:**
```bash
curl https://<YOUR-URL>/actuator/health
```
