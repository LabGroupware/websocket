package org.cresplanex.nova.websocket.exception;

import lombok.Getter;
import org.cresplanex.api.state.common.constants.WebSocketApplicationCode;

@Getter
public class AuthServerError {

    private final String code;
    private final String message;

    public AuthServerError() {
        this(WebSocketApplicationCode.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    public AuthServerError(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
