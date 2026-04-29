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

### Запуск в Docker
В случае доступности Docker Compose можно установить и запустить приложение командой:

```cmd
  docker compose up --build --detach
```
После запуска интерфейс будет доступен по URL:

[http://localhost:8088](http://localhost:8088)

URL Swagger REST API:

[http://localhost:8088/swagger-ui/index.html](http://localhost:8088/swagger-ui/index.html)


Команда для остановки и удаления приложения:

```cmd
  docker compose down
```
Команда для остановки и удаления приложения вместе созданными данными БД PostgreSQL:

```cmd
  docker compose down -v
```

### Локальный запуск

Предварительные требования:
- Java 21
- PostgreSQL 16.x

БД должна быть доступна по localhost:5432, необходимо запустить последовательно скрипты из src/main/resources/db/local:
- 001_init.sql (Создание пользователя и БД taskdb)
- Переключиться на базу данных taskdb
- 002_grant_role.sql (Передача прав на public task_user)
- 003_init_liquebase.sql (создание schema для liquebase)

Запуск сервиса:
```cmd
  ./mvnw spring-boot:run
```

После запуска интерфейс будет доступен по URL:

[http://localhost:8080](http://localhost:8080)

URL Swagger REST API:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)


## Запуск тестов

Предварительные требования:
- Java 21

Интеграционный тест contextLoads() выключен, запустятся только Unit тесты. Команда для запуска тестов:
```cmd
  ./mvnw clean verify
```
## Возможные проблемы при локальном запуске

Если по умолчанию подтягивается более ранняя jdk и возникает ошибка:
```
[ERROR] org/junit/platform/launcher/Launcher has been compiled by a more recent version of the Java Runtime... 
```
Можно настроить путь до нужной jdk в консоли: 
```cmd
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
```


