# ---------- Build ----------
FROM gradle:8.10.2-jdk21-alpine AS builder
WORKDIR /app
COPY . .
RUN gradle clean bootJar --no-daemon

# ---------- Runtime ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
ENV JAVA_OPTS=""
# Busca el jar generado (con o sin -SNAPSHOT)
COPY --from=builder /app/build/libs/*.jar /app/app.jar
EXPOSE 8083
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
