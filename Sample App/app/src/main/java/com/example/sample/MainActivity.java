package com.example.sample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.service.controls.templates.ThumbnailTemplate;
import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.sample.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    Button CameraBtn, SelectBtn;
    ImageView ImageView;
    TextView Result;
    Bitmap bitmap;
    // Mat src;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 3) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);

                Mat matRGB = new Mat();

                Mat matLab = new Mat();
                Mat AAA = new Mat();
                Mat BBB = new Mat();
                ArrayList<Mat> LabChannels = new ArrayList<>(3);

                Mat maskA = new Mat();
                Mat maskB = new Mat();

                Utils.bitmapToMat(image, matRGB);

                Imgproc.cvtColor(matRGB, matLab, Imgproc.COLOR_RGB2Lab);
                Core.split(matLab, LabChannels);
                Core.merge(new ArrayList<>(Arrays.asList(LabChannels.get(1), LabChannels.get(1), LabChannels.get(1))), AAA);
                Core.merge(new ArrayList<>(Arrays.asList(LabChannels.get(2), LabChannels.get(2), LabChannels.get(2))), BBB);

                Imgproc.cvtColor(AAA, AAA, Imgproc.COLOR_RGB2GRAY);
                Imgproc.cvtColor(BBB, BBB, Imgproc.COLOR_RGB2GRAY);

                double tsa = Imgproc.adaptiveThreshold();

                Utils.matToBitmap(BBB, image);

                ImageView.setImageBitmap(image);
            }else{
                Uri dat = data.getData();
                Bitmap image = null;
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);

                    Mat matRGB = new Mat();

                    Utils.bitmapToMat(image, matRGB);

                    Imgproc.cvtColor(matRGB, matRGB, Imgproc.COLOR_RGB2GRAY);

                    Utils.matToBitmap(matRGB, image);

                    ImageView.setImageBitmap(image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}