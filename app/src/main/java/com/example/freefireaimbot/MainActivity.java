package com.example.freefireaimbot;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        int pad = Math.round(24 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad, pad, pad);

        Button btnToggleAnchor = new Button(this);
        btnToggleAnchor.setText("Bat / Tat Tam Ao (Anchor)");
        btnToggleAnchor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, "Vui long cap quyen hien thi tren ung dung khac", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                    return;
                }
                if (AnchorService.isRunning()) {
                    AnchorService.hide(MainActivity.this);
                } else {
                    AnchorService.show(MainActivity.this);
                }
            }
        });

        layout.addView(btnToggleAnchor);
        setContentView(layout);
    }
}
