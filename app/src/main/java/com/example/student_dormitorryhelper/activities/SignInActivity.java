package com.example.student_dormitorryhelper.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student_dormitorryhelper.MainActivity;
import com.example.student_dormitorryhelper.R;
import com.example.student_dormitorryhelper.databinding.ActivitySignInBinding;
import com.example.student_dormitorryhelper.models.User;
import com.example.student_dormitorryhelper.utilities.UserManager;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private CallbackManager callbackManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private AccessTokenTracker accessTokenTracker;
    private ImageView image;
    private MaterialButton materialButton;
    private LoginButton loginButton;
    private TextView welcomeText;
    private TextView descriptionText;
    private UserManager userManager;
    private AccessToken currentAccessToken;
    private static final String TAG = "FacebookAuthentication";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        image = binding.logo;
        materialButton = binding.buttonSignIn;
        loginButton = binding.loginButton;
        welcomeText = binding.welcomeText;
        descriptionText = binding.descriptionText;
        userManager = UserManager.getInstance();
        setSendToUser();

        setListeners();

        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "onSucces" + loginResult);
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel");
            }

            @Override
            public void onError(@NonNull FacebookException e) {
                Log.d(TAG, "onError" + e);
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if(firebaseUser != null) {
                    updateUI(firebaseUser);
                } else {
                    updateUI(null);
                }
            }
        };

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(@Nullable AccessToken accessToken, @Nullable AccessToken accessToken1) {
                if(accessToken1 == null) {
                    firebaseAuth.signOut();
                    updateUI(null);
                }
            }
        };
    }

    private void setSendToUser() {
        User user = new User();
        user.fbId = "5115103881911483";
        user.firstName = "Residents";
        user.secondName = "Council";
        userManager.setSendToUser(user);
    }

    private void handleFacebookToken(AccessToken accessToken) {
        Log.d(TAG, "handleFacebookToken" + accessToken);

        currentAccessToken = accessToken;

        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(
                this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "sign in with credential: successful");
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            updateUI(firebaseUser);
                        } else {
                            Log.d(TAG, "sign in with credential: failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint("SetTextI18n")
    private void updateUI(FirebaseUser firebaseUser) {
        if(firebaseUser != null) {
            welcomeText.setText("Witaj " + firebaseUser.getDisplayName() + "!");
            materialButton.setVisibility(View.VISIBLE);
            descriptionText.setVisibility(View.INVISIBLE);
        } else {
            materialButton.setVisibility(View.INVISIBLE);
            descriptionText.setVisibility(View.VISIBLE);
            image.setImageResource(R.drawable.logokolor);
            welcomeText.setText(R.string.welcome_back);
        }
    }

    private void setListeners() {
        materialButton.setOnClickListener(v -> {
            userManager.setUserData();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            });
//        materialButton.setOnClickListener(v -> addDataToFirestore());

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
        LoginManager.getInstance().logOut();
    }
}