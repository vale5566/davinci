package de.valeapps.davinci.yearbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import de.valeapps.davinci.R;
import de.valeapps.davinci.asynctasks.DownloadFileFromURL;

class RVAdapter extends RecyclerView.Adapter<RVAdapter.JahrbuchViewHolder> {

    private List<Yearbooks> jahrbuchs;
    private long mLastClickTime = System.currentTimeMillis();
    private static final long CLICK_TIME_INTERVAL = 300;

    RVAdapter(List<Yearbooks> jahrbuchs) {
        this.jahrbuchs = jahrbuchs;
    }

    @Override
    public JahrbuchViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_yearbook, viewGroup, false);
        return new JahrbuchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(JahrbuchViewHolder jahrbuchViewHolder, int i) {
        jahrbuchViewHolder.jahrbuchName.setText(jahrbuchs.get(i).name);
    }

    @Override
    public int getItemCount() {
        return jahrbuchs.size();
    }

    class JahrbuchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView jahrbuchName;
        Button button;

        JahrbuchViewHolder(View itemView) {
            super(itemView);
            jahrbuchName = itemView.findViewById(R.id.tv_jahrbuch);
            button = itemView.findViewById(R.id.btn_jahrbuch);
            CardView cardView = itemView.findViewById(R.id.cv_jahrbuch);
            button.setOnClickListener(this);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            long now = System.currentTimeMillis();

//            if (now - mLastClickTime > CLICK_TIME_INTERVAL) {
            mLastClickTime = now;
            File jahrbuch = new File(v.getContext().getFilesDir() + "/Jahrbücher/" + jahrbuchs.get(getLayoutPosition()).name + ".pdf");
            if (v.getId() == button.getId()) {
                String url = "https://valeapps.de/davinci/" + jahrbuchs.get(getLayoutPosition()).name + ".pdf";
                if (!jahrbuch.exists()) {
                    new DownloadFileFromURL(v.getContext()).execute(url, jahrbuch);
                } else {
                    Toast.makeText(v.getContext(), "Bereits heruntergeladen", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (jahrbuch.exists()) {
                    Intent intent = new Intent(v.getContext(), YearbookPDFView.class);
                    Bundle b = new Bundle();
                    b.putString("file", jahrbuch.getAbsolutePath());
                    intent.putExtras(b);
                    v.getContext().startActivity(intent);
                } else {
                    Toast.makeText(v.getContext(), "Noch nicht heruntergeladen!", Toast.LENGTH_SHORT).show();
                }
//                }
            }
        }
    }
}
