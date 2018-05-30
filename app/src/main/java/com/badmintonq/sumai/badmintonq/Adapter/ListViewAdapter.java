package com.badmintonq.sumai.badmintonq.Adapter;

/**
 * Created by sumai on 8/14/2016.
 */
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.badmintonq.sumai.badmintonq.BadmintonSVC;
import com.badmintonq.sumai.badmintonq.LoginActivity;
import com.badmintonq.sumai.badmintonq.Model.Queue;
import com.badmintonq.sumai.badmintonq.Model.QueueData;
import com.badmintonq.sumai.badmintonq.QueueActivity;
import com.badmintonq.sumai.badmintonq.R;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListViewAdapter extends BaseSwipeAdapter {

    private Context mContext;
    private List<QueueData> queueDataList;
    private int groupNo;
    private String UserID;
    private boolean section;
    BadmintonSVC badmintonSVC;
    Call<Queue> deleteCall, skipCall, swapCall;

    public ListViewAdapter(Context mContext, List<QueueData> queueDataList, boolean section, String playerID) {
        this.mContext = mContext;
        this.queueDataList = queueDataList;
        this.section = section;
        groupNo = 0;
        this.UserID = playerID;
        badmintonSVC = new BadmintonSVC();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        final QueueData queueData = (QueueData) getItem(position);
        final View v = LayoutInflater.from(mContext).inflate(R.layout.swipe_layout, null);
        //SwipeLayout swipeLayout = (SwipeLayout)v.findViewById(getSwipeLayoutResourceId(position));
        v.findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteAlert(view, queueData);
            }
        });
        v.findViewById(R.id.btnSkip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSkipAlert(view,queueData);
            }
        });
        v.findViewById(R.id.btnSwap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupPlayers(view,queueData);
            }
        });
      return v;
    }

    @Override
    public void fillValues(int position, View convertView) {
        int group = position%4;
        QueueData queueData = (QueueData) getItem(position);
        if(position == 0) groupNo = 1;
        else groupNo = ((position+1)/4)+1;

        TextView txtSeparator = (TextView)convertView.findViewById(R.id.textSeparator);
        TextView txtListText = (TextView)convertView.findViewById(R.id.txtItem);

       if(section)
            if(group == 0)
            {
                txtSeparator.setText("Group " + groupNo);
                txtSeparator.setVisibility(View.VISIBLE);
            }
            else
            {
                txtSeparator.setVisibility(View.GONE);
            }

        txtListText.setText(queueData.getPlayerName());
    }

    @Override
    public int getCount() {
        return queueDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return queueDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void showPopupPlayers(View view, final QueueData sourceQ)
    {
        final PopupWindow popupWindow = new PopupWindow(mContext);
        ListView lstPlayers = new ListView(mContext);
        ArrayAdapter<QueueData> playerAdapter = new ArrayAdapter<QueueData>(mContext,android.R.layout.simple_list_item_1,queueDataList)
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

            // setting the ID and text for every items in the list
            QueueData queueData = getItem(position);
            String strPlayer = queueData.getPlayerName();
            // visual settings for the list item
            TextView listItem = new TextView(mContext);

            listItem.setText(strPlayer);
            listItem.setTag(position);
            listItem.setTextSize(22);
            listItem.setPadding(10, 10, 10, 10);
            listItem.setTextColor(Color.WHITE);

            return listItem;
            }
        };
        lstPlayers.setAdapter(playerAdapter);
        lstPlayers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QueueData targetQ = (QueueData) getItem(position);
                showSwapAlert(view,popupWindow, sourceQ,targetQ);
            }
        });

        popupWindow.setFocusable(true);
        popupWindow.setWidth(700);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(lstPlayers);
        popupWindow.showAsDropDown(view,0,-100-view.getHeight());
    }

    public void showDeleteAlert(final View view, final QueueData queueData)
    {
        android.app.AlertDialog.Builder alertbox = new android.app.AlertDialog.Builder(view.getContext());
        alertbox.setMessage("Do you wish to delete the player " + queueData.getPlayerName() + " from queue?");
        alertbox.setTitle("Confirmation");
        alertbox.setIcon(R.mipmap.ic_launcher);
        alertbox.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0,
                                        int arg1) {
                        DeletePlayer(queueData);
                    }
                });
        alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertbox.show();
    }

    private void DeletePlayer(final QueueData queueData)
    {
        deleteCall = badmintonSVC.queueService().deleteQueue(queueData.getQueueID());
        //Toast.makeText(mContext, "click delete", Toast.LENGTH_SHORT).show();
        deleteCall.enqueue(new Callback<Queue>() {
            @Override
            public void onResponse(Call<Queue> call, Response<Queue> response) {
                Toast.makeText(mContext, "Player " + queueData.getPlayerName() + " removed from the queue", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext,QueueActivity.class);
                mContext.startActivity(intent);
            }

            @Override
            public void onFailure(Call<Queue> call, Throwable t) {
                Toast.makeText(mContext, "Error deleting the player from queue", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showSkipAlert(final View view, final QueueData queueData)
    {
        if(!UserID.equals(queueData.getPlayerID().toString()))
        {
            Toast.makeText(mContext, "Player cannot be skipped!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        android.app.AlertDialog.Builder alertbox = new android.app.AlertDialog.Builder(view.getContext());
        alertbox.setMessage("Do you wish to skip the player " + queueData.getPlayerName() + " from group?");
        alertbox.setTitle("Confirmation");
        alertbox.setIcon(R.mipmap.ic_launcher);
        alertbox.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0,
                                        int arg1) {
                        skipPlayer(queueData);
                    }
                });
        alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertbox.show();
    }

    private void skipPlayer(final QueueData queueData)
    {
        skipCall = badmintonSVC.queueService().skipPlayer(queueData.getQueueID());
        //Toast.makeText(mContext, "click skip", Toast.LENGTH_SHORT).show();
        skipCall.enqueue(new Callback<Queue>() {
            @Override
            public void onResponse(Call<Queue> call, Response<Queue> response) {
                Toast.makeText(mContext, "Player " + queueData.getPlayerName() + " has been skipped!!!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext,QueueActivity.class);
                mContext.startActivity(intent);
            }

            @Override
            public void onFailure(Call<Queue> call, Throwable t) {
                Toast.makeText(mContext, "Error while performing skip player", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showSwapAlert(View view, final PopupWindow popupWindow, final QueueData sourceQ, final QueueData targetQ)
    {
        if(UserID.equals(sourceQ.getPlayerID().toString()) || UserID.equals(targetQ.getPlayerID().toString()) )
        {
            android.app.AlertDialog.Builder alertbox = new android.app.AlertDialog.Builder(view.getContext());
            alertbox.setMessage("Do you wish to swap the players " + sourceQ.getPlayerName() + " and " + targetQ.getPlayerName() + "?");
            alertbox.setTitle("Confirmation");
            alertbox.setIcon(R.mipmap.ic_launcher);
            alertbox.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0,
                                            int arg1) {
                            SwapPlayer(popupWindow, sourceQ.getQueueID(),targetQ.getQueueID());
                        }
                    });
            alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            alertbox.show();
        }
        else
        {
            Toast.makeText(mContext, "Players cannot be swapped!!!", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void SwapPlayer(PopupWindow popupWindow, int FrQID, int ToQID)
    {
        // dismiss the pop up
        swapCall = badmintonSVC.queueService().swapPlayer(FrQID,ToQID);
        popupWindow.dismiss();
        swapCall.enqueue(new Callback<Queue>() {
            @Override
            public void onResponse(Call<Queue> call, Response<Queue> response) {
                Toast.makeText(mContext, "Players have been swapped!!!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext,QueueActivity.class);
                mContext.startActivity(intent);
            }

            @Override
            public void onFailure(Call<Queue> call, Throwable t) {
                Toast.makeText(mContext, "Error while swapping the players", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
