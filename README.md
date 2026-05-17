# 🏥 MediScheduler - Healthcare Appointment & Clinical Management System

![Java](https://img.shields.io/badge/Java-17-orange?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring--Boot-3.x-brightgreen?logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue?logo=mysql)
![Maven](https://img.shields.io/badge/Build-Maven-red?logo=apachemaven)
![License](https://img.shields.io/badge/License-Proprietary-red)
![Status](https://img.shields.io/badge/Status-Finished-brightgreen)
![Build](https://img.shields.io/badge/Build-Passing-blue)


**MediScheduler** is a comprehensive, full-stack Spring Boot web application designed to bridge the gap between healthcare professionals and patients. It provides a highly responsive, app-like experience for patients to book and manage medical appointments, while offering doctors a powerful clinical dashboard to manage their daily timelines, write clinical notes, and seamlessly interact with patient feedback.

## ✨ Key Features

### 👨‍⚕️ Doctor Workspace

* **Smart Daily Timeline:** An intuitive, chronological day-view of appointments with visual status markers and real-time locking for past events.
* **Clinical Notes & Audit Logging:** Write, edit, and delete post-consultation clinical notes. All changes are securely snapshotted in a historical audit table.
* **Feedback Administration:** A dedicated dashboard to view patient evaluations. Doctors can officially acknowledge reviews, write threaded clinical replies, or escalate administrative concerns.
* **Automated Schedule Management:** Background chron jobs automatically cancel unpaid expired appointments and auto-complete finished consultations.
* **Dynamic Analytics:** Real-time tracking of daily consultation hours, upcoming patient counts, and average satisfaction scores.

### 🤒 Patient Portal

* **Seamless Booking:** Schedule, reschedule, or cancel medical appointments with strict validation rules tied to payment statuses.
* **Financial Tracking:** Monitor pending invoices, view past payments, and manage consultation fees securely.
* **Interactive Feedback:** Rate appointments and provide categorized feedback (Clinical Inquiry, Service Appreciation, etc.) with a dynamic UI.
* **Doctor Communications:** View direct replies, acknowledgments, and clinical instructions from doctors straight from the feedback dashboard.

### 🎨 UI/UX Highlights

* **Native Dark Mode:** Fully integrated, user-toggled dark mode with persistent local storage.
* **Glassmorphism & Micro-interactions:** Premium, modern UI featuring smooth CSS grid animations, seamless inline-editing, and intelligent state-swapping without page reloads (via AJAX).
* **Fully Responsive:** Optimized for both desktop workspaces and mobile devices.

---

## 🛠️ Tech Stack

* **Backend:** Java 17, Spring Boot 3.x, Spring MVC, Spring Scheduling (`@Scheduled`)
* **Database:** MySQL, Spring Data JPA / Hibernate
* **Frontend:** Thymeleaf, HTML5, CSS3 (Custom modular architecture), Vanilla JavaScript (AJAX/Fetch API)
* **Icons & Fonts:** Google Material Symbols, Inter Font Family
* **Build Tool:** Maven

---

## 🚀 Getting Started

### Prerequisites

Ensure you have the following installed on your local development environment:

* [Java Development Kit (JDK) 17 or higher](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
* [Maven](https://maven.apache.org/download.cgi)
* [MySQL Server](https://dev.mysql.com/downloads/mysql/)
* An IDE such as IntelliJ IDEA, Eclipse, or VS Code

### Installation & Setup

1. **Clone the repository:**
```bash
git clone https://github.com/Chenitha-Vindiya/Medical-Appointment-Scheduling-System.git
cd medischeduler

```


2. **Configure the Database:**
* Open your MySQL client and create the database:
```sql
CREATE DATABASE medischeduler;

```


* Open `src/main/resources/application.properties` and configure your credentials:
```properties
# Database Connection
spring.datasource.url=jdbc:mysql://localhost:3306/medischeduler?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD

# Hibernate Settings
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

```




3. **Run the Application:**
* Open your terminal in the project root and execute:
```bash
mvn spring-boot:run

```


* Or simply run `MediSchedulerApplication.java` from your IDE.


4. **Access the Application:**
* The server will launch on port `8080`.
* Open your browser and navigate to: [http://localhost:8080](http://localhost:8080)



---

## 📁 Project Structure

MediScheduler utilizes a clean, role-based MVC architecture:

```text
medischeduler/
├── src/main/java/com/medischeduler/
│   ├── controller/      # Route handling for /doctor and /patient endpoints
│   ├── model/           # JPA Entities (Doctor, Patient, Appointment, History, Payment, Feedback)
│   ├── repository/      # Spring Data JPA interfaces for SQL execution
│   └── service/         # Business logic & background Scheduled Tasks
├── src/main/resources/
│   ├── static/          # Assets
│   │   ├── css/         # Modular stylesheets (split by doctor/patient/fragments)
│   │   └── js/          # Vanilla JavaScript logic
│   ├── templates/       # Thymeleaf HTML views
│   │   ├── doctor/      # Doctor dashboard, appointments, feedback
│   │   └── patient/     # Patient booking, history, reviews
│   └── application.properties # Spring Boot configurations
└── pom.xml              # Maven dependencies

```

---

## 🔮 Future Enhancements

* **Payment Gateway Integration:** Direct integration with Stripe API for processing online invoices.
* **Spring Security Implementation:** Role-based access control (RBAC) with BCrypt password hashing and JWT session management.
* **E-Prescriptions:** Allow doctors to generate and export PDF prescriptions attached to clinical notes.
* **SMS/Email Notifications:** Automated reminders for upcoming appointments via Twilio or SendGrid.

---

## 📝 License

**Copyright © 2026 MediScheduler. All Rights Reserved.**

This repository and its contents are proprietary and confidential. No part of this software, including but not limited to source code, compiled binaries, and design assets, may be reproduced, distributed, modified, or transmitted in any form or by any means without the prior written permission of the copyright owner.

This is **not** an open-source project. Unauthorized copying, cloning, or distribution of this project, via any medium, is strictly prohibited.