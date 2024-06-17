package com.atakmap.android.DarkMode;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Toast;

import com.atakmap.android.maps.MapView;
import com.atakmap.coremap.log.Log;

public class DarkModeSwitchManager implements OnKeyListener {

    private static DarkModeSwitchManager _instance = null;

    private final String TAG = "DarkModeSwitchManager";
    private boolean volumeUpPressed = false;
    private boolean volumeDownPressed = false;
    private long lastVolumePressTime = 0;
    private static final long VOLUME_PRESS_DELAY = 500;
    private final DarkModeDropDownReceiver darkModeDropDownReceiver;

    private DarkModeSwitchManager(DarkModeDropDownReceiver receiver) {
        this.darkModeDropDownReceiver = receiver;
    }

    public static synchronized DarkModeSwitchManager getInstance(DarkModeDropDownReceiver receiver) {
        if (_instance == null) {
            _instance = new DarkModeSwitchManager(receiver);
        }
        return _instance;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        int action = event.getAction();

        if (action != KeyEvent.ACTION_DOWN)
            return false;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastVolumePressTime > VOLUME_PRESS_DELAY) {
            volumeUpPressed = false;
            volumeDownPressed = false;
        }

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            volumeUpPressed = true;
            lastVolumePressTime = currentTime;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            volumeDownPressed = true;
            lastVolumePressTime = currentTime;
        }

        if (volumeUpPressed && volumeDownPressed) {
            toggleDarkMode();
            volumeUpPressed = false;
            volumeDownPressed = false;
            return true;
        }

        return false;
    }

    private void toggleDarkMode() {
        darkModeDropDownReceiver.toggle();
    }
}
