package airhacks.ebank.accounting.boundary;

import static airhacks.ebank.accounting.Requirement.Rn.R1_1;
import static airhacks.ebank.accounting.Requirement.Rn.R1_2;
import static airhacks.ebank.accounting.Requirement.Rn.R1_3;
import static airhacks.ebank.accounting.Requirement.Rn.R1_4;
import static airhacks.ebank.accounting.Requirement.Rn.R2_1;
import static airhacks.ebank.accounting.Requirement.Rn.R2_2;

import airhacks.ebank.Boundary;
import airhacks.ebank.accounting.Requirement;
import airhacks.ebank.accounting.control.AccountCreationResult.AlreadyExists;
import airhacks.ebank.accounting.control.AccountCreationResult.Created;
import airhacks.ebank.accounting.control.AccountCreationResult.Invalid;
import airhacks.ebank.accounting.control.AccountOpening;
import airhacks.ebank.accounting.control.AccountFinder;
import airhacks.ebank.accounting.control.CreationResponses;
import airhacks.ebank.accounting.entity.Account;
import airhacks.ebank.http.control.Responses;
import airhacks.ebank.logging.control.EBLog;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/// HTTP entry point for the account lifecycle: initial creation and lookup by
/// IBAN. All decisions are delegated to [AccountOpening] and [AccountFinder];
/// the exhaustive switch over the creation result forces every outcome to be
/// mapped to an HTTP status.
@Boundary
@Path("accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountsResource {

    @Inject
    AccountOpening opening;

    @Inject
    AccountFinder finder;

    @Inject
    EBLog log;

    @GET
    @Path("{iban}")
    @Requirement({R2_1, R2_2})
    public Response account(@PathParam("iban") String iban) {
        this.log.info("get account " + iban);
        return this.finder
                .account(iban)
                .map(Responses::ok)
                .orElseGet(Responses::noContent);
    }

    @POST
    @Requirement({R1_1, R1_2, R1_3, R1_4})
    public Response initialCreation(Account account){
        this.log.info("initialCreation " + account);
        var result = this.opening.initialCreation(account);
        return switch(result){
            case Created created -> CreationResponses.created(created);
            case AlreadyExists exists -> CreationResponses.alreadyExists(exists);
            case Invalid invalid -> CreationResponses.invalid(invalid);
        };
    }

}
