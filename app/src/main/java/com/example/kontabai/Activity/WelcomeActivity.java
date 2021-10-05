package com.example.kontabai.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.kontabai.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WelcomeActivity extends AppCompatActivity {
    DatabaseReference databaseReference;
    String signed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        databaseReference=FirebaseDatabase.getInstance().getReference().child("OnlyUsers").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Handler handler=new Handler();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser==null){
            handler.postDelayed(() -> {
                startActivity(new Intent(WelcomeActivity.this,MobileRegistration.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            },1000);
        }  if(firebaseUser!=null){
            handler.postDelayed(() -> {
                startActivity(new Intent(WelcomeActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            },1000);
        }

    }
}