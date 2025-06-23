package com.LaVoz.LaVoz.web.apiResponse;

public enum StatusCode {

    COMMON("COMMON"),
    USER("USER"),
    ORGANIZATION("ORGANIZATION")
    ;

    private final String prefix;

    StatusCode(String prefix){
        this.prefix = prefix;
    }

    public String getCode(int codeNumber){
        return this.prefix + codeNumber;
    }
}
