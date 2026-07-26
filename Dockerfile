# 1. Aşama: Maven ve Java 21 ile Derleme
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Maven bağımlılıklarını ve kaynak kodları kopyala
COPY optivolt-core/pom.xml .
COPY optivolt-core/.mvn ./.mvn
COPY optivolt-core/mvnw .
COPY optivolt-core/src ./src

# Projeyi derle ve JAR paketini oluştur
RUN mvn clean package -DskipTests

# 2. Aşama: Çalıştırma Ortamı (Java 21 JRE)
FROM eclipse-temurin:21-jre
WORKDIR /app

# İlk aşamada üretilen JAR dosyasını kopyala
COPY --from=build /app/target/optivolt-core-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# Apache Ignite ve Spring Boot için gerekli JVM izinleriyle başlat
ENTRYPOINT ["java", \
  "--add-exports=java.base/jdk.internal.misc=ALL-UNNAMED", \
  "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED", \
  "--add-exports=java.management/com.sun.jmx.mbeanserver=ALL-UNNAMED", \
  "--add-exports=jdk.internal.jvmstat/sun.jvmstat.monitor=ALL-UNNAMED", \
  "--add-exports=java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED", \
  "--add-exports=java.base/jdk.internal.access=ALL-UNNAMED", \
  "--add-opens=java.base/jdk.internal.access=ALL-UNNAMED", \
  "--add-opens=jdk.management/com.sun.management.internal=ALL-UNNAMED", \
  "--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED", \
  "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED", \
  "--add-opens=java.base/java.lang=ALL-UNNAMED", \
  "--add-opens=java.base/java.nio=ALL-UNNAMED", \
  "--add-opens=java.base/java.util=ALL-UNNAMED", \
  "--add-opens=java.base/java.util.concurrent=ALL-UNNAMED", \
  "--add-opens=java.base/java.util.concurrent.locks=ALL-UNNAMED", \
  "--add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED", \
  "--add-opens=java.base/java.lang.invoke=ALL-UNNAMED", \
  "--add-opens=java.base/java.math=ALL-UNNAMED", \
  "--add-opens=java.sql/java.sql=ALL-UNNAMED", \
  "-jar", "app.jar"]