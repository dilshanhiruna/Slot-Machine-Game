package User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.oop.slot_machine_game.MainActivity;
import com.oop.slot_machine_game.R;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    EditText password, email;
    Button signup,signininstead;
    FirebaseAuth fAuth;
    String userID;
    private static final String TAG = "REG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email= findViewById(R.id.emailReginput);
        password= findViewById(R.id.passwordReginput);
        signup= findViewById(R.id.signupbtn);
        signininstead= findViewById(R.id.signininstead);

        fAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        signininstead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String memail = email.getText().toString().trim();
                String mpassword = password.getText().toString().trim();

                if(TextUtils.isEmpty(memail)){
                    email.setError("Email is required");
                    return;
                }
                if(TextUtils.isEmpty(mpassword)){
                    password.setError("Password is required");
                    return;
                }
                if(mpassword.length() < 6){
                    password.setError("Password must greater than 4 characters");
                    return;
                }

                fAuth.createUserWithEmailAndPassword(memail,mpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull  Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Register.this, "Registered Successfully", Toast.LENGTH_SHORT).show();

                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference =db.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("points", 1000);

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG,"user profile is created for "+ userID);
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();

                        }


                    }





                });

            }
        });




    }
}