package com.badmintonq.sumai.badmintonq;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.badmintonq.sumai.badmintonq.Adapter.ClubAdapter;
import com.badmintonq.sumai.badmintonq.Adapter.CourtAdapter;
import com.badmintonq.sumai.badmintonq.Model.CloseQueue;
import com.badmintonq.sumai.badmintonq.Model.Club;
import com.badmintonq.sumai.badmintonq.Model.QueueData;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourtActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String strName, strEmail, strSelectedClub, strClubInfo;
    BadmintonSVC badmintonSVC;
    CourtAdapter mAdapter;
    List<QueueData> queueData;
    ListView lstQueue;
    Call<List<QueueData>> queueMeCall;
    List<Club> lstClub;
    int clubID;
    SwipeRefreshLayout laySwipe;
    ProgressBar ctlProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_court);

        badmintonSVC = new BadmintonSVC();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ctlProgress = (ProgressBar)findViewById(R.id.ctlProgress);
        ctlProgress.setVisibility(View.VISIBLE);

        View navView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        TextView txtEmail = (TextView) navView.findViewById(R.id.txtEmail);
        TextView txtName = (TextView) navView.findViewById(R.id.txtName);
        lstQueue = (ListView)findViewById(R.id.lstQueue);
        laySwipe = (SwipeRefreshLayout) findViewById(R.id.laySwipe);
        laySwipe.setColorSchemeResources(R.color.colorAccent,android.R.color.holo_blue_bright, R.color.green);
        laySwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                PopulateQueue();
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        strName = sharedPreferences.getString(LoginActivity.Name,"");
        strEmail = sharedPreferences.getString(LoginActivity.Email,"");
        strSelectedClub = sharedPreferences.getString(LoginActivity.Club,"");
        Boolean isOwner = sharedPreferences.getBoolean(LoginActivity.isOwner,false);

        txtName.setText(strName);
        txtEmail.setText(strEmail);

        badmintonSVC = new BadmintonSVC();

        MenuItem navClub = navigationView.getMenu().findItem(R.id.nav_club);
        if(!isOwner)
            navClub.setVisible(false);
        else navClub.setVisible(true);

        if (strSelectedClub.isEmpty())
        {
            Gson gson = new Gson();
            strClubInfo = sharedPreferences.getString(LoginActivity.ClubList,"");
            Type type = new TypeToken<List<Club>>(){}.getType();
            lstClub = gson.fromJson(strClubInfo,type);
            showSelectClubDialog();
        }
        else PopulateQueue();

        lstQueue.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showGameCloseAlert(view, parent,position);
            }
        });
    }

    public void showGameCloseAlert(final View view, final AdapterView<?> parent, final int position)
    {
        android.app.AlertDialog.Builder alertbox = new android.app.AlertDialog.Builder(view.getContext());
        alertbox.setMessage("Do you wish to close this game?");
        alertbox.setTitle("Confirmation");
        alertbox.setIcon(R.mipmap.ic_launcher);
        alertbox.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0,
                                        int arg1) {
                        GameClose(parent,position,view);
                    }
                });
        alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertbox.show();
    }

    private void GameClose(AdapterView<?> parent,int position, View view)
    {
        QueueData queueData;
        CloseQueue closeQueue;
        EditText txtScore;
        int iScore1 = 0, iScore2 = 0;
        String strWinner = "";
        Map<String,CloseQueue> closeQueueMap = new HashMap<String, CloseQueue>(4);
        int startPosition;
        startPosition = position*2;
        txtScore = (EditText)view.findViewById(R.id.txtScore);
        if(TextUtils.isEmpty(txtScore.getText().toString()))
        {
            txtScore.setError("Please enter the score");
            return;
        }

        ctlProgress.setVisibility(View.VISIBLE);
        // win or lose determine based on score
        if(position%2==0)
        {
            iScore1 = Integer.parseInt(txtScore.getText().toString());
            View alternateView = lstQueue.getChildAt(position+1);
            txtScore = (EditText)alternateView.findViewById(R.id.txtScore);
            if(TextUtils.isEmpty(txtScore.getText().toString()))
            {
                ctlProgress.setVisibility(View.GONE);
                txtScore.setError("Please enter the score");
                return;
            }
            iScore2 = Integer.parseInt(txtScore.getText().toString());
            if(iScore1 > iScore2)  strWinner="Team1"; else strWinner = "TeamB";

            // first item
            queueData = (QueueData) parent.getItemAtPosition(startPosition);
            closeQueue = new CloseQueue();
            closeQueue.setScore(iScore1);
            closeQueue.setQueueID(queueData.getQueueID());
            closeQueue.setWon(strWinner.equals("Team1"));
            closeQueueMap.put("Queue1",closeQueue);

            // second item
            queueData = (QueueData) parent.getItemAtPosition(startPosition+1);
            closeQueue = new CloseQueue();
            closeQueue.setScore(iScore1);
            closeQueue.setQueueID(queueData.getQueueID());
            closeQueue.setWon(strWinner.equals("Team1"));
            closeQueueMap.put("Queue2",closeQueue);

            // third item
            queueData = (QueueData) parent.getItemAtPosition(startPosition+2);
            closeQueue = new CloseQueue();
            closeQueue.setScore(iScore2);
            closeQueue.setQueueID(queueData.getQueueID());
            closeQueue.setWon(strWinner.equals("Team2"));
            closeQueueMap.put("Queue3",closeQueue);

            // fourth item
            queueData = (QueueData) parent.getItemAtPosition(startPosition+3);
            closeQueue = new CloseQueue();
            closeQueue.setScore(iScore2);
            closeQueue.setQueueID(queueData.getQueueID());
            closeQueue.setWon(strWinner.equals("Team2"));
            closeQueueMap.put("Queue4",closeQueue);
        }
        else
        {
            iScore2 = Integer.parseInt(txtScore.getText().toString());
            View alternateView = lstQueue.getChildAt(position-1);
            txtScore = (EditText)alternateView.findViewById(R.id.txtScore);
            if(TextUtils.isEmpty(txtScore.getText().toString()))
            {
                ctlProgress.setVisibility(View.GONE);
                txtScore.setError("Please enter the score");
                return;
            }
            iScore1 = Integer.parseInt(txtScore.getText().toString());
            if(iScore1 > iScore2)  strWinner="Team1"; else strWinner = "TeamB";

            // first item
            queueData = (QueueData) parent.getItemAtPosition(startPosition-2);
            closeQueue = new CloseQueue();
            closeQueue.setScore(iScore1);
            closeQueue.setQueueID(queueData.getQueueID());
            closeQueue.setWon(strWinner.equals("Team1"));
            closeQueueMap.put("Queue1",closeQueue);

            // second item
            queueData = (QueueData) parent.getItemAtPosition(startPosition-1);
            closeQueue = new CloseQueue();
            closeQueue.setScore(iScore1);
            closeQueue.setQueueID(queueData.getQueueID());
            closeQueue.setWon(strWinner.equals("Team1"));
            closeQueueMap.put("Queue2",closeQueue);

            // third item
            queueData = (QueueData) parent.getItemAtPosition(startPosition);
            closeQueue = new CloseQueue();
            closeQueue.setScore(iScore2);
            closeQueue.setQueueID(queueData.getQueueID());
            closeQueue.setWon(strWinner.equals("Team2"));
            closeQueueMap.put("Queue3",closeQueue);

            // fourth item
            queueData = (QueueData) parent.getItemAtPosition(startPosition+1);
            closeQueue = new CloseQueue();
            closeQueue.setScore(iScore2);
            closeQueue.setQueueID(queueData.getQueueID());
            closeQueue.setWon(strWinner.equals("Team2"));
            closeQueueMap.put("Queue4",closeQueue);
        }
        CloseGame(closeQueueMap);
    }

    private void CloseGame(Map<String, CloseQueue> closeQueueMap) {
        Call<Map<String,CloseQueue>> closeCall = badmintonSVC.queueService().gameClose(closeQueueMap);
        closeCall.enqueue(new Callback<Map<String, CloseQueue>>() {
            @Override
            public void onResponse(Call<Map<String, CloseQueue>> call, Response<Map<String, CloseQueue>> response) {
                if(response.isSuccessful())
                {
                    ctlProgress.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Game was closed!!!",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<Map<String, CloseQueue>> call, Throwable t) {
                ctlProgress.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Error while closing the game!!!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void PopulateQueue()
    {
        if(!strSelectedClub.isEmpty())
            queueMeCall = badmintonSVC.queueService().getQueuePlaying(Integer.parseInt(strSelectedClub));
        else
        {
            Toast.makeText(getApplicationContext(), "Please select a club to continue!!!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        queueMeCall.enqueue(new Callback<List<QueueData>>() {
            @Override
            public void onResponse(Call<List<QueueData>> call, Response<List<QueueData>> response) {
                if (response.isSuccessful()) {
                    queueData = response.body();
                    mAdapter = new CourtAdapter(CourtActivity.this,R.layout.court_view,queueData);
                    lstQueue.setAdapter(mAdapter);
                    laySwipe.setRefreshing(false);
                    ctlProgress.setVisibility(View.GONE);
                    if(mAdapter!=null)
                        if(mAdapter.getCount()==0)
                            Toast.makeText(getApplicationContext(), "No players in the court!!!",
                                    Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Error getting the queue information!!!",
                            Toast.LENGTH_SHORT).show();
                    laySwipe.setRefreshing(false);
                    ctlProgress.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<List<QueueData>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error getting the queue information!!!",
                        Toast.LENGTH_SHORT).show();
                laySwipe.setRefreshing(false);
                ctlProgress.setVisibility(View.GONE);
            }
        });
    }

     private void showSelectClubDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ClubAdapter clubAdapter = new ClubAdapter(CourtActivity.this, R.layout.clubs, lstClub);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View clubView = inflater.inflate(R.layout.autocomplete, null);
        final AutoCompleteTextView txtClubs = (AutoCompleteTextView)clubView.findViewById(R.id.txtClubs);
        txtClubs.setAdapter(clubAdapter);
        txtClubs.setThreshold(1);
        txtClubs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Club club = (Club) adapterView.getItemAtPosition(position);
                txtClubs.setText(club.getClubName());
                clubID = club.getClubID();
            }
        });
        builder.setTitle("Club Selection:");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setView(clubView);
        final AlertDialog alertDialog;
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                strSelectedClub = String.valueOf(clubID);
                SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(LoginActivity.Club,strSelectedClub);
                editor.apply();
                PopulateQueue();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            if (accessToken!=null)
            {
                LoginManager.getInstance().logOut();
            }
            SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(CourtActivity.this, LoginActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(CourtActivity.this,MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_queue) {
            Intent intent = new Intent(CourtActivity.this,QueueActivity.class);
            startActivity(intent);
        }  else if (id == R.id.nav_QSkill) {
            Intent intent = new Intent(CourtActivity.this,QSkillActivity.class);
            startActivity(intent);
        }  else if (id == R.id.nav_court) {
            Intent intent = new Intent(CourtActivity.this,CourtActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_account) {
            Intent intent = new Intent(CourtActivity.this,PlayerActivity.class);
            intent.putExtra("Update","Update");
            startActivity(intent);
        }
        else if (id == R.id.nav_club) {
            Intent intent = new Intent(CourtActivity.this,ClubActivity.class);
            intent.putExtra("Update","Update");
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
