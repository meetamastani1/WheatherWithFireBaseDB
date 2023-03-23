package com.weather.firebaseauth.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.weather.firebaseauth.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding activityLoginBinding;
    private FirebaseAuth mAuth;
    private String loginemail, loginpassword;
    private long lastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //bind view for activity
        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = activityLoginBinding.getRoot();
        setContentView(view);

        initView();
        initClick();
    }

    private void initClick() {
        activityLoginBinding.btnSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });
        activityLoginBinding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - lastClickTime < 3000){
                    return;
                }

                lastClickTime = SystemClock.elapsedRealtime();
                if (!validateEmail() | !validatePassword()) {
                    return;
                }
                //    progressbar VISIBLE
//                login_progress.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(loginemail, loginpassword).
                        addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //    progressbar GONE
//                                login_progress.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, ProfilePageActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    //    progressbar GONE
//                                    login_progress.setVisibility(View.GONE);
                                    Log.d("TAG", "onComplete: "+task.getException().toString());
                                    Toast.makeText(LoginActivity.this, "Email id or Password mismatch", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void initView() {

        //you can also put string from string.xml file
        activityLoginBinding.lytToolbar.toolbar.setTitle("Login to your app");
        //        Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        //if user already login
//        if (mAuth.getCurrentUser()!=null){
//            Intent intent = new Intent(LoginActivity.this, ProfilePageActivity.class);
//            startActivity(intent);
//        }
    }

    private boolean validateEmail() {
        loginemail = activityLoginBinding.edtEmail.getText().toString().trim();
        if (TextUtils.isEmpty(loginemail)) {
            Toast.makeText(LoginActivity.this, "Enter Your Email", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(loginemail).matches()) {
            Toast.makeText(LoginActivity.this, "Please enter valid Email", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validatePassword() {
        loginpassword = activityLoginBinding.edtPass.getText().toString().trim();
        if (TextUtils.isEmpty(loginpassword)) {
            Toast.makeText(LoginActivity.this, "Enter Your Password", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}