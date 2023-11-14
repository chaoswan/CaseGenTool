package com.spin.cgt.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.spin.cgt.cmd.Cmd;
import com.spin.cgt.cmd.CmdClient;
import com.spin.cgt.cmd.CmdResult;
import com.spin.cgt.cmd.model.GenModel;
import com.spin.cgt.constant.Constant;
import com.spin.cgt.tool.FileTool;
import com.spin.cgt.tool.GotestRunTool;
import com.spin.cgt.tool.MethodDialogTool;
import com.spin.cgt.tool.MethodModelTool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class RunMethodAction extends AnAction {

    public RunMethodAction() {
        super();
    }

    public RunMethodAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        MethodDialogTool.showDialog("运行方法", MethodModelTool.getDefaultModel(e), model -> runMethod(e, model));
    }

    public void runMethod(AnActionEvent e, GenModel model) {
        StringBuilder emptyFields = new StringBuilder();
        if (model.region == null || model.region.isEmpty()) {
            emptyFields.append("地区").append(",");
        }
        if (model.env == null || model.env.isEmpty()) {
            emptyFields.append("环境").append(",");
        }
        if (model.type == null || model.type.isEmpty()) {
            emptyFields.append("用例类型").append(",");
        }
        if (model.method == null || model.method.isEmpty()) {
            emptyFields.append("方法路径").append(",");
        }
        if (model.request[0] == null || model.request[0].isEmpty()) {
            emptyFields.append("请求参数").append(",");
        }

        if (emptyFields.length() > 0) {
            emptyFields.setLength(emptyFields.length() - 1);
            emptyFields.append(" 不能为空");
            Messages.showErrorDialog(emptyFields.toString(), "参数为空");
            return;
        }


        if (!model.dir.startsWith("/")) {//相对路径补充全
            model.dir = FileTool.getProject(e).getBasePath() + "/" + Constant.CASE_DIR + model.dir;
        }
        Cmd<GenModel> genModelCmd = new Cmd<>(Constant.CMD_TYPE_RUN_METHOD, model);
        if (CmdClient.checkServer(FileTool.getProject(e).getName()) == 0) {
            CmdResult<String> cmdResult = new CmdResult<>("");
            CmdClient.Cmd(genModelCmd, cmdResult);
            if (cmdResult.isSuccess()) {
                String data = cmdResult.getData();
                if (data.isEmpty()) {
                    Messages.showInfoMessage("执行成功", "执行成功");
                } else {
                    Messages.showErrorDialog(data, "执行失败");
                }
            } else {
                Messages.showErrorDialog("执行失败", "执行失败");
            }
        } else {
            GotestRunTool.runGoTest(e, "TestCmd", genModelCmd);
        }
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(e.getProject() != null);
    }
}

