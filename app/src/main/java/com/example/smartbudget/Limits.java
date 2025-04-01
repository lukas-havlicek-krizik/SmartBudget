package com.example.smartbudget;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
        nastavenyLimitPref = sp.getString("nastavenyLimit",String.valueOf(0));
        aktualMesic = sp.getString("aktualMesic",String.valueOf(LocalDate.now().getMonthValue()));

        if(!aktualMesic.equals(String.valueOf(LocalDate.now().getMonthValue()))){
            SharedPreferences.Editor spE= sp.edit();
            spE.putString("zbyvajiciLimit",nastavenyLimitPref);
            spE.putString("aktualMesic",String.valueOf(LocalDate.now().getMonthValue()));
            spE.commit();
        }

        if(Integer.parseInt(nastavenyLimitPref)!=0) {
            zbyvajiciLimitPref = sp.getString("zbyvajiciLimit",nastavenyLimitPref);
            nastavLimitCastka.setText(nastavenyLimitPref);
            zbyvaLimitCastka.setText(zbyvajiciLimitPref);
        }

    }
    public void changeScreen(View view){
        Intent intentMain = new Intent(Limits.this, MainActivity.class);
        startActivity(intentMain);
    }

    public void nastavLimit(View view){
        String limitText = nastavLimitCastka.getText().toString();
        if(!limitText.isEmpty()) {
            try {
                int limit = Integer.parseInt(limitText);
                if (limit != 0) {
                    SharedPreferences.Editor spE = sp.edit();
                    spE.putString("nastavenyLimit", nastavLimitCastka.getText().toString());
                    spE.putString("aktualMesic", String.valueOf(LocalDate.now().getMonthValue()));
                    spE.commit();
                    finish();
                    startActivity(getIntent());
                } else {
                    Toast.makeText(this, "Limit nesmí být 0.", Toast.LENGTH_LONG).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Zadejte platné číslo.", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this, "Nastavte limit.", Toast.LENGTH_LONG).show();
        }
    }
}