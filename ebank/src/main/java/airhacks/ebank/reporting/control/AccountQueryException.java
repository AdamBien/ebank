package airhacks.ebank.reporting.control;

/// Signals a failure while querying `accounting`'s persisted accounts over
/// JDBC. Unchecked and protocol-agnostic: the `reporting` boundaries decide
/// how to surface it (the JAX-RS runtime maps an unmapped runtime exception to
/// HTTP 500), so no REST/HTTP type leaks into this control.
public class AccountQueryException extends RuntimeException {

    public AccountQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
