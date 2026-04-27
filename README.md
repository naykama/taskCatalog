# taskCatalog
REST-сервис для управления задачами

## Используемые технологии:
- Kotlin
- Spring Boot
- Spring WebFlux
- Spring JdbcClient
- PostgreSQL
- Swagger

## Запуск сервиса
После запуска интерфейс будет доступен по URL:

[http://localhost:8080](http://localhost:8080)

Документация Swagger REST API доступна по URL:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

URL к OpenAPI в json:

[http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
### Запуск в Docker
В случае доступности Docker Compose можно установить и запустить приложение командой:

```cmd
  docker compose up --build --detach
```

Команда для остановки и удаления приложения:

```cmd
  docker compose down
```
Команда для остановки и удаления приложения вместе созданными данными БД PostgreSQL:

```cmd
  docker compose down -v
```

Команда для удаления созданных образов сервисов приложения:

```cmd
  docker image ls -q taskCatalog-* | xargs docker image rm
```

### Локальный запуск

Предварительные требования:
- Java 21
- PostgreSQL 16.x

БД должна быть доступна по localhost:5432, в ней должна быть создана БД скриптом postgres/init.sql.

Запуск сервиса:
```cmd
  ./mvnw spring-boot:run
```

## Запуск тестов

Предварительные требования:
- Java 21

Если по умолчанию подтягивается более ранняя jdk и возникает ошибка:
```
[ERROR] org/junit/platform/launcher/Launcher has been compiled by a more recent version of the Java Runtime... 
```
Можно настроить путь до нужной jdk в консоли: 
```cmd
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
```

Интеграционный тест contextLoads() выключен, запустятся только Unit тесты. Команда для запуска тестов:
```cmd
  ./mvnw clean verify
```

