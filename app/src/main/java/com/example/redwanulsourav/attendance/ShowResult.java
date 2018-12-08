package com.example.redwanulsourav.attendance;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class ShowResult extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DatabaseReference db2;

    private DocumentReference ref;
    private boolean sentRequest;
    private ArrayList<String> uids = new ArrayList<String>();
    private ArrayList<Person> persons = new ArrayList<>();
    private ListView listView;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profile_images");
    private ShowStudentCustomAdapter adap;
    private Map<String,String> decoderMap;
    private class ShowStudentCustomAdapter extends ArrayAdapter<Person> {
        Context mContext;
        ArrayList<Person> dataSet;

        ShowStudentCustomAdapter(Context mContext, ArrayList<Person> data) {
            super(mContext, 0, data);
            this.dataSet = data;
            this.mContext = mContext;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View listItem = convertView;
            if (listItem == null) {
                listItem = LayoutInflater.from(mContext).inflate(R.layout.show_student_list_elem, parent, false);
            }
            final Person current = dataSet.get(position);

            TextView tv = (TextView) listItem.findViewById(R.id.student_name);
            TextView tv2 = (TextView) listItem.findViewById(R.id.student_roll);
            ImageView imageView = (ImageView) listItem.findViewById(R.id.student_image);
            Bitmap bitmap;

            if (current.getImage() != null) {
                Log.d("hello", "entered1");
                bitmap = BitmapFactory.decodeByteArray(current.getImage(), 0, current.getImage().length);
                Log.d("hello", "entered2");
                imageView.setImageBitmap(bitmap);
                Log.d("hello", "entered3");

            }

            tv.setText(current.getFirstName());
            tv2.setText(current.getRoll());

            /*
            Button button = (Button) listItem.findViewById(R.id.item_view_button);
            button.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(),ViewCourse.class);
                            Gson gson = new Gson();
                            intent.putExtra("courseInfo",gson.toJson(current));
                            startActivity(intent);
                        }
                    }
            );
            */
            return listItem;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);
        ref = db.collection("server_status").document("status");
        db2 = FirebaseDatabase.getInstance().getReference();
        listView = findViewById(R.id.listView4);
        ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.d("hello","Listen failed: ",e);
                    return;
                }

                if(documentSnapshot != null && documentSnapshot.exists()){
                    Log.d("hello","Currend data: " + documentSnapshot.getData());
                    Log.d("hello"," "+documentSnapshot.get("done"));
                    if((boolean)documentSnapshot.get("done") == true){
                        Log.d("hello","entered");
                        String sids = documentSnapshot.get("ids").toString();
                        Log.d("hello",sids);

                        String temp = "";
                        StringBuilder sb = new StringBuilder();
                        for(int i = 0; i < sids.length(); i++){
                            if(sids.charAt(i) == ','){
                                temp = sb.toString();
                                sb = new StringBuilder();
                                Log.d("hello",temp);
                                uids.add(temp);
                                temp = "";

                            }
                            else{
                                sb.append(sids.charAt(i));
                                //temp = temp + sids.charAt(i);
                            }
                        }
                        db2.child("decode_id").addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Log.d("hello","entered\n");
                                        decoderMap = new HashMap<>();
                                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                                            decoderMap.put(ds.getKey(),ds.getValue(String.class));
                                            Log.d("hello",ds.getKey() + ": "+ds.getValue());
                                        }
                                        for(final String uid: uids){
                                            db2.child(decoderMap.get(uid)).addListenerForSingleValueEvent(
                                                    new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            final Person temp = new Person();
                                                            temp.setFirstName(dataSnapshot.child("first_name").getValue(String.class));
                                                            temp.setLastName(dataSnapshot.child("last_name").getValue(String.class));
                                                            temp.setEmail(dataSnapshot.child("email").getValue(String.class));
                                                            temp.setRoll(dataSnapshot.child("roll").getValue(String.class));
                                                            temp.setHas_image(dataSnapshot.child("has_image").getValue(String.class));
                                            /*
                                            Log.d("hello",dataSnapshot.child("has_image").getValue(String.class));
                                            if(temp.getHas_image().equals("true")) {
                                                storageRef.child(uid + ".jpg").getBytes(5 * 1024 * 1024).addOnSuccessListener(
                                                        new OnSuccessListener<byte[]>() {
                                                            @Override
                                                            public void onSuccess(byte[] bytes) {
                                                                Log.d("hello", "came here on success");
                                                                Log.d("hello", "Success first_name: " + temp.getFirstName());
                                                                temp.setImage(bytes);
                                                                persons.add(temp);
                                                                adap = new ShowStudentCustomAdapter(ShowResult.this, persons);
                                                                listView.setAdapter(adap);

                                                            }

                                                            public void onFailure(byte[] bytes) {
                                                                Log.d("hello", "came here on failure");
                                                                Log.d("hello", "Failed first_name: " + temp.getFirstName());
                                                                temp.setImage(null);
                                                                persons.add(temp);
                                                                adap = new ShowStudentCustomAdapter(ShowResult.this, persons);
                                                                listView.setAdapter(adap);

                                                            }
                                                        }
                                                );
                                            }
                                            */
                                                            //else{
                                                            persons.add(temp);
                                                            adap = new ShowStudentCustomAdapter(ShowResult.this, persons);
                                                            listView.setAdapter(adap);
                                                            Log.d("hello","first_name: "+temp.getFirstName());
                                                            Log.d("hello","last_name: "+temp.getLastName());
                                                            Log.d("hello","roll: "+temp.getRoll());

                                                            //}
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    }
                                            );
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                }
                        );




                    }
                }
                else{
                    Log.d("hello","Current data: null");
                }
            }
        });

    }



}
