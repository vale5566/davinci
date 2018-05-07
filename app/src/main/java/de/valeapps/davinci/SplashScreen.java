package de.valeapps.davinci;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import de.valeapps.davinci.login.LoginActivity;


public class SplashScreen extends Activity {

    /**
     * Duration of wait
     **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splash);
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        new Handler().postDelayed(() -> {
            boolean auth = sp.getBoolean("auth", false);
            if (auth) {
//                Utils.scheduleSubstituteTableJob(this);
//                Utils.scheduleTimeTableJob(this);
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                finish();
            } else {
                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
