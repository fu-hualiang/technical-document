package com.elegrp.contract.util.result;

public class ResultUtils {

    public static <T> Result<T> define(Integer code, String msg, T data) {
        Result<T> result = new Result<>();
        return result.setCode(code).setMsg(msg).setData(data);
    }

    public static <T> Result<T> define(ResultEnum resultEnum, String msg, T data) {
        Result<T> result = new Result<>();
        return result.setCode(resultEnum).setMsg(msg).setData(data);
    }

    public static <T> Result<T> defineSuccess(Integer code, T data) {
        Result<T> result = new Result<>();
        return result.setCode(code).setData(data);
    }

    public static <T> Result<T> defineSuccess(ResultEnum resultEnum, T data) {
        Result<T> result = new Result<>();
        return result.setCode(resultEnum).setData(data);
    }

    public static <T> Result<T> defineFail(Integer code, String msg) {
        Result<T> result = new Result<>();
        return result.setCode(code).setMsg(msg);
    }

    public static <T> Result<T> defineFail(ResultEnum resultEnum, String msg) {
        Result<T> result = new Result<>();
        return result.setCode(resultEnum).setMsg(msg);
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        return result.setCode(ResultEnum.SUCCESS).setData(data);
    }

    public static <T> Result<T> fail(String msg) {
        Result<T> result = new Result<>();
        result.setCode(ResultEnum.FAIL).setMsg(msg);
        return result;
    }
}