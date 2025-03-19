package airhacks.ebank.reporting.boundary;

import airhacks.ebank.reporting.control.AccountQuery;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("reports")
@Produces(MediaType.TEXT_PLAIN)
public class ReportsResource {

    @Inject
    AccountQuery accounts;

    @GET
    @Path("accounts")
    public Response accounts() {
        var allAccounts = this.accounts.asIBANs();
        if (allAccounts.isEmpty())
            return Response
                    .noContent()
                    .build();
        return Response
                .ok(allAccounts)
                .build();
    }
}
