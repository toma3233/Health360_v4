package com.example.health360_v2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.health360_tools.Nutrition;
import com.example.health360_tools.TextNutrition;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainCamera extends AppCompatActivity implements ImageToText.ImageToTextCompleteListener {

    private ImageToText imageToText;
    private static final String TAG = "CAMERA_ACTIVITY";
    private Button camButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_camera_activity);

        camButton = findViewById(R.id.cam_button_2);
        camButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Starting image to text");
                imageToText.start();
            }
        });

        imageToText = new ImageToText(getApplicationContext(), this);
        imageToText.setOnImageToTextComplete(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        imageToText.extractText(requestCode, resultCode, data);

    }

    @Override
    public void onImageToTextComplete(String s) {
        //Log.d(TAG, s);
        Log.d(TAG, new TextNutrition(s.split("\n")).toString());
        TextNutrition final_str = new TextNutrition(s.split("\n"));
        DatabaseReference reff;
        MealValues mealvalues;
        EditText img_weight, img_servings;
        Spinner choose_meal;
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        mealvalues = new MealValues();
        reff = FirebaseDatabase.getInstance().getReference().child("MealValues");

//      img_weight = (EditText)findViewById(R.id.enter_weight);
        img_servings = (EditText)findViewById(R.id.enter_servings);
        double serv = Double.parseDouble(img_servings.getText().toString().trim());
        //mealvalues.setWeight(final_str.get);
        mealvalues.setCalories((final_str.getCalories()) * serv);
        mealvalues.setCarbs(final_str.getCarbohydrates() * serv);
        mealvalues.setFiber(final_str.getFiber() * serv);
        mealvalues.setProtein(final_str.getProtein() * serv);
        mealvalues.setSodium(final_str.getSodium() * serv);
        //mealvalues.setCurrent_meal(current_meal.trim());
        mealvalues.setCurrentDate(currentDate);
        reff.push().setValue(mealvalues);
        Toast.makeText(MainCamera.this, "data inserted successfully", Toast.LENGTH_LONG).show();
    }
}