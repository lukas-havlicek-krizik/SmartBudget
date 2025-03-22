package com.example.smartbudget;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UpdateDetails extends AppCompatActivity {
    EditText vstupDatum;
    EditText vstupCastka;
    Spinner kategorie;
    Switch prepinac;
    ArrayAdapter<CharSequence> adapter;
    private ZaznamOperations zaznamDBoperation;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        vstupDatum = findViewById(R.id.vstupDatum);
        vstupCastka = findViewById(R.id.vstupCastka);
        kategorie = findViewById(R.id.kategorie);
        prepinac = findViewById(R.id.prepinac);
        zaznamDBoperation = new ZaznamOperations(this);
        zaznamDBoperation.open();

        prepinac.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked) {
                    adapter = ArrayAdapter.createFromResource(UpdateDetails.this, R.array.spinnerKategoriePrijem, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    kategorie.setAdapter(adapter);
                }else{
                    adapter = ArrayAdapter.createFromResource(UpdateDetails.this, R.array.spinnerKategorieVydej, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    kategorie.setAdapter(adapter);
                }
            }
        });

        intent = getIntent();
        if (intent != null) {
            vstupDatum.setText(intent.getStringExtra("datum"));
            vstupCastka.setText(String.valueOf(intent.getDoubleExtra("castka", 0)));
            prepinac.setChecked(true);
            prepinac.setChecked(false);
            prepinac.setChecked("Výdaj".equals(intent.getStringExtra("typ")));
            kategorie.setSelection(adapter.getPosition(intent.getStringExtra("kategorie")));
        }
    }

    public void changeScreen(View view){
        Intent intentOverview = new Intent(UpdateDetails.this, Overview.class);
        startActivity(intentOverview);
    }
    public void smazatZaznam(View view){
        intent = getIntent();
        long defaultniHodnota = 0;
        long idZIntent = intent.getLongExtra("id",defaultniHodnota);
        zaznamDBoperation.deleteZaznam(idZIntent);
        Intent intentOverview = new Intent(UpdateDetails.this, Overview.class);
        startActivity(intentOverview);
    }

    @Override
    protected void onResume() {
        zaznamDBoperation.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        zaznamDBoperation.close();
        super.onPause();
    }
}