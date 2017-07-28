package de.valeapps.davinci.teacher;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.List;

import de.valeapps.davinci.R;

class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder> {

    List<Teachers> teacherses;
    Context mcontext;
    PersonViewHolder pvh;

    class PersonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView cv;
        TextView personName;
        TextView personmail;
        TextView personsubjects;


        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            personName = (TextView) itemView.findViewById(R.id.person_name);
            personmail = (TextView) itemView.findViewById(R.id.person_mail);
            personsubjects = (TextView) itemView.findViewById(R.id.person_subjects);
            itemView.setOnClickListener(this);
        }

        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                String to = teacherses.get(position).mail + "@davinci-gesamtschule.de";
                Log.i("DaVinci", "sendmail to: " + to);
                Team.sendMailtoTeacher(to, mcontext);
            }
        }
    }

    RVAdapter(List<Teachers> teacherses) {
        this.teacherses = teacherses;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_teacher_rv, viewGroup, false);
        pvh = new PersonViewHolder(v);
        mcontext = viewGroup.getContext();
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        personViewHolder.personName.setText(teacherses.get(i).name);
        personViewHolder.personmail.setText("Kürzel: " + teacherses.get(i).mail);
        personViewHolder.personsubjects.setText(" Fächer: " + teacherses.get(i).subjects);
    }

    @Override
    public int getItemCount() {
        return teacherses.size();
    }
}
