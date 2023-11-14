package com.spin.cgt.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.spin.cgt.cmd.Cmd;
import com.spin.cgt.cmd.CmdClient;
import com.spin.cgt.cmd.CmdResult;
import com.spin.cgt.constant.Constant;
import com.spin.cgt.tool.FileTool;
import org.jetbrains.annotations.NotNull;

public class StopAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = FileTool.getProject(e);
        String projectName = project.getName();

        if (CmdClient.checkServer(projectName) > 1) {
            return;
        }

        Cmd<String> stringCmd = new Cmd<>(Constant.CMD_TYPE_STOP, projectName);
        CmdResult<String> cmdResult = new CmdResult<>("");
        CmdClient.Cmd(stringCmd, cmdResult);
        if (cmdResult.isSuccess()) {
            Messages.showErrorDialog(cmdResult.getData(), "执行成功");
        } else {
            Messages.showErrorDialog("执行异常", "执行失败");
        }
    }
}
