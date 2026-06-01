# TagNote Web

Веб-приложение для управления заметками с тегами, с возможностью фильтрации.

## Технологический стек

Техническое задание - https://docs.google.com/document/d/15QSEWcP95njOxAOgagKuKnFzyLInbdvVVZtWAfC08Xg/edit?tab=t.0

- Java 21
- Spring Boot 4.0.6
- Spring Security + JWT
- Spring Data JPA (Hibernate)
- PostgreSQL
- Liquibase
- Swagger/OpenAPI
- JUnit 5 + Mockito

## Статус проекта

Готов к использованию

## Запуск проекта

```bash
# Клонирование репозитория
git clone https://github.com/mdatamanov/tagnote-web.git

# Переход в папку проекта
cd tagnote-web

# Сборка проекта
mvn clean install

# Запуск приложения
mvn spring-boot:run
