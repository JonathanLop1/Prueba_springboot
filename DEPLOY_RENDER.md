# 🚀 Guía de Despliegue en Render

Esta guía te permitirá desplegar el backend de **CoopCredit** en [Render.com](https://render.com) de forma rápida y gratuita.

## 📋 Prerrequisitos

1.  Tener una cuenta en [Render.com](https://render.com).
2.  Tener este código subido a un repositorio de **GitHub** o **GitLab**.

---

## ⚡ Opción A: Despliegue Automático (Recomendado)

Hemos creado un archivo `render.yaml` que define toda la infraestructura necesaria (Base de datos + 2 Microservicios).

1.  En el dashboard de Render, ve a **Blueprints**.
2.  Haz clic en **New Blueprint Instance**.
3.  Conecta tu repositorio de GitHub/GitLab.
4.  Render detectará automáticamente el archivo `render.yaml`.
5.  Haz clic en **Apply**.

¡Listo! Render creará:
*   Una base de datos PostgreSQL.
*   El servicio `risk-central-mock`.
*   El servicio `credit-application-service` (conectado automáticamente a la BD y al mock).

---

## 🛠️ Opción B: Despliegue Manual

Si prefieres hacerlo paso a paso:

### 1. Crear Base de Datos
1.  **New +** -> **PostgreSQL**.
2.  Name: `coopcredit-db`.
3.  Database: `coopcredit_db`.
4.  User: `postgres`.
5.  Plan: **Free**.
6.  **IMPORTANTE**: Copia la `Internal Connection URL` cuando termine de crearse.

### 2. Desplegar Risk Central Mock
1.  **New +** -> **Web Service**.
2.  Conecta tu repo.
3.  **Root Directory**: `risk-central-mock-service`.
4.  **Runtime**: Docker.
5.  **Name**: `risk-central-mock`.
6.  **Plan**: Free.
7.  **Environment Variables**:
    *   `SERVER_PORT`: `8080`
8.  Crear Web Service.

### 3. Desplegar Credit Application Service
1.  **New +** -> **Web Service**.
2.  Conecta tu repo.
3.  **Root Directory**: `credit-application-service`.
4.  **Runtime**: Docker.
5.  **Name**: `credit-application-service`.
6.  **Plan**: Free.
7.  **Environment Variables**:
    *   `SERVER_PORT`: `8080`
    *   `JWT_SECRET`: (Inventa una clave larga y segura)
    *   `RISK_CENTRAL_URL`: (La URL del servicio creado en el paso 2, ej: `https://risk-central-mock.onrender.com`)
    *   `SPRING_DATASOURCE_URL`: (Pega la `Internal Connection URL` del paso 1. **OJO**: Si empieza por `postgres://`, cámbialo a `jdbc:postgresql://` manteniendo el resto igual).
    *   `SPRING_DATASOURCE_USERNAME`: `postgres`
    *   `SPRING_DATASOURCE_PASSWORD`: (La contraseña de la BD).
8.  Crear Web Service.

---

## 🔍 Verificación

Una vez desplegado, puedes probar los endpoints usando la URL pública que te da Render (ej: `https://credit-application-service.onrender.com`).

**Health Check:**
```bash
curl https://<TU-URL>/actuator/health
```
