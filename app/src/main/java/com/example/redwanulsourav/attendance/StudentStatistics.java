package com.example.redwanulsourav.attendance;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;


public class StudentStatistics extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    View inflatedView;

    private class CoursesCustomAdapter extends ArrayAdapter<CoursePOJO>{
        Context mContext;
        ArrayList<CoursePOJO> dataSet;
        CoursesCustomAdapter(Context mContext, ArrayList<CoursePOJO> data) {
            super(mContext, 0, data);
            this.dataSet = data;
            this.mContext = mContext;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View listItem = convertView;
            if(listItem == null){
                listItem = LayoutInflater.from(mContext).inflate(R.layout.course_list_item,parent,false);
            }
            final CoursePOJO current = dataSet.get(position);

            TextView tv = (TextView) listItem.findViewById(R.id.item_textview);
            tv.setText(current.getCourseNumber());


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
            return listItem;
        }

    }
    public StudentStatistics() {
    }

    public static StudentStatistics newInstance(String param1, String param2) {
        StudentStatistics fragment = new StudentStatistics();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_student_statistics, container, false);

        ListView listView = (ListView) inflatedView.findViewById(R.id.listview2);
        ArrayList < CoursePOJO > arr = new ArrayList<>();

        for(Map.Entry<String,String> entry: GlobalData.loggedInUser.getCourses().entrySet()){
            arr.add(new CoursePOJO(GlobalData.course_details.get(entry.getValue()),entry.getValue()));
        }
        /*
        arr.add(new CoursePOJO("CSE3200","System Development Project"));
        arr.add(new CoursePOJO("CSE3201","Compiler Design"));
        arr.add(new CoursePOJO("HUM3207","Humanities & Government"));
        arr.add(new CoursePOJO("CSE3200","System Development Project"));
        */
        CoursesCustomAdapter customAdapter = new CoursesCustomAdapter(getContext(),arr);

        listView.setAdapter(customAdapter);
        return inflatedView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement com.example.redwanulsourav.attendance.OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
