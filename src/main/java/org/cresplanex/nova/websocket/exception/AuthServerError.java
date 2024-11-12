package org.cresplanex.nova.websocket.exception;

import lombok.Getter;
import org.cresplanex.nova.websocket.constants.ServerErrorCode;

@Getter
public class AuthServerError {

    private final String code;
    private final String message;

    public AuthServerError() {
        this(ServerErrorCode.WS_INTERNAL_ERROR, "Internal Server Error");
    }

    public AuthServerError(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
