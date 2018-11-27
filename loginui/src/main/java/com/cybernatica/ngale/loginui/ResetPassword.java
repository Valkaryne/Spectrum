package com.cybernatica.ngale.loginui;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText etResetEmail;
    private Button btnResetPassword;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        initUi();

        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void initUi() {
        etResetEmail = findViewById(R.id.etResetEmail);

        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnResetPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnResetPassword:
                resetPassword();
                break;
        }
    }

    private void resetPassword() {
        String email = etResetEmail.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your account email", Toast.LENGTH_SHORT).show();
        } else {
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ResetPassword.this, "Password reset link has been sent", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ResetPassword.this, "Password reset link sending error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
