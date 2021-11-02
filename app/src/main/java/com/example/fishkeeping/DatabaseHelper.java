package com.example.fishkeeping;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "FishKeeping.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_AQUA_NAME = "aquarium";
    public static final String COLUMN_AQUA_ID = "aquarium_id";
    public static final String COLUMN_AQUA_NAME = "aquarium_name";
    public static final String COLUMN_AQUA_TYPE = "aquarium_type";
    public static final String COLUMN_AQUA_LENGTH = "aquarium_length";
    public static final String COLUMN_AQUA_WIDTH = "aquarium_width";
    public static final String COLUMN_AQUA_HEIGHT = "aquarium_height";
    public static final String COLUMN_AQUA_VOLUME = "aquarium_volume";
    public static final String COLUMN_AQUA_IMAGE = "aquarium_image";
    public static final String COLUMN_AQUA_FISHNO = "aquarium_fishNo";

    public static final String TABLE_FISH_NAME = "fish";
    public static final String COLUMN_FISH_ID = "fish_id";
    public static final String COLUMN_FISH_AQUA = "fish_aquarium";
    public static final String COLUMN_FISH_NAME = "fish_name";
    public static final String COLUMN_FISH_SPECIES = "fish_species";
    public static final String COLUMN_FISH_GENDER = "fish_gender";
    public static final String COLUMN_FISH_QUANTITY = "fish_quantity";
    public static final String COLUMN_FISH_IMAGE = "fish_image";

    public static final String TABLE_TODO_NAME = "reminder";
    public static final String COLUMN_TODO_ID = "reminder_id";
    public static final String COLUMN_TODO_TITLE = "task_title";
    public static final String COLUMN_TODO_DEADLINE = "deadline";
    public static final String COLUMN_TODO_NOTIFICATION = "notification";
    public static final String COLUMN_TODO_REMINDER_TIME = "reminder_time";
    public static final String COLUMN_TODO_NOTES = "notes";

    public static final String TABLE_AQUAISSUE_NAME = "aquaIssue";
    public static final String COLUMN_AQUAISSUE_ID = "aquaIssue_id";
    public static final String COLUMN_AQUAISSUE_TITLE = "aquaIssue_title";
    public static final String COLUMN_AQUAISSUE_CAUSE = "aquaCause";
    public static final String COLUMN_AQUAISSUE_SOLUTION = "aquaSolution";

    public static final String TABLE_FISHISSUE_NAME = "fishIssue";
    public static final String COLUMN_FISHISSUE_ID = "fishIssue_id";
    public static final String COLUMN_FISHISSUE_TITLE = "fishIssue_title";
    public static final String COLUMN_FISHISSUE_CAUSE = "fishCause";
    public static final String COLUMN_FISHISSUE_SOLUTION = "fishSolution";

    public static final String FORCE_FOREIGN_KEY="PRAGMA foreign_keys=ON";


    private static final String CREATE_AQUA_TABLE="CREATE TABLE IF NOT EXISTS " + TABLE_AQUA_NAME+"("+
            COLUMN_AQUA_ID +" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"+
            COLUMN_AQUA_NAME + " VARCHAR NOT NULL, " +
            COLUMN_AQUA_TYPE + " VARCHAR NOT NULL, " +
            COLUMN_AQUA_LENGTH + " INTEGER NOT NULL, " +
            COLUMN_AQUA_WIDTH + " INTEGER NOT NULL, " +
            COLUMN_AQUA_HEIGHT + " INTEGER NOT NULL, " +
            COLUMN_AQUA_VOLUME + " FLOAT NOT NULL, " +
            COLUMN_AQUA_IMAGE + " LONGBLOB NOT NULL, " +
            COLUMN_AQUA_FISHNO + " INTEGER NOT NULL)";

    private static final String CREATE_FISH_TABLE="CREATE TABLE IF NOT EXISTS " + TABLE_FISH_NAME+"("+
            COLUMN_FISH_ID +" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"+
            COLUMN_FISH_AQUA +" INTEGER NOT NULL,"+
            COLUMN_FISH_NAME + " VARCHAR NOT NULL, " +
            COLUMN_FISH_SPECIES + " VARCHAR NOT NULL, " +
            COLUMN_FISH_GENDER + " VARCHAR NOT NULL, " +
            COLUMN_FISH_QUANTITY + " INTEGER NOT NULL, " +
            COLUMN_FISH_IMAGE + " BLOB NOT NULL, " +
            "FOREIGN KEY("+ COLUMN_FISH_AQUA +") REFERENCES "+ TABLE_AQUA_NAME +"("+ COLUMN_AQUA_ID +") ON UPDATE CASCADE ON DELETE CASCADE"+")";

    private static final String CREATE_TODO_TABLE="CREATE TABLE IF NOT EXISTS " + TABLE_TODO_NAME+"("+
            COLUMN_TODO_ID +" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"+
            COLUMN_TODO_TITLE + " STRING NOT NULL, " +
            COLUMN_TODO_DEADLINE + " STRING NOT NULL, " +
            COLUMN_TODO_NOTIFICATION + " STRING NOT NULL, " +
            COLUMN_TODO_REMINDER_TIME + " STRING NOT NULL, " +
            COLUMN_TODO_NOTES + " STRING NOT NULL)";

    private static final String CREATE_AQUAISSUE_TABLE="CREATE TABLE IF NOT EXISTS " + TABLE_AQUAISSUE_NAME+"("+
            COLUMN_AQUAISSUE_ID +" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"+
            COLUMN_AQUAISSUE_TITLE + " VARCHAR NOT NULL, " +
            COLUMN_AQUAISSUE_CAUSE + " VARCHAR NOT NULL, " +
            COLUMN_AQUAISSUE_SOLUTION + " VARCHAR NOT NULL, " +
            "UNIQUE(" + COLUMN_AQUAISSUE_ID + ", " + COLUMN_AQUAISSUE_TITLE + ", " +
            COLUMN_AQUAISSUE_CAUSE + ", " + COLUMN_AQUAISSUE_SOLUTION + "))";

    private static final String CREATE_FISHISSUE_TABLE="CREATE TABLE IF NOT EXISTS " + TABLE_FISHISSUE_NAME+"("+
            COLUMN_FISHISSUE_ID +" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"+
            COLUMN_FISHISSUE_TITLE + " VARCHAR NOT NULL, " +
            COLUMN_FISHISSUE_CAUSE + " VARCHAR NOT NULL, " +
            COLUMN_FISHISSUE_SOLUTION + " VARCHAR NOT NULL, " +
            "UNIQUE(" + COLUMN_FISHISSUE_ID + ", " + COLUMN_FISHISSUE_TITLE + ", " +
            COLUMN_FISHISSUE_CAUSE + ", " + COLUMN_FISHISSUE_SOLUTION + "))";

    private static final String ADD_AQUAISSUE="INSERT OR IGNORE INTO " + TABLE_AQUAISSUE_NAME
            +"("+ COLUMN_AQUAISSUE_TITLE +", "+ COLUMN_AQUAISSUE_CAUSE +", "+ COLUMN_AQUAISSUE_SOLUTION +
            ") VALUES (\"Clean Aquarium\", \"None\", \"None\"), " +
            "(\"Green Aquarium Water\", \"Algae Overgrowth Caused by Excessive Light & Nutrient Imbalance\", \"Try not to place the aquarium under too much light, especially sunlight; test the aquarium water regularly, and maintain a balanced feeding routine.\"), " +
            "(\"Cloudy Aquarium Water\", \"Bacterial Bloom Caused by Overfeeding and Plant Decay\", \"Do not overfeed your fishes, do not put too many fishes in the tank, and if the water is cloudy, do not change the water often; let it be, because the bacteria will run out of nutrients and die eventually.\");";

    private static final String ADD_FISHISSUE="INSERT OR IGNORE INTO " + TABLE_FISHISSUE_NAME
            +"("+ COLUMN_FISHISSUE_TITLE +", "+ COLUMN_FISHISSUE_CAUSE +", "+ COLUMN_FISHISSUE_SOLUTION +
            ") VALUES (\"Healthy Fish\", \"None\", \"None\"), " +
            "(\"Fish Ich\", \"Protozoan Parasites from Infected Fish, Aquarium or Equipment\", \"Quarantine all fishes, change one-third of the aquarium water, apply 5ml of Ich-X medicine per 10 gallons of water, and wait 24 hours. Repeat the process every 24 hours until ich is gone.\"), " +
            "(\"Fish Ulcer\", \"Weakened Fish Immune System Caused by Poor Water Quality, Poor or Inadequate Nutrition, and Aggression\", \"Severe ulcers require veterinary treatment, often with antibiotic prescription. To prevent ulcers, maintain good water quality and proper maintenance, feed appropriate and fresh diet, and quarantine the fishes properly. Prevention is better than cure.\"), " +
            "(\"Fish Fungus\", \"Poor Water Conditions and Nutrient Deficiencies\", \"Do regular partial water changes to prevent fungal infection. For treatment, dose 1 packet of Maracyn per 10 gallons of water and repeat the process every 24 hours for 5 days.\");";


    private static final String DROP_AQUA_TABLE="DROP TABLE IF EXISTS " + TABLE_AQUA_NAME;
    private static final String DROP_FISH_TABLE="DROP TABLE IF EXISTS " + TABLE_FISH_NAME;
    private static final String DROP_TODO_TABLE="DROP TABLE IF EXISTS " + TABLE_TODO_NAME;
    private static final String DROP_AQUAISSUE_TABLE="DROP TABLE IF EXISTS " + TABLE_AQUAISSUE_NAME;
    private static final String DROP_FISHISSUE_TABLE="DROP TABLE IF EXISTS " + TABLE_FISHISSUE_NAME;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_AQUA_TABLE);
        db.execSQL(CREATE_FISH_TABLE);
        db.execSQL(CREATE_TODO_TABLE);
        db.execSQL(CREATE_AQUAISSUE_TABLE);
        db.execSQL(ADD_AQUAISSUE);
        db.execSQL(CREATE_FISHISSUE_TABLE);
        db.execSQL(ADD_FISHISSUE);
        db.execSQL(FORCE_FOREIGN_KEY);
    } //This function inserts the SQL command to create the database table for the app.

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(DROP_AQUA_TABLE);
        db.execSQL(DROP_FISH_TABLE);
        db.execSQL(DROP_TODO_TABLE);
        db.execSQL(DROP_AQUAISSUE_TABLE);
        db.execSQL(DROP_FISHISSUE_TABLE);
        onCreate(db);
    } //If any similar tables exist, it will be dropped.


    //START OF ALL AQUARIUM FUNCTIONS
    public void addAquarium(String name, String type, int length, int width, int height, float volume, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_AQUA_NAME, name);
        cv.put(COLUMN_AQUA_TYPE, type);
        cv.put(COLUMN_AQUA_LENGTH, length);
        cv.put(COLUMN_AQUA_WIDTH, width);
        cv.put(COLUMN_AQUA_HEIGHT, height);
        cv.put(COLUMN_AQUA_VOLUME, volume);
        cv.put(COLUMN_AQUA_IMAGE, image);
        cv.put(COLUMN_AQUA_FISHNO, 1);
        long result = db.insert(TABLE_AQUA_NAME, null, cv);
        if(result == -1){ //Prompt toast message based on the function result
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Added Successfully!", Toast.LENGTH_SHORT).show();
        }
    }


    public Cursor readAquarium(){ //Lets user view all aquarium data
        String query = "SELECT * FROM " + TABLE_AQUA_NAME; // Select from SQL command
        SQLiteDatabase db = this.getReadableDatabase(); //Get readable database for reading new data

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public void updateAquarium(String aquarium_id, String name, String type, int length, int width,
                               int height, float volume, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_AQUA_NAME, name);
        cv.put(COLUMN_AQUA_TYPE, type);
        cv.put(COLUMN_AQUA_LENGTH, length);
        cv.put(COLUMN_AQUA_WIDTH, width);
        cv.put(COLUMN_AQUA_HEIGHT, height);
        cv.put(COLUMN_AQUA_VOLUME, volume);
        cv.put(COLUMN_AQUA_IMAGE, image);
        cv.put(COLUMN_AQUA_FISHNO, 1);

        long result = db.update(TABLE_AQUA_NAME, cv, "aquarium_id=?", new String[]{aquarium_id});
        if(result == -1){
            Toast.makeText(context, "Update Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Update Successful!", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteAquarium(String aqua_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DatabaseHelper.FORCE_FOREIGN_KEY);
        long result = db.delete(TABLE_AQUA_NAME, "aquarium_id=?", new String[]{aqua_id});
        if(result == -1){ //Prompt toast message based on the function result
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "The aquarium has been deleted.", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateNo(String aquarium_id, int fishNo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_AQUA_FISHNO, fishNo);
        long result = db.update(TABLE_AQUA_NAME, cv, "aquarium_id=?", new String[]{aquarium_id});

        if(result == -1){ //If the process fails, an error message will occur.
            Toast.makeText(context, "Failed to Calculate.", Toast.LENGTH_SHORT).show();
        }
    }

    // END OF ALL AQUARIUM FUNCTIONS


    // START OF ALL TASK FUNCTIONS
    public void addTask(String title, String deadline, String notification, String time, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TODO_TITLE, title);
        cv.put(COLUMN_TODO_DEADLINE, deadline);
        cv.put(COLUMN_TODO_NOTIFICATION, notification);
        cv.put(COLUMN_TODO_REMINDER_TIME, time);
        cv.put(COLUMN_TODO_NOTES, notes);
        long result = db.insert(TABLE_TODO_NAME, null, cv);
        if(result == -1){ //Prompt toast message based on the function result
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Added Successfully!", Toast.LENGTH_SHORT).show();
        }
    }


    public Cursor readTask(){
        String query = "SELECT * FROM " + TABLE_TODO_NAME; // Select from SQL command
        SQLiteDatabase db = this.getReadableDatabase(); //Get readable database for reading new data

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public void updateTask(String todo_id, String title, String deadline, String notification, String time, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TODO_TITLE, title);
        cv.put(COLUMN_TODO_DEADLINE, deadline);
        cv.put(COLUMN_TODO_NOTIFICATION, notification);
        cv.put(COLUMN_TODO_REMINDER_TIME, time);
        cv.put(COLUMN_TODO_NOTES, notes);

        long result = db.update(TABLE_TODO_NAME, cv, "reminder_id=?", new String[]{todo_id});
        if(result == -1){
            Toast.makeText(context, "Update Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Update Successful!", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteTask(String todo_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_TODO_NAME, "reminder_id=?", new String[]{todo_id});
        if(result == -1){ //Prompt toast message based on the function result
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "The task has been deleted.", Toast.LENGTH_SHORT).show();
        }
    }

    // END OF ALL TASK FUNCTIONS


    // START OF ALL FISH FUNCTIONS
    public void addFish(int aquaID, String name, String species, String gender, int quantity, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_FISH_AQUA, aquaID);
        cv.put(COLUMN_FISH_NAME, name);
        cv.put(COLUMN_FISH_SPECIES, species);
        cv.put(COLUMN_FISH_GENDER, gender);
        cv.put(COLUMN_FISH_QUANTITY, quantity);
        cv.put(COLUMN_FISH_IMAGE, image);
        long result = db.insert(TABLE_FISH_NAME, null, cv);
        if(result == -1){ //Prompt toast message based on the function result
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Added Successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    public Cursor readFish(){
        String query = "SELECT * FROM " + TABLE_FISH_NAME; // Select from SQL command
        SQLiteDatabase db = this.getReadableDatabase(); //Get readable database for reading new data

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public void updateFish(String fish_id, int aquaID, String name, String species,
    String gender, int quantity, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_FISH_AQUA, aquaID);
        cv.put(COLUMN_FISH_NAME, name);
        cv.put(COLUMN_FISH_SPECIES, species);
        cv.put(COLUMN_FISH_GENDER, gender);
        cv.put(COLUMN_FISH_QUANTITY, quantity);
        cv.put(COLUMN_FISH_IMAGE, image);

        long result = db.update(TABLE_FISH_NAME, cv, "fish_id=?", new String[]{fish_id});
        if(result == -1){
            Toast.makeText(context, "Update Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Update Successful!", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteFish(String fish_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_FISH_NAME, "fish_id=?", new String[]{fish_id});
        if(result == -1){ //Prompt toast message based on the function result
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "The fish has been deleted.", Toast.LENGTH_SHORT).show();
        }
    }

    // END OF ALL FISH FUNCTIONS



    public int countAquarium(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query="SELECT " + COLUMN_AQUA_ID + " FROM " + TABLE_AQUA_NAME;
        Cursor cursor= db.rawQuery(query,null);
        return cursor.getCount();
    }

    public int countFish(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query="SELECT " + COLUMN_FISH_ID + " FROM " + TABLE_FISH_NAME;
        Cursor cursor= db.rawQuery(query,null);
        return cursor.getCount();
    }


    public byte[] fetchAquaImg(int aquaID){
        SQLiteDatabase db = this.getWritableDatabase();
        String query="SELECT " + COLUMN_AQUA_IMAGE + " FROM " + TABLE_AQUA_NAME + " WHERE " + COLUMN_AQUA_ID +"=?";
        Cursor cursor=db.rawQuery(query,new String[]{String.valueOf(aquaID)});
        byte[] img = null;
        if(cursor.moveToFirst()){
            img=cursor.getBlob(cursor.getColumnIndex(COLUMN_AQUA_IMAGE));
        }
        cursor.close();
        db.close();
        return img;
    }

    public byte[] fetchFishImg(int fishID){
        SQLiteDatabase db = this.getWritableDatabase();
        String query="SELECT " + COLUMN_FISH_IMAGE + " FROM " + TABLE_FISH_NAME + " WHERE " + COLUMN_FISH_ID +"=?";
        Cursor cursor=db.rawQuery(query,new String[]{String.valueOf(fishID)});
        byte[] img = null;
        if(cursor.moveToFirst()){
            img=cursor.getBlob(cursor.getColumnIndex(COLUMN_FISH_IMAGE));
        }
        cursor.close();
        db.close();
        return img;
    }


    public ArrayList<String> fetchAquariumName(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> aquaName = new ArrayList<>();
        String query="SELECT " + COLUMN_AQUA_NAME + " FROM " + TABLE_AQUA_NAME;
        Cursor cursor=db.rawQuery(query,null);
        while (cursor.moveToNext()){
            aquaName.add(cursor.getString(cursor.getColumnIndex(COLUMN_AQUA_NAME)));
        }
        cursor.close();
        db.close();
        return aquaName;
    }

    public ArrayList<String> fetchFishName(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> fishName = new ArrayList<>();
        String query="SELECT " + COLUMN_FISH_NAME + " FROM " + TABLE_FISH_NAME;
        Cursor cursor=db.rawQuery(query,null);
        while (cursor.moveToNext()){
            fishName.add(cursor.getString(cursor.getColumnIndex(COLUMN_FISH_NAME)));
        }
        cursor.close();
        db.close();
        return fishName;
    }


    public int fetchAquariumID(String aquaName){
        SQLiteDatabase db = this.getReadableDatabase();
        String fetchTitle="SELECT " + COLUMN_AQUA_ID + " FROM " + TABLE_AQUA_NAME
                + " WHERE " + COLUMN_AQUA_NAME + "=?";
        Cursor cursor=db.rawQuery(fetchTitle,new String[]{aquaName});
        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex(COLUMN_AQUA_ID));
    }

    public int fetchFishID(String fishName){
        SQLiteDatabase db = this.getReadableDatabase();
        String fetchTitle="SELECT " + COLUMN_FISH_ID + " FROM " + TABLE_FISH_NAME
                + " WHERE " + COLUMN_FISH_NAME + "=?";
        Cursor cursor=db.rawQuery(fetchTitle,new String[]{fishName});
        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex(COLUMN_FISH_ID));
    }


    public String fetchAquaName(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String fetchTitle="SELECT " + COLUMN_AQUA_NAME + " FROM " + TABLE_AQUA_NAME
                + " WHERE " + COLUMN_AQUA_ID + "=?";
        Cursor cursor=db.rawQuery(fetchTitle,new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(COLUMN_AQUA_NAME));
    }


    // READ AQUARIUM AND FISH ISSUES FUNCTIONS
    public String fetchAquaIssue(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String fetchTitle="SELECT " + COLUMN_AQUAISSUE_TITLE + " FROM " + TABLE_AQUAISSUE_NAME
                + " WHERE " + COLUMN_AQUAISSUE_ID + "=?";
        Cursor cursor=db.rawQuery(fetchTitle,new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(COLUMN_AQUAISSUE_TITLE));
    }

    public String fetchFishIssue(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String fetchTitle="SELECT " + COLUMN_FISHISSUE_TITLE + " FROM " + TABLE_FISHISSUE_NAME
                + " WHERE " + COLUMN_FISHISSUE_ID + "=?";
        Cursor cursor=db.rawQuery(fetchTitle,new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(COLUMN_FISHISSUE_TITLE));
    }


    public String fetchAquaCause(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String fetchTitle="SELECT " + COLUMN_AQUAISSUE_CAUSE + " FROM " + TABLE_AQUAISSUE_NAME
                + " WHERE " + COLUMN_AQUAISSUE_ID + "=?";
        Cursor cursor=db.rawQuery(fetchTitle,new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(COLUMN_AQUAISSUE_CAUSE));
    }

    public String fetchFishCause(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String fetchTitle="SELECT " + COLUMN_FISHISSUE_CAUSE + " FROM " + TABLE_FISHISSUE_NAME
                + " WHERE " + COLUMN_FISHISSUE_ID + "=?";
        Cursor cursor=db.rawQuery(fetchTitle,new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(COLUMN_FISHISSUE_CAUSE));
    }

    public String fetchAquaSolution(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String fetchTitle="SELECT " + COLUMN_AQUAISSUE_SOLUTION + " FROM " + TABLE_AQUAISSUE_NAME
                + " WHERE " + COLUMN_AQUAISSUE_ID + "=?";
        Cursor cursor=db.rawQuery(fetchTitle,new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(COLUMN_AQUAISSUE_SOLUTION));
    }


    public String fetchFishSolution(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String fetchTitle="SELECT " + COLUMN_FISHISSUE_SOLUTION+ " FROM " + TABLE_FISHISSUE_NAME
                + " WHERE " + COLUMN_FISHISSUE_ID + "=?";
        Cursor cursor=db.rawQuery(fetchTitle,new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(COLUMN_FISHISSUE_SOLUTION));
    }


}
