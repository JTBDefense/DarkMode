package com.atakmap.android.DarkMode.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;

import com.atakmap.android.DarkMode.DarkModeDropDownReceiver;
import com.atakmap.android.DarkMode.plugin.R;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.widgets.LinearLayoutWidget;
import com.atakmap.android.widgets.MapWidget;
import com.atakmap.android.widgets.MarkerIconWidget;
import com.atakmap.android.widgets.RootLayoutWidget;
import com.atakmap.coremap.log.Log;
import com.atakmap.coremap.maps.assets.Icon;
import com.atakmap.map.opengl.GLRenderGlobals;

public class ToggleIconWidget extends MarkerIconWidget implements MapWidget.OnClickListener {

    private final static int ICON_WIDTH = 32;
    private final static int ICON_HEIGHT = 32;
    private final MapView mapView;
    private final Context context;

    private final DarkModeDropDownReceiver dropDown;

    public ToggleIconWidget(final Context context, final MapView mapView, final DarkModeDropDownReceiver dropDown) {
        this.mapView = mapView;
        this.context = context;
        this.dropDown = dropDown;

        Log.d("DarkMode", "ctr");

        Icon.Builder builder = new Icon.Builder();
        builder.setAnchor(0, 0);
        builder.setColor(Icon.STATE_DEFAULT, Color.BLACK);
        builder.setSize(ICON_WIDTH*2, ICON_HEIGHT*2);
        builder.setImageUri(Icon.STATE_DEFAULT, "android.resource://" + context.getPackageName() + "/" + R.drawable.icon);

        Icon icon = builder.build();
        setIcon(icon);
        setMargins(16,0,0,0);
        getBottomLeftLayoutWidget().addWidget(this);

        addOnClickListener(this);

    }

    public void destroy() {
        getBottomLeftLayoutWidget().removeWidget(this);
    }

    private LinearLayoutWidget getBottomLeftLayoutWidget() {
        RootLayoutWidget root = (RootLayoutWidget) mapView.getComponentExtra("rootLayoutWidget");
        return root.getLayout(RootLayoutWidget.BOTTOM_LEFT);
    }

    @Override
    public void onMapWidgetClick(MapWidget mapWidget, MotionEvent motionEvent) {
        dropDown.toggle();
    }
}