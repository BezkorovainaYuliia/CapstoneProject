FROM openjdk:21
LABEL authors="yuliiabezkor"
EXPOSE 8080
ARG APP_URL
ARG APP_CORS_LINKS

ENV APP_URL=$APP_URL
ENV APP_CORS_LINKS=$APP_CORS_LINKS
COPY /backend/target/filmdb.jar /filmdb.jar
ENTRYPOINT ["java","-jar","/filmdb.jar"]