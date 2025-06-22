package com.LaVoz.LaVoz.common.exception;

import com.LaVoz.LaVoz.web.apiResponse.error.ErrorStatus;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorStatus errorStatus;

    public CustomException(ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }
}
