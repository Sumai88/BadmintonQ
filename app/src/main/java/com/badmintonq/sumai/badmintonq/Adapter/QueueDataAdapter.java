package com.badmintonq.sumai.badmintonq.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.badmintonq.sumai.badmintonq.Model.QueueData;
import com.badmintonq.sumai.badmintonq.R;

import java.util.List;

/**
 * Created by sumai on 8/8/2016.
 */
public class QueueDataAdapter extends ArrayAdapter<QueueData> {

    private LayoutInflater mInflater;

    public QueueDataAdapter(Context mContext, int resource, List<QueueData> queueList) {
        super(mContext,resource,queueList);
        mInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView txtListText;
        TextView txtStatus;
        QueueData queueData = getItem(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.queue_skill, parent, false);
        }
        txtStatus = (TextView)convertView.findViewById(R.id.txtStatus);
        txtListText = (TextView)convertView.findViewById(R.id.txtItem);
        txtStatus.setText(getSkillName(queueData.getQStatusID()));
        txtListText.setText(queueData.getPlayerName());
        return convertView;
    }

    private String getSkillName(int QStatusID)
    {
        String status="";
        switch (QStatusID)
        {
            case 1:
                status = "Waiting ";
                break;
            case 2:
                status = "Next ";
                break;
            case 3:
                status = "Skipped ";
                break;
            case 4:
                status = "Playing ";
                break;
        }
         return status;
    }
}

