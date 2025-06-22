package com.LaVoz.LaVoz.common.exception;

import com.LaVoz.LaVoz.web.apiResponse.error.ErrorStatus;

public class ResourceNotFoundException extends CustomException {
    public ResourceNotFoundException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
