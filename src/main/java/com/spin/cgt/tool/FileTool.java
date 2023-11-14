package com.spin.cgt.tool;

import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.spin.cgt.excetion.CgtException;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;

public class FileTool {
    private static String TPL_ROOT_DIR = "tpl/";

    public static VirtualFile getFile(@NotNull AnActionEvent e) {
        return CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
    }

    public static Project getProject(@NotNull AnActionEvent e) {
        VirtualFile file = getFile(e);
        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        for (Project project : projects) {
            if (file.getPath().startsWith(project.getBasePath())) {
                return project;
            }
        }
        return null;
    }

    public static VirtualFile getProjectDir(@NotNull Project project) {
        return LocalFileSystem.getInstance().findFileByPath(project.getBasePath());
    }

    public static PsiFile generateFileWithTpl(@NotNull String tplName, @NotNull Map<String, String> placeholder, @NotNull Project project, @NotNull String fileName) {
        ClassLoader classLoader = FileTool.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(TPL_ROOT_DIR + tplName);

        if (inputStream != null) {
            try (Scanner scanner = new Scanner(inputStream)) {
                StringBuilder sb = new StringBuilder();
                // 读取文件内容
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    for (Map.Entry<String, String> entry : placeholder.entrySet()) {
                        line = line.replace("${{" + entry.getKey() + "}}", entry.getValue());
                    }
                    sb.append(line).append("\n");
                }

                PsiFile psiFile = PsiFileFactory.getInstance(project).createFileFromText(fileName, Language.findLanguageByID("go"), sb.toString());
                CodeStyleManager.getInstance(project).reformat(psiFile);

                return psiFile;
            }
        } else {
            throw new CgtException("tpl not exist!");
        }
    }

    public static void addFileWithTpl(@NotNull String tplName, @NotNull Map<String, String> placeholder, @NotNull Project project, @NotNull VirtualFile dir, @NotNull String fileName) {
        PsiDirectory psiDir = PsiManager.getInstance(project).findDirectory(dir);
        PsiFile psiFile = generateFileWithTpl(tplName, placeholder, project, fileName);
        psiDir.add(psiFile);
    }

    public static String getProjectPackageName(@NotNull VirtualFile projectDir) {
        String projectPackageName = "";
        VirtualFile modFile = projectDir.findFileByRelativePath("./go.mod");
        if (modFile == null) {
            throw new CgtException("go.mod not exist!");
        }
        Document modDocument = FileDocumentManager.getInstance().getDocument(modFile);
        String modText = modDocument.getText();
        for (String line : modText.split("\n")) {
            if (line.trim().startsWith("module")) {
                projectPackageName = line.split("\\s+")[1];
                break;
            }
        }
        return projectPackageName;
    }

    public static String getPackage(@NotNull PsiFile psiFile, @NotNull VirtualFile projectDir) {
        String projectPackageName = FileTool.getProjectPackageName(projectDir);

        String path = VfsUtilCore.getRelativePath(psiFile.getContainingDirectory().getVirtualFile(), projectDir);
        return projectPackageName + "/" + path;
    }
}
