package com.example.auralock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import com.google.firebase.storage.StorageReference;

public class RealTimeImage extends AppCompatActivity {

    private Button btnHome6;
    private ImageView imageLive;
    private TextView textUpdate;
    private DatabaseReference mLiveImage;
    private DatabaseReference mLiveUpdate;
    private StorageReference mLiveStore, mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_image);

        imageLive = findViewById(R.id.imageView5);
        textUpdate = findViewById(R.id.textUpdate);
        btnHome6 = findViewById(R.id.btnHome6);

        mLiveImage = FirebaseDatabase.getInstance().getReference("AuraLock Data")
                .child("LiveCapture").child("captureProgress");
        mLiveStore = FirebaseStorage.getInstance().getReference("AuraLock Data")
                .child("Real Time Images/");
        mLiveUpdate = FirebaseDatabase.getInstance().getReference("AuraLock Data")
                .child("LiveCapture").child("newCapture");


        mLiveImage.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String updateState = snapshot.getValue().toString();
                    if(updateState.equals("working")){
                        textUpdate.setText("Retrieving Image...");
                    }
                    if(updateState.equals("complete")){
                        textUpdate.setText("Image Retrieved!");
                    }
                    if(updateState.equals("working")){
                        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                    }
                    if(updateState.equals("complete")){
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    }
                    mImage = mLiveStore.child("LiveCapture.jpg");
                    long MAXBYTES = 1024*1024;
                    mImage.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            //convert byte[] to bitmap
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            imageLive.setImageBitmap(bitmap);
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

        btnHome6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

    }
}