package com.example.kontabai.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.kontabai.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MobileRegistration extends AppCompatActivity {
    EditText mobileNumber, verificationCode;
    Button nextButton, submitButton;
    FirebaseAuth firebaseAuth;
    RelativeLayout relativeLayout1,relativeLayout2;
    String verificationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_registration);
        initViews();
        nextButton.setOnClickListener(view -> {
            String number = mobileNumber.getText().toString();
            if (mobileNumber.getText().toString().equals("")) {
                mobileNumber.setError("Enter the mobile number!");
            } else if (mobileNumber.getText().toString().length() != 10) {
                mobileNumber.setError("Enter the valid number!");
            } else {
                registrationUserMobile(number);
                relativeLayout1.setVisibility(View.GONE);
                relativeLayout2.setVisibility(View.VISIBLE);
            }
        });

        submitButton.setOnClickListener(view -> {
            String verification=verificationCode.getText().toString();
            if(verification.equals("")){
                verificationCode.setError("Enter verification code!");
            }else if(verification.length()!=6){
                verificationCode.setError("Enter valid code!");
            }else {
                verifyCode(verification);
            }
        });
    }

    private void registrationUserMobile(String number) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth).
                setPhoneNumber("+91" + number)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallBacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            final String code= phoneAuthCredential.getSmsCode();
            if(code!=null){
                verificationCode.setText(code);
            }
        }
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId=s;
        }
        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            relativeLayout1.setVisibility(View.VISIBLE);
            relativeLayout2.setVisibility(View.GONE);
            Toast.makeText(MobileRegistration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential authCredential=PhoneAuthProvider.getCredential(verificationId,code);
        signWithPhoneCredentail(authCredential);
    }

    private void signWithPhoneCredentail(PhoneAuthCredential authCredential)
    {
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(MobileRegistration.this,MainActivity.class)
                       .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                }else{
                    relativeLayout1.setVisibility(View.VISIBLE);
                    relativeLayout2.setVisibility(View.GONE);
                    Toast.makeText(MobileRegistration.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void initViews() {
        mobileNumber = findViewById(R.id.Mobilenumber);
        verificationCode = findViewById(R.id.verificationCode);
        submitButton = findViewById(R.id.submitButton);
        nextButton = findViewById(R.id.nextButton);
        firebaseAuth = FirebaseAuth.getInstance();
        relativeLayout1=findViewById(R.id.relativeLayout1);
        relativeLayout2=findViewById(R.id.relativeLayout2);
    }
}