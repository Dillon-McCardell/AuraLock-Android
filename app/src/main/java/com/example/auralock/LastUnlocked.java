package com.example.auralock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Date;

public class LastUnlocked extends AppCompatActivity {
    ImageView imageView;
    TextView textName, textTime;
    Button btnHome;
    String name, time;

    private StorageReference mStoreImage;
    private DatabaseReference mRefName;
    private StorageReference mFindImage;
    private DatabaseReference mFindTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_unlocked);

        imageView = findViewById(R.id.imageView);
        textName = findViewById(R.id.textName);
        btnHome = findViewById(R.id.btnHome2);
        textTime = findViewById(R.id.txtFaceTime);

        mRefName = FirebaseDatabase.getInstance().getReference("AuraLock Data")
                .child("newUnlockName");
        mStoreImage = FirebaseStorage.getInstance().getReference("AuraLock Data")
                .child("Images to App/");
        mFindTime = FirebaseDatabase.getInstance().getReference("AuraLock Data")
                .child("datetime");

        mFindTime.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    time = snapshot.getValue().toString();
                    textTime.setText("Last Unlocked: " + time);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        mRefName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    name = snapshot.getValue().toString();
                    textName.setText("Unlocked By: " + name);
                    mFindImage = mStoreImage.child(name);
                    long MAXBYTES = 1024*1024;
                    mFindImage.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            //convert byte[] to bitmap
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            imageView.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });
    }
}