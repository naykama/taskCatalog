# taskCatalog
REST-сервис для управления задачами

## Используемые технологии:
- Kotlin
- Spring Boot
- Spring WebFlux
- Spring JdbcClient
- PostgreSQL
- Swagger
- Liquebase

## Требования

Реализовать REST-сервис для управления задачами.
Использовать Kotlin, Spring Boot, Reactor, JdbcClient и native SQL.
Архитектура должна включать Controller, Service и Repository.
Обязательно добавьте unit-тесты для сервиса и контроллера.
Базовые операции: создание, получение, обновление статуса, удаление, список с пагинацией и фильтрацией.

### Технические требования

Стек

- Kotlin
- Spring Boot
- Spring WebFlux или Web MVC с Reactor-типами в сервисном слое
- JdbcClient
- PostgreSQL или H2
- Flyway/Liquibase для миграций

Repository должен:
- использовать JdbcClient
- содержать native SQL
- не использовать ORM/JPA
- реализовать операции CRUD

В сервисном слое все методы должны возвращать:
- Mono<Task>
- Flux<Task>
- либо DTO-обертки на их основе

### Требования к Unit Test

Обязательно покрыть тестами:
Service
- успешное создание задачи
- получение задачи по id
- ошибка при отсутствии задачи
- обновление статуса
- удаление задачи
- получение списка задач с фильтрацией и пагинацией

Controller
- корректные HTTP-статусы
- валидация входных данных
- 404 для отсутствующей задачи

Repository
- замокать JdbcClient
- проверить формирование SQL-вызовов и маппинг результатов

### Дополнительные требования
- Использовать DTO для запросов и ответов
- Добавить валидацию:
title не пустой,
длина title — от 3 до 100 символов
- Ошибки обрабатывать централизованно через @RestControllerAdvice
- Код должен быть читаемым и разделённым по пакетам

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


