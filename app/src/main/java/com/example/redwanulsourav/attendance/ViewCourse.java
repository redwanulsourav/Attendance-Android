package com.example.redwanulsourav.attendance;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.drm.DrmStore;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ViewCourse extends AppCompatActivity {

    private TextView courseNumber;
    private TextView courseName;
    private Button button1;
    private Button button2;
    final int REQUEST_CODE_ASK_PERMISSIONS_CAMERA = 100;
    final int REQUEST_CODE_ASK_PERMISSIONS_READ_EXTERNAL_STORAGE = 200;
    final int REQUEST_CODE_ASK_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 300;

    ImageView imageView;
    private Uri imageUri;
    private String mCurrentPhotoPath;
    private FirebaseFirestore db;

    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"Camera");

        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        mCurrentPhotoPath = "file:"+image.getAbsolutePath();
        return image;
    }
    private void startCameraIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try{
            imageUri = FileProvider.getUriForFile(ViewCourse.this,
                    BuildConfig.APPLICATION_ID+ ".provider",
                    createImageFile());

        }
        catch(IOException ex){

        }

        if(imageUri != null) {


            //intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, 1);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_course);
        db = FirebaseFirestore.getInstance();
        CoursePOJO course = new Gson().fromJson(getIntent().getStringExtra("courseInfo"), CoursePOJO.class);
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
                            hasPermission = ContextCompat.checkSelfPermission(ViewCourse.this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            if(hasPermission != PackageManager.PERMISSION_GRANTED) {
                                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                                } else {
                                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
                                }
                            }
                            else {
                                Log.d("myMSG", "Came here");
                                startCameraIntent();
                            }
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
                    int hasPermission = ContextCompat.checkSelfPermission(ViewCourse.this,Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    if(hasPermission != PackageManager.PERMISSION_GRANTED){
                        if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){

                        }
                        else{
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE_ASK_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
                        }
                    }
                    else{
                        startCameraIntent();
                    }

                } else {
                    Toast.makeText(ViewCourse.this,"Permission required",Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_ASK_PERMISSIONS_WRITE_EXTERNAL_STORAGE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startCameraIntent();
                }
                else{
                    Toast.makeText(ViewCourse.this,"External Storage Permission required",Toast.LENGTH_SHORT).show();
                }
                break;
            default:

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
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
                /*
                Toast.makeText(ViewCourse.this,"Image Capture Done",Toast.LENGTH_SHORT).show();
                Uri uri = Uri.parse(mCurrentPhotoPath);
                File file = new File(uri.getPath());

                try{
                    Log.d("hello","came here");
                    InputStream ims = new FileInputStream(file);
                    Bitmap bitmap = BitmapFactory.decodeStream(ims);
                    ims.close();
                    imageView.setImageBitmap(bitmap );
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,50,bytes);
                    byte[] arr = bytes.toByteArray();
                    String encoded = Base64.encodeToString(arr, Base64.DEFAULT);
                    Map<String,Object> map = new HashMap<>();
                    map.put("request",true);
                    map.put("image_data",encoded);
                    Log.d("hello","about to upload");
                    GlobalData.db.collection("status").document("device_status").set(map).
                        addOnSuccessListener(
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("hello","successful");
                                    }
                                }
                        );
                }
                catch(Exception ex){

                }
                */
            }
        }
    }
}
