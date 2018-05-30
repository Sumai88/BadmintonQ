package com.badmintonq.sumai.badmintonq;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.badmintonq.sumai.badmintonq.Adapter.ClubAdapter;
import com.badmintonq.sumai.badmintonq.Adapter.PlayerAdapter;
import com.badmintonq.sumai.badmintonq.Model.Club;
import com.badmintonq.sumai.badmintonq.Model.Player;
import com.badmintonq.sumai.badmintonq.Model.Queue;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    BadmintonSVC badmintonSVC;
    List<Club> clubList;
    List<Player> playerList;
    private AutoCompleteTextView txtClubs, txtPlayers;
    Call<List<Player>> playerListCall;
    Call<List<Club>> clubListCall;
    Call<Club> clubCall;
    PlayerAdapter playerAdapter;
    ClubAdapter clubAdapter;
    int clubID, playerID, iSkillID, playerSkill = -1;
    String clubName, playerName, strEmail;
    Button btnQueue;
    MultiStateToggleButton btnSkills;
    //FloatingActionButton fabClub, fabPlayer;
    ImageButton fabClub, fabPlayer;
    NavigationView navigationView;
    Club myClub;
    ProgressBar ctlProgress;
    boolean success, skillPredef = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabClub = (ImageButton) findViewById(R.id.fabClub);
        fabPlayer = (ImageButton) findViewById(R.id.fabPlayer);

        ctlProgress = (ProgressBar)findViewById(R.id.ctlProgress);
        ctlProgress.setVisibility(View.VISIBLE);

        fabClub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ClubActivity.class);
                startActivity(intent);
            }
        });

        fabPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,PlayerActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView txtWelcome;
        TextView txtName;
        TextView txtEmail;
        String strName, strWelcome, strLoginType;

        Intent intent = getIntent();
        strLoginType = intent.getStringExtra("LoginType");

        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        strName = sharedPreferences.getString(LoginActivity.Name,"");
        strEmail = sharedPreferences.getString(LoginActivity.Email,"");
        strWelcome = "Welcome " + strName + "!!!";

        View navView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        txtEmail = (TextView) navView.findViewById(R.id.txtEmail);
        txtName = (TextView) navView.findViewById(R.id.txtName);
        txtWelcome = (TextView) findViewById(R.id.txtWelcome);

        btnQueue = (Button) findViewById(R.id.btnQueue);
        btnSkills = (MultiStateToggleButton) findViewById(R.id.multiSkill);
        txtClubs = (AutoCompleteTextView) findViewById(R.id.txtClubs);
        txtPlayers = (AutoCompleteTextView) findViewById(R.id.txtPlayers);

        txtEmail.setText(strEmail);
        txtName.setText(strName);
        txtWelcome.setText(strWelcome);

        badmintonSVC = new BadmintonSVC();
        Call<Player> cPlayer = badmintonSVC.playerService().loginFB(strEmail, "test", strName);
        playerListCall = badmintonSVC.playerService().getPlayers();
        clubListCall = badmintonSVC.clubService().getClubs();
        clubCall = badmintonSVC.clubService().getClubInfo(strEmail);

        btnSkills.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                iSkillID = value + 1;
            }
        });

        if(strLoginType != null)
        {
            if (strLoginType.equals("FB")) {
                cPlayer.enqueue(new Callback<Player>() {
                    @Override
                    public void onResponse(Call<Player> call, retrofit2.Response<Player> response) {
                        if (response.isSuccessful()) {
                            if (response.body().getPlayerID() == null) {
                                Toast.makeText(getApplicationContext(), "Error updating your profile from facebook!!!",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(LoginActivity.PlayerID, response.body().getPlayerID().toString());
                                editor.apply();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Player> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Error updating your profile from facebook!!!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        PopulatePlayersNClubs();
    }

    private void PopulatePlayersNClubs() {
        final Menu menu = navigationView.getMenu();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    playerList = playerListCall.execute().body();
                    clubList = clubListCall.execute().body();
                    myClub = clubCall.execute().body();
                    playerAdapter = new PlayerAdapter(MainActivity.this, R.layout.players, playerList);
                    clubAdapter = new ClubAdapter(MainActivity.this, R.layout.clubs, clubList);
                    success = true;
                } catch (Exception e) {
                    success = false;
                }
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!success)
                        {
                            ctlProgress.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Error retrieving players and clubs!!!",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        txtPlayers.setAdapter(playerAdapter);
                        txtPlayers.setThreshold(1);
                        txtPlayers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                Player player = (Player) adapterView.getItemAtPosition(position);
                                playerID = player.getPlayerID();
                                playerName = player.getPlayerName();
                                playerSkill = player.getSkillsetID();
                                txtPlayers.setText(playerName);
                                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                                if(skillPredef) {
                                    if(playerSkill!=0)
                                    {
                                        btnSkills.setEnabled(false);
                                        btnSkills.setValue(playerSkill-1);
                                    }
                                    else
                                        btnSkills.setEnabled(true);
                                }
                            }
                        });
                        txtClubs.setAdapter(clubAdapter);
                        txtClubs.setThreshold(1);
                        txtClubs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                Club club = (Club) adapterView.getItemAtPosition(position);
                                clubID = club.getClubID();
                                clubName = club.getClubName();
                                skillPredef = club.getSkillPredefined();
                                txtClubs.setText(clubName);
                                if(skillPredef) {
                                    btnSkills.setEnabled(false);
                                    btnSkills.setValue(playerSkill);
                                }
                            }
                        });
                        ctlProgress.setVisibility(View.GONE);
                        Gson gson = new Gson();
                        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        String clubInfo = gson .toJson(clubList);
                        editor.putString(LoginActivity.ClubList,clubInfo);

                        if(myClub!=null) {
                            if (myClub.getClubID() != null && myClub.getClubID() > 0) {
                                editor.putBoolean(LoginActivity.isOwner, true);
                                menu.findItem(R.id.nav_club).setVisible(true);
                            }
                            else menu.findItem(R.id.nav_club).setVisible(false);
                        }
                        else menu.findItem(R.id.nav_club).setVisible(false);
                        editor.apply();
                    }
                });
            }
        });
        t1.start();
    }

     public void AddToQueue(View view) {
         ctlProgress.setVisibility(View.VISIBLE);
         if(clubID <=0)
         {
             ctlProgress.setVisibility(View.GONE);
             Toast.makeText(getApplicationContext(), "Please select a Club!!!",
                     Toast.LENGTH_SHORT).show();
             return;
         }
         if(playerID <=0)
         {
             ctlProgress.setVisibility(View.GONE);
             Toast.makeText(getApplicationContext(), "Please select a Player!!!",
                     Toast.LENGTH_SHORT).show();
             return;
         }
         if(iSkillID <=0)
         {
             ctlProgress.setVisibility(View.GONE);
             Toast.makeText(getApplicationContext(), "Please select a Skillset!!!",
                     Toast.LENGTH_SHORT).show();
             return;
         }
         Thread t1 = new Thread(new Runnable() {
             @Override
             public void run() {
                 try {
                     Queue queue = new Queue();
                     queue.setClubID(clubID);
                     queue.setPlayerID(playerID);
                     queue.setQStatusID(1); //default values while adding a queue
                     queue.setSkillsetID(iSkillID);
                     queue.setScore(0); //default values while adding a queue
                     Call<Queue> queueCall = badmintonSVC.queueService().postQueue(queue);
                     queueCall.execute();
                     success = true;
                 } catch (Exception e) {
                     success = false;
                 }
                 MainActivity.this.runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                         if(!success)
                         {
                             Toast.makeText(getApplicationContext(), "Error adding player to the queue!!!",
                                     Toast.LENGTH_SHORT).show();
                             return;
                         }
                         SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                         SharedPreferences.Editor editor = sharedPreferences.edit();
                         editor.putString(LoginActivity.Club,String.valueOf(clubID));
                         editor.apply();
                         ctlProgress.setVisibility(View.GONE);
                         Intent intent = new Intent(MainActivity.this,CourtActivity.class);
                         startActivity(intent);
                     }
                 });
             }
         });
         t1.start();
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
    public boolean onCreateOptionsMenu(final Menu menu) {
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
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
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
            Intent intent = new Intent(MainActivity.this,MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_queue) {
            Intent intent = new Intent(MainActivity.this,QueueActivity.class);
            startActivity(intent);
        }   else if (id == R.id.nav_QSkill) {
            Intent intent = new Intent(MainActivity.this,QSkillActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_court) {
            Intent intent = new Intent(MainActivity.this,CourtActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_account) {
            Intent intent = new Intent(MainActivity.this,PlayerActivity.class);
            intent.putExtra("Update","Update");
            startActivity(intent);
        }
        else if (id == R.id.nav_club) {
            Intent intent = new Intent(MainActivity.this,ClubActivity.class);
            intent.putExtra("Update","Update");
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
