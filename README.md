# 🐷 Spring AI Ledger Platform
AI 사용량(토큰 / 비용)을 수집하고 분석하는 **LLMOps 플랫폼**
---
## 🚀 Demo
- 🌐 Frontend: https://token-ledger-platform.vercel.app  
- ⚙️ Backend API: http://52.78.69.13:8080  
---
## 🧱 Architecture

Frontend (Next.js / Vercel)
↓ (API Proxy)
Backend (Spring Boot / EC2 Docker)
↓
Database (AWS RDS MySQL)

---
## 📊 주요 기능
- 💰 총 비용 / 토큰 / 차단 요청 KPI 제공
- 📈 모델별 비용 집계
- 🏆 프로젝트별 비용 랭킹
- 📦 usage log 기반 실시간 데이터 반영
- 🚧 (확장 예정) Budget / Circuit Breaker
---
## 🔌 API
### ✅ KPI 조회

GET /api/dashboard/kpi?projectId=1&period=week

---
### 📊 모델 비용 요약

GET /api/dashboard/model-cost-summary?projectId=1&period=week

---
### 🏆 프로젝트 비용 랭킹

GET /api/dashboard/project-ranking?period=month

---
### 📥 Usage Log 수집 (핵심 API)

POST /internal/usage-logs

👉 라이브러리 / 외부 시스템은 이 API로 데이터 전송
---
## 📥 Usage Log 예시
```bash
curl -X POST http://52.78.69.13:8080/internal/usage-logs \
  -H "Content-Type: application/json" \
  -d '{
    "eventId": "evt-001",
    "idempotencyKey": "idem-001",
    "projectId": 1,
    "applicationId": 1,
    "userId": 1,
    "modelId": "gpt-4o-mini",
    "inputTokens": 1200,
    "outputTokens": 800,
    "totalTokens": 2000,
    "totalCost": 0.0035,
    "currencyCode": "USD",
    "status": "SUCCESS",
    "startedAt": "2026-04-29T12:00:00",
    "finishedAt": "2026-04-29T12:00:02",
    "latencyMs": 2000
  }'
```
⸻

⚙️ Local 실행

🔧 Backend (Spring Boot)

./gradlew bootRun

⸻

🎨 Frontend (Next.js)

npm install
npm run dev

⸻

🐳 배포 (EC2)

docker build -t token-ai-ledger .
docker run -d -p 8080:8080 token-ai-ledger

⸻

🗄️ Database (RDS)

Host: token-ai-ledger-db.crumy8a0eyuf.ap-northeast-2.rds.amazonaws.com
Port: 3306
DB: token_ledger

⸻

🔐 환경변수 (Backend)

DB_HOST=
DB_USERNAME=
DB_PASSWORD=

⸻

🧠 프로젝트 목적

* AI API 비용 추적 및 분석
* 토큰 사용량 기반 운영 지표 제공
* Budget 기반 요청 제어 (예정)
* SaaS형 LLMOps 플랫폼 구축

⸻

🔥 핵심 개념

라이브러리 → usage-log API 전송
        ↓
백엔드 → 비용 / 토큰 집계
        ↓
대시보드 → 실시간 반영

👉 즉, API 한 번 호출로 모든 분석이 자동 처리됨

⸻

🛠️ Tech Stack

* Backend: Spring Boot, JPA, MySQL
* Frontend: Next.js (App Router), TailwindCSS
* Infra: AWS EC2, RDS, Docker, Vercel
* Monitoring: Micrometer (확장 예정)

⸻

🚧 TODO (Next Step)

* JWT 인증 / 사용자 관리
* Multi-tenant 구조
* Budget / Circuit Breaker
* Grafana 연동
* Streaming usage tracking
