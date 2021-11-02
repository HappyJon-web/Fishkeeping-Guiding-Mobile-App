package com.example.fishkeeping.ui.fish;

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

public class FishFragment extends Fragment {

    RecyclerView rvFish;
    ImageView empty_image;
    TextView no_fish_data;
    DatabaseHelper fishDB;
    ArrayList<ModelFish> fishList;
    AdapterFish fishAdapter;

    ArrayList<String> fish_id;
    ArrayList<String> fish_aquaID;
    ArrayList<String> fish_name;
    ArrayList<String> fish_species;
    ArrayList<String> fish_gender;
    ArrayList<String> fish_quantity;
    ArrayList<byte[]> fish_image;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_fish, container, false);

        rvFish = root.findViewById(R.id.rvFish);
        empty_image = root.findViewById(R.id.empty_image);
        no_fish_data = root.findViewById(R.id.no_fish_data);

        fishDB = new DatabaseHelper(getActivity());
        fish_id = new ArrayList<>();
        fish_aquaID = new ArrayList<>();
        fish_name = new ArrayList<>();
        fish_species = new ArrayList<>();
        fish_gender = new ArrayList<>();
        fish_quantity = new ArrayList<>();
        fish_image = new ArrayList<>();

        fishList = new ArrayList<>();
        fishList.addAll(storeDataInArrays());
        fishAdapter = new AdapterFish(getActivity(), fishList);
        rvFish.setAdapter(fishAdapter);
        rvFish.setLayoutManager(new LinearLayoutManager(getActivity()));

        return root;

    }

    public ArrayList<ModelFish> storeDataInArrays() {
        Cursor cursor = fishDB.readFish();
        ArrayList<ModelFish> arrData = new ArrayList<>();

        if (cursor.getCount() == 0) {
            rvFish.setVisibility(View.GONE);
            empty_image.setVisibility(View.VISIBLE);
            no_fish_data.setVisibility(View.VISIBLE);
        } else {
            while (cursor.moveToNext()) {
                int fishID = cursor.getInt(0);
                int fishAquarium = cursor.getInt(1);
                String fishName = cursor.getString(2);
                String fishSpecies = cursor.getString(3);
                String fishGender = cursor.getString(4);
                int fishQuantity = cursor.getInt(5);
                byte[] fishImg = cursor.getBlob(6);

                arrData.add(new ModelFish(fishID, fishAquarium, fishName, fishSpecies,
                        fishGender, fishQuantity, fishImg));
                rvFish.setVisibility(View.VISIBLE);
                empty_image.setVisibility(View.GONE);
                no_fish_data.setVisibility(View.GONE);
            }
        }

        return arrData;
    }

}