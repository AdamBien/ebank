# ebank system tests

System tests validate the entire ebank application's functionality from an end-user perspective, ensuring all components work together correctly in a production-like environment.

## Running the Tests

```bash
mvn clean verify
```

Tests are executed automatically during the verify phase using the Failsafe plugin. Ensure the application is running before executing system tests.

