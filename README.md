📝 Task Manager API
Este é um projeto de API RESTful para gerenciamento de tarefas, construído com **Spring Boot 3**, **Spring Data JPA** e **Spring Security** para autenticação via **JWT (JSON Web Token)**.

------------------------------------------------------------
🚀 Como Executar
------------------------------------------------------------
Pré-requisitos:
- Java 21+
- Maven (ou Gradle, dependendo da sua estrutura de build)
- PostgreSQL (local ou via Docker)

1. Clone o repositório:
   git clone https://github.com/wbrendo/task-manager-api.git
   cd task-manager-api

2. Configure o banco de dados:
   Edite o arquivo `src/main/resources/application.properties` (ou `application.yml`).

   Exemplo para `application.properties`:
   spring.datasource.url=jdbc:postgresql://localhost:5432/taskdb
   spring.datasource.username=seu_usuario
   spring.datasource.password=sua_senha
   spring.jpa.hibernate.ddl-auto=update
   # Use 'validate' ou 'none' em produção!

3. Execute o aplicativo:
   ./mvnw spring-boot:run
   ou
   java -jar target/task-manager-api.jar

➡️ A API estará disponível em: http://localhost:8080

------------------------------------------------------------
⚙️ Endpoints da API
------------------------------------------------------------

📌 Módulo de Autenticação (/auth)
---------------------------------
POST   /auth/register   → Cria uma nova conta
Body:
{
  "email": "...",
  "password": "...",
  "role": "USER"
}

POST   /auth/login      → Autentica e retorna JWT
Body:
{
  "email": "...",
  "password": "..."
}

GET    /auth/validate   → Valida o token JWT
Requer: Bearer Token

📌 Módulo de Tarefas (/tasks)
---------------------------------
Todas as rotas requerem: Authorization: Bearer <TOKEN_JWT>

GET     /tasks           → Lista todas as tarefas
POST    /tasks           → Cria nova tarefa
Body:
{
  "title": "...",
  "description": "...",
  "dueDate": "YYYY-MM-DD"
}

GET     /tasks/{id}      → Retorna tarefa específica
PUT     /tasks/{id}      → Atualiza tarefa
Body:
{
  "title": "...",
  "description": "...",
  "completed": true/false
}

PATCH   /tasks/{id}/toggle → Alterna status de conclusão
DELETE  /tasks/{id}        → Remove tarefa

------------------------------------------------------------
🛡️ Segurança
------------------------------------------------------------
- Spring Security → Autenticação e autorização
- JWT (JSON Web Token) → Comunicação stateless
- BCrypt → Criptografia segura de senhas
