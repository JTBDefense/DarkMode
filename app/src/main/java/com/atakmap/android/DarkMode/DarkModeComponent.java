
package com.atakmap.android.DarkMode;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.atakmap.android.DarkMode.plugin.DarkModePreferenceFragment;
import com.atakmap.android.DarkMode.plugin.R;
import com.atakmap.android.DarkMode.ui.ToggleIconWidget;
import com.atakmap.android.dropdown.DropDownMapComponent;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapView;
import com.atakmap.app.SettingsActivity;
import com.atakmap.app.preferences.ToolsPreferenceFragment;


public class DarkModeComponent extends DropDownMapComponent {

    private static final String TAG = "DarkModeComponent";

    public static final String OPEN_PREFERENCES_ACTION = "com.atakmap.android.DarkMode.plugin.openPreferences";

    private DarkModeDropDownReceiver dropDown;

    private BroadcastReceiver openPreferencesReceiver;

    SharedPreferences sharedPref;

    private ToggleIconWidget iconWidget;


    private DarkModeSwitchManager switchManager;


    @Override
    public void onResume(Context context, MapView view) {
        super.onResume(context, view);
   
    }

    public void onCreate(final Context context, Intent intent, final MapView view) {
        context.setTheme(R.style.ATAKPluginTheme);
        DarkModePreferenceFragment preferencesFragment = new DarkModePreferenceFragment(context);
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(view.getContext());

        Log.d(TAG, "Instantiating...");
        dropDown = new DarkModeDropDownReceiver(view, context);
        iconWidget = new ToggleIconWidget(context, view, dropDown);

        switchManager = DarkModeSwitchManager.getInstance(context, view, dropDown);


        view.addOnKeyListener(switchManager);

        AtakBroadcast.DocumentedIntentFilter ddFilter = new AtakBroadcast.DocumentedIntentFilter();
        ddFilter.addAction(DarkModeDropDownReceiver.SHOW_LAYOUT,
                "Show the Dark Mode drop-down");
        this.registerDropDownReceiver(dropDown, ddFilter);
        Log.d(TAG, "Registered...");

        ToolsPreferenceFragment
                .register(
                        new ToolsPreferenceFragment.ToolPreference(
                                "Dark Mode Plugin Preferences",
                                "Preferences for Dark Mode Plugin",
                                "DarkModePreferences",
                                context.getResources().getDrawable(R.drawable.ic_jtb, null),
                                preferencesFragment));

        openPreferencesReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive in openPreferencesReceiver, handling: " + intent.getAction());

                Intent prefIntent = new Intent(MapView.getMapView().getContext(),
                        SettingsActivity.class);
                prefIntent.putExtra("toolkey", "DarkModePreferences");

                ((Activity) MapView.getMapView().getContext()).startActivityForResult(prefIntent, 0);
            }
        };
        AtakBroadcast.getInstance().registerReceiver(openPreferencesReceiver, new AtakBroadcast.DocumentedIntentFilter(OPEN_PREFERENCES_ACTION));


    }





    @Override
    protected void onDestroyImpl(Context context, MapView view) {
        Log.d(TAG, "onDestroyImpl");
        dropDown.cleanup();
        iconWidget.destroy();
        AtakBroadcast.getInstance().unregisterReceiver(openPreferencesReceiver);


    }


}