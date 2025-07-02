# Load Balancer with Health Checks and UI

This project is a custom-built **Load Balancer** in Java Spring Boot with a React.js frontend. It supports:

- ✅ **Round Robin**, **Weighted**, and **Sticky Session** strategies
- 🔁 Automatic **Health Checks** for backend servers
- 🖥️ Simple **UI to route traffic and manage servers**
- 🛠️ Server status stored in **PostgreSQL**

---

## 🚀 Features

### 🔃 Load Balancer
- Round Robin: Distributes requests evenly
- Weighted: Routes based on assigned weights
- Sticky: Remembers client-server mapping

### ❤️ Health Checks
- Checks server health every 5s
- Marks `isAlive = false` if server goes down
- Cached results to reduce DB hits

### 🌐 Frontend UI
- Dashboard to view server health and route traffic
- Server Registration page to register/remove backend servers
- Auto refresh every 5s for server status

---

## 🧪 How to Run

### ✅ Backend (Spring Boot)

cd LoadBalancerApplication
./mvnw spring-boot:run

✅ Frontend (React)

cd load-balancer-ui
npm install
npm start

🛠️ Tech Stack
Backend: Java, Spring Boot, Spring Data JPA, PostgreSQL

Frontend: React.js, Axios

Database: PostgreSQL
