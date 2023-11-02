package com.spin.cgt.action;

import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import org.jetbrains.annotations.NotNull;

public class ReGenCaseAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println(e.getPlace());
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
