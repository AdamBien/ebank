package airhacks.ebank.accounting.boundary;

import org.eclipse.microprofile.metrics.annotation.Timed;

import airhacks.ebank.Boundary;
import airhacks.ebank.accounting.control.AccountCreationResult.AlreadyExists;
import airhacks.ebank.accounting.control.AccountCreationResult.Created;
import airhacks.ebank.accounting.control.AccountCreationResult.Invalid;
import airhacks.ebank.accounting.control.Responses;
import airhacks.ebank.accounting.control.TransactionProcessor;
import airhacks.ebank.accounting.entity.Account;
import airhacks.ebank.accounting.entity.Transaction;
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

@Boundary
@Path("accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountsResource {

    @Inject
    TransactionProcessor processor;

    @Inject
    EBLog log;

    @GET
    @Path("{iban}")
    @Timed
    public Response account(@PathParam("iban") String iban) {
        this.log.info("get account " + iban);
        return processor
                .account(iban)
                .map(Responses::ok)
                .orElseGet(Responses::noContent);
    }

    @POST
    public Response initialCreation(Account account){
        this.log.info("initialCreation " + account);
        var result = this.processor.initialCreation(account);
        return switch(result){
            case Created created -> Responses.created(created);
            case AlreadyExists exists -> Responses.alreadyExists(exists);
            case Invalid invalid -> Responses.invalid(invalid);
        };
    }

    @POST
    @Path("/{iban}/transactions")
    public Response processTransaction(@PathParam("iban") String iban,TransactionCarrier serializedTransaction) {
        this.log.info("processTransaction " + iban);
        var transaction = Transaction.from(serializedTransaction);
        return processor
                .processTransaction(iban, transaction)
                .map(Responses::ok)
                .orElseGet(Responses::noContent);

    }

}
