package com.spin.cgt.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class GenPreAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        System.out.println(e.getPlace());


//        Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        Project project = ProjectManager.getInstance().getOpenProjects()[0];
        System.out.println(project.getBasePath());
    }

    public void update(AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        if (e.getPlace().equals(ActionPlaces.MAIN_MENU)) {
            presentation.setText("My Menu item name");
        } else if (e.getPlace().equals(ActionPlaces.MAIN_TOOLBAR)) {
            presentation.setText("My Toolbar item name");
        }
    }
}
