package com.LaVoz.LaVoz.web.apiResponse;

public enum StatusCode {

    COMMON("COMMON"),
    USER("USER"),
    STATE("STATE"),
    ORGANIZATION("ORGANIZATION"),
    NOTE("NOTE"),
    ISSUE("ISSUE"),
    COMMENT("COMMENT"),
    BOARD("BOARD"),
    BOARDCOMMENT("BOARDCOMMENT"),
    ;

    private final String prefix;

    StatusCode(String prefix){
        this.prefix = prefix;
    }

    public String getCode(int codeNumber){
        return this.prefix + codeNumber;
    }
}
