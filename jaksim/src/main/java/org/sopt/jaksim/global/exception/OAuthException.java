package org.sopt.jaksim.global.exception;

import org.sopt.jaksim.global.message.ErrorMessage;

public class OAuthException extends BusinessException {
    public OAuthException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
