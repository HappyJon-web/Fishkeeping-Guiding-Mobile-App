package com.example.fishkeeping.ui.todo;

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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fishkeeping.DatabaseHelper;
import com.example.fishkeeping.R;

import java.text.DateFormat;
import java.util.Calendar;

public class UpdateToDo extends AppCompatActivity {

    EditText todo_title, todo_notes, todo_deadline, reminder_time;
    Switch notify;
    Button update_button;
    int id;
    String title, date, time, notification, notes, on_off;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_task);

        todo_title = findViewById(R.id.update_todoName);
        todo_notes = findViewById(R.id.update_todoNotes);
        todo_deadline = findViewById(R.id.update_todo_deadline);
        notify = findViewById(R.id.update_notificationSwitch);
        reminder_time = findViewById(R.id.update_todo_time);
        update_button = findViewById(R.id.btnUpdate);

        final Calendar calendar= Calendar.getInstance();
        final int year=calendar.get(Calendar.YEAR);
        final int month=calendar.get(Calendar.MONTH);
        final int day=calendar.get(Calendar.DAY_OF_MONTH);
        final int hour=calendar.get(Calendar.HOUR);
        final int minute=calendar.get(Calendar.MINUTE);

        todo_deadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog=new DatePickerDialog(UpdateToDo.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        calendar.set(Calendar.YEAR,i);
                        calendar.set(Calendar.MONTH,i1);
                        calendar.set(Calendar.DAY_OF_MONTH,i2);
                        todo_deadline.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime()));
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        reminder_time.setEnabled(false);
        notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
                if (check == true) {
                    on_off = "ON";
                    reminder_time.setEnabled(true);
                }else {
                    on_off = "OFF";
                    reminder_time.setEnabled(false);
                }
            }
        });

        reminder_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog=new TimePickerDialog(UpdateToDo.this, new TimePickerDialog.OnTimeSetListener() {
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

        //First call this
        getAndSetIntentData();

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(title);
        }


        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper myDB = new DatabaseHelper(UpdateToDo.this);

                boolean titleEmpty=todo_title.getText().toString().isEmpty();
                boolean dateEmpty=todo_deadline.getText().toString().isEmpty();
                boolean timeEmpty=reminder_time.getText().toString().isEmpty();
                boolean noteEmpty=todo_notes.getText().toString().isEmpty();

                if(titleEmpty){
                    Toast.makeText(UpdateToDo.this, "Title required", Toast.LENGTH_SHORT).show();
                } else if(dateEmpty){
                    Toast.makeText(UpdateToDo.this, "Date required", Toast.LENGTH_SHORT).show();
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

                    title = todo_title.getText().toString().trim();
                    date = todo_deadline.getText().toString().trim();
                    notification = on_off.trim();
                    time = time.trim();
                    notes = note.trim();
                    myDB.updateTask(String.valueOf(id), title, date, notification, time, notes);
                }
                }
        });


    }


    //THis function gets and sets the selected book data from the database
    void getAndSetIntentData(){

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //Getting Data from Intent
            id = extras.getInt("reminder_id");
            title = extras.getString("task_title");
            date = extras.getString("todo_deadline");
            notification = extras.getString("notification");
            time = extras.getString("reminder_time");
            notes = extras.getString("notes");

            //Setting Intent Data
            todo_title.setText(title);
            todo_notes.setText(notes);
        } else {
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        }
    }

}
