package airhacks.ebank.accounting.boundary;

import io.helidon.integrations.langchain4j.Ai;

import airhacks.ebank.accounting.control.AccountCreationResult;
import airhacks.ebank.accounting.control.TransactionProcessor;
import airhacks.ebank.accounting.entity.Account;
import airhacks.ebank.ai.BankAssistant;
import airhacks.ebank.logging.control.EBLog;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.V;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("assistant")
@Ai.Tool
@Transactional
public class BankAssistantResource {

    @Inject
    BankAssistant assistant;

    @Inject
    TransactionProcessor processor;

    @Inject
    EBLog log;

    @POST
    @Path("/chat")
    public Response chat(String question) {
        return Response.ok(assistant.chat(question)).build();
    }

    @Tool("Get information about given account identified by given IBAN number.")
    public Account account(@V("IBAN number") String iban) {
        this.log.info("tool get account " + iban);
        return processor
                .account(iban)
                .orElseGet(null);
    }

    @Tool("Create new account with provided iban and balance")
    public void createAccount(Account account) {
        this.log.info("tool initialCreation " + account);
        this.processor.initialCreation(account);
    }
}
