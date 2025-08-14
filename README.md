# ebank

A banking API implemented with MicroProfile, powered with Quarkus, demonstrating Java 21+ features. The application provides account management and transaction processing capabilities with a focus on simplicity, observability, and testability.


## Architecture Philosophy

The application follows the Boundary-Control-Entity (BCE) pattern to maintain clear separation of concerns. 

## Development Setup

### PostgreSQL Database
```bash
docker pull postgres
docker run --rm --name ebank-postgres -e POSTGRES_USER=ebank -e POSTGRES_DB=ebankdb -e POSTGRES_PASSWORD=ebanksecret -p 5432:5432 -d postgres
```

### Application Start
```bash
cd ebank
mvn quarkus:dev
```

### System Testing
```bash
cd ebank-st
mvn failsafe:integration-test
```

## Technology Stack

- **Quarkus**: Supersonic subatomic Java framework optimized for cloud deployments
- **PostgreSQL**: Popular relational database for transaction consistency
- **Jakarta Persistence (JPA)**: Standard ORM for domain object mapping
- **Jakarta REST**: RESTful web services following industry standards
- **MicroProfile Health**: Production-ready health check endpoints
- **MicroProfile Metrics**: Application performance monitoring