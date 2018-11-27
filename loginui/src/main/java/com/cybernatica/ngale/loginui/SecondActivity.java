package com.cybernatica.ngale.loginui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SecondActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static final String TAG = "SecTag";

    private TextView tvProfileEmail;

    private NavigationView navigationView;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        initNavigationView();

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String email = firebaseUser.getEmail();

        tvProfileEmail.setText("User: " + email);
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
        }
        return true;
    }

    private void signOut() {
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }
}
