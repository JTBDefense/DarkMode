package com.atakmap.android.DarkMode.plugin;


import com.atak.plugins.impl.AbstractPlugin;
import gov.tak.api.plugin.IServiceController;
import com.atak.plugins.impl.PluginContextProvider;
import com.atakmap.android.DarkMode.DarkModeComponent;
import com.atakmap.android.DarkMode.DarkModeTool;

public class PluginTemplateLifecycle extends AbstractPlugin {

   public PluginTemplateLifecycle(IServiceController serviceController) {
        super(serviceController, new DarkModeTool(serviceController.getService(PluginContextProvider.class).getPluginContext()), new DarkModeComponent());
        PluginNativeLoader.init(serviceController.getService(PluginContextProvider.class).getPluginContext());
    }
}

