package com.spin.cgt.cmd;

import com.spin.cgt.constant.Constant;
import com.spin.cgt.tool.LogTool;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class CmdClient {
    public static void Cmd(Cmd cmd, CmdResult cmdResult) {
        try (Socket socket = new Socket(Constant.CMD_SERVER_ADDR, Constant.CMD_SERVER_PORT)) {
            socket.setSoTimeout(10000);
            // 发送请求
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(cmd);

            // 接收响应
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            cmdResult.setSuccess(true);
            cmdResult.setStringData(result.toString(), cmdResult.getData().getClass());
            LogTool.LOGGER.info("Server response: " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int checkServer(@NotNull String serverName) {
        try (Socket socket = new Socket(Constant.CMD_SERVER_ADDR, Constant.CMD_SERVER_PORT)) {
            socket.setSoTimeout(2000);
            // 发送请求
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(new Cmd<>(Constant.CMD_TYPE_GET_SERVER_INFO, serverName));
            out.flush();

            // 接收响应
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = in.readLine();
            LogTool.LOGGER.info("Server response: " + response);
            if (serverName.equals(response.replaceAll("^\"|\"$", ""))) {
                return 0;//已存在相同服务
            }
            return 1;//存在其他服务
        } catch (IOException e) {
            e.printStackTrace();
            return 2;//服务未启动
        }
    }
}
