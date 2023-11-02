package com.example.javawebtest.exception;

/**
 * 自定义异常
 */
public class MyException extends Exception {

    private Integer code;

    private String msg;

    public MyException() {
        super();
    }

    public MyException(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}