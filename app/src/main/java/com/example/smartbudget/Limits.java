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

import java.time.LocalDate;

public class Limits extends AppCompatActivity {
    SharedPreferences sp;
    String zbyvajiciLimitPref, nastavenyLimitPref, aktualMesic;
    EditText nastavLimitCastka, zbyvaLimitCastka;
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
        nastavLimitCastka = findViewById(R.id.nastavlimitVstup);
        zbyvaLimitCastka = findViewById(R.id.zbyvalimitVstup);

        sp = getSharedPreferences("limits",MODE_PRIVATE);
        nastavenyLimitPref = sp.getString("nastavenyLimit",String.valueOf(1000));
        aktualMesic = sp.getString("aktualMesic",String.valueOf(LocalDate.now().getMonthValue()));

        if(!aktualMesic.equals(String.valueOf(LocalDate.now().getMonthValue()))){
            SharedPreferences.Editor spE= sp.edit();
            spE.putString("zbyvajiciLimit",nastavenyLimitPref);
            spE.putString("aktualMesic",String.valueOf(LocalDate.now().getMonthValue()));
            spE.commit();
        }

        zbyvajiciLimitPref = sp.getString("zbyvajiciLimit",nastavenyLimitPref);

        nastavLimitCastka.setText(nastavenyLimitPref);
        zbyvaLimitCastka.setText(zbyvajiciLimitPref);

    }
    public void changeScreen(View view){
        Intent intentMain = new Intent(Limits.this, MainActivity.class);
        startActivity(intentMain);
    }

    public void nastavLimit(View view){
        SharedPreferences.Editor spE= sp.edit();
        spE.putString("nastavenyLimit",nastavLimitCastka.getText().toString());
        spE.putString("aktualMesic",String.valueOf(LocalDate.now().getMonthValue()));
        spE.commit();
        finish();
        startActivity(getIntent());
    }
}