FROM eclipse-temurin:23-jdk
WORKDIR /app
COPY batch/build/libs/batch.jar app.jar

# wait-for-it.sh 다운로드 및 실행 권한 부여
RUN apt-get update && apt-get install -y curl \
  && curl -o /wait-for-it.sh https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh \
  && chmod +x /wait-for-it.sh \
  && apt-get clean \
  && rm -rf /var/lib/apt/lists/*

# MySQL 연결 확인 후 JAR 실행
ENTRYPOINT ["/wait-for-it.sh", "bucket_mysql:3306", "--", "java", "-jar", "app.jar"]