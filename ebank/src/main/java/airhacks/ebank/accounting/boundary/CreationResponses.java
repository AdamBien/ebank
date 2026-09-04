package airhacks.ebank.accounting.boundary;

import java.net.URI;

import airhacks.ebank.accounting.control.AccountCreationResult.AlreadyExists;
import airhacks.ebank.accounting.control.AccountCreationResult.Created;
import airhacks.ebank.accounting.control.AccountCreationResult.Invalid;
import airhacks.ebank.http.boundary.Responses;
import jakarta.ws.rs.core.Response;

/// Translates the sealed [airhacks.ebank.accounting.control.AccountCreationResult]
/// into HTTP status codes. Accounting-specific and therefore owned by this
/// boundary; the status-code plumbing itself comes from [Responses].
public interface CreationResponses {

    static Response created(Created created) {
        var iban = created.account().iban();
        return Responses.created(URI.create("/" + iban));
    }

    static Response alreadyExists(AlreadyExists exists) {
        return Responses.conflict(exists);
    }

    static Response invalid(Invalid invalid) {
        return Responses.badRequest(invalid);
    }
}
