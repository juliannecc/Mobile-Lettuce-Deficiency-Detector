package com.example.sample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;

import com.example.sample.ml.Vggseg;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;

import com.example.sample.databinding.ActivityMainBinding;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.model.Model;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MainActivity extends AppCompatActivity {

    Button CameraBtn, SelectBtn;
    ImageView ImageView;
    TextView Result;
    Bitmap bitmap;
    // Mat src;
    int imageSize =  256;

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    static {
        if (OpenCVLoader.initDebug()) {
            Log.d("MainActivity", "OpenCV is loaded");
        } else {
            Log.d("MainActivity", "OpenCV is not loaded");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CameraBtn = findViewById(R.id.CameraBtn);
        SelectBtn = findViewById(R.id.SelectBtn);

        Result = findViewById(R.id.Result);

        ImageView = findViewById(R.id.ImageView);

        CameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 3);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });

        SelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(cameraIntent, 1);
            }
        });

    }
    public TensorBuffer segmentImage(Bitmap image) {
        TensorBuffer outputFeature0 = null;
        try {
            Vggseg model = Vggseg.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 256, 256, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocate(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());
            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;

            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++];
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 1));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Vggseg.Outputs outputs = model.process(inputFeature0);
            outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }

        return outputFeature0;
    }

    public void classifyImage(Bitmap image){
        try {
            Model model = Model.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 256, 256, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocate(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());
            int [] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;

            for(int i = 0; i < imageSize; i++){
                for(int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++];
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f/1));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f/1));
                    byteBuffer.putFloat((val & 0xFF)  * (1.f/1));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 3) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                ImageView.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                Bitmap maskA = segmentImage(image);
                Bitmap segmentedImage = [];
                Core.bitwise_or(MaskA, image, segmentedImage);
                Core.add(dest, Scalar.all(0), dest);



//                Mat matRGB = new Mat();
//                Mat matLab = new Mat();
//                ArrayList<Mat> LabChannels = new ArrayList<>(3);
//
//                Mat maskA = new Mat();
//                Mat maskB = new Mat();
//                Mat maskAB = new Mat();
//
//                Mat dest = new Mat();
//
//                Utils.bitmapToMat(image, matRGB);
//
//                Imgproc.cvtColor(matRGB, matLab, Imgproc.COLOR_RGB2Lab);
//                Core.split(matLab, LabChannels);
//
//                Imgproc.threshold(LabChannels.get(1), maskA, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
//                Imgproc.threshold(LabChannels.get(2), maskB, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);

//                Core.bitwise_or(maskA, maskB, maskAB);
//
//                Core.add(dest, Scalar.all(0), dest);
//
//                matRGB.copyTo(dest, maskAB);
//
//                Utils.matToBitmap(dest, image);
                ImageView.setImageBitmap(image);
            }else{
                Uri dat = data.getData();
                Bitmap image = null;
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
                    ImageView.setImageBitmap(image);

                    image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                    Mat maskA = segmentImage(image);

//                    Mat matRGB = new Mat();
//                    Mat matLab = new Mat();
//                    ArrayList<Mat> LabChannels = new ArrayList<>(3);
//
//                    Mat maskA = new Mat();
//                    Mat maskB = new Mat();
//                    Mat maskAB = new Mat();
//
//                    Mat dest = new Mat();
//
//                    Utils.bitmapToMat(image, matRGB);
//
//                    Imgproc.cvtColor(matRGB, matLab, Imgproc.COLOR_RGB2Lab);
//                    Core.split(matLab, LabChannels);
//
//                    Imgproc.threshold(LabChannels.get(1), maskA, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
//                    Imgproc.threshold(LabChannels.get(2), maskB, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);
//
//                    Core.bitwise_or(maskA, maskB, maskAB);
//
//                    Core.add(dest, Scalar.all(0), dest);
//
//                    matRGB.copyTo(dest, maskAB);
//
//                    Utils.matToBitmap(dest, image);
                    ImageView.setImageBitmap(image);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}