package com.spin.cgt.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Toggleable;
import com.intellij.openapi.util.Key;
import com.spin.cgt.setting.CgtPluginSettings;
import org.jetbrains.annotations.NotNull;

public class ToggleDebugAction extends AnAction implements Toggleable {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        CgtPluginSettings.setEnableDebug(!CgtPluginSettings.enableDebug());
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        // 设置勾选状态
        e.getPresentation().putClientProperty(new Key<>(Toggleable.SELECTED_PROPERTY), CgtPluginSettings.enableDebug());
    }
}
