package com.example.smartbudget;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Limits extends AppCompatActivity {
    SharedPreferences sp;
    private String zbyvajiciLimitPref;
    private String nastavenyLimitPref;
    private EditText nastavLimit;
    private EditText zbyvaLimit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_limits);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        nastavLimit = findViewById(R.id.nastavlimitVstup);
        zbyvaLimit = findViewById(R.id.zbyvalimitVstup);

        sp = getSharedPreferences("limits",MODE_PRIVATE);
        nastavenyLimitPref = sp.getString("nastavenyLimit",String.valueOf(1000));
        zbyvajiciLimitPref = sp.getString("zbyvajiciLimit",String.valueOf(900));

        nastavLimit.setText(nastavenyLimitPref);
        zbyvaLimit.setText(zbyvajiciLimitPref);

    }
    public void changeScreen(View view){
        Intent intentMain = new Intent(Limits.this, MainActivity.class);
        startActivity(intentMain);
    }
}