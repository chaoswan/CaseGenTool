package com.spin.cgt.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.spin.cgt.constant.Constant;
import com.spin.cgt.excetion.CgtException;
import com.spin.cgt.tool.FileTool;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class GenPreAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = FileTool.getProject(e);
        VirtualFile projectDir = FileTool.getProjectDir(project);

        try {
            VirtualFile test = createDir(projectDir, "test");
            VirtualFile autoRoot = createDir(test, "auto_test");
            VirtualFile pre = createDir(autoRoot, "pre");
            createDir(autoRoot, "cases");

            createPreFile(project, projectDir, pre);
            createEntryFile(project, projectDir, autoRoot);
        } catch (IOException ex) {
            throw new CgtException(ex);
        }

    }

    private VirtualFile createDir(VirtualFile dir, String childPath) throws IOException {
        VirtualFile child = dir.findChild(childPath);
        if (child == null) {
            child = dir.createChildDirectory(null, childPath);
        } else if (!child.isDirectory()) {
            throw new IOException("is not directory");
        }
        return child;
    }

    private List<String> GetAnonymousImports(PsiElement parent) {
        List<String> result = new LinkedList<>();
        for (PsiElement child : parent.getChildren()) {
            if ("IMPORT_DECLARATION".equals(child.getNode().getElementType().toString())) {
                String text = child.getText();
                text = text.replaceAll("import|\\(|\\)", "");
                text = text.replaceAll("\\n\\s+\"\\S+\"", "");
                text = text.replaceAll("\\n\\s+(\\w{2,}|[^_])s+\"\\S+\"", "");
                result.add(text);
            } else if ("FUNCTION_DECLARATION".equals(child.getNode().getElementType().toString())) {
                break;
            } else {
                result.addAll(GetAnonymousImports(child));
            }
        }
        return result;
    }

    private void createPreFile(@NotNull Project project, @NotNull VirtualFile projectDir, VirtualFile preDir) {
        VirtualFile mainFile = projectDir.findFileByRelativePath("./src/main.go");
        if (mainFile == null) {
            throw new CgtException("src/main.go not exist");
        }

        PsiFile psiFile = PsiManager.getInstance(project).findFile(mainFile);
        List<String> imports = GetAnonymousImports(psiFile);
        StringBuilder importStrB = new StringBuilder();
        for (String text : imports) {
            if ("\n".equals(text)) {
                importStrB.append(text);
            } else {
                importStrB.append("\t").append(text).append("\n");
            }
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("imports", importStrB.toString());
        map.put("preLoad", "\t// TODO\n\t// eg: task.RegisterTask()");

        VirtualFile preFile = projectDir.findFileByRelativePath("./test/auto_test/pre/pre.go");
        if (preFile == null) {
            FileTool.addFileWithTpl("pre.go.tpl", map, project, preDir, "pre.go");
        } else {
            updatePreFile(imports, preFile);
        }
    }

    private void updatePreFile(List<String> imports, VirtualFile preFile) {
        Document document = FileDocumentManager.getInstance().getDocument(preFile);
        String text = document.getText();
        StringBuilder builder = new StringBuilder();

        boolean deleteFlag = false;
        boolean filledFlag = false;
        for (String line : text.split("\n")) {
            if (deleteFlag && line.trim().startsWith("_")) {
                continue;
            } else if (deleteFlag && !filledFlag && !line.trim().startsWith("_") && line.trim().length() > 0) {
                deleteFlag = false;
                filledFlag = true;
                for (String iText : imports) {
                    builder.append(iText).append("\n");
                }
            }

            builder.append(line).append("\n");

            if (!filledFlag && line.matches("\\s*_\\s+\"\\S+/gunit-trace-replay\".*")) {
                deleteFlag = true;
            }
        }
        ApplicationManager.getApplication().runWriteAction(() -> {
            document.setText(builder.toString().replaceAll("\\n{2,}", "\n\n"));
        });
    }

    private void createEntryFile(@NotNull Project project, @NotNull VirtualFile projectDir, VirtualFile dir) {
        HashMap<String, String> map = new HashMap<>();

        String projectPackageName = "";
        projectPackageName = FileTool.getProjectPackageName(projectDir);
        String importText = new StringBuilder().append("_ \"").append(projectPackageName).append("/test/auto_test/pre\"").toString();

        map.put("import", importText);
        VirtualFile entryFile = projectDir.findFileByRelativePath(Constant.ENTRY_FILE);
        if (entryFile == null) {
            FileTool.addFileWithTpl("gen_case_test.go.tpl", map, project, dir, "gen_case_test.go");
        } else {
            Document entryDocument = FileDocumentManager.getInstance().getDocument(entryFile);
            PsiFile newFile = FileTool.generateFileWithTpl("gen_case_test.go.tpl", map, project, "gen_case_test.go");
            ApplicationManager.getApplication().runWriteAction(() -> {
                entryDocument.setText(newFile.getText());
            });
        }
    }
}
