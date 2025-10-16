# 🧩 Client-Contract API

**Backend exercise (Vaudoise)** implemented with **Java 17 / Spring Boot 3.5.x**.  
This API allows counselors to manage **clients (PERSON or COMPANY)** and their **contracts**.

✅ Persists data in an **H2 file database** (keeps data across restarts).  
✅ Clean domain: **DTOs + MapStruct + Services + Repositories**.  
✅ Strong **validation rules** (Jakarta Bean Validation) and **centralized error handling**.  
✅ **Batch endpoints** and **time-based filtering** (`updatedSince`).  
✅ **Postman Proof of Concept** with runnable tests included.

---

## ⚙️ Tech Stack

- **Java 17**
- **Maven**
- **Spring Boot 3.5.x**
  - `spring-web` → REST controllers
  - `spring-data-jpa` → repositories
  - `h2` → persistent local database
  - `validation` → Jakarta Bean Validation
- **MapStruct** → DTO ↔ Entity mapping
- **Lombok** → boilerplate reduction (getters/setters/constructors)

---

## 📁 Project Structure

```text
src/
 └─ main/
    ├─ java/ch/vaudoise/apifactory
    │   ├─ client/
    │   │   ├─ controller/   # ClientController
    │   │   ├─ domain/       # Client, PersonClient, CompanyClient, ClientType
    │   │   ├─ dto/          # ClientCreateDto, ClientUpdateDto, ClientResponseDto
    │   │   ├─ mapper/       # ClientMapper (MapStruct)
    │   │   ├─ repository/   # ClientRepository, PersonClientRepository, CompanyClientRepository
    │   │   └─ service/      # ClientService, ClientServiceImpl
    │   ├─ contract/
    │   │   ├─ controller/   # ContractController
    │   │   ├─ domain/       # Contract
    │   │   ├─ dto/          # ContractCreateDto, ContractCostUpdateDto, ContractResponseDto
    │   │   ├─ mapper/       # ContractMapper
    │   │   ├─ repository/   # ContractRepository (custom JPQL)
    │   │   └─ service/      # ContractService, ContractServiceImpl
    │   └─ common/
    │       ├─ error/        # GlobalExceptionHandler
    │       └─ exception/    # NotFoundException, BadRequestException, ConflictException
    └─ resources/
        ├─ application.properties
        └─ (optional) data.sql
```
---

## 🚀 How to Run

### Prerequisites
- Java 17
- Maven 3.8+
- Git

### Commands

```bash
# 1. Clone repository
git clone https://github.com/Nicolas-R-Dev/vaudoise-api-factory.git

# 2. Build
mvn clean package -DskipTests

# 3. Run
mvn spring-boot:run
# or
java -jar target/client-contract-api-*.jar
```



## Default URLs

- **Base API** → http://localhost:8080/api

- **H2 Console** → http://localhost:8080/h2-console

JDBC URL: jdbc:h2:file:./data/vaudoise-db
Username: sa
Password: (empty)

## ⚙️ Configuration
```properties
spring.application.name=ClientContractApi
spring.datasource.url=jdbc:h2:file:./data/vaudoise-db
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.jackson.serialization.WRITE_DATES_AS_TIMESTAMPS=false
```

🟢 Data persistence:
The database (vaudoise-db.mv.db) is stored in /data/ and remains after application restarts.

🟠 Reset:
To reset the database completely:

```bash
rm -f data/vaudoise-db*
```

## 📘 API Overview
### 🧍 Clients

| Method | Endpoint             | Description                                    |
| ------ | -------------------- | ---------------------------------------------- |
| POST   | `/api/clients`       | Create a PERSON or COMPANY client              |
| POST   | `/api/clients/batch` | Create several clients at once (transactional) |
| GET    | `/api/clients/{id}`  | Get client by ID                               |
| PUT    | `/api/clients/{id}`  | Update mutable fields (name, email, phone)     |
| DELETE | `/api/clients/{id}`  | Delete client and its contracts                |


### 📄 Contracts

| Method | Endpoint                            | Description                                                    |
| ------ | ----------------------------------- | -------------------------------------------------------------- |
| POST   | `/api/clients/{id}/contracts`       | Create a contract for one client                               |
| POST   | `/api/clients/{id}/contracts/batch` | Create several contracts for one client                        |
| GET    | `/api/clients/{id}/contracts`       | List contracts (supports pagination, `active`, `updatedSince`) |
| PATCH  | `/api/contracts/{id}/cost`          | Update cost amount                                             |
| GET    | `/api/clients/{id}/contracts/sum`   | Sum of all active contract costs                               |
| DELETE | `/api/contracts/{id}`               | Delete contract by ID                                          |

