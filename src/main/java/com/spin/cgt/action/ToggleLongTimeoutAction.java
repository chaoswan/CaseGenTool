package com.spin.cgt.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Toggleable;
import com.intellij.openapi.util.Key;
import com.spin.cgt.setting.CgtPluginSettings;
import org.jetbrains.annotations.NotNull;

public class ToggleLongTimeoutAction extends AnAction implements Toggleable {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        CgtPluginSettings.setLongTimeout(!CgtPluginSettings.longTimeout());
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        // 设置勾选状态
        e.getPresentation().putClientProperty(new Key<>(Toggleable.SELECTED_PROPERTY), CgtPluginSettings.longTimeout());
    }
}
