package com.badmintonq.sumai.badmintonq.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.badmintonq.sumai.badmintonq.Model.Club;
import com.badmintonq.sumai.badmintonq.R;

import java.util.List;

public class ClubAdapter extends ArrayAdapter<Club> {
    public ClubAdapter(Context context, int resource, List<Club> clubList) {
        super(context, resource, clubList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.clubs, parent, false);
        }

        Club club = getItem(position);

        if (club != null) {
            TextView txtClubID = (TextView) v.findViewById(R.id.clubID);
            TextView txtClubName = (TextView) v.findViewById(R.id.clubName);
            //TextView txtSkill = (TextView) v.findViewById(R.id.SkillPreDef);
            txtClubID.setText( Integer.toString(club.getClubID()));
            txtClubName.setText(club.getClubName());
            //txtSkill.setText(club.getSkillPredefined().toString());
        }

        return v;
    }

}
