
package com.atakmap.android.DarkMode;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.atakmap.android.DarkMode.plugin.R;
import com.atakmap.android.DarkMode.ui.ToggleIconWidget;
import com.atakmap.android.dropdown.DropDownMapComponent;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapView;


public class DarkModeComponent extends DropDownMapComponent {

    private static final String TAG = "DarkModeComponent";

    private DarkModeDropDownReceiver dropDown;

    SharedPreferences sharedPref;

    private ToggleIconWidget iconWidget;


    @Override
    public void onResume(Context context, MapView view) {
        super.onResume(context, view);
   
    }

    public void onCreate(final Context context, Intent intent, final MapView view) {
        context.setTheme(R.style.ATAKPluginTheme);
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(view.getContext());

        Log.d(TAG, "Instantiating...");
        dropDown = new DarkModeDropDownReceiver(view, context);
        iconWidget = new ToggleIconWidget(context, view, dropDown);

        DarkModeSwitchManager switchManager = DarkModeSwitchManager.getInstance(dropDown);
        view.addOnKeyListener(switchManager);

        AtakBroadcast.DocumentedIntentFilter ddFilter = new AtakBroadcast.DocumentedIntentFilter();
        ddFilter.addAction(DarkModeDropDownReceiver.SHOW_LAYOUT,  "Show the Dark Mode drop-down");
        this.registerDropDownReceiver(dropDown, ddFilter);
        Log.d(TAG, "Registered...");
    }

    @Override
    protected void onDestroyImpl(Context context, MapView view) {
        Log.d(TAG, "onDestroyImpl");
        dropDown.cleanup();
        iconWidget.destroy();
    }


}