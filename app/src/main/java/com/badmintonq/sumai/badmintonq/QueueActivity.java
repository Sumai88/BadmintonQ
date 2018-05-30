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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.badmintonq.sumai.badmintonq.Adapter.ClubAdapter;
import com.badmintonq.sumai.badmintonq.Adapter.ListViewAdapter;
import com.badmintonq.sumai.badmintonq.Model.Club;
import com.badmintonq.sumai.badmintonq.Model.QueueData;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QueueActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String strName, strEmail, strSelectedClub, strClubInfo, UserID;
    ListViewAdapter mAdapter;
    BadmintonSVC badmintonSVC;
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
        setContentView(R.layout.activity_queue);

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
        UserID = sharedPreferences.getString(LoginActivity.PlayerID,"");
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
    }


    private void PopulateQueue()
    {
        if(!strSelectedClub.isEmpty())
            queueMeCall = badmintonSVC.queueService().getQueueNext(Integer.parseInt(strSelectedClub));
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
                    mAdapter = new ListViewAdapter(QueueActivity.this,queueData, true, UserID);
                    lstQueue.setAdapter(mAdapter);
                    mAdapter.setMode(Attributes.Mode.Single);
                    lstQueue.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ((SwipeLayout)(lstQueue.getChildAt(position - lstQueue.getFirstVisiblePosition()))).open(true);
                        }
                    });
                    laySwipe.setRefreshing(false);
                    ctlProgress.setVisibility(View.GONE);
                    if(mAdapter!=null)
                    if(mAdapter.getCount()==0)
                        Toast.makeText(getApplicationContext(), "No players in the 'Next' Queue!!!",
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
        ClubAdapter clubAdapter = new ClubAdapter(QueueActivity.this, R.layout.clubs, lstClub);
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
            Intent intent = new Intent(QueueActivity.this, LoginActivity.class);
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
            Intent intent = new Intent(QueueActivity.this,MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_queue) {
            Intent intent = new Intent(QueueActivity.this,QueueActivity.class);
            startActivity(intent);
        }  else if (id == R.id.nav_QSkill) {
            Intent intent = new Intent(QueueActivity.this,QSkillActivity.class);
            startActivity(intent);
        }  else if (id == R.id.nav_court) {
            Intent intent = new Intent(QueueActivity.this,CourtActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_account) {
            Intent intent = new Intent(QueueActivity.this,PlayerActivity.class);
            intent.putExtra("Update","Update");
            startActivity(intent);
        }
        else if (id == R.id.nav_club) {
            Intent intent = new Intent(QueueActivity.this,ClubActivity.class);
            intent.putExtra("Update","Update");
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
