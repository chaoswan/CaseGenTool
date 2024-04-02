package com.spin.cgt;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.vfs.VirtualFileAdapter;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.spin.cgt.listener.GoFileLstener;
import org.jetbrains.annotations.NotNull;

public class CgtStartupActivity implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        VirtualFileManager.getInstance().addVirtualFileListener(new GoFileLstener());
    }
}
