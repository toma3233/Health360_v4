package com.example.health360_v2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Semaphore;

public class ImageToText {
  Context context;
  Activity activity;
  private final int IMAGE_REQ_CODE = 101;
  private String imagePath = "";
  private Uri imageUri;
  String imageText;
  private ImageToTextCompleteListener textAvailableListener;
  private static final String TAG = "TEXT_RECOGNITION";

  public ImageToText(Context context, Activity activity) {
    this.context = context;
    this.activity = activity;
    textAvailableListener = null;

    // check for camera permissions
    ArrayList<String> permissionList = new ArrayList<String>();
    if (context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
      permissionList.add(Manifest.permission.CAMERA);
    }

    // check for read external storage permission
    if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
      permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    // check for write external storage permission
    if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
      permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    if (permissionList.size() > 0) {
      String[] permissionRequest = new String[permissionList.size()];
      activity.requestPermissions(permissionList.toArray(permissionRequest), IMAGE_REQ_CODE);
    }

    Log.d(TAG, "Camera permission: " + context.checkSelfPermission(Manifest.permission.CAMERA));
    Log.d(TAG, "Read storage:  " + context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE));
    Log.d(TAG, "Write storage: " + context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE));


  }

  public void setOnImageToTextComplete(ImageToTextCompleteListener listener) {
    textAvailableListener = listener;
  }

  public void start() {
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if(intent.resolveActivity(context.getPackageManager()) != null) {

      if (!imagePath.isEmpty()) {
        cleanup();
      }

      try {
        // create a new image file
        File file = createImageFile();
        imageUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
      } catch (IOException e) {
        Log.d(TAG, "Error in creating image file");
        return;
      }

      activity.startActivityForResult(intent, IMAGE_REQ_CODE);
    } else {
      Log.d(TAG, "Unable to resolve activity with package manager");
    }

  }

  // create the image file in the correct directory
  private File createImageFile() throws IOException {

    // create the file name so that it wont conflict with any other file
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String imageFileName = "JPEG_" + timeStamp + "_";
    Log.d(TAG, "Create image file: name : " + imageFileName);

    File storageDir = context.getFilesDir();

    if (!storageDir.exists()) {
      Log.d(TAG, "Storage folder \"" + storageDir.getName() + "\" doesn't exists");
      return null;
    }

    Log.d(TAG, "Create image file: storage dir : " + storageDir.getAbsolutePath().toString());

    // create the image file in storageDir
    File image = null;
    try {
      image = File.createTempFile(imageFileName, ".jpg", storageDir);
      // Save a file: path for use with ACTION_VIEW intents
      imagePath = image.getAbsolutePath();
      Log.d(TAG, "Created image temp file : " + imagePath);
    } catch (Exception e) {
      Log.d(TAG, "Create image file: Exception : " + e.getMessage());
    }

    return image;
  }

  public void extractText(int requestCode, int resultCode, @Nullable Intent data) {
    Log.d(TAG, "onActivityResult: request code: " + requestCode + " result code: " + resultCode);
    Bitmap bitmap;

    if (data == null) {
      Log.d(TAG, "onActivityResult: data pointer is NULL");

      // wait for some time so that the image can load into the directory
      try {
        Thread.sleep(500);
      } catch (Exception e) {
        Log.d(TAG, "onActivityResult: sleep failed");
      }

      // get the image from the given file path
      File imageResultFile = new File(imagePath);
      if (imageResultFile.exists()) {
        Log.d(TAG, "onActivityResult: image file exists. size " + imageResultFile.length());
        bitmap = BitmapFactory.decodeFile(imagePath);
        //cleanup();

        try {
          ExifInterface exif = new ExifInterface(imagePath);
          int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
          Log.d(TAG, "Detected image orientation as " + orientation);
          int rotate;
          switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270: rotate = 270; break;
            case ExifInterface.ORIENTATION_ROTATE_180: rotate = 180; break;
            case ExifInterface.ORIENTATION_ROTATE_90: rotate = 90; break;
            default: rotate = 0;
          }

          Matrix matrix = new Matrix();
          matrix.postRotate(rotate);
          bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        } catch (Exception e) {
          Log.d(TAG, "Unable to open exit interface to image file");
        }

        // adjust the resolution of the bitmap
        if (bitmap.getWidth() > 1000) {
          double scale = 1000.0 / bitmap.getWidth();
          int newWidth = (int) (bitmap.getWidth() * scale);
          int newHeight = (int) (bitmap.getHeight() * scale);
          bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        }

      } else {
        Log.d(TAG, "onActivityResult: image file not found " + imageResultFile.getAbsolutePath());
        return;
      }

    } else {
      Bundle bundle = data.getExtras();
      bitmap = (Bitmap) bundle.get("data");
    }

    // create bitmap to make VisionImage Object
    int width = bitmap.getWidth();
    int height = bitmap.getHeight();
    Log.d(TAG, "Image resolution: WxH = " + width + "x" + height + " result code: " + resultCode);

    FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

    // make a Text Recognizer object
    FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();

    // process the image
    // two scenarios if processing fails or succeeds
    detector.processImage(image)
      .addOnSuccessListener(
        new OnSuccessListener<FirebaseVisionText>() {
          @Override
          public void onSuccess(FirebaseVisionText texts) {
            imageText = texts.getText();
            Log.d(TAG, "Got text successfully\n"  + imageText);
            textAvailableListener.onImageToTextComplete(imageText);
          }
        })
      .addOnFailureListener(
        new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            Log.d(TAG, "Failed to get text " + e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            imageText = "";
            textAvailableListener.onImageToTextComplete(imageText);
          }
        });
  }

  public void save(String prefix) {
    File storageDir = context.getFilesDir();

    if (imagePath.isEmpty()) {
      Toast.makeText(context, "File already cleaned up", Toast.LENGTH_LONG).show();
      return;
    }

    File imageFile = new File(storageDir, prefix + "_image.jpg");
    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
    if (bitmap.getWidth() > 500) {
      double scale = 500.0 / bitmap.getWidth();
      int newWidth = (int) (bitmap.getWidth() * scale);
      int newHeight = (int) (bitmap.getHeight() * scale);
      bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }
    try {
      FileOutputStream outImage = new FileOutputStream(imageFile);
      bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outImage);
      outImage.flush();
      outImage.close();
      Log.d(TAG, "Save image file successfully");
    } catch (Exception e) {
      Log.d(TAG, "Unable to save image file, error " + e.getMessage());
    }

    File txtFile = new File(storageDir, prefix + "_info.txt");
    try {
      FileWriter infoTextWriter = new FileWriter(txtFile);
      infoTextWriter.write(imageText);
      infoTextWriter.flush();
      infoTextWriter.close();
      Log.d(TAG, "Save text file successfully");
    } catch (Exception e) {
      Log.d(TAG, "Unable to save text file, error " + e.getMessage());
    }
  }

  public void cleanup() {
    File imageFile = new File(imagePath);
    imageFile.delete();
    imagePath = "";
  }

  public interface ImageToTextCompleteListener {
    void onImageToTextComplete(String s);
  }

}
