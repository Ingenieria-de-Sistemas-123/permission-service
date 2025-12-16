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
# Expone el puerto estándar del servicio
EXPOSE 8080
# Usa sh -c para que JAVA_OPTS se aplique si se necesita (p.ej. -Xms/-Xmx, system props)
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
