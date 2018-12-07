package com.example.redwanulsourav.attendance;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ShowStudentList extends AppCompatActivity {

    private CoursePOJO course;
    private DatabaseReference db;
    private ArrayList<String> uids = new ArrayList<String>();
    private ArrayList<Person> persons = new ArrayList<>();
    private ShowStudentCustomAdapter adap;
    private ListView listView;
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
        public View getView(int position, View convertView, ViewGroup parent){
            View listItem = convertView;
            if(listItem == null){
                listItem = LayoutInflater.from(mContext).inflate(R.layout.show_student_list_elem,parent,false);
            }
            final Person current = dataSet.get(position);

            TextView tv = (TextView) listItem.findViewById(R.id.student_name);
            TextView tv2 = (TextView) listItem.findViewById(R.id.student_roll);

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
        setContentView(R.layout.activity_show_student_list);
        course = new Gson().fromJson(getIntent().getStringExtra("courseInfo"), CoursePOJO.class);
        db = FirebaseDatabase.getInstance().getReference();
        listView = findViewById(R.id.listView3);

        db.child("course_students").child(course.getCourseNumber()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            uids.add(ds.getValue(String.class));
                            String uid = ds.getValue(String.class);
                            Log.d("hello",ds.getValue(String.class));

                            db.child(uid).addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Person temp = new Person();
                                            temp.setFirstName(dataSnapshot.child("first_name").getValue(String.class));
                                            temp.setLastName(dataSnapshot.child("last_name").getValue(String.class));
                                            temp.setEmail(dataSnapshot.child("email").getValue(String.class));
                                            temp.setRoll(dataSnapshot.child("roll").getValue(String.class));
                                            persons.add(temp);
                                            adap = new ShowStudentCustomAdapter(ShowStudentList.this,persons);
                                            listView.setAdapter(adap);
                                            Log.d("hello","first_name: "+temp.getFirstName());
                                            Log.d("hello","last_name: "+temp.getLastName());
                                            Log.d("hello","roll: "+temp.getRoll());
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
