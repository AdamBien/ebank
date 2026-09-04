/// # HTTP
/// > Protocol-level helpers shared by all boundaries.
///
/// Technical, business-neutral counterpart to [airhacks.ebank.logging]:
/// keeps generic JAX-RS [jakarta.ws.rs.core.Response] construction out of the
/// business BCs, so no BC has to depend on another one for it. The helper
/// lives in the `boundary` layer, where protocol-specific types belong.
package airhacks.ebank.http;
