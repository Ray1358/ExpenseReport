Finance Manager (Java)

A desktop personal finance manager built with Java, JavaFX, and SQLite.
This application allows users to track expenses, view monthly summaries, and analyze spending by category using a clean graphical interface.

FEATURES
- Add and view expenses with date, amount, category, and notes
- Persistent storage using SQLite (JDBC)
- Monthly total calculation
- Category-based spending breakdown
- Desktop UI built with JavaFX
- Clean layered architecture (model, service, repository, UI)
- Unit-tested business logic using JUnit

TECH STACK
- Java 17
- JavaFX
- SQLite (JDBC)
- Maven
- JUnit 5

PROJECT STRUCTURE
src/main/java/com/ray/finance
- model: Domain models (Expense, Category)
- repo: Data access layer (SQLite via JDBC)
- service: Business logic
- ui: JavaFX user interface

src/test/java
- service: Unit tests

HOW TO RUN
Prerequisites:
- Java 17+
- Maven

Commands:
mvn clean test
mvn javafx:run

DATABASE
The application uses a local SQLite database stored at:
data/finance.db

The database schema is automatically created on first run.

TESTING
Run unit tests with:
mvn test

WHAT I LEARNED
- Designing a layered Java application with separation of concerns
- Using JDBC for database persistence
- Building desktop UIs with JavaFX
- Writing unit tests for service-layer logic
- Managing dependencies and builds with Maven

FUTURE IMPROVEMENTS
- Edit and delete existing expenses
- Charts for visual spending analysis
- Export data to CSV or PDF
- User-defined categories

AUTHOR
Ramon Baez
Computer Science Student | Java Developer

<img width="432" height="654" alt="image" src="https://github.com/user-attachments/assets/59e5e694-90dd-483f-9c0f-93af24f909ae" />
