package com.example.redwanulsourav.attendance;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ViewCourse extends AppCompatActivity {

    private TextView courseNumber;
    private TextView courseName;
    private Button button1;
    private Button button2;
    final int REQUEST_CODE_ASK_PERMISSIONS_CAMERA = 100;
    final int REQUEST_CODE_ASK_PERMISSIONS_EXTERNAL_STORAGE = 200;
    ImageView imageView;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_course);
        CoursePOJO course = new Gson().fromJson(getIntent().getStringExtra("courseInfo"), CoursePOJO.class);
        db = FirebaseFirestore.getInstance();
        courseNumber = (TextView) findViewById(R.id.view_course_course_number);
        courseName = (TextView) findViewById(R.id.view_course_course_name);
        imageView = (ImageView)     findViewById(R.id.image123);

        courseNumber.setText(course.getCourseNumber());
        courseName.setText(course.getCourseName());

        button1 = (Button) findViewById(R.id.take_photo);
        button1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int hasPermission = ContextCompat.checkSelfPermission(ViewCourse.this, Manifest.permission.CAMERA);
                        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {

                            } else {
                                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_ASK_PERMISSIONS_CAMERA);
                            }
                        } else {
                            
                            Log.d("myMSG","Came here");
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent,1);

                        }


                    }
                }
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch(requestCode){
            case REQUEST_CODE_ASK_PERMISSIONS_CAMERA:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,1);
                } else {
                    Toast.makeText(ViewCourse.this,"Permission required",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == 1){
                Toast.makeText(ViewCourse.this,"Image Capture Done",Toast.LENGTH_SHORT).show();
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(bitmap );
                Uri uri;
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
                byte[] arr = bytes.toByteArray();
                String encoded = Base64.encodeToString(arr, Base64.DEFAULT);
                Map<String,Object> map = new HashMap<>();
                map.put("request",true);
                map.put("image_data",encoded);

                db.collection("status").document("device_status").set(map);
            }
        }
    }
}
