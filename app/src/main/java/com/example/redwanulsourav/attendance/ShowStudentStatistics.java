package com.example.redwanulsourav.attendance;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class ShowStudentStatistics extends AppCompatActivity {
    Person person;
    private TextView studentNameField,studentRollField;
    ImageView studentPicture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_student_statistics);
        person = new Gson().fromJson(getIntent().getStringExtra("studentInfo"), Person.class);

        studentNameField = findViewById(R.id.student_name2);
        studentRollField = findViewById(R.id.student_roll2);
        studentPicture = findViewById(R.id.imageView3);
        studentNameField.setText(person.getFirstName());
        studentRollField.setText(person.getRoll());
        if(person.getImage() != null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(person.getImage(),0,person.getImage().length);
            studentPicture.setImageBitmap(bitmap);
        }

        PieChartView pieChartView = findViewById(R.id.chart);
        List<SliceValue> pieData = new ArrayList<>();
        pieData.add(new SliceValue(10, 0xFF009900));
        pieData.add(new SliceValue(10, 0xFFAA0000));

        PieChartData pieChartData = new PieChartData(pieData);
        pieChartView.setPieChartData(pieChartData);

    }
}
