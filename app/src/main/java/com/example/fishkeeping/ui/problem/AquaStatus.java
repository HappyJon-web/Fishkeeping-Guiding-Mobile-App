package com.example.fishkeeping.ui.problem;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.fishkeeping.DatabaseHelper;
import com.example.fishkeeping.R;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AquaStatus extends Fragment {

    Spinner spAquarium;
    ImageView imgAquarium, emptyImg;
    TextView no_data, aqua_problem, aqua_cause, aqua_solution;
    Button classifyAqua;
    DatabaseHelper dbHelper;

    protected Interpreter tflite;
    private TensorImage inputImageBuffer;
    private  int imageSizeX;
    private  int imageSizeY;
    private  TensorBuffer outputProbabilityBuffer;
    private  TensorProcessor probabilityProcessor;
    private static final float IMAGE_MEAN = 0.0f;
    private static final float IMAGE_STD = 1.0f;
    private static final float PROBABILITY_MEAN = 0.0f;
    private static final float PROBABILITY_STD = 255.0f;
    private List<String> labels;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_aqua_status, container, false);

        imgAquarium = (ImageView)root.findViewById(R.id.imgAquariumID);
        classifyAqua = (Button)root.findViewById(R.id.btnClassifyAquarium);
        spAquarium = (Spinner) root.findViewById(R.id.spAquariumID);

        emptyImg = root.findViewById(R.id.empty_image);
        no_data = root.findViewById(R.id.no_data);

        aqua_problem = root.findViewById(R.id.txtAquaProblem);
        aqua_cause = root.findViewById(R.id.txtAquaCause);
        aqua_solution = root.findViewById(R.id.txtAquaSolution);

        dbHelper = new DatabaseHelper(getActivity());
        int count = dbHelper.countAquarium();

        if(count == 0){
            imgAquarium.setVisibility(View.GONE);
            classifyAqua.setVisibility(View.GONE);
            spAquarium.setVisibility(View.GONE);
            aqua_problem.setVisibility(View.GONE);
            aqua_cause.setVisibility(View.GONE);
            aqua_solution.setVisibility(View.GONE);
            emptyImg.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.VISIBLE);
        } else {
            emptyImg.setVisibility(View.GONE);
            no_data.setVisibility(View.GONE);
            imgAquarium.setVisibility(View.VISIBLE);
            classifyAqua.setVisibility(View.VISIBLE);
            spAquarium.setVisibility(View.VISIBLE);
            aqua_problem.setVisibility(View.VISIBLE);
            aqua_cause.setVisibility(View.VISIBLE);
            aqua_solution.setVisibility(View.VISIBLE);
            ArrayList<String> aquaName = dbHelper.fetchAquariumName();

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(),
                    android.R.layout.simple_spinner_item, aquaName);
            adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            spAquarium.setAdapter(adapter);
            spAquarium.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    String text = spAquarium.getSelectedItem().toString();
                    int aquaID = dbHelper.fetchAquariumID(text);
                    final byte[] aquariumImage = dbHelper.fetchAquaImg(aquaID);
                    final Bitmap bitmap = BitmapFactory.decodeByteArray(aquariumImage, 0, aquariumImage.length);
                    imgAquarium.setImageBitmap(bitmap);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {

                }

            });


            try{
                tflite=new Interpreter(loadmodelfile(getActivity()));
            }catch (Exception e) {
                e.printStackTrace();
            }

            classifyAqua.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BitmapDrawable drawable = (BitmapDrawable) imgAquarium.getDrawable();
                    Bitmap bmap = drawable.getBitmap();
                    int imageTensorIndex = 0;
                    int[] imageShape = tflite.getInputTensor(imageTensorIndex).shape(); // {1, height, width, 3}
                    imageSizeY = imageShape[1];
                    imageSizeX = imageShape[2];
                    DataType imageDataType = tflite.getInputTensor(imageTensorIndex).dataType();

                    int probabilityTensorIndex = 0;
                    int[] probabilityShape =
                            tflite.getOutputTensor(probabilityTensorIndex).shape(); // {1, NUM_CLASSES}
                    DataType probabilityDataType = tflite.getOutputTensor(probabilityTensorIndex).dataType();

                    inputImageBuffer = new TensorImage(imageDataType);
                    outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);
                    probabilityProcessor = new TensorProcessor.Builder().add(getPostprocessNormalizeOp()).build();

                    inputImageBuffer = loadImage(bmap);

                    tflite.run(inputImageBuffer.getBuffer(),outputProbabilityBuffer.getBuffer().rewind());
                    showresult();
                }
            });

        }

        return root;
    }


    private TensorImage loadImage(final Bitmap bitmap) {
        // Loads bitmap into a TensorImage.
        inputImageBuffer.load(bitmap);

        // Creates processor for the TensorImage.
        int cropSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
                        .add(new ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                        .add(getPreprocessNormalizeOp())
                        .build();
        return imageProcessor.process(inputImageBuffer);
    }

    private MappedByteBuffer loadmodelfile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor=activity.getAssets().openFd("aquaModel.tflite");
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startoffset = fileDescriptor.getStartOffset();
        long declaredLength=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startoffset,declaredLength);
    }

    private TensorOperator getPreprocessNormalizeOp() {
        return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
    }
    private TensorOperator getPostprocessNormalizeOp(){
        return new NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD);
    }

    private void showresult(){

        try{
            labels = FileUtil.loadLabels(getActivity(),"aquaLabel.txt");
        }catch (Exception e){
            e.printStackTrace();
        }
        Map<String, Float> labeledProbability =
                new TensorLabel(labels, probabilityProcessor.process(outputProbabilityBuffer))
                        .getMapWithFloatValue();
        Map.Entry<String, Float> maxEntry = null;

        for (Map.Entry<String, Float> entry : labeledProbability.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
            {
                maxEntry = entry;
            }
        }

        String res = String.valueOf(maxEntry);
        int pos = res.indexOf('=');
        String result = res.substring(2,pos);
        DatabaseHelper db = new DatabaseHelper(getActivity());

        if(result.equals("Clean Aquarium Water")){
            aqua_problem.setText(db.fetchAquaIssue(1));
            aqua_cause.setText(db.fetchAquaCause(1));
            aqua_solution.setText(db.fetchAquaSolution(1));
        } else if(result.equals("Green Aquarium Water")){
            aqua_problem.setText(db.fetchAquaIssue(2));
            aqua_cause.setText(db.fetchAquaCause(2));
            aqua_solution.setText(db.fetchAquaSolution(2));
        } else if(result.equals("Cloudy Aquarium Water")){
            aqua_problem.setText(db.fetchAquaIssue(3));
            aqua_cause.setText(db.fetchAquaCause(3));
            aqua_solution.setText(db.fetchAquaSolution(3));
        }

    }

}