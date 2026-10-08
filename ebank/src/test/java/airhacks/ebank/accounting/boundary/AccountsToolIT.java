package airhacks.ebank.accounting.boundary;

import static airhacks.ebank.accounting.Requirement.Rn.R3_1;
import static airhacks.ebank.accounting.Requirement.Rn.R3_2;
import static airhacks.ebank.accounting.Requirement.Rn.R3_3;
import static airhacks.ebank.accounting.Requirement.Rn.R3_4;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import airhacks.ebank.accounting.Requirement;
import airhacks.ebank.accounting.control.AccountOpening;
import airhacks.ebank.accounting.control.AccountFinder;
import airhacks.ebank.accounting.entity.Account;
import airhacks.ebank.customers.control.AccountOwnership;
import airhacks.ebank.customers.control.CustomerRegistrar;
import airhacks.ebank.customers.entity.Customer;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

/// Exercises the MCP tool in-process — the protocol plumbing is the
/// extension's job, this asserts the sentence the model actually receives.
///
/// Requires a running PostgreSQL (dev services are disabled on purpose, see
/// `application.properties`), hence the `IT` suffix: `mvn test` skips it,
/// `mvn verify -DskipITs=false` runs it against the manually started database.
@QuarkusTest
class AccountsToolIT {

    static final int BALANCE = 42;

    @Inject
    AccountsTool cut;

    @Inject
    AccountOpening accounts;

    @Inject
    AccountFinder finder;

    @Inject
    CustomerRegistrar customers;

    @Inject
    AccountOwnership ownerships;

    @ParameterizedTest(name = "{0}")
    @MethodSource("summarizeAccountCases")
    @DisplayName("R3: summarize an account")
    @Requirement({ R3_1, R3_2, R3_3 })
    void summarizeAccount(String requirement, boolean existing, boolean owned) {
        var iban = randomIBAN();
        if (existing)
            createAccount(iban);
        var owner = owned ? registerOwner(iban) : null;

        var summary = this.cut.summarizeAccount(iban);

        if (!existing) {
            assertThat(summary).as(requirement)
                    .contains("No account exists")
                    .contains(iban);
            return;
        }
        assertThat(summary).as(requirement)
                .contains(iban)
                .contains(String.valueOf(BALANCE));
        if (owned)
            assertThat(summary).as(requirement)
                    .contains(String.valueOf(owner.id))
                    .contains(owner.name);
        else
            assertThat(summary).as(requirement)
                    .contains("no customer");
    }

    static Stream<Arguments> summarizeAccountCases() {
        return Stream.of(
                arguments("R3.1", true, true),
                arguments("R3.2", true, false),
                arguments("R3.3", false, false));
    }

    @Test
    @DisplayName("R3.4: summarizing leaves the account and its ownership unchanged")
    @Requirement(R3_4)
    void summarizingLeavesEverythingUnchanged() {
        var iban = randomIBAN();
        createAccount(iban);
        var owner = registerOwner(iban);

        this.cut.summarizeAccount(iban);

        QuarkusTransaction.requiringNew().run(() -> {
            var account = this.finder.account(iban);
            assertThat(account).as("R3.4").isPresent();
            assertThat(account.get().balance()).as("R3.4").isEqualTo(BALANCE);
            var stillOwned = this.ownerships.owner(iban);
            assertThat(stillOwned).as("R3.4").isPresent();
            assertThat(stillOwned.get().id).as("R3.4").isEqualTo(owner.id);
        });
    }

    void createAccount(String iban) {
        QuarkusTransaction.requiringNew()
                .run(() -> this.accounts.initialCreation(new Account(iban, BigDecimal.valueOf(BALANCE))));
    }

    Customer registerOwner(String iban) {
        return QuarkusTransaction.requiringNew().call(() -> {
            var duke = this.customers.register(new Customer("duke"));
            this.ownerships.own(duke.id, iban);
            return duke;
        });
    }

    static String randomIBAN() {
        return UUID.randomUUID().toString();
    }
}
