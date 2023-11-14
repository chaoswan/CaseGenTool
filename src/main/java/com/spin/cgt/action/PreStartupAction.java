package com.spin.cgt.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.spin.cgt.cmd.Cmd;
import com.spin.cgt.cmd.CmdClient;
import com.spin.cgt.cmd.CmdResult;
import com.spin.cgt.constant.Constant;
import com.spin.cgt.tool.FileTool;
import com.spin.cgt.tool.GotestRunTool;
import org.jetbrains.annotations.NotNull;

public class PreStartupAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = FileTool.getProject(e);
        String projectName = project.getName();

        int flag = CmdClient.checkServer(projectName);
        switch (flag) {
            case 0: //避免重复启动
                return;
            case 1:
                CmdClient.Cmd(new Cmd(Constant.CMD_TYPE_STOP, ""), new CmdResult<>(""));
                break;
        }

        GotestRunTool.runGoTest(e, "TestBoot", new Cmd<>(Constant.CMD_TYPE_STARTUP, projectName));
    }
}
