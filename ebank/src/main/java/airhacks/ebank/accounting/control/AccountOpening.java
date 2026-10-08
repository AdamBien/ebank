package airhacks.ebank.accounting.control;

import airhacks.ebank.Control;
import airhacks.ebank.accounting.entity.Account;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/// Decides whether an account may be created: the initial balance must be
/// positive and below 1000 (the cap on initial deposits deters fraud, R1.3),
/// and the IBAN must be unused. Only valid accounts reach the persistence layer.
@Control
public class AccountOpening {
    @PersistenceContext
    EntityManager em;

    @Inject
    AccountFinder finder;

    public AccountCreationResult initialCreation(Account account) {
        if (!this.isValidForCreation(account))
            return new AccountCreationResult.Invalid(account);
        if (this.finder.exists(account))
            return new AccountCreationResult.AlreadyExists(account);
        this.em.persist(account);
        return new AccountCreationResult.Created(account);
    }

    boolean isValidForCreation(Account account) {
        return account.isBalancePositive()
                && (account.balance() < 1000);
    }
}
