package com.spin.cgt.tool;

import com.goide.execution.GoBuildingRunConfiguration;
import com.goide.execution.testing.GoTestRunConfiguration;
import com.goide.execution.testing.GoTestRunConfigurationType;
import com.goide.execution.testing.frameworks.gotest.GotestFramework;
import com.goide.psi.GoFile;
import com.intellij.execution.*;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.spin.cgt.cmd.Cmd;
import com.spin.cgt.constant.Constant;
import org.jetbrains.annotations.NotNull;

public class GotestRunTool {
    public static void runGoTest(@NotNull AnActionEvent e, @NotNull String methodName, Cmd cmd) {
        Project project = FileTool.getProject(e);
        VirtualFile projectDir = FileTool.getProjectDir(project);
        VirtualFile entryFile = projectDir.findFileByRelativePath(Constant.ENTRY_FILE);
        if (entryFile == null) {
            Messages.showErrorDialog(Constant.ENTRY_FILE + " 文件不存在", "启动失败");
            return;
        }
        PsiManager psiManager = PsiManager.getInstance(project);
        PsiFile psiFile = psiManager.findFile(entryFile);
        if (psiFile instanceof GoFile) {
            runGoTest((GoFile) psiFile, methodName, "'" + cmd.toString().replace("'", "\\'") + "'");
        } else {
            Messages.showErrorDialog(Constant.ENTRY_FILE + " 文件异常", "启动失败");
        }
    }

    public static void runGoTest(@NotNull GoFile goFile, @NotNull String methodName, String carg) {
        try {
            Project project = goFile.getProject();
            VirtualFile virtualFile = goFile.getVirtualFile();

            ConfigurationFactory configurationFactory = GoTestRunConfigurationType.getInstance().getConfigurationFactories()[0];
            GoTestRunConfiguration runConfiguration = new GoTestRunConfiguration(project, "[CaseGenTool]" + methodName, configurationFactory.getType());
            runConfiguration.setTestFramework(GotestFramework.INSTANCE);
            runConfiguration.setKind(GoBuildingRunConfiguration.Kind.PACKAGE);
            runConfiguration.setPackage(FileTool.getPackage(goFile, FileTool.getProjectDir(project)));
            runConfiguration.setPattern("^\\Q" + methodName + "\\E$");
            runConfiguration.setWorkingDirectory(virtualFile.getParent().getPath());
            runConfiguration.setGoToolParams("-gcflags=all=-l");
            runConfiguration.setModule(ModuleUtilCore.findModuleForFile(virtualFile, project));
            StringBuilder param = new StringBuilder().append("-carg ").append(carg);
            runConfiguration.setParams(param.toString());


            Executor executor = DefaultRunExecutor.getRunExecutorInstance();
            RunnerAndConfigurationSettings configurationSettings = RunManager.getInstance(project).createConfiguration(runConfiguration, configurationFactory);
            ExecutionEnvironment environment = ExecutionEnvironmentBuilder.create(executor, configurationSettings).build();

            ExecutionManager.getInstance(project).restartRunProfile(environment);
//            RunContentManager.getInstance(project).showRunContent(executor, environment.getContentToReuse());

            // 注册项目关闭监听器，确保资源正确释放
            ProjectManagerListener projectManagerListener = new ProjectManagerListener() {
                @Override
                public void projectClosing(@NotNull Project project) {
                    Disposer.dispose(environment.getContentToReuse());
                }
            };
            ProjectManager.getInstance().addProjectManagerListener(project, projectManagerListener);

            LogTool.LOGGER.info("Running test started. [CaseGenTool]" + methodName + " : " + carg);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
