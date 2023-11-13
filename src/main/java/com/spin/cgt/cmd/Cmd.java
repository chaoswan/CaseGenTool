package com.spin.cgt.cmd;

import com.spin.cgt.util.JsonUtil;

public class Cmd<T> {
    public String type;
    public T data;

    public Cmd(String type, T data) {
        this.type = type;
        this.data = data;
    }

    public String toString() {
        return JsonUtil.toJson(this);
    }
}