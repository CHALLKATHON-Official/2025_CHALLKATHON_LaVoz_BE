package com.LaVoz.LaVoz.web.apiResponse;

public enum StatusCode {

    COMMON("COMMON"),
    USER("USER"),
    ;

    private final String prefix;

    StatusCode(String prefix){
        this.prefix = prefix;
    }

    public String getCode(int codeNumber){
        return this.prefix + codeNumber;
    }
}
