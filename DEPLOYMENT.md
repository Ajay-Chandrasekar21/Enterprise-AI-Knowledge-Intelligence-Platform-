# EAKIP Multi-Module Platform Deployment Guide

This guide outlines the steps required to deploy and run the **Vite/React/TypeScript frontend** and the **FastAPI/Python backend** application.

---

## 1. Prerequisites

Ensure you have the following installed on your machine:
*   **Python**: Version 3.10 to 3.13
*   **Node.js**: Version 18+ (with `npm`)
*   **Docker & Docker Compose**: Recommended for local database dependency services (PostgreSQL, Redis, RabbitMQ)

---

## 2. Backend Deployment (FastAPI)

The backend exposes the core API, WebSocket channels, background scheduling, RAG pipelines, and agent runtime configurations.

### Option A: Local Run (Virtual Environment)

1.  **Navigate to the project root directory**:
    ```bash
    cd d:/Study/ajay/Library
    ```
2.  **Create and activate the virtual environment**:
    *   **Windows**:
        ```powershell
        python -m venv .venv
        .venv\Scripts\activate
        ```
    *   **Linux/macOS**:
        ```bash
        python -m venv .venv
        source .venv/bin/activate
        ```
3.  **Install the dependencies**:
    ```bash
    pip install -r requirements.txt
    ```
4.  **Set the Python Path and Start the Server**:
    *   **Windows (PowerShell)**:
        ```powershell
        $env:PYTHONPATH="eakip_core/src;eakip_security/src;eakip_api/src;eakip_document_processing/src;eakip_rag/src;eakip_agent_orchestrator/src;eakip_analytics/src"
        python -m uvicorn eakip_api.main:app --host 127.0.0.1 --port 8080 --reload
        ```
    *   **Linux/macOS (Bash)**:
        ```bash
        export PYTHONPATH="eakip_core/src:eakip_security/src:eakip_api/src:eakip_document_processing/src:eakip_rag/src:eakip_agent_orchestrator/src:eakip_analytics/src"
        python -m uvicorn eakip_api.main:app --host 0.0.0.0 --port 8080 --reload
        ```

### Option B: Docker Compose (All Services & Backend Container)

To launch the backend along with PostgreSQL (with pgvector), Redis, RabbitMQ, MongoDB, and Elasticsearch:

1.  **Ensure Docker is running**.
2.  **Build and start all services**:
    ```bash
    docker-compose up --build -d
    ```
3.  The backend service will listen on [http://localhost:8080](http://localhost:8080).

---

## 3. Frontend Deployment (Vite + React)

The frontend is a TypeScript web client styled with TailwindCSS and bundled with Vite.

### Step 1: Install Dependencies
Navigate to the frontend package directory and run `npm install`:
```bash
cd d:/Study/ajay/Library/eakip-frontend
npm install
```

### Step 2: Configure Environment Variables
Create a `.env` file inside `eakip-frontend/` directory (or modify environment parameters):
```env
VITE_API_URL=http://localhost:8080
VITE_WS_URL=ws://localhost:8080/ws/notifications
```

### Step 3: Development Server Run
Start Vite's live hot-reload development server:
```bash
npm run dev
```
The server typically boots on [http://localhost:5173](http://localhost:5173).

### Step 4: Production Static Build
To bundle the frontend for static hosting servers (such as Nginx, AWS S3, Vercel, or Netlify):
```bash
npm run build
```
This compiles assets and creates a production bundle in the `eakip-frontend/dist` directory, which can be served as static HTML/JS/CSS assets.

---

## 4. Verification Checkpoints

*   **API Documentation**: Check [http://localhost:8080/docs](http://localhost:8080/docs) (Swagger UI) or [http://localhost:8080/redoc](http://localhost:8080/redoc).
*   **WebSockets Feed**: Make sure WebSocket connections connect to `ws://localhost:8080/ws/notifications`.
