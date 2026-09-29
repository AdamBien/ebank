package airhacks.ebank.transactions.boundary;

import static airhacks.ebank.transactions.Requirement.Rn.R1_1;
import static airhacks.ebank.transactions.Requirement.Rn.R1_2;
import static airhacks.ebank.transactions.Requirement.Rn.R1_3;
import static airhacks.ebank.transactions.Requirement.Rn.R1_4;

import airhacks.ebank.Boundary;
import airhacks.ebank.http.boundary.Responses;
import airhacks.ebank.logging.control.EBLog;
import airhacks.ebank.transactions.Requirement;
import airhacks.ebank.transactions.control.TransactionProcessor;
import airhacks.ebank.transactions.entity.Transaction;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/// HTTP entry point for applying deposits and debits to an account. Converts
/// the wire-level [TransactionCarrier] into the sealed [Transaction] before
/// delegating to [TransactionProcessor], so the domain never sees
/// serialization concerns.
@Boundary
@Path("transactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionsResource {

    @Inject
    TransactionProcessor processor;

    @Inject
    EBLog log;


    @POST
    @Path("/{iban}/")
    @Requirement({R1_1, R1_2, R1_3, R1_4})
    public Response processTransaction(@PathParam("iban") String iban,TransactionCarrier serializedTransaction) {
        this.log.info("processTransaction " + iban);
        var transaction = Transaction.from(serializedTransaction);
        return processor
                .processTransaction(iban, transaction)
                .map(Responses::ok)
                .orElseGet(Responses::noContent);
    }
}
