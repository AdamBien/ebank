package airhacks.ebank.accounting.boundary;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;
@ApplicationScoped
public class AccountDelegate {

    @Inject
    @RestClient
    AccountsResourceClient rut;

    String iban;

    Response lastResponse;

    public JsonObject initialCreationAndFetch(int balance) {
        var iban = randomIBAN();
        return this.initialCreationAndFetch(iban, balance);
    }

    public JsonObject initialCreationAndFetch(String iban, int balance) {
        this.lastResponse = this.initialCreation(iban, balance);
        this.iban = iban;
        return this.account();
    }

    String randomIBAN(){
        return String.valueOf(System.currentTimeMillis());

    }

    public Response initialCreation(int balance) {
        var iban = randomIBAN();
        return this.initialCreation(iban, balance);
    }

    public Response initialCreation(String iban, int balance) {
        var accountJSON = """
                {
                  "iban": "%s",
                  "balance": %d
                }
                """
                .formatted(iban, balance);
        this.lastResponse = this.rut.initialCreation(accountJSON);
        return this.lastResponse;
    }

    public boolean lastResponseWas(int status){
        return this.lastResponse.getStatus() == status;
    }

    public boolean lastResponseSuccessful(){
        return isSuccessful(this.lastResponse);
    }
    public boolean lastResponseConflict(){
        return lastResponseWas(409);
    }

    public boolean lastResponseInvalid(){
        return lastResponseWas(400);
    }

    public int deposit(int amount) {
        this.transaction("DEPOSIT", amount);
        return this.balance();
    }

    public int debit(int amount) {
        this.transaction("DEBIT", amount);
        return this.balance();
    }

    void transaction(String type, int amount) {
        var accountJSON = """
                {
                  "type": "%s",
                  "amount": %d
                }
                """
                .formatted(type, amount);

        this.lastResponse = this.rut.processTransaction(this.iban, accountJSON);
    }

    static boolean isSuccessful(Response response) {
        return response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL;
    }

    public JsonObject account() {
        this.lastResponse = this.rut.account(this.iban);
        return this.lastResponse.readEntity(JsonObject.class);
    }

    public int balance() {
        return this
                .account()
                .getInt("balance");
    }

}
