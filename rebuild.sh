#!/data/data/com.termux/files/usr/bin/bash
set -e

mkdir -p app/src/main/java/com/fadixd/fadivpn

cat > settings.gradle.kts <<'EOT'
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "FADI-VPN"
include(":app")
EOT

cat > build.gradle.kts <<'EOT'
plugins {
    id("com.android.application") version "8.7.3" apply false
}
EOT

cat > app/build.gradle.kts <<'EOT'
plugins {
    id("com.android.application")
}

android {
    namespace = "com.fadixd.fadivpn"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.fadixd.fadivpn"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
EOT

cat > app/src/main/AndroidManifest.xml <<'EOT'
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.INTERNET"/>

    <application
        android:theme="@style/AppTheme"
        android:label="FADI - VPN"
        android:allowBackup="false"
        android:supportsRtl="true">

        <activity
            android:name=".MainActivity"
            android:screenOrientation="portrait"
            android:exported="true">

            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>

        </activity>

    </application>

</manifest>
EOT

mkdir -p app/src/main/res/values

cat > app/src/main/res/values/styles.xml <<'EOT'
<?xml version="1.0" encoding="utf-8"?>
<resources>

    <style name="AppTheme" parent="@android:style/Theme.Material.NoActionBar">
        <item name="android:fontFamily">sans</item>
        <item name="android:windowLightStatusBar">false</item>
        <item name="android:statusBarColor">#080B12</item>
        <item name="android:navigationBarColor">#080B12</item>
        <item name="android:colorAccent">#2196F3</item>
    </style>

</resources>
EOT

cat > app/src/main/java/com/fadixd/fadivpn/MainActivity.java <<'EOT'
package com.fadixd.fadivpn;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {

    LinearLayout root;
    TextView status;
    TextView timer;
    Button connectButton;

    Handler handler = new Handler();
    long connectedAt = 0;
    boolean connected = false;

    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (connected) {
                long seconds = (System.currentTimeMillis() - connectedAt) / 1000;
                long h = seconds / 3600;
                long m = (seconds % 3600) / 60;
                long s = seconds % 60;

                timer.setText(String.format("%02d:%02d:%02d", h, m, s));
                handler.postDelayed(this, 1000);
            }
        }
    };

    int bg = Color.rgb(8, 11, 18);
    int card = Color.rgb(18, 23, 34);
    int blue = Color.rgb(33, 150, 243);
    int white = Color.WHITE;
    int gray = Color.rgb(160, 170, 185);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHome();
    }

    TextView text(String value, float size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER);
        t.setPadding(8, 8, 8, 8);
        return t;
    }

    GradientDrawable rounded(int color, int radius) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(radius);
        return g;
    }

    void setupRoot() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(24, 25, 24, 15);
        root.setBackgroundColor(bg);

        setContentView(root);
    }

    void showHome() {
        setupRoot();

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        top.setOrientation(LinearLayout.HORIZONTAL);

        TextView logo = text("F", 30, white);
        logo.setGravity(Gravity.CENTER);
        logo.setBackground(rounded(blue, 100));
        top.addView(logo, new LinearLayout.LayoutParams(58, 58));

        TextView title = text("FADI - VPN", 25, white);
        title.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(0, 70, 1);
        titleParams.setMargins(18, 0, 0, 0);
        top.addView(title, titleParams);

        Button settings = new Button(this);
        settings.setText("⚙");
        settings.setTextSize(22);
        settings.setTextColor(white);
        settings.setBackgroundColor(Color.TRANSPARENT);
        settings.setOnClickListener(v -> showSettings());
        top.addView(settings, new LinearLayout.LayoutParams(65, 65));

        root.addView(top);

        TextView subtitle = text("سريع • آمن • خاص", 15, gray);
        root.addView(subtitle);

        Space space1 = new Space(this);
        root.addView(space1, new LinearLayout.LayoutParams(1, 25));

        status = text("غير متصل", 22, gray);
        root.addView(status);

        timer = text("00:00:00", 17, gray);
        root.addView(timer);

        Space space2 = new Space(this);
        root.addView(space2, new LinearLayout.LayoutParams(1, 25));

        connectButton = new Button(this);
        connectButton.setText("اتصال");
        connectButton.setTextSize(22);
        connectButton.setTextColor(white);
        connectButton.setBackground(rounded(blue, 500));
        connectButton.setOnClickListener(v -> toggleConnection());

        root.addView(connectButton,
                new LinearLayout.LayoutParams(190, 190));

        Space space3 = new Space(this);
        root.addView(space3, new LinearLayout.LayoutParams(1, 25));

        LinearLayout stats = new LinearLayout(this);
        stats.setOrientation(LinearLayout.HORIZONTAL);
        stats.setGravity(Gravity.CENTER);

        stats.addView(statCard("PING", "—"));
        stats.addView(statCard("DOWNLOAD", "0 KB/s"));
        stats.addView(statCard("UPLOAD", "0 KB/s"));

        root.addView(stats,
                new LinearLayout.LayoutParams(-1, 110));

        TextView server = text("الخادم\nAuto", 16, white);
        server.setBackground(rounded(card, 25));
        root.addView(server,
                new LinearLayout.LayoutParams(-1, 75));

        Space bottom = new Space(this);
        root.addView(bottom,
                new LinearLayout.LayoutParams(1, 0, 1));

        TextView footer =
                text("تم صنع التطبيق بواسطة FADI - XD", 14, gray);
        root.addView(footer);
    }

    TextView statCard(String name, String value) {
        TextView t = text(name + "\n" + value, 13, white);
        t.setBackground(rounded(card, 25));

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(0, 95, 1);
        p.setMargins(5, 0, 5, 0);

        t.setLayoutParams(p);
        return t;
    }

    void toggleConnection() {
        connected = !connected;

        if (connected) {
            status.setText("متصل");
            status.setTextColor(Color.rgb(76, 175, 80));

            connectButton.setText("قطع الاتصال");
            connectedAt = System.currentTimeMillis();

            handler.post(timerRunnable);
        } else {
            status.setText("غير متصل");
            status.setTextColor(gray);

            connectButton.setText("اتصال");
            timer.setText("00:00:00");

            handler.removeCallbacks(timerRunnable);
        }
    }

    void showSettings() {
        setupRoot();

        TextView title = text("الإعدادات", 28, white);
        root.addView(title);

        Space s = new Space(this);
        root.addView(s, new LinearLayout.LayoutParams(1, 30));

        TextView server = text(
                "الخادم\n\nAuto\n\nسيتم إضافة إعدادات الخادم لاحقاً",
                18,
                white
        );
        server.setBackground(rounded(card, 30));

        root.addView(server,
                new LinearLayout.LayoutParams(-1, 190));

        Space s2 = new Space(this);
        root.addView(s2,
                new LinearLayout.LayoutParams(1, 0, 1));

        Button back = new Button(this);
        back.setText("رجوع");
        back.setTextSize(18);
        back.setTextColor(white);
        back.setBackground(rounded(blue, 40));
        back.setOnClickListener(v -> showHome());

        root.addView(back,
                new LinearLayout.LayoutParams(-1, 65));

        TextView footer =
                text("تم صنع التطبيق بواسطة FADI - XD", 14, gray);
        root.addView(footer);
    }
}
EOT

echo "===================================="
echo "تم تجهيز مشروع FADI - VPN"
echo "الآن سيتم بناء APK..."
echo "===================================="

gradle clean assembleDebug

mkdir -p "$HOME/storage/downloads"

cp app/build/outputs/apk/debug/app-debug.apk \
   "$HOME/storage/downloads/FADI-VPN.apk"

echo ""
echo "===================================="
echo "نجح البناء!"
echo "APK:"
echo "$HOME/storage/downloads/FADI-VPN.apk"
echo "===================================="
