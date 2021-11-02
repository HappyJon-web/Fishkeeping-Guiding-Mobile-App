package com.example.fishkeeping.ui.aquarium;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import com.example.fishkeeping.DatabaseHelper;
import com.example.fishkeeping.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class UpdateAquarium extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner aquaSpType;
    ArrayList<String> aquaType;
    EditText aquaName, aqualength, aquawidth, aquaheight, aquavolume;
    ImageView aquaImage;
    Button selectImage, update_aqua, takePhoto;
    int id;
    String name, type, length, width, height, vol;
    private String getAquariumTypeString;
    private static final int CAMERA_REQUEST = 1888;
    int SELECT_PICTURE = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_aquarium);

        aquaSpType = (Spinner) findViewById(R.id.update_aquariumType);
        aquaType = new ArrayList<String>();
        aquaType.add("Freshwater");
        aquaType.add("Saltwater");
        aquaType.add("Other");

        aquaName = findViewById(R.id.update_aquariumName);
        aqualength = findViewById(R.id.update_aquariumLength);
        aquawidth = findViewById(R.id.update_aquariumWidth);
        aquaheight = findViewById(R.id.update_aquariumHeight);
        aquavolume = findViewById(R.id.update_aquariumVolume);
        aquaImage = (ImageView)findViewById(R.id.update_aquariumPhoto);
        selectImage = (Button)findViewById(R.id.btnSelect);
        takePhoto = (Button)findViewById(R.id.btnPhoto);
        update_aqua = findViewById(R.id.btnUpdate);


        ArrayAdapter<String> adMeat = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, aquaType);
        adMeat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aquaSpType.setAdapter(adMeat);
        aquaSpType.setOnItemSelectedListener(this);

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        //First call this
        getAndSetIntentData();

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(name);
        }


        update_aqua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    DatabaseHelper myDB = new DatabaseHelper(UpdateAquarium.this);
                    boolean nameEmpty=aquaName.getText().toString().isEmpty();
                    boolean emptyL=aqualength.getText().toString().isEmpty();
                    boolean emptyW=aquawidth.getText().toString().isEmpty();
                    boolean emptyH=aquaheight.getText().toString().isEmpty();
                    boolean emptyVol=aquavolume.getText().toString().isEmpty();

                    if(nameEmpty){
                        Toast.makeText(UpdateAquarium.this, "Aquarium name required", Toast.LENGTH_SHORT).show();
                    } else if(emptyL){
                        Toast.makeText(UpdateAquarium.this, "Aquarium length required", Toast.LENGTH_SHORT).show();
                    } else if(emptyW){
                        Toast.makeText(UpdateAquarium.this, "Aquarium width required", Toast.LENGTH_SHORT).show();
                    } else if(emptyH){
                        Toast.makeText(UpdateAquarium.this, "Aquarium height required", Toast.LENGTH_SHORT).show();
                    } else if(emptyVol){
                        Toast.makeText(UpdateAquarium.this, "Aquarium volume required", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        name = aquaName.getText().toString().trim();
                        type = aquaSpType.getSelectedItem().toString().trim();
                        length = aqualength.getText().toString().trim();
                        width = aquawidth.getText().toString().trim();
                        height = aquaheight.getText().toString().trim();
                        vol = aquavolume.getText().toString().trim();

                        int l = Integer.parseInt(length);
                        int w = Integer.parseInt(width);
                        int h = Integer.parseInt(height);
                        float v = Float.parseFloat(vol);
                        int size = l * w * h;

                        if(size <= 1728){
                            Toast.makeText(UpdateAquarium.this,
                                    "Aquarium is too small.", Toast.LENGTH_SHORT).show();
                        } else if (v <= 3.8){
                            Toast.makeText(UpdateAquarium.this,
                                    "Aquarium has too little water for the fishes.", Toast.LENGTH_SHORT).show();
                        } else if (size > 13 && v > 3.8){
                            int length = l;
                            int width = w;
                            int height = h;
                            float water = v;
                            myDB.updateAquarium(String.valueOf(id), name, type, length, width,
                                    height, water, imageToByte(aquaImage));
                        }

                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        getAquariumTypeString = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    void imageChooser() {
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            i.putExtra("return-data", true);
            startActivityForResult(
                    Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
        }catch (ActivityNotFoundException ex){
            ex.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            aquaImage.setImageBitmap(photo);
        }

        if (resultCode == RESULT_OK) {
            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    aquaImage.setImageURI(selectedImageUri);
                }
            }
        }
    }

    private byte[] imageToByte (ImageView aquaImage) {
        Bitmap b = ((BitmapDrawable)aquaImage.getDrawable()).getBitmap();
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 90, s);
        byte[] byteArray = s.toByteArray();
        return byteArray;
    }


    void getAndSetIntentData(){

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //Getting Data from Intent
            id = extras.getInt("aquarium_id");
            name = extras.getString("aquarium_name");
            length = extras.getString("aquarium_length");
            width = extras.getString("aquarium_width");
            height = extras.getString("aquarium_height");
            vol = extras.getString("aquarium_volume");

            //Setting Intent Data
            aquaName.setText(name);
            aqualength.setText(String.valueOf(length));
            aquawidth.setText(String.valueOf(width));
            aquaheight.setText(String.valueOf(height));
            aquavolume.setText(String.valueOf(vol));
        } else {
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        }
    }


}
