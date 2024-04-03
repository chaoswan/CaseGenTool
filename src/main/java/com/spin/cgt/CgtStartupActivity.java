package com.spin.cgt;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.messages.MessageBus;
import com.spin.cgt.listener.GoFileLstener;
import org.jetbrains.annotations.NotNull;

public class CgtStartupActivity implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        MessageBus messageBus = ApplicationManager.getApplication().getMessageBus();
        messageBus.connect().subscribe(VirtualFileManager.VFS_CHANGES, new GoFileLstener());
    }
}
