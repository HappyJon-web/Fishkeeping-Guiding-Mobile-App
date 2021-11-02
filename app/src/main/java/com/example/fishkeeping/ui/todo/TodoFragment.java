package com.example.fishkeeping.ui.todo;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fishkeeping.DatabaseHelper;
import com.example.fishkeeping.R;

import java.util.ArrayList;

public class TodoFragment extends Fragment {

    RecyclerView rvTask;
    ImageView empty_image;
    TextView no_task;
    DatabaseHelper todoDB;
    ArrayList<ModelTask> taskList;
    AdapterTask taskAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_todo, container, false);

        rvTask = root.findViewById(R.id.rvTask);
        empty_image = root.findViewById(R.id.empty_image);
        no_task = root.findViewById(R.id.no_todo_data);

        todoDB = new DatabaseHelper(getActivity());

        taskList = new ArrayList<>();
        taskList.addAll(storeDataInArrays());
        taskAdapter = new AdapterTask(getActivity(), taskList);
        rvTask.setAdapter(taskAdapter);
        rvTask.setLayoutManager(new LinearLayoutManager(getActivity()));

        return root;
    }

    public ArrayList<ModelTask> storeDataInArrays() {
        Cursor cursor = todoDB.readTask();
        ArrayList<ModelTask> taskData = new ArrayList<>();

        if(cursor.getCount() == 0){
            empty_image.setVisibility(View.VISIBLE);
            no_task.setVisibility(View.VISIBLE);
        } else {
            while (cursor.moveToNext()){
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String deadline = cursor.getString(2);
                String notification = cursor.getString(3);
                String time = cursor.getString(4);
                String note = cursor.getString(5);

                taskData.add(new ModelTask(id, name, deadline, notification, time, note));
            }
            empty_image.setVisibility(View.GONE);
            no_task.setVisibility(View.GONE);
        }

        return taskData;
    }

}