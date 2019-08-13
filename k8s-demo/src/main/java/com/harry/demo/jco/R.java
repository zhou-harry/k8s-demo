package com.harry.demo.jco;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author zhouhong
 * @version 1.0
 * @title: R
 * @description: TODO
 * @date 2019/8/7 10:39
 */
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int SUCCESS = 0;
    public static final int FAIL = 1;

    @Getter
    @Setter
    private String msg = "success";

    @Getter
    @Setter
    private int code = SUCCESS;

    @Getter
    @Setter
    private T data;

    public R() {
        super();
    }

    public R(T data) {
        super();
        this.data = data;
    }

    public R(T data, String msg) {
        super();
        this.code = FAIL;
        this.data = data;
        this.msg = msg;
    }

    public R(Throwable e) {
        super();
        this.msg = e.getMessage();
        this.code = FAIL;
    }

    public R(int code, String msg){
        super();
        this.code = code;
        this.msg = msg;
    }

    public R(int code, String msg, T data) {
        this.msg = msg;
        this.code = code;
        this.data = data;
    }

    @Override
    public String toString() {
        return "R{" +
                "msg='" + msg + '\'' +
                ", code=" + code +
                ", data=" + data +
                '}';
    }
}
