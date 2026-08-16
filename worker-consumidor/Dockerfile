# --- Etapa 1: Build da aplicação ---
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Copia os arquivos do Maven e código fonte
COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY src ./src

# Compila o projeto
RUN ./mvnw clean package -DskipTests

# --- Etapa 2: Imagem final leve para execução ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copia o artefato gerado
COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
