package com.spin.cgt.listener;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFileAdapter;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.spin.cgt.cmd.Cmd;
import com.spin.cgt.cmd.CmdClient;
import com.spin.cgt.cmd.CmdResult;
import com.spin.cgt.constant.Constant;
import com.spin.cgt.setting.CgtPluginSettings;
import com.spin.cgt.tool.GotestRunTool;

import java.util.Timer;
import java.util.TimerTask;

public class GoFileLstener extends VirtualFileAdapter {
    private static Timer dialogTimer;

    @Override
    public void contentsChanged(VirtualFileEvent event) {
        if (CgtPluginSettings.enableCodeMonitor() && isGoFile(event)) {
            showRestartCmdServerDialog(event);
        }
    }

    @Override
    public void fileCreated(VirtualFileEvent event) {
        if (CgtPluginSettings.enableCodeMonitor() && isGoFile(event)) {
            showRestartCmdServerDialog(event);
        }
    }

    @Override
    public void fileDeleted(VirtualFileEvent event) {
        if (CgtPluginSettings.enableCodeMonitor() && isGoFile(event)) {
            showRestartCmdServerDialog(event);
        }
    }

    private boolean isGoFile(VirtualFileEvent event) {
        String fileName = event.getFileName();
        return fileName.endsWith(".go") && !fileName.endsWith("_test.go");
    }

    private void showRestartCmdServerDialog(VirtualFileEvent event) {
        if (dialogTimer != null) {
            return;
        }
        dialogTimer = new Timer();
        dialogTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                dialogTimer.cancel();
                dialogTimer = null;
                Project project = ProjectUtil.guessProjectForFile(event.getFile());
                if (CmdClient.checkServer(project.getName()) == 0) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        int result = Messages.showOkCancelDialog("代码发生变更，是否重新预启动单测生成工具？", "检测代码变更", Messages.getQuestionIcon());
                        if (result == Messages.OK) {
                            // 重启 预启动服务
                            CmdClient.Cmd(new Cmd(Constant.CMD_TYPE_STOP, ""), new CmdResult<>(""));
                            GotestRunTool.runGoTest(project, "TestBoot", new Cmd<>(Constant.CMD_TYPE_STARTUP, project.getName()));
                        }
                    });
                }
            }
        }, 5000); //5秒延迟

    }
}
