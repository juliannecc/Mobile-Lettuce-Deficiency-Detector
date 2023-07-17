package com.example.sample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;

import com.example.sample.databinding.ActivityMainBinding;
import com.example.sample.ml.MNetLarge;
import com.example.sample.ml.MNetSmall;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button CameraBtn, SelectBtn;
    ImageView ImageView;
    TextView Result;
    Bitmap bitmap;
    // Mat src;
    int imageSize =  512;

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
    public void classifyImage(Bitmap image){
        try {
            MNetSmall model = MNetSmall.newInstance(getApplicationContext());

            TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
            tensorImage.load(image);

            TensorBuffer outputBuffer = TensorBuffer.createFixedSize(new int[]{1, 3}, DataType.FLOAT32);
            MNetSmall.Outputs outputs = model.process(tensorImage.getTensorBuffer());
            outputBuffer = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputBuffer.getFloatArray();

            int MaxPos = 0;
            float Maxconfidence = 0;
            for(int i = 0; i < confidences.length; i++){
                if(confidences[i]>Maxconfidence){
                    Maxconfidence = confidences[i];
                    MaxPos = i;
                }
            }
            String[] classes = {"Potassium Deficient", "Nitrogen Deficient", "Healthy"};
            Result.setText(classes[MaxPos]);

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }
    public float[] segmentImage(Bitmap image) {
        float[] data = new float[0];
        try {
            Interpreter interpreter = new Interpreter(loadModelFile());
            interpreter.allocateTensors();

            ImageProcessor imageProcessor =
                    new ImageProcessor.Builder()
                            .add(new ResizeOp(imageSize, imageSize, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                            .build();
            TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
            tensorImage.load(image);
            tensorImage = imageProcessor.process(tensorImage);

            TensorBuffer outputBuffer = TensorBuffer.createFixedSize(new int[]{1, 65536, 2}, DataType.FLOAT32);
            interpreter.run(tensorImage.getBuffer(), outputBuffer.getBuffer());
            interpreter.close();
            data = outputBuffer.getFloatArray();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return data;
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor=this.getAssets().openFd("vggseg.tflite");
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startOffset=fileDescriptor.getStartOffset();
        long declareLength=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,declareLength);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        if (resultCode == RESULT_OK) {
            if (requestCode == 3) {
                Bitmap image = ((Bitmap) data.getExtras().get("data"))
                        .copy(Bitmap.Config.ARGB_8888, true);

                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);

                Bitmap convertedImage = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight());
                convertedImage.setConfig(Bitmap.Config.ARGB_8888);

                float[] outputArr = segmentImage(convertedImage);
                ArrayList<Float> mask = new ArrayList<>();

                for(int i =0;i< outputArr.length;i=i+2){
                    mask.add(outputArr[i]);
                }

                Bitmap resized = Bitmap.createScaledBitmap(image, 256, 256, false);
                Bitmap output = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);

                for (int y = 0; y < 256; y++) {
                    for (int x = 0; x < 256; x++) {
                        output.setPixel(x, y, mask.get(y * 256 + x) > 0.9 ? Color.TRANSPARENT : resized.getPixel(x,y));
                    }
                }
                Bitmap outputResized = Bitmap.createScaledBitmap(output, 512, 512, false);
                classifyImage(outputResized);
                ImageView.setImageBitmap(outputResized);
            }else{
                Uri dat = data.getData();
                Bitmap image = null;
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
                    float[] outputArr = segmentImage(image);
                    ArrayList<Float> mask = new ArrayList<>();

                    for(int i =0;i< outputArr.length;i=i+2){
                        mask.add(outputArr[i]);
                    }

                    Bitmap resized = Bitmap.createScaledBitmap(image, 256, 256, false);
                    Bitmap output = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);

                    for (int y = 0; y < 256; y++) {
                        for (int x = 0; x < 256; x++) {
                                output.setPixel(x, y, mask.get(y * 256 + x) > 0.9 ? Color.TRANSPARENT : resized.getPixel(x,y));
                             }
                    }
                    Bitmap outputResized = Bitmap.createScaledBitmap(output, 512, 512, false);
                    classifyImage(outputResized);
                    ImageView.setImageBitmap(outputResized);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}