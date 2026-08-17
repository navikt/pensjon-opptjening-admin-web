FROM gcr.io/distroless/java25-debian13:nonroot

WORKDIR /app

ENV TZ="Europe/Oslo"

COPY build/libs/pensjon-opptjening-admin-web.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]