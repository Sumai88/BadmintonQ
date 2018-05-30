package com.badmintonq.sumai.badmintonq;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.badmintonq.sumai.badmintonq.Model.Player;
import com.facebook.FacebookSdk;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.*;
/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity
{

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String PlayerID = "IDKey";
    public static final String Name = "nameKey";
    public static final String Email = "emailKey";
    public static final String LoginType = "LoginKey";
    public static final String isOwner = "Owner";
    public static final String Club = "clubID";
    public static final String ClubList = "lstClub";
    /**
     *
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    CallbackManager callbackManager;
    private  TextView txtLoginError;
    AccessToken accessToken;
    private String strName, strEmail, strUser, strPhone;
    BadmintonSVC badmintonSVC;
    Player newPlayer;
    boolean success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        txtLoginError = (TextView) findViewById(R.id.lblError);
        mPasswordView = (EditText) findViewById(R.id.password);

        LoginButton loginButton;
        Button mEmailSignInButton, btnRegister;

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        btnRegister = (Button) findViewById(R.id.Register);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        btnRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        loginButton = (LoginButton) findViewById(R.id.login_button);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        TelephonyManager tMgr = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        //strPhone = tMgr.getLine1Number();

        loginButton.setReadPermissions("email");

        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String strLoginType = sharedPreferences.getString(LoginActivity.LoginType,"");
        String playerID = sharedPreferences.getString(LoginActivity.PlayerID,"");

        if(!strLoginType.isEmpty())
        {
            if(strLoginType.equals("Direct") && !playerID.isEmpty())
            //if(strLoginType.equals("Direct"))
            {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }

        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken!=null)
        {
            getFBProfileInfo(accessToken);
        }
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getFBProfileInfo(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                txtLoginError.setText("Login attempt was cancelled!!!");
            }

            @Override
            public void onError(FacebookException error) {
                txtLoginError.setText("Login attempt failed!!!");
            }
        });
    }

    private void getFBProfileInfo(AccessToken accToken)
    {
        GraphRequest request = GraphRequest.newMeRequest(accToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            //strUser=object.getString("id");
                            strName=object.getString("name");
                            strEmail=object.getString("email");
                            txtLoginError.setText("Hi, " + strName);
                            //first time user -- insert record into table
                            SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.putString(Name, strName);
                            editor.putString(Email, strEmail);
                            editor.commit();
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            intent.putExtra("LoginType","FB");
                            startActivity(intent);
                        } catch(JSONException ex) {
                            ex.printStackTrace();
                        }
                                            }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        final TextView lblError = (TextView)findViewById(R.id.lblError);

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            lblError.setText(getString(R.string.error_invalid_password));
            return;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            lblError.setText("Please enter your email id!!!");
            return;
        } else if (!isEmailValid(email)) {
            lblError.setText(getString(R.string.error_invalid_email));
            return;
        }

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            badmintonSVC = new BadmintonSVC();
            final Call<Player> playerCall = badmintonSVC.playerService().loginDirect(email,password);

            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        newPlayer = playerCall.execute().body();
                        success = true;
                    } catch (Exception ex) {
                        success = false;
                    }
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(newPlayer == null)
                            {
                                showProgress(false);
                                lblError.setText("Login failed; User not found!!!");
                                return;
                            }
                            else if(!success)
                            {
                                showProgress(false);
                                lblError.setError("Login failed; Invalid email/password!!!");
                                return;
                            }
                            else if (newPlayer.getPlayerID() != null && newPlayer.getPlayerID() != 0) {
                                if (newPlayer.getPassword().equals("Incorrect Password")) {
                                    showProgress(false);
                                    lblError.setText("Login failed; Incorrect password!!!");
                                    return;
                                }
                                Toast.makeText(getApplicationContext(), "Logged in successfully!!!",
                                        Toast.LENGTH_SHORT).show();
                                SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.putString(LoginActivity.PlayerID, newPlayer.getPlayerID().toString());
                                editor.putString(LoginActivity.Name, newPlayer.getPlayerName());
                                editor.putString(LoginActivity.Email, newPlayer.getPlayerEmail());
                                editor.putString(LoginActivity.LoginType, "Direct");
                                editor.apply();
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
            });
            t1.start();
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >=8;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
   @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

