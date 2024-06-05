package com.atakmap.android.DarkMode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.DarkMode.plugin.R;
import com.atakmap.android.dropdown.DropDown.OnStateListener;
import com.atakmap.android.dropdown.DropDownReceiver;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapView;
import com.atakmap.coremap.log.Log;

import java.util.prefs.Preferences;


public class DarkModeDropDownReceiver extends DropDownReceiver implements OnStateListener {


    private static final String TAG = "DarkModeDropDownRcvr";
    public static final String SHOW_LAYOUT = "com.atakmap.android.DarkMode.SHOW_LAYOUT";

    public static final String PREF_MAP = "darkmodeVisibilityMap";
    public static final String PREF_INT = "darkmodeVisibilityInterface";
    public static final String PREF_ICONS = "darkmodeVisibilityIcons";

    public static final String PREF_BRIGHTNESS = "darkmodeScreenBrightness";
    public static final String PREF_ENABLED = "darkmodeEnabled";

    private final View _view;

    private SeekBar sliderMap;
    private SeekBar sliderInterface;
    private SeekBar sliderIcons ;

    private SeekBar sliderBrightness ;
    private Switch onOffSwitch ;

    private int prevBrightness = -1;

    private ContentResolver cResolver;

    protected DarkModeDropDownReceiver(MapView mapView, Context context) {
        super(mapView);
        Log.d(TAG, "DarkModeDropDownReceiver ctr");
        _view = PluginLayoutInflater.inflate(context,
                R.layout.dm_layout, null);

        cResolver = mapView.getContext().getContentResolver();

        sliderMap = _view.findViewById(R.id.slider_map_visibility);
        sliderInterface = _view.findViewById(R.id.slider_interface_visibility);
        sliderIcons = _view.findViewById(R.id.slider_icons_visibility);
        sliderBrightness = _view.findViewById(R.id.slider_phone_brightness);
        onOffSwitch = _view.findViewById(R.id.switch_on_off);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mapView.getContext());

        sliderMap.setProgress(prefs.getInt(PREF_MAP, 255));
        sliderInterface.setProgress(prefs.getInt(PREF_INT, 255));
        sliderIcons.setProgress(prefs.getInt(PREF_ICONS, 255));
        onOffSwitch.setChecked(prefs.getBoolean(PREF_ENABLED, false));

        handleSwitch(onOffSwitch.isChecked());

        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.edit().putBoolean(PREF_ENABLED, isChecked).apply();
                handleSwitch(isChecked);
            }
        });

        sliderIcons.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
           @Override
           public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
               prefs.edit().putInt(PREF_ICONS, progress).apply();
               if (!onOffSwitch.isChecked()) return;
               setIconsVisibility(progress);
           }

           @Override
           public void onStartTrackingTouch(SeekBar seekBar) {

           }

           @Override
           public void onStopTrackingTouch(SeekBar seekBar) {

           }
       });

        sliderInterface.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                prefs.edit().putInt(PREF_INT, progress).apply();
                if (!onOffSwitch.isChecked()) return;
                setInterfaceVisibility(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sliderMap.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                prefs.edit().putInt(PREF_MAP, progress).apply();
                if (!onOffSwitch.isChecked()) return;
                setMapVisibility(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sliderBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                prefs.edit().putInt(PREF_BRIGHTNESS, progress).apply();
                if (!onOffSwitch.isChecked()) return;
                setBrightness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void toggle() {
        Toast.makeText(MapView.getMapView().getContext(), "Dark Mode Toggled", Toast.LENGTH_SHORT).show();
        onOffSwitch.setChecked(!onOffSwitch.isChecked());
    }


    private boolean checkPermissions() {
        boolean canWriteSettings = Settings.System.canWrite(MapView.getMapView().getContext());
        if (!canWriteSettings) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
                    MapView.getMapView().getContext());
            alertBuilder.setTitle("Permission required");
            alertBuilder.setMessage("Please allow the permission for ATAK on the next screen.");

            final AlertDialog alertDialog = alertBuilder.create();
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    MapView.getMapView().getContext().startActivity(intent);
                }
            });
            alertDialog.setCancelable(false);
            alertDialog.show();
            return false;
        } else {
            return true;
        }
    }

    private void setBrightness(int value) {
        try {
            Settings.System.putInt(
                    cResolver,
                    Settings.System.SCREEN_BRIGHTNESS,
                    value
            );
        } catch (Exception e) {
            Log.e("Error", "Cannot restore system brightness");
        }
    }

    private void setSystemBarColor(Activity activity, boolean isGray) {
        View decorView = activity.getWindow().getDecorView();
        if (isGray) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        } else {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    public void cleanup() {
        handleSwitch(false);
    }

     void handleSwitch(boolean isChecked) {

        if (!checkPermissions()) {
            if (isChecked) onOffSwitch.setChecked(false);
            return;
        }

        if (isChecked) {

            setIconsVisibility(sliderIcons.getProgress());
            setMapVisibility(sliderMap.getProgress());
            setInterfaceVisibility(sliderInterface.getProgress());

            try {

                setSystemBarColor((Activity) MapView.getMapView().getContext(), true);

                Settings.System.putInt(
                        cResolver,
                        Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
                );
                prevBrightness = Settings.System.getInt(
                        cResolver, Settings.System.SCREEN_BRIGHTNESS
                );

                setBrightness(sliderBrightness.getProgress());

            } catch (Settings.SettingNotFoundException e) {
                Log.e("Error", "Cannot access system brightness");
            }
        } else {
            MapView.getMapView().getRootView().setForeground(null);
            MapView.getMapView().getGLSurface().setForeground(null);
            setIconsVisibility(255);
            setSystemBarColor((Activity) MapView.getMapView().getContext(), false);
            if (prevBrightness != -1) {
                setBrightness(prevBrightness);
            }
        }
    }

    private void setMapVisibility(int progress) {
        ColorDrawable blackDrawable = new ColorDrawable(0xFF000000);
        blackDrawable.setAlpha(255-progress);
        MapView.getMapView().getGLSurface().setForeground(blackDrawable);
    }

    private void setInterfaceVisibility(int progress) {
        ColorDrawable blackDrawable = new ColorDrawable(0xFF000000);
        blackDrawable.setAlpha(255-progress);
        MapView.getMapView().getRootView().setForeground(blackDrawable);
    }

    private void setIconsVisibility(int progress) {
        String hexValue = String.format("%02X", progress);
        String colorText = "#" +hexValue + hexValue + hexValue;
        PreferenceManager.getDefaultSharedPreferences(MapView.getMapView().getContext())
                .edit().putString("actionbar_icon_color_key", colorText).apply();
    }

    @Override
    protected void disposeImpl() {
        setBrightness(prevBrightness);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Log.d(TAG, "onReceive: " +action);
        if (action == null)
            return;

        // Show drop-down
        switch (action) {
            case SHOW_LAYOUT:
                showDropDown(_view, 0.4, FULL_HEIGHT,
                        0.5, HALF_HEIGHT, false, this);
                //setAssociationKey("SARToolkitPreferences");
        }
    }

    @Override
    public void onDropDownSelectionRemoved() {

    }

    @Override
    public void onDropDownClose() {

    }

    @Override
    public void onDropDownSizeChanged(double v, double v1) {

    }

    @Override
    public void onDropDownVisible(boolean b) {

    }

}
