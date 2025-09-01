FROM openjdk:21
LABEL authors="yuliiabezkor"
EXPOSE 8080
COPY /backend/target/filmdb.jar /filmdb.jar
ENTRYPOINT ["java","-jar","/filmdb.jar"]