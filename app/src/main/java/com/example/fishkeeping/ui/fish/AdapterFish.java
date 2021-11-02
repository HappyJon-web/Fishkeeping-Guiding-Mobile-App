package com.example.fishkeeping.ui.fish;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fishkeeping.DatabaseHelper;
import com.example.fishkeeping.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class AdapterFish extends RecyclerView.Adapter<AdapterFish.MyViewHolder> {

    Context context;
    ArrayList<ModelFish> fishList;

    public AdapterFish(Context context, ArrayList<ModelFish> fishList) {
        this.context = context;
        this.fishList = fishList;
    }
    
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_fish, parent, false);
        return new AdapterFish.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.fish_name_txt.setText(fishList.get(position).getFishName());
        holder.fish_species_txt.setText(fishList.get(position).getFishSpecies());
        holder.fish_gender_txt.setText("Gender: " + fishList.get(position).getFishGender());
        holder.fish_qty_txt.setText("Qty: " + String.valueOf(fishList.get(position).getFishQuantity()));

        final byte[] fishPhoto = fishList.get(position).getFishImage();
        final Bitmap bitmap = BitmapFactory.decodeByteArray(fishPhoto, 0, fishPhoto.length);
        holder.fish_img.setImageBitmap(bitmap);

        DatabaseHelper myDB = new DatabaseHelper(context);
        int aquarium = fishList.get(position).getAquaID();
        String fishAqua = myDB.fetchAquaName(aquarium);
        holder.fish_aquarium_txt.setText("Aquarium: " + fishAqua);

        final int fishID = fishList.get(position).getFish_id();

        holder.menu_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.getMenuInflater().inflate(R.menu.edit_fish_menu, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.share:
                                shareFish(String.valueOf(fishID));
                                return true;
                            case R.id.edit:
                                Intent i = new Intent(context, UpdateFish.class);
                                i.putExtra("fish_id",fishList.get(position).getFish_id());
                                i.putExtra("fish_name",fishList.get(position).getFishName());
                                i.putExtra("fish_species",fishList.get(position).getFishSpecies());
                                i.putExtra("fish_quantity",String.valueOf(fishList.get(position).getFishQuantity()));
                                context.startActivity(i);
                                return true;
                            case R.id.delete:
                                removeFish(String.valueOf(fishID));
                                return true;
                            default:
                                return false;
                        }
                    }


                    public void shareFish(final String fishID) {
                        Uri imagePath = getImageUri(context, bitmap);
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_TEXT,
                                "Fish Name: " + fishList.get(position).getFishName() + "\n" +
                                        "Fish Species: " + fishList.get(position).getFishSpecies() + "\n" +
                                        "Fish Gender: " + fishList.get(position).getFishGender() + "\n" +
                                        "Fish Quantity: " + fishList.get(position).getFishQuantity() );
                        shareIntent.putExtra(Intent.EXTRA_STREAM, imagePath);
                        shareIntent.setType("image/jpeg");
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        context.startActivity(Intent.createChooser(shareIntent, "send"));
                    }

                    public void removeFish(final String fishID) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Delete Fish?");
                        builder.setMessage("Are you sure you want to delete this fish?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DatabaseHelper myDB = new DatabaseHelper(context);
                                myDB.deleteFish(fishID);
                                Navigation.findNavController(view).navigate(R.id.nav_fish);
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
    public int getItemCount() {
        return fishList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView fish_name_txt, fish_species_txt, fish_gender_txt, fish_qty_txt, fish_aquarium_txt;
        private ImageButton menu_btn;
        ImageView fish_img;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            fish_name_txt = itemView.findViewById(R.id.nameFish);
            fish_species_txt = itemView.findViewById(R.id.speciesFish);
            fish_gender_txt = itemView.findViewById(R.id.genderFish);
            fish_aquarium_txt = itemView.findViewById(R.id.fishAquarium);
            fish_qty_txt = itemView.findViewById(R.id.qtyFish);
            fish_img = (ImageView) itemView.findViewById(R.id.imgFish);
            menu_btn = (ImageButton) itemView.findViewById(R.id.fishEditMenu);
        }
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


}
