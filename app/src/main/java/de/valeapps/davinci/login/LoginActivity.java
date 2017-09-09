package de.valeapps.davinci.login;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import de.valeapps.davinci.MainActivity;
import de.valeapps.davinci.R;
import de.valeapps.davinci.Utils;

public class LoginActivity extends AppCompatActivity {

    private static final String LOGIN_REQUEST_URL = "http://valeapps.de/davinci/login.php";
    EditText etUsername;
    EditText etPassword;
    Button bLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);
        bLogin = findViewById(R.id.sign_in);

        bLogin.setOnClickListener(v -> {
            login();
        });

    }

    private void login() {
        final String username = etUsername.getText().toString();
        final String password = etPassword.getText().toString();
        if (!username.isEmpty() && !password.isEmpty()) {

            if (Utils.isNetworkAvailable(this)) {

//                    JSONObject jsonObject = new JSONObject();
//                    try {
//                        jsonObject.put("username", username);
//                        jsonObject.put("password", password);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }

                AndroidNetworking.post(LOGIN_REQUEST_URL)
                        .addBodyParameter("username", username)
                        .addBodyParameter("password", password)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                Boolean success = false;
                                try {
                                    success = jsonObject.getBoolean("success");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                saveLogin(username, success);
                            }

                            @Override
                            public void onError(ANError anError) {
                                Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                Log.e(Utils.TAG, String.valueOf(anError));
                            }
                        });
            } else {
                Toast.makeText(this, "Kein Internet", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Felder müssen ausgefüllt sein!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveLogin(String username, Boolean success) {
        if (success) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("auth", true);
            editor.putString("username", username);
            editor.apply();
            Toast.makeText(getApplicationContext(), "Erfolgreich Eingeloggt !", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            Utils.scheduleSubstituteTableJob(this);
            Utils.scheduleTimeTableJob(this);
            finish();
        } else {
            Toast.makeText(this, "Login Fehlgeschlagen", Toast.LENGTH_SHORT).show();
        }
    }
}
