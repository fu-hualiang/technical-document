package com.example.javawebtest.exception;

import com.example.javawebtest.utils.resultUtils.Result;
import com.example.javawebtest.utils.resultUtils.ResultUtil;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = MyException.class)
    @ResponseBody
    public Result<Object> exceptionHandler(MyException e){
        return ResultUtil.defineFail(e.getCode(),e.getMsg());
    }
}
