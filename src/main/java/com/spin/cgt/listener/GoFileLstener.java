package com.spin.cgt.listener;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.spin.cgt.cmd.Cmd;
import com.spin.cgt.cmd.CmdClient;
import com.spin.cgt.cmd.CmdResult;
import com.spin.cgt.constant.Constant;
import com.spin.cgt.setting.CgtPluginSettings;
import com.spin.cgt.tool.GotestRunTool;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GoFileLstener implements BulkFileListener {
    private static Timer dialogTimer;

    @Override
    public void after(@NotNull List<? extends @NotNull VFileEvent> events) {
        if (CgtPluginSettings.enableCodeMonitor() && containsGoSrcFile(events)) {
            showRestartCmdServerDialog(events);
        }
    }

    private boolean containsGoSrcFile(List<? extends @NotNull VFileEvent> events) {
        for (VFileEvent event : events) {
            String filePath = event.getPath();
            if (filePath.endsWith(".go") && !filePath.endsWith("_test.go")) {
                return true;
            }
        }
        return false;
    }

    private void showRestartCmdServerDialog(List<? extends @NotNull VFileEvent> events) {
        if (dialogTimer != null) {
            return;
        }
        dialogTimer = new Timer();
        dialogTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                dialogTimer.cancel();
                dialogTimer = null;
                Project project = ProjectUtil.guessProjectForFile(events.get(0).getFile());
                if (CmdClient.checkServer(project.getName()) == 0) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        int result = Messages.showOkCancelDialog("代码发生变更，是否重新预启动单测生成工具？", "检测代码变更", "重新启动", "取消", Messages.getQuestionIcon());
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
