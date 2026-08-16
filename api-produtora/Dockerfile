# --- Etapa 1: Build da aplicação ---
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Copia os arquivos de configuração do Maven e o código fonte usando COPY
COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY src ./src

# Realiza o empacotamento do projeto ignorando testes
RUN ./mvnw clean package -DskipTests

# --- Etapa 2: Imagem final leve para execução ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copia apenas o jar gerado da etapa anterior
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
