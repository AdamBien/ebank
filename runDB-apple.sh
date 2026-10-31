#!/bin/zsh
# PostgreSQL via Apple's container CLI (macOS 26, Apple silicon): https://github.com/apple/container
container system start
container rm -f ebank-postgres
container run --rm --name ebank-postgres -e POSTGRES_USER=ebank -e POSTGRES_DB=ebankdb -e POSTGRES_PASSWORD=ebanksecret -p 5432:5432 -d postgres
