package com.example.fishkeeping.ui.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.fishkeeping.DatabaseHelper;
import com.example.fishkeeping.R;

import java.text.DateFormat;
import java.util.Calendar;

public class AddToDo extends AppCompatActivity {

    EditText todo_title, todo_notes, deadline, reminder_time;
    Switch notify;
    Button add_button;
    String on_off;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_do);

        todo_title = findViewById(R.id.todoName);
        todo_notes = findViewById(R.id.todoNotes);
        deadline = findViewById(R.id.todo_deadline);
        notify = (Switch) findViewById(R.id.notificationSwitch);
        reminder_time = findViewById(R.id.todo_time);
        add_button = findViewById(R.id.btnAdd);

        final Calendar calendar= Calendar.getInstance();
        final int year=calendar.get(Calendar.YEAR);
        final int month=calendar.get(Calendar.MONTH);
        final int day=calendar.get(Calendar.DAY_OF_MONTH);
        final int hour=calendar.get(Calendar.HOUR);
        final int minute=calendar.get(Calendar.MINUTE);

        deadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog=new DatePickerDialog(AddToDo.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        calendar.set(Calendar.YEAR,i);
                        calendar.set(Calendar.MONTH,i1);
                        calendar.set(Calendar.DAY_OF_MONTH,i2);
                        deadline.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime()));
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        reminder_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog=new TimePickerDialog(AddToDo.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        calendar.set(Calendar.HOUR_OF_DAY,i);
                        calendar.set(Calendar.MINUTE,i1);
                        String timeFormat= DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
                        reminder_time.setText(timeFormat);
                    }
                },hour,minute,false);
                timePickerDialog.show();
            }
        });


        reminder_time.setEnabled(false);
        on_off = "OFF";
        notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    reminder_time.setEnabled(true);
                    on_off = "ON";
                } else {
                    reminder_time.setText("");
                    reminder_time.setEnabled(false);
                    on_off = "OFF";
                }
            }
        });


        add_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DatabaseHelper myDB = new DatabaseHelper(AddToDo.this);

                boolean titleEmpty=todo_title.getText().toString().isEmpty();
                boolean dateEmpty=deadline.getText().toString().isEmpty();
                boolean timeEmpty=reminder_time.getText().toString().isEmpty();
                boolean noteEmpty=todo_notes.getText().toString().isEmpty();

                if(titleEmpty){
                    Toast.makeText(AddToDo.this, "Title required", Toast.LENGTH_SHORT).show();
                } else if(dateEmpty){
                    Toast.makeText(AddToDo.this, "Date required", Toast.LENGTH_SHORT).show();
                }
                else {
                    String time, note;
                    if (timeEmpty) {
                        time = "No time.";
                    } else {
                        time = reminder_time.getText().toString();
                    }
                    if (noteEmpty) {
                        note = "No notes.";
                    } else {
                        note = todo_notes.getText().toString();
                    }

                    String getTitle = todo_title.getText().toString().trim();
                    String getDate = deadline.getText().toString().trim();
                    String getNotification = on_off.trim();
                    String getTime = time.trim();
                    String getNotes = note.trim();
                    myDB.addTask(getTitle, getDate, getNotification, getTime, getNotes);
                }
            }
        });

    }
}