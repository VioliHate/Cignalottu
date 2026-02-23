package it.portfolio.violihate.cignalottu.dto.response;

import java.time.Instant;

/**
 * DTO response for server liveness check.
 * <p>
 * Contains information to verify that the service is ALIVE
 */
public record AliveResponse(String status,
                            String timestamp,
                            String appName,
                            String appVersion){
    /**
     * Factory method for response "alive" with timestamp.
     *
     * @param appName    application name
     * @param appVersion application version
     * @return AliveResponse with status "alive" + timestamp UTC + appName + appVersion
     */
    public static AliveResponse alive(String appName, String appVersion){
        return new AliveResponse("alive",
                Instant.now().toString(),
                appName, appVersion);
    }

}