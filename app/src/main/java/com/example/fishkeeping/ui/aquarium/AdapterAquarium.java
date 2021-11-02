package com.example.fishkeeping.ui.aquarium;

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

public class AdapterAquarium extends RecyclerView.Adapter<AdapterAquarium.MyViewHolder> {

    Context context;
    ArrayList<ModelAquarium> aquaList;
    private DatabaseHelper dbHelper;

    public AdapterAquarium(Context context, ArrayList<ModelAquarium> aquaList) {
        this.context = context;
        this.aquaList = aquaList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_aquarium, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.aqua_name_txt.setText(aquaList.get(position).getAquaName());
        holder.aqua_type_txt.setText(aquaList.get(position).getAquaType());
        holder.aqua_vol_txt.setText(String.valueOf(aquaList.get(position).getAquaVolume()) + " L");


        final byte[] recordImage = aquaList.get(position).getAquaImage();
        final Bitmap bitmap = BitmapFactory.decodeByteArray(recordImage, 0, recordImage.length);
        holder.aqua_img.setImageBitmap(bitmap);


        final float aquaVol = aquaList.get(position).getAquaVolume();
        int fishNo = aquaList.get(position).getAquaFishNo();
        final int aquaID = aquaList.get(position).getAquaID();

        //1 gallon of water = about 3.8L
        if (aquaVol <= 3.8) {
            fishNo = 0;
            holder.aqua_fishNo.setText("Your aquarium can hold up to " + String.valueOf(aquaList.get(position).getAquaFishNo()) + " fishes.");
        } else if (aquaVol > 3.8 && aquaVol <= 7.6) {
            fishNo = 1;
            holder.aqua_fishNo.setText("Your aquarium can hold up to " + String.valueOf(aquaList.get(position).getAquaFishNo()) + " fish.");
        } else if (aquaVol > 7.6 && aquaVol <= 11.3) {
            fishNo = 2;
            holder.aqua_fishNo.setText("Your aquarium can hold up to " + String.valueOf(aquaList.get(position).getAquaFishNo()) + " fishes.");
        } else if (aquaVol > 11.3 && aquaVol <= 15.1) {
            fishNo = 3;
            holder.aqua_fishNo.setText("Your aquarium can hold up to " + String.valueOf(aquaList.get(position).getAquaFishNo()) + " fishes.");
        } else if (aquaVol > 15.1 && aquaVol <= 19.0) {
            fishNo = 4;
            holder.aqua_fishNo.setText("Your aquarium can hold up to " + String.valueOf(aquaList.get(position).getAquaFishNo()) + " fishes.");
        } else if (aquaVol > 19.0) {
            fishNo = 5;
            holder.aqua_fishNo.setText("Your aquarium can hold up to " + String.valueOf(aquaList.get(position).getAquaFishNo()) + " fishes and more.");
        }
        DatabaseHelper myDB = new DatabaseHelper(context);
        myDB.updateNo(String.valueOf(aquaID), fishNo);



        int l = aquaList.get(position).getAquaLength();
        int w = aquaList.get(position).getAquaWidth();
        int h = aquaList.get(position).getAquaHeight();
        holder.aqua_size_txt.setText(l + " X " + w + " X " + h);


        holder.fish_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.nav_fish);
            }
        });


        holder.error_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.nav_problem);
            }
        });

        holder.edit_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.getMenuInflater().inflate(R.menu.edit_aquarium_menu, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.share:
                                shareAqua(String.valueOf(aquaID));
                                return true;
                            case R.id.edit:
                                Intent i = new Intent(context, UpdateAquarium.class);
                                i.putExtra("aquarium_id",aquaList.get(position).getAquaID());
                                i.putExtra("aquarium_name",aquaList.get(position).getAquaName());
                                i.putExtra("aquarium_length",String.valueOf(aquaList.get(position).getAquaLength()));
                                i.putExtra("aquarium_width",String.valueOf(aquaList.get(position).getAquaWidth()));
                                i.putExtra("aquarium_height",String.valueOf(aquaList.get(position).getAquaHeight()));
                                i.putExtra("aquarium_volume",String.valueOf(aquaList.get(position).getAquaVolume()));
                                context.startActivity(i);
                                return true;
                            case R.id.delete:
                                removeAqua(String.valueOf(aquaID));
                                return true;
                            default:
                                return false;
                        }
                    }


                    public void shareAqua(final String aquaID) {
                        Uri imagePath = getImageUri(context, bitmap);
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_TEXT,
                                "Aquarium Name: " + aquaList.get(position).getAquaName() + "\n" +
                                        "Aquarium Type: " + aquaList.get(position).getAquaType() + "\n" +
                                        "Aquarium Size: " + aquaList.get(position).getAquaLength() + " X " +
                                        aquaList.get(position).getAquaWidth() + " X " +
                                        aquaList.get(position).getAquaHeight() + "\n" +
                                        "Aquarium Volume: " + aquaList.get(position).getAquaVolume() );
                        shareIntent.putExtra(Intent.EXTRA_STREAM, imagePath);
                        shareIntent.setType("image/jpeg");
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        context.startActivity(Intent.createChooser(shareIntent, "send"));
                    }

                    public void removeAqua(final String aquaID) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Delete Aquarium?");
                        builder.setMessage("Deleting this aquarium will also delete any fishes associated with the aquarium. Are you sure you want to delete the aquarium?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DatabaseHelper myDB = new DatabaseHelper(context);
                                myDB.deleteAquarium(String.valueOf(aquaID));
                                Navigation.findNavController(view).navigate(R.id.nav_aquarium);
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
        return aquaList.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView aqua_name_txt, aqua_type_txt, aqua_size_txt, aqua_vol_txt, aqua_fishNo;
        private ImageButton fish_btn, error_btn, edit_btn;
        ImageView aqua_img;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            aqua_name_txt = itemView.findViewById(R.id.nameAqua);
            aqua_size_txt = itemView.findViewById(R.id.sizeAqua);
            aqua_type_txt = itemView.findViewById(R.id.typeAqua);
            aqua_vol_txt = itemView.findViewById(R.id.volAqua);
            aqua_fishNo = itemView.findViewById(R.id.noAqua);
            aqua_img = (ImageView) itemView.findViewById(R.id.imgAqua);
            fish_btn = (ImageButton) itemView.findViewById(R.id.btnFish);
            error_btn = (ImageButton) itemView.findViewById(R.id.btnError);
            edit_btn = (ImageButton) itemView.findViewById(R.id.btnMenu);

        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}