package org.sopt.jaksim.global.exception;

import org.sopt.jaksim.global.message.ErrorMessage;

public class IOException extends BusinessException {
    public IOException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
