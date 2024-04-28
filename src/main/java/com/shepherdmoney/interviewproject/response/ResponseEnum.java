package com.shepherdmoney.interviewproject.response;

public enum ResponseEnum {
    SUCCESS(200, "OK"),
    SYSTEM_ERROR(500, "Internal Server Error"),
    PARAM_EXCEPTION(400, "Bad Request"),
    CARD_NOT_FOUND(404, "There is no such credit card related to the given user_id/credit card number"),
    USER_NOT_FOUND(404, "There is no such user");

    private final int code;
    private final String message;

    ResponseEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
