package com.cybernatica.ngale.loginui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class MainActivity extends AppCompatActivity implements OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final int RC_GOOGLE_SIGN_IN = 9001;

    private TextView tvSignUpSuggest, tvLoginForgot;
    private TextInputEditText etLoginEmail, etLoginPassword;
    private Button btnLoginSignin, btnLoginGoogle;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupUI();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        checkUserIsSignedIn();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed
                Log.d(TAG, "Google Sign In failed", e);
            }
        }
    }

    private void setupUI() {
        progressDialog = new ProgressDialog(this);

        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);

        btnLoginSignin = findViewById(R.id.btnLoginSignin);
        btnLoginSignin.setOnClickListener(this);

        btnLoginGoogle = findViewById(R.id.btnLoginGoogle);
        btnLoginGoogle.setOnClickListener(this);

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

    private void showProgressDialog() {
        progressDialog.setMessage("I'll even share my cat with you");
        progressDialog.show();
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
            case R.id.btnLoginGoogle:
                signInGoogle();
                break;
        }
    }

    private void signIn() {
        String email = etLoginEmail.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();

        showProgressDialog();

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

    private void signInGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
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

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle: " + account.getId());
        showProgressDialog();
        AuthCredential credential;
        try {
            credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        } catch (RuntimeException ex) {
            Log.d(TAG, ex.getMessage());
            return;
        }
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign In success
                            Log.d(TAG, "signInWithCredential: success");
                            startActivity(new Intent(MainActivity.this, SecondActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user
                            Log.d(TAG, "signInWithCredential: failure", task.getException());
                        }
                    }
                });
    }
}
