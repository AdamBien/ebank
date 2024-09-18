package airhacks.ebank.health.boundary;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import jakarta.enterprise.context.ApplicationScoped;

@Liveness
@ApplicationScoped
public class NoOpProbe implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse
                .up("Basic Availability");
    }

}
