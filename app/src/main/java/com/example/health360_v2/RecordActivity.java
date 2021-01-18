package com.example.health360_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RecordActivity extends AppCompatActivity {
//    DatabaseReference freff;
//    Button querybtn = (Button)findViewById(R.id.query_button);
//    EditText date_entry;
//
//    TextView meal_v = (TextView)findViewById(R.id.meal_view);
//    TextView date_v = (TextView)findViewById(R.id.date_view);
//    TextView calorie_v = (TextView)findViewById(R.id.calorie_view);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.record_activity);

//        querybtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                date_entry = (EditText)findViewById(R.id.enter_date);
//                freff = FirebaseDatabase.getInstance().getReference().child("MealValues").child("-MRGEs4K4rxrzlT87rWd");
//                freff.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        String m = snapshot.child("current_meal").getValue().toString();
//                        String d = snapshot.child("currentDate").getValue().toString();
//                        String c = snapshot.child("calories").getValue().toString();
//                        meal_v.setText(m);
//                        date_v.setText(d);
//                        calorie_v.setText(c);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//            }
//        });

    }
}