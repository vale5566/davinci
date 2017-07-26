package de.valeapps.davinci.yearbook;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

import de.valeapps.davinci.R;

public class YearbookPDFView extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yearbook_pdf_view);

        PDFView pdfView = (PDFView) findViewById(R.id.pdfView);
        Bundle bundle = getIntent().getExtras();
        String fileString = bundle.getString("file");
        File fileyearbook = null;
        if (fileString != null) {
            fileyearbook = new File(fileString);
            pdfView.fromFile(fileyearbook)
                    .load();
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
