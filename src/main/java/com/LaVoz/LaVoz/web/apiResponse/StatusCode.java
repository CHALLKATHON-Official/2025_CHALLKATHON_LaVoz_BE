package com.LaVoz.LaVoz.web.apiResponse;

public enum StatusCode {

    COMMON("COMMON"),
    USER("USER"),
    STATE("STATE"),
    ORGANIZATION("ORGANIZATION"),
    NOTE("NOTE"),
    ;

    private final String prefix;

    StatusCode(String prefix){
        this.prefix = prefix;
    }

    public String getCode(int codeNumber){
        return this.prefix + codeNumber;
    }
}
