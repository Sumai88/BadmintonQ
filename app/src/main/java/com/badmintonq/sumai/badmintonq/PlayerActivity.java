package com.badmintonq.sumai.badmintonq;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.badmintonq.sumai.badmintonq.Model.Player;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import retrofit2.Call;

public class PlayerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String strName, strEmail,strUpdate;
    Player newPlayer;
    BadmintonSVC badmintonSVC;
    boolean success;
    ProgressBar ctlProgress;
    Call<Player> updatePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        TextView txtEmail = (TextView) navView.findViewById(R.id.txtEmail);
        TextView txtName = (TextView) navView.findViewById(R.id.txtName);

        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        strName = sharedPreferences.getString(LoginActivity.Name,"");
        strEmail = sharedPreferences.getString(LoginActivity.Email,"");
        Boolean isOwner = sharedPreferences.getBoolean(LoginActivity.isOwner,false);

        txtName.setText(strName);
        txtEmail.setText(strEmail);

        Intent intent = getIntent();
        strUpdate = intent.getStringExtra("Update");

        badmintonSVC = new BadmintonSVC();

        ctlProgress = (ProgressBar)findViewById(R.id.ctlProgress);

        MenuItem navClub = navigationView.getMenu().findItem(R.id.nav_club);
        if(!isOwner)
            navClub.setVisible(false);
        else navClub.setVisible(true);

        if(strUpdate!=null)
        if(strUpdate.equals("Update"))
        {
            UpdatePlayer(strEmail);
        }
    }

    private void UpdatePlayer(String strEmail)
    {
        final Call<Player> playerCall = badmintonSVC.playerService().getPlayerInfo(strEmail);
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    newPlayer = playerCall.execute().body();
                    success = true;
                }
                catch (Exception ex) {
                    success = false;
                }
                PlayerActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!success)
                        {
                            Toast.makeText(getApplicationContext(), "Error while retrieving player info!!!",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        EditText txtEmail = (EditText) findViewById(R.id.txtEmail);
                        EditText txtName = (EditText) findViewById(R.id.txtName);
                        EditText txtPhone = (EditText) findViewById(R.id.txtPhone);
                        EditText txtPassword = (EditText) findViewById(R.id.txtPassword);
                        txtEmail.setText(newPlayer.getPlayerEmail());
                        txtName.setText(newPlayer.getPlayerName());
                        txtPassword.setText(newPlayer.getPassword());
                        txtPhone.setText(String.valueOf(newPlayer.getPhone()));
                        if(newPlayer.getLoginType().equals("Direct"))
                            txtPassword.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        t1.start();
    }

    public void cancelChanges(View v)
    {
        Intent intent = new Intent(PlayerActivity.this,MainActivity.class);
        startActivity(intent);
    }

    public void submitChanges(View v)
    {
        v.setEnabled(false);
        ctlProgress.setVisibility(View.VISIBLE);
        Player player = new Player();
        EditText txtName = (EditText)findViewById(R.id.txtName);
        EditText txtEmail = (EditText)findViewById(R.id.txtEmail);
        EditText txtPhone = (EditText)findViewById(R.id.txtPhone);
        EditText txtPassword = (EditText)findViewById(R.id.txtPassword);
        if(TextUtils.isEmpty(txtName.getText().toString()))
        {
            v.setEnabled(true);
            ctlProgress.setVisibility(View.GONE);
            txtName.setError("Please enter a Name");
            return;
        }
        if(!IsValidEmail(txtEmail.getText().toString()))
        {
            v.setEnabled(true);
            ctlProgress.setVisibility(View.GONE);
            txtEmail.setError("Please enter an email address");
            return;
        }
        if(!IsValidPhone(txtPhone.getText().toString()))
        {
            v.setEnabled(true);
            ctlProgress.setVisibility(View.GONE);
            txtPhone.setError("Please enter a contact number");
            return;
        }
        if(txtPassword.getVisibility() == View.VISIBLE)
        {
            if(!isPasswordValid(txtPassword.getText().toString()))
            {
                v.setEnabled(true);
                ctlProgress.setVisibility(View.GONE);
                txtPassword.setError("Please enter a valid password");
                return;
            }
        }
        if(newPlayer!=null)
        {
            newPlayer.setPlayerName(txtName.getText().toString());
            newPlayer.setPlayerEmail(txtEmail.getText().toString());
            newPlayer.setPhone(Long.parseLong(txtPhone.getText().toString()));
            newPlayer.setPassword(txtPassword.getText().toString());
            player = newPlayer;
            updatePlayer = badmintonSVC.playerService().putPlayer(newPlayer.getPlayerID(),newPlayer);
        }
        else {
            player.setPlayerName(txtName.getText().toString());
            player.setPlayerEmail(txtEmail.getText().toString());
            player.setPhone(Long.parseLong(txtPhone.getText().toString()));
        }
        final Call<Player> playerCall = badmintonSVC.playerService().postPlayer(player);

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(newPlayer!=null)
                        newPlayer = updatePlayer.execute().body();
                    else
                        newPlayer = playerCall.execute().body();
                    success = true;
                }
                catch (Exception ex) {
                    success = false;
                }
                PlayerActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.btnSubmit).setEnabled(true);
                        ctlProgress.setVisibility(View.GONE);
                        if(!success)
                        {
                            Toast.makeText(getApplicationContext(), "Error adding player!!!",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (newPlayer.getPlayerID() != null && newPlayer.getPlayerID() != 0) {
                            Toast.makeText(getApplicationContext(), "Player added/updated successfully!!!",
                                    Toast.LENGTH_SHORT).show();
                            if(strUpdate==null) {
                                Intent intent = new Intent(PlayerActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }
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
            Intent intent = new Intent(PlayerActivity.this, LoginActivity.class);
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
            Intent intent = new Intent(PlayerActivity.this,MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_queue) {
            Intent intent = new Intent(PlayerActivity.this,QueueActivity.class);
            startActivity(intent);
        }  else if (id == R.id.nav_QSkill) {
            Intent intent = new Intent(PlayerActivity.this,QSkillActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_court) {
            Intent intent = new Intent(PlayerActivity.this,CourtActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_account) {
            Intent intent = new Intent(PlayerActivity.this,PlayerActivity.class);
            intent.putExtra("Update","Update");
            startActivity(intent);
        }
        else if (id == R.id.nav_club) {
            Intent intent = new Intent(PlayerActivity.this,ClubActivity.class);
            intent.putExtra("Update","Update");
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean IsValidPhone(String strPhone) {
        return android.util.Patterns.PHONE.matcher(strPhone).matches();
    }

    private boolean IsValidEmail(String strEmail) {
        return !TextUtils.isEmpty(strEmail) && android.util.Patterns.EMAIL_ADDRESS.matcher(strEmail).matches();
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 8;
    }
}
