package com.example.redwanulsourav.attendance;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText emailField;
    private EditText passwordField;
    private String userType;
    private FirebaseUser user;
    private Map <String,String> map = new HashMap<>();

    private void init(){
        loginButton = findViewById(R.id.login_button);
        emailField = findViewById(R.id.email_edit);
        passwordField = findViewById(R.id.password_edit);

        loginButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GlobalData.mAuth.signInWithEmailAndPassword(emailField.getText().toString(),passwordField.getText().toString()).
                                addOnSuccessListener(
                                        new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {
                                                user = GlobalData.mAuth.getCurrentUser();
                                                GlobalData.database.child(user.getUid()).child("type").addListenerForSingleValueEvent(
                                                        new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                String userType = dataSnapshot.getValue(String.class);
                                                                Log.d("hello",userType);
                                                                if(userType == "Student"){
                                                                    GlobalData.loggedInUser = new Student();
                                                                }
                                                                else if(userType == "Teacher"){
                                                                    GlobalData.loggedInUser = new Teacher();
                                                                }
                                                                else{
                                                                    GlobalData.loggedInUser = new Admin();
                                                                }
                                                                GlobalData.database.child(user.getUid()).child("first_name").addListenerForSingleValueEvent(
                                                                        new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                GlobalData.loggedInUser.setFirstName(dataSnapshot.getValue(String.class));
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                            }
                                                                        }
                                                                );

                                                                GlobalData.database.child(user.getUid()).child("last_name").addListenerForSingleValueEvent(
                                                                        new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                GlobalData.loggedInUser.setLastName(dataSnapshot.getValue(String.class));
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                            }
                                                                        }
                                                                );
                                                                GlobalData.database.child(user.getUid()).child("email").addListenerForSingleValueEvent(
                                                                        new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                GlobalData.loggedInUser.setEmail(dataSnapshot.getValue(String.class));
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                            }
                                                                        }
                                                                );
                                                                GlobalData.database.child(user.getUid()).child("courses").addListenerForSingleValueEvent(
                                                                        new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                Map <String,String> map = new HashMap<>();
                                                                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                                                                    map.put(ds.getKey(),ds.getValue(String.class));
                                                                                }
                                                                                GlobalData.loggedInUser.setCourses(map);


                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                            }
                                                                        }
                                                                );
                                                                GlobalData.database.child("course_details").addListenerForSingleValueEvent(
                                                                        new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                GlobalData.course_details = new HashMap<>();
                                                                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                                                                    GlobalData.course_details.put(ds.getKey(),ds.getValue(String.class));
                                                                                    Log.d("hello",ds.getKey() + ": " +ds.getValue(String.class));
                                                                                }
                                                                                GlobalData.loggedInUser.setId(user.getUid());
                                                                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                                                                startActivity(intent);

                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                            }
                                                                        }
                                                                );


                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        }
                                                );
                                            }
                                        }
                                ).
                                addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                getErrorDialog("Email or password don't match",LoginActivity.this).show();
                                            }
                                        }
                                );

                    }
                }
        );
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private AlertDialog.Builder getErrorDialog(String message, Context context){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(getString(R.string.app_name)).setMessage(message);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return alertDialog;
    }
}
