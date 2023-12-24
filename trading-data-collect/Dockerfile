FROM eclipse-temurin:21-jre
WORKDIR /
ADD target/trading-data-collect.jar .
EXPOSE 61616
CMD ["java","-jar","trading-data-collect.jar"]
