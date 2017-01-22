package com.geniusnine.android.geniusnineframework;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;

import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private LoginButton mLoginBtn;
    private CallbackManager mCallbackManager;
    private static final String TAG = "FacebookLogin";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListner;

    JSONObject jsonObject = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_login);
        mCallbackManager = CallbackManager.Factory.create();
        mAuth=FirebaseAuth.getInstance();
        /*mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){

                    Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }
                else
                {
                    Toast.makeText(LoginActivity.this, "User appers to be not present.", Toast.LENGTH_LONG).show();
                }

            }
        };*/
        mLoginBtn = (LoginButton)findViewById(R.id.facebook_login_btn);

        mLoginBtn.setReadPermissions(Arrays.asList("email", "public_profile", "user_friends", "user_birthday", "user_location"));


        mLoginBtn.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
             //   handleFacebookAccessToken(loginResult.getAccessToken());

                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/me/friends",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {

                            public void onCompleted( GraphResponse response) {
                                JSONObject object=null;
                                Intent intent = new Intent(LoginActivity.this,FriendsList.class);
                                try {

                                       JSONArray rawName = response.getJSONObject().getJSONArray("data");
                                        intent.putExtra("jsondata", rawName.toString());
                                        startActivity(intent);
                                } catch (JSONException e) {
                                        e.printStackTrace();
                                }
                            }
                        }
                ).executeAsync();


            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }



    private void handleFacebookAccessToken(AccessToken token){
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential= FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());


                if (!task.isSuccessful()) {

                    Log.w(TAG, "signInWithCredential", task.getException());

                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }

               else {
                    Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                    Toast.makeText(LoginActivity.this, "User logged in.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
