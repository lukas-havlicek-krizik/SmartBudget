package com.example.smartbudget;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddNew extends AppCompatActivity {
    Spinner spinner;
    Switch prepinac;
    ArrayAdapter<CharSequence> adapter;
    private ZaznamOperations zaznamDBoperation;
    EditText vstupDatum;
    EditText vstupCastka;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinner = findViewById(R.id.kategorie);
        prepinac = findViewById(R.id.prepinac);
        vstupDatum = findViewById(R.id.vstupDatum);
        vstupCastka = findViewById(R.id.vstupCastka);
        zaznamDBoperation = new ZaznamOperations(this);
        zaznamDBoperation.open();

        adapter = ArrayAdapter.createFromResource(AddNew.this, R.array.spinnerKategoriePrijem, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        prepinac.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked) {
                    adapter = ArrayAdapter.createFromResource(AddNew.this, R.array.spinnerKategoriePrijem, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                }else{
                    adapter = ArrayAdapter.createFromResource(AddNew.this, R.array.spinnerKategorieVydej, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                }
            }
        });
    }
    public void changeScreen(View view){
        Intent intentMain = new Intent(AddNew.this, MainActivity.class);
        startActivity(intentMain);
    }

    public void addZaznam(View view) {
        String typ;
        String datum;
        double castka;
        String kategorie;

        if(prepinac.isChecked()){
            typ = "Výdaj";
        }else{
            typ = "Příjem";
        }
        datum = vstupDatum.getText().toString();

        String castkaString = vstupCastka.getText().toString();
        if (castkaString.isEmpty()) {
            castka = 0;
        } else {
            castka = Double.parseDouble(castkaString);
        }

        kategorie = spinner.getSelectedItem().toString();

        zaznamDBoperation.addZaznam(typ,datum,castka,kategorie);
        Toast.makeText(this, "Záznam přidán.",Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onResume() {
        zaznamDBoperation.open();
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        zaznamDBoperation.close();
        super.onDestroy();
    }
}