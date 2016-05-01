package com.naviapp.invis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    SignInButton googleButton;
    LoginButton facebookButton;
    TextView emailButton;
    CallbackManager callBackManager;
    ProfileTracker mProfileTracker;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("loginData", Context.MODE_PRIVATE);
        editor = preferences.edit();
        if(preferences.getBoolean("isLogin",false)){
            finish();
            Intent i = new Intent(MainActivity.this,Main.class);
            startActivity(i);
        }
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);


        facebookButton = (LoginButton) findViewById(R.id.btSigninFacebook);
        googleButton = (SignInButton) findViewById(R.id.btSigninGoogle);
        emailButton = (TextView) findViewById(R.id.btSigninEmail);
        googleButton.setOnClickListener(this);
        emailButton.setOnClickListener(this);

        AppEventsLogger.activateApp(this);
        callBackManager = CallbackManager.Factory.create();
        List<String> permissions = new ArrayList<String>();
        permissions.add("user_friends");
        permissions.add("email");
        permissions.add("user_birthday");
        facebookButton.setReadPermissions(permissions);
        facebookButton.registerCallback(callBackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();

                mProfileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                        // profile2 is the new
                        if(profile2!=null) {
                            Log.v("facebook - profile", profile2.getFirstName());
                            mProfileTracker.stopTracking();
                            String name = profile2.getName();
                            Uri photo = profile2.getProfilePictureUri(100, 100);
                            editor.putBoolean("isLogin", true);
                            editor.putString("name", name);
                            editor.putString("email", "Facebook");
                            editor.putString("photo",photo.toString());
                            editor.commit();

                        }else{
                            editor.putBoolean("isLogin",false);
                            editor.putString("name","");
                            editor.putString("email","");
                            editor.commit();
                        }
                    }
                };
                mProfileTracker.startTracking();
                finish();

                Intent i = new Intent(MainActivity.this, Main.class);

                startActivity(i);


        }

        @Override
        public void onCancel () {

        }

        @Override
        public void onError (FacebookException error){

        }
    }

    );

        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
// options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
// Customize sign-in button. The sign-in button can be displayed in
// multiple sizes and color schemes. It can also be contextually
// rendered based on the requested scopes. For example. a red button may
// be displayed when Google+ scopes are requested, but a white button
// may be displayed when only basic profile is requested. Try adding the
// Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
// difference.
        SignInButton signInButton = (SignInButton) findViewById(R.id.btSigninGoogle);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        signInButton.setOnClickListener(this);
}

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btSigninGoogle:
                signIn();
                break;
            default:
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
        }

    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, 2);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callBackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 2) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("Google Signin", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
//            mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
//            updateUI(true);
            editor.putBoolean("isLogin",true);
            editor.putString("name",acct.getDisplayName());
            editor.putString("email",acct.getEmail());
            editor.putString("photo",acct.getPhotoUrl().toString());
            editor.commit();
            finish();

            Intent i = new Intent(MainActivity.this, Main.class);

            startActivity(i);
        } else {
            // Signed out, show unauthenticated UI.
            //updateUI(false);
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
