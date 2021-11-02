package com.example.fishkeeping.ui.todo;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fishkeeping.DatabaseHelper;
import com.example.fishkeeping.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.ALARM_SERVICE;

public class AdapterTask extends RecyclerView.Adapter<AdapterTask.MyViewHolder> {

    Context context;
    ArrayList<ModelTask> taskList;
    Calendar c;
    AlarmManager alarm;
    PendingIntent pendingIntent;

    public AdapterTask(Context context, ArrayList<ModelTask> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_task, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.title_txt.setText(taskList.get(position).getTodotitle());
        holder.deadline_txt.setText("To be completed by " + taskList.get(position).getDeadline() );
        holder.notification_txt.setText("Notification: " + taskList.get(position).getNotification() );
        holder.time_txt.setText("Time: " + taskList.get(position).getTodotime() );
        holder.notes_txt.setText("Notes: " + taskList.get(position).getTodonotes());

        createNotificationChannel();
        //GET TIME FROM THE REMINDER_TIME STRING
        String no = taskList.get(position).getNotification();
        String todotime = taskList.get(position).getTodotime();
        String tododay = taskList.get(position).getDeadline();

        if (no.equals("OFF")){
            cancelAlarm();
        } else if (no.equals("ON")){
            if(todotime.equals("No time.")){
                cancelAlarm();
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
                Date reminderTime = null;
                try {
                    reminderTime = dateFormat.parse(tododay + " " + todotime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
//                setAlarm(reminderTime);
                c = Calendar.getInstance();
                c.setTime(reminderTime);

                alarm = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                Intent intent = new Intent(context, NotificationReceiver.class);
                intent.putExtra("TASK", taskList.get(position).getTodotitle());
                intent.putExtra("DEADLINE", taskList.get(position).getDeadline());
                intent.putExtra("TIME", taskList.get(position).getTodotime());
                final int id = (int) System.currentTimeMillis();
                pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_ONE_SHOT);
                alarm.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),pendingIntent);
            }
        }


        final int taskID = taskList.get(position).getTodoid();

        holder.task_completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                boolean checked = ((CheckBox) view).isChecked();
                holder.task_completed.setChecked(false);
                if(checked){
                    holder.task_completed.setChecked(true);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Completed Task?");
                    builder.setMessage("Have you completed the task? \n" +
                            "Task Title: " + taskList.get(position).getTodotitle() + "\n" +
                            "Task Deadline: " + taskList.get(position).getDeadline());
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DatabaseHelper myDB = new DatabaseHelper(context);
                            myDB.deleteTask(String.valueOf(taskID));
                            Navigation.findNavController(view).navigate(R.id.nav_todo);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            holder.task_completed.setChecked(false);
                            Navigation.findNavController(view).navigate(R.id.nav_todo);
                            Toast.makeText(context, "Task not completed", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.create().show();
                } else {
                    holder.task_completed.setChecked(false);
                    Toast.makeText(context, "Task not completed", Toast.LENGTH_SHORT).show();
                }
            }
        });


        holder.editBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.getMenuInflater().inflate(R.menu.edit_task_menu, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.edit:
                                Intent i = new Intent(context, UpdateToDo.class);
                                i.putExtra("reminder_id",taskList.get(position).getTodoid());
                                i.putExtra("task_title",taskList.get(position).getTodotitle());
                                i.putExtra("deadline",taskList.get(position).getDeadline());
                                i.putExtra("notification",taskList.get(position).getNotification());
                                i.putExtra("reminder_time",taskList.get(position).getTodotime());
                                i.putExtra("notes",taskList.get(position).getTodonotes());
                                context.startActivity(i);
                                return true;
                            case R.id.delete:
                                removeTask(String.valueOf(taskID));
                                return true;
                            default:
                                return false;
                        }
                    }

                    public void removeTask(final String taskID) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Delete Task?");
                        builder.setMessage("Are you sure you want to delete this task?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DatabaseHelper myDB = new DatabaseHelper(context);
                                myDB.deleteTask(taskID);
                                Navigation.findNavController(view).navigate(R.id.nav_todo);
                            }
                        }); //If user clicks yes, the aquarium will be deleted.
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            } //If user clicks no, nothing happens.
                        });
                        builder.create().show();
                    }


                });
            }
        });


    }


    @Override
    public int getItemCount() { return taskList.size(); }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title_txt, deadline_txt, notification_txt, time_txt, notes_txt;
        private ImageButton editBtn;
        CheckBox task_completed;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title_txt = itemView.findViewById(R.id.nameTask);
            deadline_txt = itemView.findViewById(R.id.dateTask);
            notification_txt = itemView.findViewById(R.id.notifyTask);
            time_txt = itemView.findViewById(R.id.task_time);
            notes_txt = itemView.findViewById(R.id.notes);
            editBtn = (ImageButton) itemView.findViewById(R.id.menuBtn);
            task_completed = (CheckBox) itemView.findViewById(R.id.cbCompleted);

        }

    }

//    private Date setAlarm(Date taskdeadline) {
//        c = Calendar.getInstance();
//        c.setTime(taskdeadline);
//
//        alarm = (AlarmManager) context.getSystemService(ALARM_SERVICE);
//        Intent intent = new Intent(context, NotificationReceiver.class);
//        intent.putExtra("TASK", 1);
//        final int id = (int) System.currentTimeMillis();
//        pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_ONE_SHOT);
//        alarm.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),pendingIntent);
////        Toast.makeText(context, "Alarm created.", Toast.LENGTH_SHORT).show();
//
//        return taskdeadline;
//    }

    private void cancelAlarm() {
        Intent intent = new Intent(context, NotificationReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context,0, intent, 0);
        if(alarm == null){
            alarm = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        }
        alarm.cancel(pendingIntent);
//        Toast.makeText(context, "Alarm cancelled.", Toast.LENGTH_SHORT).show();
    }

    private void createNotificationChannel() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "foxandroidReminderChannel";
            String desc = "Channel for Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("foxandroid", name, importance);
            channel.setDescription(desc);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

}
