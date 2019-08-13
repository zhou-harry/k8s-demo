package com.harry.demo.jco;

/**
 * @author zhouhong
 * @version 1.0
 * @title: CustomJcoException
 * @description: TODO
 * @date 2019/8/7 10:46
 */
public class CustomJcoException extends RuntimeException {

    public CustomJcoException(Throwable cause) {
        super(cause);
    }

    public CustomJcoException(String message) {
        super(message);
    }
}
