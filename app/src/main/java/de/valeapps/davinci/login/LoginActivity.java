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

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import de.valeapps.davinci.MainActivity;
import de.valeapps.davinci.R;
import de.valeapps.davinci.Utils;
import okhttp3.OkHttpClient;

public class LoginActivity extends AppCompatActivity {

    private static final String LOGIN_REQUEST_URL = "https://valeapps.de/davinciapp/api/login/";
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

                AndroidNetworking.post(LOGIN_REQUEST_URL)
                        .addBodyParameter("username", username)
                        .addBodyParameter("password", password)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                Boolean success = false;
                                String message = "";
                                try {
                                    success = jsonObject.getBoolean("success");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    message = jsonObject.getString("message");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                saveLogin(username, success, message);
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

    private void saveLogin(String username, Boolean success, String message) {
        if (success) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("auth", true);
            editor.putString("username", username);
            editor.apply();
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
//            Utils.scheduleSubstituteTableJob(this);
//            Utils.scheduleTimeTableJob(this);
            finish();
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
}
