package com.spin.cgt.cmd;

import com.spin.cgt.util.JsonUtil;

public class CmdResult<T extends Object> {
    private boolean success;
    private T data;

    public CmdResult(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setStringData(String dataText, Class<T> clazz) {
        setData(JsonUtil.fromJson(dataText, clazz));
    }
}