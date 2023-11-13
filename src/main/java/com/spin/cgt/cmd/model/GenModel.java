package com.spin.cgt.cmd.model;

import java.util.Arrays;

public class GenModel {
    public String region;
    public String env;
    public String type;
    public String method;
    public String suffix;
    public String[] request;
    public String dir;

    @Override
    public String toString() {
        return "GenModel{" +
                "region='" + region + '\'' +
                ", env='" + env + '\'' +
                ", type='" + type + '\'' +
                ", method='" + method + '\'' +
                ", suffix='" + suffix + '\'' +
                ", request=" + Arrays.toString(request) +
                ", dir='" + dir + '\'' +
                '}';
    }
}