package com.example.admin.prototypekidzup1;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by ADMIN on 27.11.2017.
 */

public class ScheduleMain extends Fragment {



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.schedule_main, container, false);
    }

    public void applyChanges(int dayNum) {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("Local DB", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        String day = days[dayNum];
        EditText editText;
        String subj = day + "_slot1";
        String time = subj + "_time";

        editText = getActivity().findViewById(R.id.monday_slot1_subject);
        editor.putString(subj,editText.getText().toString());
        editText = getActivity().findViewById(R.id.monday_slot1_time);
        editor.putString(time,editText.getText().toString());


        subj = day + "_slot2";
        time = subj + "_time";
        editText = getActivity().findViewById(R.id.monday_slot2_subject);
        editor.putString(subj,editText.getText().toString());
        editText = getActivity().findViewById(R.id.monday_slot2_time);
        editor.putString(time,editText.getText().toString());


        subj = day + "_slot3";
        time = subj + "_time";
        editText = getActivity().findViewById(R.id.monday_slot3_subject);
        editor.putString(subj,editText.getText().toString());
        editText = getActivity().findViewById(R.id.monday_slot3_time);
        editor.putString(time,editText.getText().toString());



        subj = day + "_slot4";
        time = subj + "_time";
        editText = getActivity().findViewById(R.id.monday_slot4_subject);
        editor.putString(subj,editText.getText().toString());
        editText = getActivity().findViewById(R.id.monday_slot4_time);
        editor.putString(time,editText.getText().toString());


        subj = day + "_slot5";
        time = subj + "_time";
        editText = getActivity().findViewById(R.id.monday_slot5_subject);
        editor.putString(subj,editText.getText().toString());
        editText = getActivity().findViewById(R.id.monday_slot5_time);
        editor.putString(time,editText.getText().toString());


        subj = day + "_slot6";
        time = subj + "_time";
        editText = getActivity().findViewById(R.id.monday_slot6_subject);
        editor.putString(subj,editText.getText().toString());
        editText = getActivity().findViewById(R.id.monday_slot6_time);
        editor.putString(time,editText.getText().toString());


        subj = day + "_slot7";
        time = subj + "_time";
        editText = getActivity().findViewById(R.id.monday_slot7_subject);
        editor.putString(subj,editText.getText().toString());
        editText = getActivity().findViewById(R.id.monday_slot7_time);
        editor.putString(time,editText.getText().toString());

        editor.apply();

    }

    public void applyDayOfTheWeekSchedule(int dayNum) {

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("Local DB", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        String day = days[dayNum];
        EditText editText;
        String id_name;

        TextView textView = getActivity().findViewById(R.id.mondayHeader);
        textView.setText(day);

        String slot = day + "_slot1";
        String subject = sharedPreferences.getString(slot,"Subject");
        editText = getActivity().findViewById(R.id.monday_slot1_subject);
        editText.setText(subject,TextView.BufferType.EDITABLE);

        slot = day + "_slot2";
        subject = sharedPreferences.getString(slot,"Subject");
        editText = getActivity().findViewById(R.id.monday_slot2_subject);
        editText.setText(subject,TextView.BufferType.EDITABLE);

        slot = day + "_slot3";
        subject = sharedPreferences.getString(slot,"Subject");
        editText = getActivity().findViewById(R.id.monday_slot3_subject);
        editText.setText(subject,TextView.BufferType.EDITABLE);

        slot = day + "_slot4";
        subject = sharedPreferences.getString(slot,"Subject");
        editText = getActivity().findViewById(R.id.monday_slot4_subject);
        editText.setText(subject,TextView.BufferType.EDITABLE);

        slot = day + "_slot5";
        subject = sharedPreferences.getString(slot,"Subject");
        editText = getActivity().findViewById(R.id.monday_slot5_subject);
        editText.setText(subject,TextView.BufferType.EDITABLE);

        slot = day + "_slot6";
        subject = sharedPreferences.getString(slot,"Subject");
        editText = getActivity().findViewById(R.id.monday_slot6_subject);
        editText.setText(subject,TextView.BufferType.EDITABLE);

        slot = day + "_slot7";
        subject = sharedPreferences.getString(slot,"Subject");
        editText = getActivity().findViewById(R.id.monday_slot7_subject);
        editText.setText(subject,TextView.BufferType.EDITABLE);


    }

    int pos = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Spinner spinner;
        Button applyScheduleChanges = getActivity().findViewById(R.id.applyChangesSchedule);

        spinner = (Spinner) getActivity().findViewById(R.id.spinner1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pos = i;
                applyDayOfTheWeekSchedule(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        applyScheduleChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyChanges(pos);
            }
        });

    }
}
