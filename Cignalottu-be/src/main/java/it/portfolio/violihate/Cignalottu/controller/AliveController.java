package it.portfolio.violihate.cignalottu.controller;

import it.portfolio.violihate.cignalottu.dto.response.AliveResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
public class AliveController {


    @Value("${spring.application.name}")
    private String appName;

    @Value("${spring.application.version}")
    private String appVersion;

    /**
     * Returns liveness status with server timestamp and build metadata.
     * <p>
     * Always returns 200 OK with current UTC timestamp.
     *
     * @return 200 OK + {@link AliveResponse}
     */
    @GetMapping(value= "/alive", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AliveResponse> alive() {
        AliveResponse response = AliveResponse.alive(appName, appVersion);
        return ResponseEntity.ok(response);
    }

}