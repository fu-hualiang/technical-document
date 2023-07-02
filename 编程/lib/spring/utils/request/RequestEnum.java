package com.example.nestle.utils.request;

/**
 * http请求类型
 *
 * @author fuhualiang
 * @author Varian
 */
public enum RequestEnum {
    GET(0),
    POST(1),
    PUT(2),
    DELETE(3);
    public final int method;

    RequestEnum(int method) {
        this.method = method;
    }
}
