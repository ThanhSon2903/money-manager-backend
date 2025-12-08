FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/MoneyManager-0.0.1-SNAPSHOT.jar MoneyManager-v1.0.jar
EXPOSE 9090
ENTRYPOINT ["java","-jar","MoneyManager-v1.0.jar]