package com.spin.cgt.setting;

import com.intellij.ide.util.PropertiesComponent;

public class CgtPluginSettings {
  private static final String ENABLE_DEBUG_KEY = "Cgt.EnableDebug";
  private static final String ENABLE_CODE_MONITOR_KEY = "Cgt.EnableCodeMonitor";
  private static final String AUTO_CLOSE_KEY = "Cgt.AutoClose";
  private static final String LONG_TIMEOUT_KEY = "Cgt.LongTimeout";


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

  public static boolean autoClose() {
    PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
    return propertiesComponent.getBoolean(AUTO_CLOSE_KEY, true);
  }

  public static void setAutoClose(boolean enableAutoClose) {
    PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
    propertiesComponent.setValue(AUTO_CLOSE_KEY, enableAutoClose, true);
  }

  public static boolean longTimeout() {
    PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
    return propertiesComponent.getBoolean(LONG_TIMEOUT_KEY, false);
  }

  public static void setLongTimeout(boolean enableLongTimeout) {
    PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
    propertiesComponent.setValue(LONG_TIMEOUT_KEY, enableLongTimeout, false);
  }
}
