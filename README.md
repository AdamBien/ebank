# ebank

CRUD with Quarkus / JPA / PostgreSQL 

# PostgreSQL Installation and Start
 
1. `docker pull postgres`
2. `docker run --rm --name ebank-postgres -e POSTGRES_USER=ebank -e POSGRES_DB=ebankdb -e POSTGRES_PASSWORD=ebanksecret -p 5432:5432 -d postgres`

# ebank backend start

from `ebank`:

`mvn quarkus:dev`

# system test exection

from `ebank-st`:
optional / on demand: 
[`mvn clean compile test-compile`]

test execution:
`mvn failsafe:integration-test`