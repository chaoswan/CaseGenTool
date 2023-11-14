package com.spin.cgt.tool;

import com.goide.psi.GoFunctionDeclaration;
import com.goide.psi.GoMethodDeclaration;
import com.goide.psi.GoReceiver;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.spin.cgt.cmd.Cmd;
import com.spin.cgt.cmd.CmdClient;
import com.spin.cgt.cmd.CmdResult;
import com.spin.cgt.cmd.model.GenModel;
import com.spin.cgt.cmd.model.MethodInfo;
import com.spin.cgt.cmd.model.MethodPath;
import com.spin.cgt.constant.Constant;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class MethodModelTool {
    public static GenModel getDefaultModel(@NotNull AnActionEvent e) {
        Project project = FileTool.getProject(e);
        VirtualFile projectDir = FileTool.getProjectDir(project);

        if (CmdClient.checkServer(project.getName()) != 0) {
            return null;
        }

        GenModel model = new GenModel();


        DataContext dataContext;
        try {
            dataContext = DataManager.getInstance().getDataContextFromFocusAsync().blockingGet(3000);
        } catch (TimeoutException | ExecutionException ex) {
            ex.printStackTrace();
            return null;
        }
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
        if (editor != null) {
            PsiFile psiFile = CommonDataKeys.PSI_FILE.getData(dataContext);

            if (psiFile != null) {
                // 获取当前编辑器中的光标位置
                int offset = editor.getCaretModel().getOffset();
                // 获取光标位置对应的 PSI 元素
                PsiElement psiElement = psiFile.findElementAt(offset);

                if (psiElement != null) {
                    MethodPath methodPath = null;
                    if ("string".equals(psiElement.getNode().getElementType().toString())) {
                        methodPath = new MethodPath();
                        methodPath.stringText = psiElement.getText().replaceAll("^\"|\"$", "");
                    } else {
                        methodPath = new MethodPath();
                        String parentName = psiElement.getParent().getNode().getElementType().toString();
                        switch (parentName) {
                            case "FUNCTION_DECLARATION":
                                methodPath.packagePath = FileTool.getPackage(psiFile, projectDir);
                                methodPath.method = ((GoFunctionDeclaration) psiElement.getParent()).getIdentifier().getText();
                                break;
                            case "METHOD_DECLARATION":
                                methodPath.packagePath = FileTool.getPackage(psiFile, projectDir);
                                GoMethodDeclaration methodPsi = (GoMethodDeclaration) psiElement.getParent();
                                methodPath.method = methodPsi.getIdentifier().getText();
                                GoReceiver receiverPsi = methodPsi.getReceiver();
                                methodPath.receiver = receiverPsi.getType().getText();
                                if (methodPath.receiver.startsWith("*")) {
                                    methodPath.receiver = methodPath.receiver.substring(1);
                                }
                                break;
                        }
                    }

                    if (methodPath != null) {
                        CmdResult<MethodInfo> cmdResult = new CmdResult<>(new MethodInfo());
                        CmdClient.Cmd(new Cmd<>(Constant.CMD_TYPE_GET_METHOD_INFO, methodPath), cmdResult);

                        if (cmdResult.isSuccess()) {
                            MethodInfo data = cmdResult.getData();
                            model.env = data.env;
                            model.type = data.type;
                            model.method = data.method;
                            model.request = data.request;
                        }
                    }
                }
            }
        }
        return model;
    }
}
