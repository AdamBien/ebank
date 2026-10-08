package airhacks.ebank.reporting.boundary;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import airhacks.ebank.accounting.control.AccountOpening;
import airhacks.ebank.accounting.entity.Account;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

/// Exercises the MCP tool in-process — the protocol plumbing is the
/// extension's job, this asserts the payload the model actually receives.
///
/// Requires a running PostgreSQL (dev services are disabled on purpose, see
/// `application.properties`), hence the `IT` suffix: `mvn test` skips it,
/// `mvn verify -DskipITs=false` runs it against the manually started database.
///
/// R1.2 (no accounts) is not asserted here — it needs a database with zero
/// accounts, a precondition only the freshly started system in `ebank-st`
/// can guarantee (see `ReportsResourceIT`, which runs first for that reason).
@QuarkusTest
public class ReportsToolIT {

    @Inject
    ReportsTool cut;

    @Inject
    AccountOpening accountOpening;

    @Test
    @DisplayName("R1.1: the tool returns the IBANs of all accounts")
    void listAccountIBANs() {
        var iban = UUID.randomUUID().toString();
        QuarkusTransaction.requiringNew()
                .run(() -> this.accountOpening.initialCreation(new Account(iban, BigDecimal.valueOf(42))));
        var actual = this.cut.listAccountIBANs();
        assertThat(actual)
                .startsWith("[")
                .contains("\"" + iban + "\"");
    }
}
