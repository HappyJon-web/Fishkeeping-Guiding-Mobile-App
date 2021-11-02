package com.example.fishkeeping.ui.aquarium;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

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

import com.example.fishkeeping.DatabaseHelper;
import com.example.fishkeeping.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class AddAquarium extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner aquaSpType;
    ArrayList<String> aquaType;
    EditText aquaName, aqualength, aquawidth, aquaheight, aquavolume;
    ImageView aquaImage;
    Button selectImage, addAqua, takePhoto;
    private String getAquariumTypeString;
    private static final int CAMERA_REQUEST = 1888;
    int SELECT_PICTURE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_aquarium);

        aquaSpType = (Spinner) findViewById(R.id.aquariumType);
        aquaType = new ArrayList<String>();
        aquaType.add("Freshwater");
        aquaType.add("Saltwater");
        aquaType.add("Other");

        aquaName = findViewById(R.id.aquariumName);
        aqualength = findViewById(R.id.aquariumLength);
        aquawidth = findViewById(R.id.aquariumWidth);
        aquaheight = findViewById(R.id.aquariumHeight);
        aquavolume = findViewById(R.id.aquariumVolume);
        aquaImage = (ImageView)findViewById(R.id.aquariumPhoto);
        selectImage = (Button)findViewById(R.id.btnSelect);
        takePhoto = (Button)findViewById(R.id.btnPhoto);
        addAqua = findViewById(R.id.btnAdd);

        ArrayAdapter<String> adAqua = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, aquaType);
        adAqua.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aquaSpType.setAdapter(adAqua);
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

        addAqua.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                try {
                    DatabaseHelper myDB = new DatabaseHelper(AddAquarium.this);

                    boolean nameEmpty=aquaName.getText().toString().isEmpty();
                    boolean emptyL=aqualength.getText().toString().isEmpty();
                    boolean emptyW=aquawidth.getText().toString().isEmpty();
                    boolean emptyH=aquaheight.getText().toString().isEmpty();
                    boolean emptyVol=aquavolume.getText().toString().isEmpty();

                    if(nameEmpty){
                        Toast.makeText(AddAquarium.this, "Aquarium name required", Toast.LENGTH_SHORT).show();
                    } else if(emptyL){
                        Toast.makeText(AddAquarium.this, "Aquarium length required", Toast.LENGTH_SHORT).show();
                    } else if(emptyW){
                        Toast.makeText(AddAquarium.this, "Aquarium width required", Toast.LENGTH_SHORT).show();
                    } else if(emptyH){
                        Toast.makeText(AddAquarium.this, "Aquarium height required", Toast.LENGTH_SHORT).show();
                    } else if(emptyVol){
                        Toast.makeText(AddAquarium.this, "Aquarium volume required", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        int l = Integer.parseInt(aqualength.getText().toString());
                        int w = Integer.parseInt(aquawidth.getText().toString());
                        int h = Integer.parseInt(aquaheight.getText().toString());
                        float litre = Float.parseFloat(aquavolume.getText().toString());
                        int volume = l * w * h;

                        if(volume <= 1728){
                            Toast.makeText(AddAquarium.this,
                                    "Aquarium is too small.", Toast.LENGTH_SHORT).show();
                        } else if (litre <= 3.8){
                            Toast.makeText(AddAquarium.this,
                                    "Aquarium has too little water for the fishes.", Toast.LENGTH_SHORT).show();
                        } else if (volume > 13 && litre > 3.8){
                            int length = l;
                            int width = w;
                            int height = h;
                            float water = litre;
                            myDB.addAquarium(
                                    aquaName.getText().toString().trim(),
                                    aquaSpType.getSelectedItem().toString().trim(),
                                    length, width, height, water, imageToByte(aquaImage) );
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


}