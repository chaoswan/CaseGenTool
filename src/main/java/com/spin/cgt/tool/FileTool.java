package com.spin.cgt.tool;

import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.Computable;
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

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class FileTool {
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
    InputStream inputStream = TplZipTool.unzipTpl(tplName);
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

        PsiFile psiFile = ApplicationManager.getApplication().runWriteAction((Computable<PsiFile>) () ->
            PsiFileFactory.getInstance(project).createFileFromText(fileName, Language.findLanguageByID("go"), sb.toString())
        );
        WriteCommandAction.runWriteCommandAction(project, () -> {
          CodeStyleManager.getInstance(project).reformat(psiFile);
        });
        return psiFile;
      }
    } else {
      throw new CgtException("tpl not exist!");
    }
  }

  public static void addFileWithTpl(@NotNull String tplName, @NotNull Map<String, String> placeholder, @NotNull Project project, @NotNull VirtualFile dir, @NotNull String fileName) {
    PsiDirectory psiDir = PsiManager.getInstance(project).findDirectory(dir);
    PsiFile psiFile = generateFileWithTpl(tplName, placeholder, project, fileName);
    ApplicationManager.getApplication().runWriteAction(() -> {
      psiDir.add(psiFile);
    });
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

  public static void gotoCaseFile(AnActionEvent e, String path) {
    Project project = getProject(e);
    VirtualFile file = LocalFileSystem.getInstance().findFileByPath(path);
    if (file == null || !file.isValid()) {
      return;
    }
    OpenFileDescriptor descriptor = new OpenFileDescriptor(project, file);
    FileEditorManager.getInstance(project).openEditor(descriptor, true);
  }

  public static void refreshFile(String path) {
    List<File> filesToRefresh = getAllFiles(new File(path));
    LocalFileSystem.getInstance().refreshIoFiles(filesToRefresh);
  }

  private static List<File> getAllFiles(File targetFile) {
    List<File> files = new ArrayList<>();
    if (!targetFile.exists()) {
      return files;
    }

    if (targetFile.isDirectory()) {
      File[] fileList = targetFile.listFiles();
      if (fileList != null) {
        for (File file : fileList) {
          files.addAll(getAllFiles(file));
        }
      }
    } else {
      files.add(targetFile);
    }
    return files;
  }
}
