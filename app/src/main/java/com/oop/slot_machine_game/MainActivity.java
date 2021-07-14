package com.oop.slot_machine_game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import User.Login;
import User.Register;

public class MainActivity extends AppCompatActivity {

    private TextView msg;
    private ImageView img1, img2, img3;
    private Wheel wheel1, wheel2, wheel3;
    private Button btn,logoutbtn;
    private boolean isStarted;
    EditText betamount;
    TextView pointview;
    String userID;
    private static final String TAG = "REG";
    public static final Random RANDOM = new Random();
    Integer currentPoints =0;


    public static long randomLong(long lower, long upper) {
        return lower + (long) (RANDOM.nextDouble() * (upper - lower));
    }
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);
        img3 = (ImageView) findViewById(R.id.img3);
        btn = (Button) findViewById(R.id.startbutton);
        msg = (TextView) findViewById(R.id.messagetxt);
        logoutbtn = findViewById(R.id.logoutbtn);
        pointview = (TextView) findViewById(R.id.pointview);
        betamount = findViewById(R.id.betamount);


        fAuth = FirebaseAuth.getInstance();
        //if user is already registered send to Home page
        if (fAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), Login.class));

            finish();
        }
        else {
            userID = fAuth.getCurrentUser().getUid();
        }



        //get user points from db
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(userID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // Document found in the offline cache
                    DocumentSnapshot document = task.getResult();
                    currentPoints = document.getLong("points").intValue();
                    pointview.setText(currentPoints.toString());
                    //display you lose message
                    if (currentPoints.equals(1)){
                        pointview.setTextColor(Color.parseColor("#F40505"));
                        pointview.setText("You Lose!");
                    }
                    Log.d(TAG, "Cached document data: " + document.getData());
//                    Toast.makeText(getApplicationContext(), currentPoints.toString(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Cached get failed: ", task.getException());
                }
            }
        });


        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!betamount.getText().toString().equals("")){

                    if (currentPoints>Integer.parseInt(betamount.getText().toString())){

                        if(Integer.parseInt(betamount.getText().toString())<2) {
                            betamount.setError("Enter Amount more than 1");
                            return;
                        }

                        if (isStarted) {

                            wheel1.stopWheel();
                            wheel2.stopWheel();
                            wheel3.stopWheel();

                            if (wheel1.currentIndex == wheel2.currentIndex && wheel2.currentIndex == wheel3.currentIndex) {
                                msg.setText("Jack Spot");
                                //calculation
                                currentPoints= currentPoints+ Integer.parseInt(betamount.getText().toString());
                                updatePoints();


                            } else if (wheel1.currentIndex == wheel2.currentIndex || wheel2.currentIndex == wheel3.currentIndex
                                    || wheel1.currentIndex == wheel3.currentIndex) {
                                msg.setText("Half Prize");
                                if(Integer.parseInt(betamount.getText().toString())!=1) {
                                    //calculation
                                currentPoints= currentPoints+ Integer.parseInt(betamount.getText().toString()) / 2;
                                updatePoints();}
                            } else {
                                msg.setText("You lose");
                                //calculation
                                currentPoints= currentPoints - Integer.parseInt(betamount.getText().toString());
                                updatePoints();
                            }

                            btn.setText("Start");
                            isStarted = false;

                        } else {

                            wheel1 = new Wheel(new Wheel.WheelListener() {
                                @Override
                                public void newImage(final int img) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            img1.setImageResource(img);
                                        }
                                    });
                                }
                            }, 50, randomLong(0, 200));

                            wheel1.start();

                            wheel2 = new Wheel(new Wheel.WheelListener() {
                                @Override
                                public void newImage(final int img) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            img2.setImageResource(img);
                                        }
                                    });
                                }
                            }, 50, randomLong(100, 400));

                            wheel2.start();

                            wheel3 = new Wheel(new Wheel.WheelListener() {
                                @Override
                                public void newImage(final int img) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            img3.setImageResource(img);
                                        }
                                    });
                                }
                            }, 50, randomLong(100, 400));

                            wheel3.start();

                            btn.setText("Stop");
                            msg.setText("");
                            isStarted = true;
                        }
                    }else {
                        betamount.setError("Invalid Amount");
                    }

                }else {

                    betamount.setError("Enter Amount");
                }


            }
        });
    }

    //update coins in db
    void updatePoints(){
        pointview.setText(currentPoints.toString());
        Map<String,Object> user = new HashMap<>();
        user.put("points", currentPoints);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(userID);
       docRef.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
           @Override
           public void onSuccess(Void unused) {
               betamount.setText("");
           }
       });

        //display you lose message
       if (currentPoints==1){
           pointview.setTextColor(Color.parseColor("#F40505"));
           pointview.setText("You Lose!");
       }
    }

}