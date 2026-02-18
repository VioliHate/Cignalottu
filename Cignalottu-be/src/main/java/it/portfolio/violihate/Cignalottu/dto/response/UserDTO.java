package it.portfolio.violihate.cignalottu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private LocalDateTime createdAt;
}
