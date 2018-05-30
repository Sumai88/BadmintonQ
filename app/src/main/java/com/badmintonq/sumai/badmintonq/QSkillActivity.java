package com.badmintonq.sumai.badmintonq;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.badmintonq.sumai.badmintonq.Adapter.ClubAdapter;
import com.badmintonq.sumai.badmintonq.Adapter.QueueDataAdapter;
import com.badmintonq.sumai.badmintonq.Model.Club;
import com.badmintonq.sumai.badmintonq.Model.QueueData;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;

public class QSkillActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    String strName, strEmail, strSelectedClub, strClubInfo;
    ListView lstBeginner, lstIntermediate, lstAdvance, lstAll;
    QueueDataAdapter bAdapter, iAdapter, aAdapter, allAdapter;
    BadmintonSVC badmintonSVC;
    List<QueueData> dataBeginner,dataAdvance,dataAll,dataIntermediate;
    Call<List<QueueData>> callBeginner, callIntermediate, callAdvance;
    List<Club> lstClub;
    int clubID;
    SwipeRefreshLayout laySwipe;
    ProgressBar ctlProgress;
    boolean success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qskill);
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

        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        strName = sharedPreferences.getString(LoginActivity.Name,"");
        strEmail = sharedPreferences.getString(LoginActivity.Email,"");
        strSelectedClub = sharedPreferences.getString(LoginActivity.Club,"");
        Boolean isOwner = sharedPreferences.getBoolean(LoginActivity.isOwner,false);

        txtName.setText(strName);
        txtEmail.setText(strEmail);

        TabHost tabHost =   (TabHost)findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("All");
        tabSpec.setContent(R.id.tabAll);
        tabSpec.setIndicator("All");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("Intermediate");
        tabSpec.setContent(R.id.tabBeginner);
        tabSpec.setIndicator("Intermediate");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("Advance");
        tabSpec.setContent(R.id.tabIntermediate);
        tabSpec.setIndicator("Advance");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("Elite");
        tabSpec.setContent(R.id.tabAdvance);
        tabSpec.setIndicator("Elite");
        tabHost.addTab(tabSpec);

        lstBeginner = (ListView)findViewById(R.id.lstBeginner);
        lstIntermediate = (ListView)findViewById(R.id.lstIntermediate);
        lstAdvance = (ListView)findViewById(R.id.lstAdvance);
        lstAll = (ListView)findViewById(R.id.lstAll);

        badmintonSVC = new BadmintonSVC();

        MenuItem navClub = navigationView.getMenu().findItem(R.id.nav_club);
        if(!isOwner)
            navClub.setVisible(false);
        else navClub.setVisible(true);

        laySwipe = (SwipeRefreshLayout) findViewById(R.id.laySwipe);
        laySwipe.setColorSchemeResources(R.color.colorAccent,android.R.color.holo_blue_bright, R.color.green);
        laySwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                PopulateList();
            }
        });

        if (strSelectedClub.isEmpty())
        {
            Gson gson = new Gson();
            strClubInfo = sharedPreferences.getString(LoginActivity.ClubList,"");
            Type type = new TypeToken<List<Club>>(){}.getType();
            lstClub = gson.fromJson(strClubInfo,type);
            showSelectClubDialog();
        }
        else PopulateList();
    }

    private void PopulateList() {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    callBeginner = badmintonSVC.queueService().getQueueBySkills(1,Integer.parseInt(strSelectedClub));
                    callIntermediate = badmintonSVC.queueService().getQueueBySkills(2,Integer.parseInt(strSelectedClub));
                    callAdvance = badmintonSVC.queueService().getQueueBySkills(3,Integer.parseInt(strSelectedClub));
                    dataBeginner = callBeginner.execute().body();
                    dataIntermediate = callIntermediate.execute().body();
                    dataAdvance = callAdvance.execute().body();
                    bAdapter = new QueueDataAdapter(QSkillActivity.this, R.layout.queue_skill,dataBeginner);
                    iAdapter = new QueueDataAdapter(QSkillActivity.this, R.layout.queue_skill,dataIntermediate);
                    aAdapter = new QueueDataAdapter(QSkillActivity.this, R.layout.queue_skill,dataAdvance);
                    success = true;

                } catch (Exception e) {
                    success = false;
                }
                QSkillActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!success)
                        {
                            Toast.makeText(getApplicationContext(), "Error retrieving players list!!!",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        lstBeginner.setAdapter(bAdapter);
                        lstIntermediate.setAdapter(iAdapter);
                        lstAdvance.setAdapter(aAdapter);

                        dataAll = new ArrayList<QueueData>(dataBeginner);
                        dataAll.addAll(dataIntermediate);
                        dataAll.addAll(dataAdvance);
                        Collections.sort(dataAll);
                        allAdapter = new QueueDataAdapter(QSkillActivity.this, R.layout.queue_skill,dataAll);
                        lstAll.setAdapter(allAdapter);
                        laySwipe.setRefreshing(false);
                        ctlProgress.setVisibility(View.GONE);
                    }
                });
            }
        });
        t1.start();

    }

    private void showSelectClubDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ClubAdapter clubAdapter = new ClubAdapter(QSkillActivity.this, R.layout.clubs, lstClub);
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
                PopulateList();
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
            Intent intent = new Intent(QSkillActivity.this, LoginActivity.class);
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
            Intent intent = new Intent(QSkillActivity.this,MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_queue) {
            Intent intent = new Intent(QSkillActivity.this,QueueActivity.class);
            startActivity(intent);
        }  else if (id == R.id.nav_QSkill) {
            Intent intent = new Intent(QSkillActivity.this,QSkillActivity.class);
            startActivity(intent);
        }  else if (id == R.id.nav_court) {
            Intent intent = new Intent(QSkillActivity.this,CourtActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_account) {
            Intent intent = new Intent(QSkillActivity.this,PlayerActivity.class);
            intent.putExtra("Update","Update");
            startActivity(intent);
        }
        else if (id == R.id.nav_club) {
            Intent intent = new Intent(QSkillActivity.this,ClubActivity.class);
            intent.putExtra("Update","Update");
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
