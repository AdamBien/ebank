package airhacks.ebank.accounting.control;

import airhacks.ebank.accounting.entity.Account;

public sealed interface AccountCreationResult {
    record AlreadyExists(Account account) implements AccountCreationResult {
    }
    record Invalid(Account account) implements AccountCreationResult {
    }

    record Created(Account account) implements AccountCreationResult {
    }
}