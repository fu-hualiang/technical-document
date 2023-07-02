package com.example.nestle.controller;

import com.example.nestle.utils.MyException;
import com.example.nestle.utils.result.Result;
import com.example.nestle.utils.result.ResultUtil;
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
