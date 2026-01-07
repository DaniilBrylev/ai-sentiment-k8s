# Build stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY app/SentimentServer.java ./SentimentServer.java
RUN javac -encoding UTF-8 -d /app/out SentimentServer.java

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app
COPY --from=build /app/out /app
EXPOSE 8080
USER app
CMD ["java", "SentimentServer"]