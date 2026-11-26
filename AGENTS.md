# ebank — dev commands

## Database (required before starting the app)

```bash
./runDB.sh          # Docker
./runDB-apple.sh    # Apple `container` CLI (macOS 26, Apple silicon)
```

## Run in dev mode

```bash
cd ebank
mvn quarkus:dev
```

Serves on http://localhost:8080 (live reload, schema recreated on start).

## Unit tests

```bash
cd ebank
mvn test
```

## System tests (black-box, against a freshly started dev instance)

```bash
cd ebank-st
mvn verify
```

Alternative:

```bash
cd ebank-st
mvn clean test-compile failsafe:integration-test
```

Against a remote instance:

```bash
BASE_URI=https://deployed.example.com mvn clean test-compile failsafe:integration-test
```
