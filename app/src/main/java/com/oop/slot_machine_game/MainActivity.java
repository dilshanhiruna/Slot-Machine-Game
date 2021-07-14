package com.oop.slot_machine_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Random;

import User.Login;

public class MainActivity extends AppCompatActivity {

    private TextView msg;
    private ImageView img1, img2, img3;
    private Wheel wheel1, wheel2, wheel3;
    private Button btn,logoutbtn;
    private boolean isStarted;
    String userID;
    public static final Random RANDOM = new Random();

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


        fAuth = FirebaseAuth.getInstance();
        //if user is already registered send to Home page
        if (fAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), Login.class));

            finish();
        }
        else {
            userID = fAuth.getCurrentUser().getUid();
        }

        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStarted) {
                    wheel1.stopWheel();
                    wheel2.stopWheel();
                    wheel3.stopWheel();

                    if (wheel1.currentIndex == wheel2.currentIndex && wheel2.currentIndex == wheel3.currentIndex) {
                        msg.setText("You win the big prize");
                    } else if (wheel1.currentIndex == wheel2.currentIndex || wheel2.currentIndex == wheel3.currentIndex
                            || wheel1.currentIndex == wheel3.currentIndex) {
                        msg.setText("Little Prize");
                    } else {
                        msg.setText("You lose");
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
                    }, 200, randomLong(0, 200));

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
                    }, 200, randomLong(150, 400));

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
                    }, 200, randomLong(150, 400));

                    wheel3.start();

                    btn.setText("Stop");
                    msg.setText("");
                    isStarted = true;
                }
            }
        });

//        request.time < timestamp.date(2021, 8, 13)


    }
}