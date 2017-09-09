package de.valeapps.davinci.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;

import java.io.File;
import java.io.IOException;

import de.valeapps.davinci.Utils;

public class DownloadFileFromURL extends AsyncTask<Object, String, Void> {

    private ProgressDialog pDialog;
    private Context mContext;
    private File file;

    public DownloadFileFromURL(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        System.out.println("Starting download");

        pDialog = new ProgressDialog(mContext);
        pDialog.setMessage("Downloading... Bitte warten...");
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected Void doInBackground(Object... params) {

        String f_url = (String) params[0];
        file = (File) params[1];

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        AndroidNetworking.download(f_url, file.getAbsolutePath(), "")
                .setPriority(Priority.MEDIUM)
                .build()
                .setDownloadProgressListener((bytesDownloaded, totalBytes) -> pDialog.setProgress(Integer.parseInt(("" + (int) ((bytesDownloaded * 100) / totalBytes)))))
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        System.out.println("Downloaded");

                        pDialog.dismiss();
                    }

                    @Override
                    public void onError(ANError error) {
                        pDialog.dismiss();
                        Log.d(Utils.TAG, "onError errorCode : " + error.getErrorCode());
                        Log.d(Utils.TAG, "onError errorBody : " + error.getErrorBody());
                        Log.d(Utils.TAG, "onError errorDetail : " + error.getErrorDetail());
                        Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                        file.delete();
                    }
                });
        return null;
    }

    @Override
    protected void onCancelled() {
        file.delete();
        pDialog.dismiss();
        Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
    }
}
