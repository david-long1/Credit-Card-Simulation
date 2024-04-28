package com.shepherdmoney.interviewproject.exception;

import com.shepherdmoney.interviewproject.response.ResponseEnum;

public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(ResponseEnum responseEnum) {
        super(responseEnum.getMessage());
        this.code = responseEnum.getCode();
    }

    public BusinessException(ResponseEnum responseEnum, Throwable cause) {
        super(responseEnum.getMessage(), cause);
        this.code = responseEnum.getCode();
    }

    public int getCode() {
        return code;
    }
}

