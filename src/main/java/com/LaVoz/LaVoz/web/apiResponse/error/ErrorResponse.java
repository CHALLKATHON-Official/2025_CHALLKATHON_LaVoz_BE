package com.LaVoz.LaVoz.web.apiResponse.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import lombok.Setter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class ErrorResponse {

    private Boolean isSuccess;
    private String code;
    private String message;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> validation;

}
