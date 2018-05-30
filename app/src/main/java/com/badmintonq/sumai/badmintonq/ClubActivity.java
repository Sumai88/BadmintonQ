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
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.badmintonq.sumai.badmintonq.Model.Club;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import retrofit2.Call;

public class ClubActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    String strName, strEmail, strUpdate;
    Club newClub;
    BadmintonSVC badmintonSVC;
    NumberPicker noCourts;
    boolean success;
    ProgressBar ctlProgress;
    Call<Club> updateClub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);

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
        noCourts = (NumberPicker) findViewById(R.id.noClubs);
        noCourts.setMaxValue(10);
        noCourts.setMinValue(1);
        noCourts.setWrapSelectorWheel(true);

        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        strName = sharedPreferences.getString(LoginActivity.Name,"");
        strEmail = sharedPreferences.getString(LoginActivity.Email,"");
        Boolean isOwner = sharedPreferences.getBoolean(LoginActivity.isOwner,false);

        ctlProgress = (ProgressBar)findViewById(R.id.ctlProgress);
        txtName.setText(strName);
        txtEmail.setText(strEmail);

        Intent intent = getIntent();
        strUpdate = intent.getStringExtra("Update");

        badmintonSVC = new BadmintonSVC();

        MenuItem navClub = navigationView.getMenu().findItem(R.id.nav_club);
        if(!isOwner)
            navClub.setVisible(false);
        else navClub.setVisible(true);

        if(strUpdate!=null)
            if(strUpdate.equals("Update"))
            {
                UpdateClub(strEmail);
            }
    }

    public void cancelChanges(View v)
    {
        Intent intent = new Intent(ClubActivity.this,MainActivity.class);
        startActivity(intent);
    }

    public void submitChanges(View v)
    {
        v.setEnabled(false);
        ctlProgress.setVisibility(View.VISIBLE);
        Club club = new Club();
        EditText txtName = (EditText)findViewById(R.id.txtName);
        EditText txtEmail = (EditText)findViewById(R.id.txtEmail);
        EditText txtOrganizer = (EditText)findViewById(R.id.txtOrganizer);
        EditText txtAddress = (EditText)findViewById(R.id.txtStreet);
        EditText txtCity = (EditText)findViewById(R.id.txtCity);
        EditText txtState = (EditText)findViewById(R.id.txtState);
        EditText txtZipcode = (EditText)findViewById(R.id.txtZip);
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
        if(TextUtils.isEmpty(txtOrganizer.getText().toString()))
        {
            v.setEnabled(true);
            ctlProgress.setVisibility(View.GONE);
            txtOrganizer.setError("Please enter Organizer name/contact");
            return;
        }
        if(TextUtils.isEmpty(txtAddress.getText().toString()))
        {
            v.setEnabled(true);
            ctlProgress.setVisibility(View.GONE);
            txtAddress.setError("Please enter street name");
            return;
        }
        if(TextUtils.isEmpty(txtCity.getText().toString()))
        {
            v.setEnabled(true);
            ctlProgress.setVisibility(View.GONE);
            txtCity.setError("Please enter City name");
            return;
        }
        if(TextUtils.isEmpty(txtState.getText().toString()))
        {
            v.setEnabled(true);
            ctlProgress.setVisibility(View.GONE);
            txtState.setError("Please enter State name/code");
            return;
        }
        if(TextUtils.isEmpty(txtZipcode.getText().toString()))
        {
            v.setEnabled(true);
            ctlProgress.setVisibility(View.GONE);
            txtZipcode.setError("Please enter zipcode");
            return;
        }
        if(newClub!=null)
        {
            newClub.setClubName(txtName.getText().toString());
            newClub.setClubEmail(txtEmail.getText().toString());
            newClub.setOrganizer(txtOrganizer.getText().toString());
            newClub.setCity(txtCity.getText().toString());
            newClub.setState(txtState.getText().toString());
            newClub.setStreetName(txtAddress.getText().toString());
            newClub.setZipcode(Integer.parseInt(txtZipcode.getText().toString()));
            newClub.setNoOfCourts(noCourts.getValue());
            updateClub = badmintonSVC.clubService().putClub(newClub.getClubID(),newClub);
        }
        else {
            club.setClubName(txtName.getText().toString());
            club.setClubEmail(txtEmail.getText().toString());
            club.setOrganizer(txtOrganizer.getText().toString());
            club.setCity(txtCity.getText().toString());
            club.setState(txtState.getText().toString());
            club.setStreetName(txtAddress.getText().toString());
            club.setZipcode(Integer.parseInt(txtZipcode.getText().toString()));
            club.setNoOfCourts(noCourts.getValue());
        }

        //BadmintonSVC badmintonSVC = new BadmintonSVC();
        final Call<Club> clubCall = badmintonSVC.clubService().postClub(club);

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(newClub!=null)
                        newClub = updateClub.execute().body();
                    else
                        newClub = clubCall.execute().body();
                    success = true;
                }
                catch (Exception ex) {
                    success = false;
                }
                ClubActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.btnSubmit).setEnabled(true);
                        ctlProgress.setVisibility(View.GONE);
                        if(!success)
                        {
                            Toast.makeText(getApplicationContext(), "Error adding club!!!",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (newClub.getClubID() != null && newClub.getClubID() != 0) {
                            Toast.makeText(getApplicationContext(), "Club added/updated successfully!!!",
                                    Toast.LENGTH_SHORT).show();
                            if(strUpdate == null) {
                                Intent intent = new Intent(ClubActivity.this, MainActivity.class);
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

    private void UpdateClub(String strEmail)
    {
        final Call<Club> clubCall = badmintonSVC.clubService().getClubInfo(strEmail);
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    newClub = clubCall.execute().body();
                    success = true;
                }
                catch (Exception ex) {
                    success = false;
                }
                ClubActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!success)
                        {
                            Toast.makeText(getApplicationContext(), "Error while retrieving club info!!!",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        EditText txtEmail = (EditText) findViewById(R.id.txtEmail);
                        EditText txtName = (EditText) findViewById(R.id.txtName);
                        EditText txtStreet = (EditText) findViewById(R.id.txtStreet);
                        EditText txtCity = (EditText) findViewById(R.id.txtCity);
                        EditText txtState = (EditText) findViewById(R.id.txtState);
                        EditText txtZip = (EditText) findViewById(R.id.txtZip);
                        EditText txtOrganizer = (EditText) findViewById(R.id.txtOrganizer);
                        txtEmail.setText(newClub.getClubEmail());
                        txtName.setText(newClub.getClubName());
                        txtOrganizer.setText(newClub.getOrganizer());
                        txtStreet.setText(newClub.getStreetName());
                        txtCity.setText(newClub.getCity());
                        txtState.setText(newClub.getState());
                        txtZip.setText(String.valueOf(newClub.getZipcode()));
                        noCourts.setValue(newClub.getNoOfCourts());
                    }
                });
            }
        });
        t1.start();
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
            Intent intent = new Intent(ClubActivity.this, LoginActivity.class);
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
            Intent intent = new Intent(ClubActivity.this,MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_queue) {
            Intent intent = new Intent(ClubActivity.this,QueueActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_QSkill) {
            Intent intent = new Intent(ClubActivity.this,QSkillActivity.class);
            startActivity(intent);
        }  else if (id == R.id.nav_court) {
            Intent intent = new Intent(ClubActivity.this,CourtActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_account) {
            Intent intent = new Intent(ClubActivity.this,PlayerActivity.class);
            intent.putExtra("Update","Update");
            startActivity(intent);
        }
        else if (id == R.id.nav_club) {
            Intent intent = new Intent(ClubActivity.this,ClubActivity.class);
            intent.putExtra("Update","Update");
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean IsValidEmail(String strEmail) {
        return !TextUtils.isEmpty(strEmail) && android.util.Patterns.EMAIL_ADDRESS.matcher(strEmail).matches();
    }

}
