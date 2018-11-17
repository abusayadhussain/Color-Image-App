package com.example.ash.colorimageapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnTakeAPicture;
    private Button btnSaveTheOicture;
    private ImageView imgPhoto;
    private SeekBar redSeekBar;
    private SeekBar greenSeekBar;
    private SeekBar blueSeekBar;
    private TextView txtRedColorValue;
    private TextView txtGreenColorValue;
    private TextView txtBlueColorValue;
    private Button btnShare;

    private static final int CAMERA_IMAGE_REQUEST_CODE = 1000;

    private Bitmap bitmap;

    private Colorful colorful;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTakeAPicture = findViewById(R.id.btnTakePicture);
        btnSaveTheOicture = findViewById(R.id.btnSavePicture);
        imgPhoto = findViewById(R.id.imgPhoto);
        redSeekBar = findViewById(R.id.redColorSeekBar);
        greenSeekBar = findViewById(R.id.greenColorSeekBar);
        blueSeekBar = findViewById(R.id.blueColorSeekBar);
        txtRedColorValue = findViewById(R.id.txtRedColorValue);
        txtGreenColorValue = findViewById(R.id.txtGreenColorValue);
        txtBlueColorValue = findViewById(R.id.txtBlueColorValue);
        btnShare = findViewById(R.id.btnShare);

        btnTakeAPicture.setOnClickListener(this);
        btnSaveTheOicture.setOnClickListener(this);
        btnShare.setOnClickListener(this);

        ColorizationHandler colorizationHandler = new ColorizationHandler();

        redSeekBar.setOnSeekBarChangeListener(colorizationHandler);
        greenSeekBar.setOnSeekBarChangeListener(colorizationHandler);
        blueSeekBar.setOnSeekBarChangeListener(colorizationHandler);

        btnSaveTheOicture.setVisibility(View.INVISIBLE);
        redSeekBar.setVisibility(View.INVISIBLE);
        greenSeekBar.setVisibility(View.INVISIBLE);
        blueSeekBar.setVisibility(View.INVISIBLE);
        btnShare.setVisibility(View.INVISIBLE);
        txtRedColorValue.setVisibility(View.INVISIBLE);
        txtGreenColorValue.setVisibility(View.INVISIBLE);
        txtBlueColorValue.setVisibility(View.INVISIBLE);


    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.btnTakePicture){

            int permissionResult = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

            if(permissionResult == PackageManager.PERMISSION_GRANTED){

                PackageManager packageManager = getPackageManager();
                if(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)){

                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_IMAGE_REQUEST_CODE);

                } else{

                    Toast.makeText(this, "Your device doesn't have a camera.", Toast.LENGTH_SHORT).show();
                }

            } else{

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            }

        } else if(v.getId() == R.id.btnSavePicture){

            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if(permissionCheck == PackageManager.PERMISSION_GRANTED){

                try {

                    SaveFile.saveFile(this, bitmap);

                    Toast.makeText(this, "Image is saved in the Storage successfully",Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    e.printStackTrace();
                }
            } else{

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2000);
            }


        } else if(v.getId() == R.id.btnShare){

            try{

                File myPictureFile = SaveFile.saveFile(this, bitmap);
                Uri myUri = Uri.fromFile(myPictureFile);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "This picture is send from The Color image app that I create");
                shareIntent.putExtra(Intent.EXTRA_STREAM, myUri);
                startActivity(Intent.createChooser(shareIntent, "Let's share your pic with others."));
            } catch(Exception e){

                e.printStackTrace();
            }


        }




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Toast.makeText(this, "onActivityResult is called",Toast.LENGTH_SHORT).show();

        if(requestCode == CAMERA_IMAGE_REQUEST_CODE && resultCode == RESULT_OK){

            btnSaveTheOicture.setVisibility(View.VISIBLE);
            redSeekBar.setVisibility(View.VISIBLE);
            greenSeekBar.setVisibility(View.VISIBLE);
            blueSeekBar.setVisibility(View.VISIBLE);
            btnShare.setVisibility(View.VISIBLE);
            txtRedColorValue.setVisibility(View.VISIBLE);
            txtGreenColorValue.setVisibility(View.VISIBLE);
            txtBlueColorValue.setVisibility(View.VISIBLE);

            Bundle bundle = data.getExtras();

            bitmap = (Bitmap) bundle.get("data");

            colorful = new Colorful(bitmap, 0.0f, 0.0f, 0.0f);

            imgPhoto.setImageBitmap(bitmap);



        }
    }

    private class ColorizationHandler implements SeekBar.OnSeekBarChangeListener {


        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


            if(fromUser){


                if(seekBar == redSeekBar){

                    colorful.setRedColorValue(progress/100);
                    redSeekBar.setProgress(100 * (int)(colorful.getRedColorValue()));
                    txtRedColorValue.setText((colorful.getRedColorValue() + ""));


                }

                if(seekBar == greenSeekBar){

                    colorful.setGreenColorValue(progress / 100);
                    greenSeekBar.setProgress(100 * (int)(colorful.getGreenColorValue()));
                    txtGreenColorValue.setText((colorful.getGreenColorValue() + ""));

                }

                if(seekBar == blueSeekBar){

                    colorful.setBlueColorValue(progress / 100);
                    blueSeekBar.setProgress(100 * (int)(colorful.getBlueColorValue()));
                    txtBlueColorValue.setText((colorful.getBlueColorValue() + ""));

                }

                bitmap = colorful.returnTheColorizedBitmap();
                imgPhoto.setImageBitmap(bitmap);
            }



        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
