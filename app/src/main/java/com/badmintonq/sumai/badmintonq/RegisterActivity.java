package com.badmintonq.sumai.badmintonq;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.badmintonq.sumai.badmintonq.Model.Player;
import retrofit2.Call;

public class RegisterActivity extends AppCompatActivity
{
    Player newPlayer;
    boolean success;
    ProgressBar ctlProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ctlProgress = (ProgressBar)findViewById(R.id.ctlProgress);
    }

   public void cancelChanges(View v)
   {
       Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
       startActivity(intent);
   }

    public void submitChanges(View v)
    {
        v.setEnabled(false);
        ctlProgress.setVisibility(View.VISIBLE);
        Player player = new Player();
        final EditText txtName = (EditText)findViewById(R.id.txtName);
        final EditText txtEmail = (EditText)findViewById(R.id.txtEmail);
        EditText txtPhone = (EditText)findViewById(R.id.txtPhone);
        //EditText txtUserName = (EditText)findViewById(R.id.txtUserName);
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
            txtEmail.setError("Please enter a valid email address");
            return;
        }
        if(!IsValidPhone(txtPhone.getText().toString()))
        {
            v.setEnabled(true);
            ctlProgress.setVisibility(View.GONE);
            txtPhone.setError("Please enter a valid contact number");
            return;
        }
        /*if(TextUtils.isEmpty(txtUserName.getText().toString()))
        {
            txtUserName.setError("Please enter an username");
            return;
        }*/
        if(!isPasswordValid(txtPassword.getText().toString()))
        {
            v.setEnabled(true);
            ctlProgress.setVisibility(View.GONE);
            txtPassword.setError("Please enter a valid password (min. 8 char)");
            //lblError.setText("Please enter a valid password (min. 8 char)");
            return;
        }
        player.setPlayerName(txtName.getText().toString());
        player.setPlayerEmail(txtEmail.getText().toString());
        player.setUsername("dummy");
        player.setPassword(txtPassword.getText().toString());
        player.setPhone(Long.parseLong(txtPhone.getText().toString()));
        player.setLoginType("Direct");

        BadmintonSVC badmintonSVC = new BadmintonSVC();
        final Call<Player> playerCall = badmintonSVC.playerService().postPlayer(player);


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
                RegisterActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.btnSubmit).setEnabled(true);
                        ctlProgress.setVisibility(View.GONE);
                        if(!success || (newPlayer == null))
                        {
                            Toast.makeText(getApplicationContext(), "Error adding player!!!",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (newPlayer.getPlayerID() != null && newPlayer.getPlayerID() != 0) {
                            Toast.makeText(getApplicationContext(), "Registered successfully!!!",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                            startActivity(intent);
                            SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.putString(LoginActivity.Name, txtName.getText().toString());
                            editor.putString(LoginActivity.Email, txtEmail.getText().toString());
                            editor.putString(LoginActivity.LoginType, "Direct");
                            editor.apply();
                        }
                    }
                });
            }
        });
        t1.start();
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
