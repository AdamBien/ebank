package airhacks.ebank.reports.boundary;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import airhacks.ebank.accounting.boundary.AccountDelegate;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
public class ReportsResourceIT {
    @Inject
    AccountDelegate accountDelegate;

    @Inject
    @RestClient
    ReportsResourceClient rut;

    @Test
    void fetchIBAN(){
        var response = this.rut.accounts();
        assertThat(response.getStatus()).isBetween(200, 204);
    }

}