## 🧩 Validation & Errors
Standardized validation using Jakarta Bean Validation and global exception handler.
### Validation Rules

PERSON → must include birthdate

COMPANY → must include companyIdentifier

companyIdentifier → must match ^[A-Z]{3}-\\d{3}$

Unique email → returns 409 CONFLICT

Error response example

```json
{
  "timestamp": "2025-10-15T10:01:03.609+02:00",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Request validation failed",
  "errors": [
    { "field": "birthdate", "message": "birthdate is required for PERSON" }
  ]
}
```

## 🧪 Proof of Concept (Postman)
### 📂 Folder structure

```
proof-of-concept/
 ├─ postman/
 │   ├─ ClientContractApi_Collection.json
 │   └─ ClientContractApi_Environment.json
 └─ screenshots/
     ├─ 01_create_client_person.png
     ├─ 02_read_client_person.png
     ├─ 03_create_contract_person.png
     ├─ 04_list_contracts_all.png
     ├─ 05_filter_updated_since.png
     ├─ 06_sum_active_contracts.png
     ├─ 07_create_many_clients.png
     ├─ 08_create_many_contracts.png
     ├─ 09_delete_contract.png
     ├─ 10_delete_client.png
     ├─ 11_delete_client_or_contract_already_deleted.png
     ├─ 12_error_missing_birthdate.png
     ├─ 13_error_missing_companyIdentifier.png
     ├─ 14_error_duplicate_email.png
     └─ 15_error_update_immutable.png
```

### ⚙️ Environment variables

| Variable          | Value                       |
| ----------------- | --------------------------- |
| `baseUrl`         | `http://localhost:8080/api` |
| `clientId`        | created by setup request    |
| `clientIdCompany` | created by setup request    |
| `contractId`      | used for PATCH/DELETE       |
| `page`            | `0`                         |
| `size`            | `10`                        |
| `active`          | `true`                      |

## 🧭 How to use

Import the collection & environment into Postman.

Run folder “0 – Setup” first → creates base data.

Run folders 1 – Client PERSON, 2 – Client COMPANY, etc.

Each request includes a test for the expected HTTP status (201, 200, 400, 409, etc.).

### 🖼️ Screenshots (proof)

Include clear screenshots of these results (in /proof-of-concept/screenshots/):

| #  | Screenshot                                         | What to show               |
|----|----------------------------------------------------|----------------------------|
| 1  | `01_create_client_person.png`                      | 201 Created                |
| 2  | `02_read_client_person.png`                        | Full client details        |
| 3  | `03_create_contract_person.png`                    | 201 Created                |
| 4  | `04_list_contracts_all.png`                        | Active + expired contracts |
| 5  | `05_filter_updated_since.png`                      | Updated contracts filtered |
| 6  | `06_sum_active_contracts.png`                      | Total cost                 |
| 7  | `07_create_many_clients.png`                       | 201 Created                |
| 8  | `08_create_many_contracts.png`                     | 201 Created                |
| 9  | `09_delete_contract.png`                           | 204 No Content             |
| 10 | `10_delete_client.png`                             | 204 No Content             |
| 11 | `11_delete_client_or_contract_already_deleted.png` | 404 Not Found              |
| 12 | `12_error_missing_birthdate.png`                   | 400 VALIDATION_ERROR       |
| 13 | `13_error_missing_companyIdentifier.png`           | 400 VALIDATION_ERROR       |
| 14 | `14_error_duplicate_email.png`                     | 409 CONFLICT               |
| 15 | `15_error_update_immutable.png`                    | 400 Bad Request            |

## 💡 Design Notes

DTOs mapped with MapStruct

Business logic in services

GlobalExceptionHandler standardizes errors

Transactional batch endpoints (all-or-nothing)

Timestamps managed for filtering and audit

## 🧰 Troubleshooting
| Problem             | Fix                                                           |
| ------------------- | ------------------------------------------------------------- |
| Port already in use | Change `server.port` in properties                            |
| H2 login error      | Use `jdbc:h2:file:./data/vaudoise-db`, user `sa`, no password |
| Tables not created  | Ensure `spring.jpa.hibernate.ddl-auto=update`                 |
| Fresh DB needed     | Stop app → delete `./data/vaudoise-db*` → restart             |

## 🏁 Author

Developed by Nicolas R. – Technical Assessment for Vaudoise Assurances
