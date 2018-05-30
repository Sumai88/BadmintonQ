package com.badmintonq.sumai.badmintonq.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import com.badmintonq.sumai.badmintonq.Model.QueueData;
import com.badmintonq.sumai.badmintonq.R;
import java.util.List;

/**
 * Created by sumai on 8/14/2016.
 */
public class CourtAdapter extends ArrayAdapter<QueueData> implements View.OnTouchListener {
    private LayoutInflater mInflater;
    private List<QueueData> queueDataList;
    private int groupNo;

    static class ViewHolder {
        private TextView txtTeam;
        private TextView txtSeparator;
        private EditText txtScore;
    }

    public CourtAdapter(Context mContext, int resource, List<QueueData> queueList) {
        super(mContext,resource,queueList);
        this.queueDataList = queueList;
        groupNo = 0;
        mInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        return (queueDataList.size())/2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
         ViewHolder holder;
        String strTeamA = "", strCourt = "";
        QueueData queueData, queueDataNext;
        if(position == 0) groupNo = 1;
        else groupNo = (position/2)+1;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.court_view, parent, false);
            holder = new ViewHolder();
            holder.txtTeam = (TextView)convertView.findViewById(R.id.txtTeam);
            holder.txtSeparator = (TextView)convertView.findViewById(R.id.textSeparator);
            holder.txtScore = (EditText) convertView.findViewById(R.id.txtScore);
            holder.txtScore.setOnTouchListener(this);
            convertView.setTag(holder);
            convertView.setOnTouchListener(this);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }

        if((position+1) <= (queueDataList.size()-1))
        {
            queueData = getItem(position*2);
            queueDataNext = getItem((position*2) + 1);
            strTeamA = queueData.getPlayerName() + " & " + queueDataNext.getPlayerName();
            holder.txtTeam.setText(strTeamA);
        }

        if(position%2 == 0)
        {
            strCourt = "Court " + groupNo;
            holder.txtSeparator.setText(strCourt);
            holder.txtSeparator.setVisibility(View.VISIBLE);
        }

        return convertView;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v instanceof EditText) {
            EditText editText = (EditText) v;
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
        } else {
            ViewHolder  holder = (ViewHolder) v.getTag();
            holder.txtScore.setFocusable(false);
            holder.txtScore.setFocusableInTouchMode(false);
        }
        return false;

    }
}
