FROM openjdk:21
ARG JAR_FILE=target/*.jar
RUN --mount=type=secret,id=OPENAI_API_KEY export OPENAI_API_KEY=$(cat /run/secrets/openai_api_key)
ENV OPENAI_API_KEY=${OPENAI_API_KEY}
COPY ./target/SpringDocumentationAI-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]