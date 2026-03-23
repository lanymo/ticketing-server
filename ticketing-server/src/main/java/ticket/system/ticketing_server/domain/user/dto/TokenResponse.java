package ticket.system.ticketing_server.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponse {

    private final String accessToken;

    // Bearer 타입 고정
    @Builder.Default
    private final String tokenType = "Bearer";
}
