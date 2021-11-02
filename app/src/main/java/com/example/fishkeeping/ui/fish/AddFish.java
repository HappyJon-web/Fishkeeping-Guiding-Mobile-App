package com.example.fishkeeping.ui.fish;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.fishkeeping.DatabaseHelper;
import com.example.fishkeeping.R;

import java.io.ByteArrayOutputStream;

public class AddFish extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spAquarium;
    EditText fishName, fishSpecies, fishQuantity;
    RadioGroup RgGender;
    RadioButton gender;
    ImageView fishImage;
    Button selectImage, addFish, takePhoto;
    private String getFishTypeString;
    private static final int CAMERA_REQUEST = 1888;
    int SELECT_PICTURE = 200;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fish);

        fishName = findViewById(R.id.fishName);
        fishSpecies = findViewById(R.id.fishSpecies);
        fishQuantity = findViewById(R.id.fishQuantity);
        RgGender = (RadioGroup) findViewById(R.id.rgFishGender);
        fishImage = (ImageView)findViewById(R.id.fishPhoto);
        selectImage = (Button)findViewById(R.id.btnSelect);
        takePhoto = (Button)findViewById(R.id.btnPhoto);
        addFish = findViewById(R.id.btnAdd);

        dbHelper = new DatabaseHelper(this);
        spAquarium = (Spinner) findViewById(R.id.fishAqua);

        ArrayAdapter<String> adFish = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, dbHelper.fetchAquariumName());
        adFish.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAquarium.setAdapter(adFish);
        spAquarium.setOnItemSelectedListener(this);



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


        addFish.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                try {
                    DatabaseHelper myDB = new DatabaseHelper(AddFish.this);

                    boolean nameEmpty = fishName.getText().toString().isEmpty();
                    boolean emptySpecies = fishSpecies.getText().toString().isEmpty();
                    boolean emptyQ = fishQuantity.getText().toString().isEmpty();

                    if (nameEmpty) {
                        Toast.makeText(AddFish.this, "Fish name required", Toast.LENGTH_SHORT).show();
                    } else if (emptySpecies) {
                        Toast.makeText(AddFish.this, "Fish species required", Toast.LENGTH_SHORT).show();
                    } else if (emptyQ) {
                        Toast.makeText(AddFish.this, "Fish number required", Toast.LENGTH_SHORT).show();
                    } else if (RgGender.getCheckedRadioButtonId() == -1) {
                        Toast.makeText(AddFish.this, "Please select gender", Toast.LENGTH_SHORT).show();
                    } else {
                        int aquaID = dbHelper.fetchAquariumID(getFishTypeString);
                        String name = fishName.getText().toString();
                        String species = fishSpecies.getText().toString();
                        String path = gender.getContentDescription().toString();
                        int fishNo = Integer.parseInt(fishQuantity.getText().toString());
                        myDB.addFish(aquaID, name.trim(), species.trim(), path.trim(), fishNo, imageToByte(fishImage));
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
        getFishTypeString = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void rbFishGender_clicked(View view){
        int rbId = RgGender.getCheckedRadioButtonId();
        gender = findViewById(rbId);
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
            fishImage.setImageBitmap(photo);
        }

        if (resultCode == RESULT_OK) {
            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    fishImage.setImageURI(selectedImageUri);
                }
            }
        }
    }

    private byte[] imageToByte (ImageView fishImage) {
        Bitmap b = ((BitmapDrawable)fishImage.getDrawable()).getBitmap();
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 90, s);
        byte[] byteArray = s.toByteArray();
        return byteArray;
    }

}