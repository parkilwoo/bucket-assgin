# 구조
* batch, generator, domain, common 모듈로 분리
* batch는 대용량 데이터가 있는 log 파일을 읽어 user, user_profile, user_log 테이블 처리
* 나머지는 generator 모듈에서 처리

# 실행방법
1. Batch, Generator 모듈 빌드
```
./gradlew :generator:bootJar
./gradlew :batch:bootJar
```
2. docker compose 빌드 및 실행
```
docker-compose up --build
```

# 특이사항
* batch 작업이 다 끝나면 generator가 시작되게 설정(애플리케이션 레벨에서 Polling 방식)
