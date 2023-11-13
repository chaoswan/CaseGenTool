package com.spin.cgt.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.spin.cgt.cmd.Cmd;
import com.spin.cgt.cmd.CmdClient;
import com.spin.cgt.cmd.CmdResult;
import com.spin.cgt.constant.Constant;
import com.spin.cgt.tool.FileTool;
import com.spin.cgt.tool.GotestRunTool;
import org.jetbrains.annotations.NotNull;

public class RunCaseAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        VirtualFile file = FileTool.getFile(e);
        String path = file.getPath();
        if (path.contains("cases") && (file.isDirectory() || path.endsWith(".json"))) {
            runCase(e, path);
        } else {
            Messages.showErrorDialog("路径错误", "执行失败");
        }
    }

    private void runCase(AnActionEvent e, String path) {
        Cmd<String> stringCmd = new Cmd<>(Constant.CMD_TYPE_RUN_CASE, path);
        if (CmdClient.checkServer(FileTool.getProject(e).getName()) == 0) {
            CmdResult<String> cmdResult = new CmdResult<>("");
            CmdClient.Cmd(stringCmd, cmdResult);
            if (cmdResult.isSuccess()) {
                Messages.showErrorDialog(cmdResult.getData(), "执行成功");
            } else {
                Messages.showErrorDialog("执行异常", "执行失败");
            }
        }else {
            GotestRunTool.runGoTest(e, "TestCmd", stringCmd);
        }
    }

}
