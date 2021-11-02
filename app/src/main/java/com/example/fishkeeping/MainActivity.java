package com.example.fishkeeping;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;

import com.example.fishkeeping.ui.aquarium.AddAquarium;
import com.example.fishkeeping.ui.fish.AddFish;
import com.example.fishkeeping.ui.todo.AddToDo;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private DatabaseHelper db;
    Navigation nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_aquarium, R.id.nav_fish, R.id.nav_problem, R.id.nav_todo)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //The activities in the menu option
        if(item.getItemId() == R.id.action_aquariumadd){
            Intent intent = new Intent (MainActivity.this, AddAquarium.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.action_fishadd){
            db =new DatabaseHelper(this);
                    if(db.countAquarium()==0){
                        showNewDialog();
                    }else{
                        showNewAquaDialog();
                    }
        }
        if(item.getItemId() == R.id.action_todoadd){
            Intent intent = new Intent (MainActivity.this, AddToDo.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    private void showNewDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("No aquarium added.");
        builder.setMessage("There is no aquarium in the database. You need to create an aquarium first to add fishes.");
        builder.setPositiveButton("Add New Aquarium", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(MainActivity.this, AddAquarium.class));
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).create().show();
    }

    private void showNewAquaDialog(){
        Intent intent = new Intent (MainActivity.this, AddFish.class);
        startActivity(intent);
    }

}