package com.example.freefireaimbot;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class AnchorService extends Service {

    private static AnchorService instance;

    private static final String PREFS = "anchor_prefs";
    private static final String KEY_X = "x";
    private static final String KEY_Y = "y";

    private WindowManager wm;
    private View anchor;
    private WindowManager.LayoutParams lp;
    private SharedPreferences prefs;

    public static void show(Context c) {
        c.startService(new Intent(c, AnchorService.class));
    }

    public static void hide(Context c) {
        c.stopService(new Intent(c, AnchorService.class));
    }

    public static boolean isRunning() {
        return instance != null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        addAnchor();
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    private void addAnchor() {
        final int size = dp(56);
        final int line = dp(1);

        FrameLayout root = new FrameLayout(this);

        View hLine = new View(this);
        hLine.setBackgroundColor(Color.parseColor("#00E5FF"));
        FrameLayout.LayoutParams hLp = new FrameLayout.LayoutParams(size, line);
        hLp.gravity = Gravity.CENTER_VERTICAL;
        root.addView(hLine, hLp);

        View vLine = new View(this);
        vLine.setBackgroundColor(Color.parseColor("#00E5FF"));
        FrameLayout.LayoutParams vLp = new FrameLayout.LayoutParams(line, size);
        vLp.gravity = Gravity.CENTER_HORIZONTAL;
        root.addView(vLine, vLp);

        View dot = new View(this);
        dot.setBackgroundColor(Color.parseColor("#FF1744"));
        FrameLayout.LayoutParams dLp = new FrameLayout.LayoutParams(dp(4), dp(4));
        dLp.gravity = Gravity.CENTER;
        root.addView(dot, dLp);

        int type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE;

        lp = new WindowManager.LayoutParams(
                size, size, type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        lp.gravity = Gravity.TOP | Gravity.START;
        lp.x = prefs.getInt(KEY_X, 0);
        lp.y = prefs.getInt(KEY_Y, 400);

        root.setOnTouchListener(new View.OnTouchListener() {
            private int startX, startY;
            private float downX, downY;

            @Override
            public boolean onTouch(View v, MotionEvent e) {
                switch (e.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = lp.x;
                        startY = lp.y;
                        downX = e.getRawX();
                        downY = e.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        lp.x = startX + Math.round(e.getRawX() - downX);
                        lp.y = startY + Math.round(e.getRawY() - downY);
                        wm.updateViewLayout(anchor, lp);
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        prefs.edit().putInt(KEY_X, lp.x).putInt(KEY_Y, lp.y).apply();
                        return true;
                }
                return false;
            }
        });

        anchor = root;
        try {
            wm.addView(anchor, lp);
        } catch (Exception e) {
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        instance = null;
        if (anchor != null && wm != null) {
            try {
                wm.removeView(anchor);
            } catch (Exception ignored) {
            }
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
