package com.spin.cgt.setting;

import com.intellij.ide.util.PropertiesComponent;

public class CgtPluginSettings {
    private static final String ENABLE_DEBUG_KEY = "Cgt.EnableDebug";
    private static final String ENABLE_CODE_MONITOR_KEY = "Cgt.EnableCodeMonitor";

    public static boolean enableDebug() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        return propertiesComponent.getBoolean(ENABLE_DEBUG_KEY, false);
    }

    public static void setEnableDebug(boolean enableDebug) {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        propertiesComponent.setValue(ENABLE_DEBUG_KEY, enableDebug, false);
    }

    public static boolean enableCodeMonitor() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        return propertiesComponent.getBoolean(ENABLE_CODE_MONITOR_KEY, true);
    }

    public static void setEnableCodeMonitor(boolean enableCodeMonitor) {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        propertiesComponent.setValue(ENABLE_CODE_MONITOR_KEY, enableCodeMonitor, true);
    }
}
