# Dockerfile

# FASE 1: BUILD (Compilação da Aplicação)
# Usa uma imagem base Java 21 LTS com Maven.
FROM maven:3.9.5-eclipse-temurin-21-alpine AS build

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia os arquivos de definição do projeto primeiro (para cache do Docker)
COPY pom.xml .

# Baixa as dependências e as armazena em cache (etapa rápida se o pom.xml não mudar)
RUN mvn dependency:go-offline

# Copia o restante do código fonte
COPY src ./src

# Empacota o projeto em um JAR (o -DskipTests pula os testes)
RUN mvn clean install -DskipTests

# FASE 2: RUN (Execução da Aplicação)
# Usa uma imagem base mais leve, contendo apenas o JRE (Java Runtime Environment)
FROM eclipse-temurin:21-jre-alpine

# Define a porta que o contêiner irá expor
EXPOSE 8080

# Define o diretório de trabalho
WORKDIR /app

# Copia o JAR compilado da FASE 1
COPY --from=build /app/target/*.jar app.jar

# Comando para rodar a aplicação Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]