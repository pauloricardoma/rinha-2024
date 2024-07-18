FROM openjdk:21

WORKDIR /app

COPY ./build/libs/*-all.jar /app/rinha-2024-all.jar

EXPOSE 3000

CMD ["java", "-jar", "rinha-2024-all.jar"]