package airhacks.ebank.health.boundary;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@Liveness
@ApplicationScoped
public class NoOpProbe implements HealthCheck {

    @Inject
    Logger log;

    @Override
    public HealthCheckResponse call() {
        log.log(Level.INFO, "liveness checked");
        return HealthCheckResponse
                .up("Basic Availability");
    }

}
