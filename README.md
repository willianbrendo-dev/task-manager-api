📝 Task Manager API
Este é um projeto de API RESTful para gerenciamento de tarefas, construído com Spring Boot 3, Spring Data JPA e Spring Security para autenticação via JWT (JSON Web Token).

🚀 Como Executar
Pré-requisitos
Certifique-se de ter instalado:

Java 21+

Maven (ou Gradle, dependendo da sua estrutura de build)

Um banco de dados PostgreSQL (configurado para rodar localmente ou via Docker).

Configuração
Clone o Repositório:

git clone [https://github.com/wbrendo/task-manager-api.git](https://github.com/wbrendo/task-manager-api.git)
cd task-manager-api

Configuração do Banco de Dados:
Edite o arquivo src/main/resources/application.properties (ou application.yml) e configure as credenciais do seu PostgreSQL.

# Exemplo para application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/taskdb
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update # Use 'validate' ou 'none' em produção!

Executar o Aplicativo:

./mvnw spring-boot:run
# ou
java -jar target/task-manager-api.jar

A API estará disponível em http://localhost:8080.

⚙️ Endpoints da API
Módulo de Autenticação (/auth)
Método

Endpoint

Descrição

Corpo da Requisição (JSON)

POST

/auth/register

Cria uma nova conta de usuário.

{ "email": "...", "password": "...", "role": "USER" }

POST

/auth/login

Autentica o usuário e retorna um token JWT.

{ "email": "...", "password": "..." }

GET

/auth/validate

Verifica a validade do token JWT (Exemplo de rota protegida).

Nenhum. Requer Bearer Token.

Módulo de Tarefas (/tasks)
Todas as rotas de Tarefas requerem um Authorization: Bearer <TOKEN_JWT> válido.

Método

Endpoint

Descrição

Corpo da Requisição (JSON)

GET

/tasks

Lista todas as tarefas do usuário autenticado.

Nenhum

POST

/tasks

Cria uma nova tarefa.

{ "title": "...", "description": "...", "dueDate": "YYYY-MM-DD" }

GET

/tasks/{id}

Retorna uma tarefa específica.

Nenhum

PUT

/tasks/{id}

Atualiza completamente uma tarefa existente.

{ "title": "...", "description": "...", "completed": true/false }

PATCH

/tasks/{id}/toggle

Alterna o status de conclusão de uma tarefa.

Nenhum

DELETE

/tasks/{id}

Remove uma tarefa específica.

Nenhum

🛡️ Segurança
A segurança é implementada utilizando:

Spring Security: Gerenciamento de autenticação e autorização.

JWT (JSON Web Token): Para comunicação stateless (sem estado) entre cliente e servidor.

BCrypt: Criptografia segura de senhas.
