package com.example.minhdung.searchcarrepairv10;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    CallbackManager callbackManager;

    GoogleSignInClient mGoogleSignInClient;

    LoginButton loginButton;
    SignInButton btnLoginGG;
    Button btnUseMap,btnSignOut;
    ProfilePictureView profilePictureView;
    TextView txtName,txtEmail,txtFirstName;
    ImageView imgAvtGG;

    String name,firstName,email;

    static final int RC_SIGN_IN=01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        AnhXa();
        invisible();
        next_Display();

        //btnLoginGG.setVisibility(View.INVISIBLE);

        callbackManager = CallbackManager.Factory.create();

        loginButton.setReadPermissions(Arrays.asList("public_profile","email"));

        setLogin_Button();
        setLogOut_Button();
        
        btnLoginGG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGG();
                visible();
                btnLoginGG.setVisibility(View.INVISIBLE);
                profilePictureView.setVisibility(View.INVISIBLE);
                imgAvtGG.setVisibility(View.VISIBLE);
            }
        });
    }

    private void next_Display() {

        btnUseMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                startActivity(intent);
            }
        });

    }

    private void signInWithGG() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void setLogOut_Button() {
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccessToken.getCurrentAccessToken() != null){
                    LoginManager.getInstance().logOut();
                    invisible();
                    loginButton.setVisibility(View.VISIBLE);
                    profilePictureView.setProfileId(null);
                    btnLoginGG.setVisibility(View.VISIBLE);
                }
                else {
                    signOutGG();
                }
            }
        });
    }

    private void signOutGG() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        invisible();
                        loginButton.setVisibility(View.VISIBLE);
                        profilePictureView.setProfileId(null);
                        profilePictureView.setVisibility(View.VISIBLE);
                        btnLoginGG.setVisibility(View.VISIBLE);
                        imgAvtGG.setImageBitmap(null);
                        imgAvtGG.setVisibility(View.INVISIBLE);
//                        imgView.setImageBitmap(null);
                    }
                });
    }

    private void invisible() {
        btnSignOut.setVisibility(View.INVISIBLE);
        btnUseMap.setVisibility(View.INVISIBLE);
        txtName.setVisibility(View.INVISIBLE);
        txtEmail.setVisibility(View.INVISIBLE);
        txtFirstName.setVisibility(View.INVISIBLE);
        txtEmail.setText("");
        txtFirstName.setText("");
        txtName.setText("");
    }

    private void visible(){
        loginButton.setVisibility(View.INVISIBLE);
        btnSignOut.setVisibility(View.VISIBLE);
        btnUseMap.setVisibility(View.VISIBLE);
        txtName.setVisibility(View.VISIBLE);
        txtEmail.setVisibility(View.VISIBLE);
        txtFirstName.setVisibility(View.VISIBLE);
    }

    private void setLogin_Button() {
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                result();
                visible();
                btnLoginGG.setVisibility(View.INVISIBLE);
                imgAvtGG.setVisibility(View.INVISIBLE);
                profilePictureView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private void result() {

        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    name = object.getString("name");
                    email = object.getString("email");
                    firstName = object.getString("first_name");

                    txtEmail.setText(email);
                    txtName.setText(name);
                    txtFirstName.setText(firstName);
                    profilePictureView.setProfileId(Profile.getCurrentProfile().getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,email,first_name");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }

    private void AnhXa(){
        loginButton = findViewById(R.id.login_button);
        txtEmail = findViewById(R.id.txtEmail);
        txtName = findViewById(R.id.txtName);
        txtFirstName = findViewById(R.id.txtFirstName);
        profilePictureView = findViewById(R.id.ProfilePictureView);
        btnSignOut = findViewById(R.id.btnSignOut);
        btnLoginGG = findViewById(R.id.btnLoginGG);
        btnUseMap = findViewById(R.id.btnUseMap);
        imgAvtGG = findViewById(R.id.imgAvtGG);

        btnLoginGG.setSize(SignInButton.SIZE_STANDARD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            txtName.setText(account.getDisplayName());
            txtEmail.setText(account.getEmail());
            txtFirstName.setText(account.getGivenName());
            Picasso.get().load(account.getPhotoUrl()).into(imgAvtGG);

            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("ok", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    @Override
    protected void onStart() {
        LoginManager.getInstance().logOut();
        super.onStart();
    }
}
