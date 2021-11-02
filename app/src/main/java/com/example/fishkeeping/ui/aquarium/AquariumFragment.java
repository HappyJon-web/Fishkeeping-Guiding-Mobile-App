package com.example.fishkeeping.ui.aquarium;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fishkeeping.DatabaseHelper;
import com.example.fishkeeping.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class AquariumFragment extends Fragment {

    RecyclerView rvAquarium;
    ImageView empty_image;
    TextView no_data;
    DatabaseHelper aquaDB;
    ArrayList<ModelAquarium> aquaList;
    AdapterAquarium aquaAdapter;

    ArrayList<String> aqua_id;
    ArrayList<String> aqua_name;
    ArrayList<String> aqua_type;
    ArrayList<String> aqua_length;
    ArrayList<String> aqua_width;
    ArrayList<String> aqua_height;
    ArrayList<String> aqua_volume;
    ArrayList<byte[]> aqua_image;
    ArrayList<String> aqua_fishNo;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_aquarium, container, false);

            rvAquarium = root.findViewById(R.id.rvAquarium);
            empty_image = root.findViewById(R.id.empty_image);
            no_data = root.findViewById(R.id.no_data);

            aquaDB = new DatabaseHelper(getActivity());
            aqua_id = new ArrayList<>();
            aqua_name = new ArrayList<>();
            aqua_type = new ArrayList<>();
            aqua_length = new ArrayList<>();
            aqua_width = new ArrayList<>();
            aqua_height = new ArrayList<>();
            aqua_volume = new ArrayList<>();
            aqua_image = new ArrayList<>();
            aqua_fishNo = new ArrayList<>();

        aquaList = new ArrayList<>();
        aquaList.addAll(storeDataInArrays());
        aquaAdapter = new AdapterAquarium(getActivity(), aquaList);
        rvAquarium.setAdapter(aquaAdapter);
        rvAquarium.setLayoutManager(new LinearLayoutManager(getActivity()));

            return root;

    }

    public ArrayList<ModelAquarium> storeDataInArrays() {
        Cursor cursor = aquaDB.readAquarium();
        ArrayList<ModelAquarium> arrData = new ArrayList<>();

        if(cursor.getCount() == 0){
            rvAquarium.setVisibility(View.GONE);
            empty_image.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.VISIBLE);
        } else {
            while (cursor.moveToNext()){
                int aquaID = cursor.getInt(0);
                String aquaName = cursor.getString(1);
                String aquaType = cursor.getString(2);
                int length = cursor.getInt(3);
                int width = cursor.getInt(4);
                int height = cursor.getInt(5);
                float vol = cursor.getFloat(6);
                byte[] img = cursor.getBlob(7);
                int fishNo = cursor.getInt(8);

                arrData.add(new ModelAquarium(aquaID, aquaName, aquaType, length, width, height,
                    vol, img, fishNo));
            }
            rvAquarium.setVisibility(View.VISIBLE);
            empty_image.setVisibility(View.GONE);
            no_data.setVisibility(View.GONE);
        }

        return arrData;
    }


}