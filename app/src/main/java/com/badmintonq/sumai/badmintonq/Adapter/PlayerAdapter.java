package com.badmintonq.sumai.badmintonq.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.badmintonq.sumai.badmintonq.Model.Player;
import com.badmintonq.sumai.badmintonq.R;

import java.util.List;

/**
 * Created by sumai on 7/18/2016.
 */
public class PlayerAdapter extends ArrayAdapter<Player> {
    public PlayerAdapter(Context context, int resource, List<Player> playerList) {
        super(context, resource, playerList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.players, parent, false);
        }

        Player player = getItem(position);

        if (player != null) {
            TextView txtClubID = (TextView) v.findViewById(R.id.playerID);
            TextView txtClubName = (TextView) v.findViewById(R.id.playerName);
            //TextView txtSkill = (TextView) v.findViewById(R.id.SkillID);
            txtClubID.setText( Integer.toString(player.getPlayerID()));
            txtClubName.setText(player.getPlayerName());
            //txtSkill.setText(player.getSkillsetID());
        }

        return v;
    }

}

