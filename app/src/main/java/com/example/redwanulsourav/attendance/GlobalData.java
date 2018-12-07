package com.example.redwanulsourav.attendance;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class GlobalData {
    public static Person loggedInUser;
    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    public static  FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static Map<String,String> course_details = new HashMap<>();


}
