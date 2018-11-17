package com.example.ash.colorimageapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.SystemClock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class SaveFile {

    public static File saveFile(Activity myActivity, Bitmap bitmap) throws IOException {

        String externalStorageState = Environment.getExternalStorageState();
        File myFile = null;

        if(externalStorageState.equals(Environment.MEDIA_MOUNTED)){

            File pictureDirectory = myActivity.getExternalFilesDir("ColorImagePictures");
            Date currentDate = new Date();
            long elapsedTime = SystemClock.elapsedRealtime();
            String uniqueImageName = "/" + currentDate + "_" + elapsedTime + ".png";

            myFile = new File(pictureDirectory + uniqueImageName);

            long remainingSpace = pictureDirectory.getFreeSpace();
            long requiredSpace = bitmap.getByteCount();
            if(requiredSpace * 1.8 < remainingSpace){

                try{

                    FileOutputStream fileOutputStream = new FileOutputStream(myFile);
                    boolean isImageSaveSuccessfully = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

                    if(isImageSaveSuccessfully){

                        return myFile;

                    } else{

                        throw  new IOException("The is image is not save successfully to external storage");
                    }
                }catch (Exception e){
                    throw new IOException("The operation of saving the image to external Storage goes wrong");
                }



            } else{

                throw new IOException("There is no enough space in order to save the image to external Storage");
            }

        } else{

            throw  new IOException("This device doesn't have an internal storage");
        }


    }
}
