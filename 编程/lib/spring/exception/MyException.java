package com.example.nestle.utils;

/**
 * 自定義異常
 * @author fuhualiang
 * @author Varian
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