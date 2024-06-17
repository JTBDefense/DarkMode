package com.atakmap.android.DarkMode;

import static com.atakmap.android.DarkMode.DarkModeDropDownReceiver.SHOW_LAYOUT;

import android.content.Context;

import com.atak.plugins.impl.AbstractPluginTool;
import com.atakmap.android.DarkMode.plugin.R;

public class DarkModeTool extends AbstractPluginTool {

    public DarkModeTool(final Context context) {
        super(context, context.getString(R.string.app_name), context.getString(R.string.app_name), context.getResources().getDrawable(R.drawable.ic_jtb), SHOW_LAYOUT);
    }
}