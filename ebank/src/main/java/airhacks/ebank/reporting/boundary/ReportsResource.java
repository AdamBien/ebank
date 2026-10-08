package airhacks.ebank.reporting.boundary;

import static airhacks.ebank.reporting.Requirement.Rn.R1_1;
import static airhacks.ebank.reporting.Requirement.Rn.R1_2;

import airhacks.ebank.reporting.Requirement;
import airhacks.ebank.reporting.control.AccountQuery;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/// Read-only reporting endpoint: renders the IBANs of all accounts as
/// plain-text CSV and never mutates state. No `@Boundary` stereotype — no
/// transaction is needed for pure reads. A failed query surfaces as an
/// unchecked exception from the control; the JAX-RS runtime maps it to 500.
@Path("reports")
@Produces(MediaType.TEXT_PLAIN)
public class ReportsResource {

    @Inject
    AccountQuery accounts;

    @GET
    @Path("accounts")
    @Requirement({R1_1, R1_2})
    public Response accounts() {
        var allAccounts = this.accounts.asIBANs();
        if (allAccounts.isEmpty())
            return Response
                    .noContent()
                    .build();
        var csv = String.join(",", allAccounts);
        return Response
                .ok(csv)
                .build();
    }
}
