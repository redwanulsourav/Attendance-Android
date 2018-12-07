package com.example.redwanulsourav.attendance;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class ShowStudentStatistics extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_student_statistics);

        PieChartView pieChartView = findViewById(R.id.chart);
        List<SliceValue> pieData = new ArrayList<>();
        pieData.add(new SliceValue(15, Color.BLUE));
        pieData.add(new SliceValue(25, Color.GRAY));
        pieData.add(new SliceValue(19, Color.RED));
        pieData.add(new SliceValue(60, Color.MAGENTA));

        PieChartData pieChartData = new PieChartData(pieData);
        pieChartView.setPieChartData(pieChartData);

    }
}
