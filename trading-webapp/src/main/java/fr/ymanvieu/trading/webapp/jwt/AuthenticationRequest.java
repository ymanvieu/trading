package fr.ymanvieu.trading.webapp.jwt;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AuthenticationRequest {

    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
}
