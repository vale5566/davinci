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

    private static final String LOGIN_REQUEST_URL = "https://valeapps.de:64495/api/login";
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
            try {
                login();
            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
        });

    }

    private void login() throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        final String username = etUsername.getText().toString();
        final String password = etPassword.getText().toString();
        if (!username.isEmpty() && !password.isEmpty()) {

            if (Utils.isNetworkAvailable(this)) {
//                CertificateFactory cf = CertificateFactory.getInstance("X.509");
//                InputStream cert = this.getResources().openRawResource(R.raw.cert);
//                Certificate ca;
//                try {
//                    ca = cf.generateCertificate(cert);
//                } finally { cert.close(); }
//
//                String keyStoreType = KeyStore.getDefaultType();
//                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
//                keyStore.load(null, null);
//                keyStore.setCertificateEntry("ca", ca);
//
//                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//                tmf.init(keyStore);
//
//                TrustManager[] trustManagers = tmf.getTrustManagers();
//
//
//                X509TrustManager trustManager = (X509TrustManager) trustManagers[0];
//
//
//                SSLContext sslContext = SSLContext.getInstance("TLS");
//                sslContext.init(null, tmf.getTrustManagers(), null);
//
//                OkHttpClient okHttpClient = new OkHttpClient() .newBuilder()
//                        .sslSocketFactory(sslContext.getSocketFactory(), trustManager)
//                        .hostnameVerifier((hostname, sslSession) -> true)
//                        .build();
//
//                AndroidNetworking.initialize(getApplicationContext(),okHttpClient);

                AndroidNetworking.initialize(getApplicationContext(), getUnsafeOkHttpClient());

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
    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
