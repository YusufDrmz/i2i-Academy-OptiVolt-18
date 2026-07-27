# 1. Aşama: Derleme (Java 21 ile devam edebilir)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY optivolt-core/pom.xml .
COPY optivolt-core/.mvn ./.mvn
COPY optivolt-core/mvnw .
COPY optivolt-core/src ./src

RUN mvn clean package -DskipTests

# 2. Aşama: Çalıştırma Ortamı (Java 17 JRE - Ignite Uyumlu)
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/optivolt-core-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
  "--add-opens=java.base/java.lang=ALL-UNNAMED", \
  "--add-opens=java.base/java.lang.invoke=ALL-UNNAMED", \
  "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED", \
  "--add-opens=java.base/java.math=ALL-UNNAMED", \
  "--add-opens=java.base/java.nio=ALL-UNNAMED", \
  "--add-opens=java.base/java.util=ALL-UNNAMED", \
  "--add-opens=java.base/java.util.concurrent=ALL-UNNAMED", \
  "--add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED", \
  "--add-opens=java.base/java.util.concurrent.locks=ALL-UNNAMED", \
  "--add-opens=java.base/jdk.internal.access=ALL-UNNAMED", \
  "--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED", \
  "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED", \
  "--add-opens=java.management/com.sun.jmx.mbeanserver=ALL-UNNAMED", \
  "--add-opens=jdk.management/com.sun.management.internal=ALL-UNNAMED", \
  "--add-exports=java.base/jdk.internal.misc=ALL-UNNAMED", \
  "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED", \
  "-jar", "app.jar"]