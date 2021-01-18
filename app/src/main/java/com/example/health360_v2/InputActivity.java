package com.example.health360_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InputActivity extends AppCompatActivity {
    // Declarations for DB inserting
    EditText weight, calories, carbs, fiber, protein, sodium;
    Spinner choose_meal_manual;
    String current_meal, currentDate;
    Button submitbtn;
    DatabaseReference reff;
    MealValues mealvalues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.input_activity);

        // activity switch when clicking "USE CAMERA" button
        Button cam_activity = (Button) findViewById(R.id.cam_button);
        cam_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                openCameraActivity();
            }
        });

        // Show Toast message confirmation of firebase db connection
        Toast.makeText(InputActivity.this, "Firebase Connection: Success", Toast.LENGTH_LONG).show();

        choose_meal_manual = findViewById(R.id.choose_meal_manual);
        choose_meal_manual.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                current_meal = choose_meal_manual.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // CODE FOR DB INSERTION BEGINS
        weight = (EditText)findViewById(R.id.enter_weight_manual);
        calories = (EditText)findViewById(R.id.enter_calories);
        carbs = (EditText)findViewById(R.id.enter_carbs);
        fiber = (EditText)findViewById(R.id.enter_fiber);
        protein = (EditText)findViewById(R.id.enter_protein);
        sodium = (EditText)findViewById(R.id.enter_sodium);
        submitbtn = (Button)findViewById(R.id.submit_button);
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        mealvalues = new MealValues();
        reff = FirebaseDatabase.getInstance().getReference().child("MealValues");

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double w = Double.parseDouble(weight.getText().toString().trim());
                double cal = Double.parseDouble(calories.getText().toString().trim());
                double car = Double.parseDouble(carbs.getText().toString().trim());
                double f = Double.parseDouble(fiber.getText().toString().trim());
                double p = Double.parseDouble(protein.getText().toString().trim());
                double sod = Double.parseDouble(weight.getText().toString().trim());
                mealvalues.setWeight(w);
                mealvalues.setCalories(cal);
                mealvalues.setCarbs(car);
                mealvalues.setFiber(f);
                mealvalues.setProtein(p);
                mealvalues.setSodium(sod);
                mealvalues.setCurrent_meal(current_meal.trim());
                mealvalues.setCurrentDate(currentDate);
                reff.push().setValue(mealvalues);
                Toast.makeText(InputActivity.this, "data inserted successfully", Toast.LENGTH_LONG).show();

            }
        });

    }

    public void openCameraActivity() {
        Intent intent = new Intent(this, MainCamera.class);
        startActivity(intent);
    }
}