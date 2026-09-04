package airhacks.ebank.http.boundary;

import java.net.URI;

import jakarta.ws.rs.core.Response;

/// Business-neutral [Response] factories. Only status-code plumbing lives
/// here; mapping a domain outcome to a status is the responsibility of the
/// owning BC's boundary.
public interface Responses {

    static Response ok(Object entity) {
        return Response
                .ok(entity)
                .build();
    }

    static Response created(URI location) {
        return Response
                .created(location)
                .build();
    }

    static Response conflict(Object entity) {
        return Response
                .status(Response.Status.CONFLICT)
                .entity(entity)
                .build();
    }

    static Response badRequest(Object entity) {
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(entity)
                .build();
    }

    static Response noContent() {
        return Response.noContent().build();
    }
}
