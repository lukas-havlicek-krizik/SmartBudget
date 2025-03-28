package com.example.smartbudget;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void changeScreen(View view) {
        if(view.getId()==R.id.btnAddNew){
            Intent intentAddNew = new Intent(MainActivity.this, AddNew.class);
            startActivity(intentAddNew);
        }else if(view.getId()==R.id.btnOverview){
            Intent intentOverview = new Intent(MainActivity.this, Overview.class);
            startActivity(intentOverview);
        }else if(view.getId()==R.id.btnLimits){
            Intent intentLimits = new Intent(MainActivity.this, Limits.class);
            startActivity(intentLimits);
        }else if(view.getId()==R.id.btnSpendings){
            Intent intentSpendings = new Intent(MainActivity.this, Spendings.class);
            startActivity(intentSpendings);
        }
    }
}