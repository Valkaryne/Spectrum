package com.cybernatica.ngale.loginui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SecondActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static final String TAG = "SecTag";
    private static final String SAVED_LOGIN = "saved_login";
    private static final int PL_LINK_GOOGLE = 1025;
    private static final int PL_LINK_FACEBOOK = 1030;
    private static final int SA_LOGIN_EMAIL = 2010;
    private static final int SA_LOGIN_GOOGLE = 2020;
    private static final int SA_LOGIN_FACEBOOK = 2030;
    private static int loginProvider = 0;

    private SharedPreferences saPreferences;

    private TextView tvProfileEmail;
    private NavigationView navigationView;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Intent intent = getIntent();
        loginProvider = intent.getIntExtra("loginProvider", 0);

        if (loginProvider == 0)
            loadPreferences();

        initNavigationView();

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String email = firebaseUser.getEmail();

        tvProfileEmail.setText("User: " + email);

        if (loginProvider != 0)
            savePreferences();
    }

    private void savePreferences() {
        saPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = saPreferences.edit();
        editor.putInt(SAVED_LOGIN, loginProvider);
        editor.apply();
    }

    private void loadPreferences() {
        saPreferences = getPreferences(MODE_PRIVATE);
        loginProvider = saPreferences.getInt(SAVED_LOGIN, 0);
    }

    private void initNavigationView() {
        navigationView = findViewById(R.id.navigationView);
        navigationView.bringToFront();
        tvProfileEmail = navigationView.getHeaderView(0).findViewById(R.id.tvProfileEmail);

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.item_logout:
                signOut();
                break;
            case R.id.item_link_google:
                startActivity(new Intent(SecondActivity.this, ProviderLinker.class)
                        .putExtra("linker", PL_LINK_GOOGLE));
                break;
            case R.id.item_link_fb:
                startActivity(new Intent(SecondActivity.this, ProviderLinker.class)
                        .putExtra("linker", PL_LINK_FACEBOOK));
                break;
        }
        return true;
    }

    private void signOut() {
        firebaseAuth.signOut();
        switch (loginProvider) {
            case SA_LOGIN_FACEBOOK:
                LoginManager.getInstance().logOut();
                Log.d(TAG, "Muhr");
                break;
            case SA_LOGIN_GOOGLE:
                signOutGoogle();
                break;
            case SA_LOGIN_EMAIL: default:
                break;
        }
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    private void signOutGoogle() {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient client = GoogleSignIn.getClient(this, options);
        client.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // do nothing, just deal with it
                    }
                });
    }
}
