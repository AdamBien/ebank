package airhacks.ebank.reporting.boundary;

import static airhacks.ebank.reporting.Requirement.Rn.R1_1;
import static airhacks.ebank.reporting.Requirement.Rn.R1_2;

import java.util.List;

import airhacks.ebank.reporting.Requirement;
import airhacks.ebank.reporting.control.AccountQuery;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolCallException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;

/// MCP counterpart of [ReportsResource]: same capability, same control, a
/// different protocol. Both boundaries share [AccountQuery] directly — the CSV
/// rendering and the 204 in the JAX-RS resource are HTTP-only concerns and are
/// deliberately not reused here. A failed query is rethrown as
/// [ToolCallException], so the model receives a tool error it can read
/// instead of a JSON-RPC failure.
///
/// `reporting` is the BC exposed to AI assistants because it is read-only by
/// construction: [AccountQuery] reaches the accounts via JDBC and cannot mutate
/// them, so a non-deterministic caller has no write surface.
@ApplicationScoped
public class ReportsTool {

    @Inject
    AccountQuery accounts;

    /// @return a JSON array of IBANs, or a sentence when the bank holds no
    ///         accounts — the caller is a language model, so absence reads
    ///         better as prose than as an empty array
    @Tool(name = "list_account_ibans",
            title = "List account IBANs",
            description = """
                    List the IBANs of all accounts held by the bank. \
                    Use this to discover which accounts exist before inspecting a single one.""",
            annotations = @Tool.Annotations(
                    readOnlyHint = true,
                    idempotentHint = true,
                    openWorldHint = false))
    @Requirement({ R1_1, R1_2 })
    String listAccountIBANs() {
        var ibans = this.ibans();
        if (ibans.isEmpty())
            return "The bank holds no accounts.";
        return Json.createArrayBuilder(ibans)
                .build()
                .toString();
    }

    /// @throws ToolCallException reported to the caller as a tool error
    List<String> ibans() {
        try {
            return this.accounts.asIBANs();
        } catch (IllegalStateException e) {
            throw new ToolCallException(e.getMessage(), e);
        }
    }
}
