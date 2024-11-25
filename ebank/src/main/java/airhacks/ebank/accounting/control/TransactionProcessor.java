
package airhacks.ebank.accounting.control;

import java.util.Optional;

import airhacks.ebank.accounting.entity.Account;
import airhacks.ebank.accounting.entity.Transaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class TransactionProcessor {
    @PersistenceContext
    EntityManager em;

    public Optional<Account> account(String iban) {
        var account = em.find(Account.class, iban);
        return Optional.ofNullable(account);
    }

    public Optional<Account> processTransaction(String iban, Transaction transaction) {
        return this.account(iban)
                .map(a -> this.applyTransaction(a, transaction))
                .map(this.em::merge);

    }

    Account applyTransaction(Account account, Transaction transaction) {
        return switch (transaction) {
            case Transaction.Debit debit -> account.debit(debit.amount());
            case Transaction.Deposit deposit -> account.deposit(deposit.amount());
        };

    }

    public AccountCreationResult initialCreation(Account account) {
        if (!this.isValidForCreation(account))
            return new AccountCreationResult.Invalid(account);
        if (this.exists(account))
            return new AccountCreationResult.AlreadyExists(account);
        this.em.persist(account);
        return new AccountCreationResult.Created(account);
    }

    boolean isValidForCreation(Account account) {
        return account.isBalancePositive()
                && (account.balance() < 1000);
    }

    boolean exists(Account account) {
        var iban = account.iban();
        return this
                .account(iban)
                .isPresent();
    }

}