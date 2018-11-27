package com.cybernatica.ngale.loginui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CheckableImageButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity implements OnClickListener {

    static final String TAG = "RegLog";

    private TextView tvSignInSuggest;
    private TextInputEditText etRegEmail, etRegPassword, etRegConfirm;
    private Button btnRegSignup;
    private TextInputLayout tilRegPassword, tilRegConfirm;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setupUi();

        firebaseAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvSignInSuggest:
                loginExistingAccount();
                break;
            case R.id.btnRegSignup:
                createNewAccount();
                break;
        }
    }

    private void setupUi() {
        tvSignInSuggest = findViewById(R.id.tvSignInSuggest);
        tvSignInSuggest.setOnClickListener(this);

        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPassword = findViewById(R.id.etRegPassword);
        etRegConfirm = findViewById(R.id.etRegConfirm);

        btnRegSignup = findViewById(R.id.btnRegSignup);
        btnRegSignup.setOnClickListener(this);

        bindPasswordFields();
    }

    private void loginExistingAccount() {
        finish();
    }

    private void createNewAccount() {
        if (validate()) {
            String email = etRegEmail.getText().toString().trim();
            String password = etRegPassword.getText().toString().trim();

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        sendEmailVerification();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @SuppressLint("RestrictedApi")
    private void bindPasswordFields() {
        tilRegPassword = findViewById(R.id.tilRegPassword);
        tilRegConfirm = findViewById(R.id.tilRegConfirm);

        final View toggleInitPassword = findTogglePasswordButton(tilRegPassword);
        final View toggleConfirmPassword = findTogglePasswordButton(tilRegConfirm);
        final Boolean[] cat = new Boolean[2];

        if (toggleInitPassword != null) {
            toggleInitPassword.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    tilRegPassword.passwordVisibilityToggleRequested(false);
                    tilRegConfirm.passwordVisibilityToggleRequested(false);
                }
            });
        }
    }

    private View findTogglePasswordButton(ViewGroup viewGroup) {
        int childCount = viewGroup.getChildCount();
        for (int ind = 0; ind < childCount; ind++) {
            View child = viewGroup.getChildAt(ind);
            if (child instanceof ViewGroup) {
                View togglePasswordButton = findTogglePasswordButton((ViewGroup) child);
                if (togglePasswordButton != null) {
                    return togglePasswordButton;
                }
            } else if (child instanceof CheckableImageButton) {
                return child;
            }
        }
        return null;
    }

    private boolean validate() {
        boolean result = false;

        String email = etRegEmail.getText().toString();
        String password = etRegPassword.getText().toString();
        String confirm = etRegConfirm.getText().toString();

        if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirm)) {
            Toast.makeText(this, "Passwords are not equal", Toast.LENGTH_SHORT).show();
        } else {
            result = true;
        }

        return result;
    }

    private void sendEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this,
                                "Registered successfully, check your email to verify the account",
                                Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Verification failed",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
