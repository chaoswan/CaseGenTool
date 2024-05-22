package com.spin.cgt.constant;

public interface Constant {
    String ENTRY_FILE = "test/auto_test/gen_case_test.go";
    String TEST_ROOT_DIR = "test/auto_test/";
    String CASE_DIR = "test/auto_test/cases/";

    String CMD_SERVER_ADDR = "localhost";
    int CMD_SERVER_PORT = 12345;

    String CMD_TYPE_STARTUP = "startup";
    String CMD_TYPE_STOP = "stop";
    String CMD_TYPE_GET_SERVER_INFO = "getServerInfo";
    String CMD_TYPE_GET_METHOD_INFO = "getMethodInfo";
    String CMD_TYPE_GEN_CASE = "genCase";
    String CMD_TYPE_RUN_CASE = "runCase";
    String CMD_TYPE_RE_GEN_CASE = "reGenCase";
    String CMD_TYPE_RUN_METHOD = "runMethod";
    String CMD_TYPE_SECOND_GEN_CASE = "secondGenCase";
}
