package com.cybernatica.ngale.loginui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity implements OnClickListener {

    private TextView tvSignUpSuggest, tvLoginForgot;
    private TextInputEditText etLoginEmail, etLoginPassword;
    private Button btnLoginSignin;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupUi();

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        checkUserIsSignedIn();
    }

    private void setupUi() {
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);

        btnLoginSignin = findViewById(R.id.btnLoginSignin);
        btnLoginSignin.setOnClickListener(this);

        tvLoginForgot = findViewById(R.id.tvLoginForgot);
        tvLoginForgot.setOnClickListener(this);

        tvSignUpSuggest = findViewById(R.id.tvSignUpSuggest);
        tvSignUpSuggest.setOnClickListener(this);
    }

    private void checkUserIsSignedIn() {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            finish();
            startActivity(new Intent(this, SecondActivity.class));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvSignUpSuggest:
                registerNewAccount();
                break;
            case R.id.tvLoginForgot:
                resetUserPassword();
                break;
            case R.id.btnLoginSignin:
                signIn();
                break;
        }
    }

    private void signIn() {
        String email = etLoginEmail.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();

        progressDialog.setMessage("I'll even share my cat with you");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    checkEmailVerification();
                } else {
                    Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerNewAccount() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void checkEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        boolean emailFlag = firebaseUser.isEmailVerified();

        if (emailFlag) {
            finish();
            startActivity(new Intent(this, SecondActivity.class));
        } else {
            Toast.makeText(this, "Confirm your email, please", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }

    private void resetUserPassword() {
        startActivity(new Intent(this, ResetPassword.class));
    }
}
