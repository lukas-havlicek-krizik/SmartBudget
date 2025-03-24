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

public class UpdateDetails extends AppCompatActivity {
    EditText vstupDatumDen;
    EditText vstupDatumMesic;
    EditText vstupDatumRok;
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
        vstupDatumDen = findViewById(R.id.vstupDatumDen);
        vstupDatumMesic = findViewById(R.id.vstupDatumMesic);
        vstupDatumRok = findViewById(R.id.vstupDatumRok);
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
            vstupDatumDen.setText(String.valueOf(intent.getIntExtra("datumDen",0)));
            vstupDatumMesic.setText(String.valueOf(intent.getIntExtra("datumMesic",0)));
            vstupDatumRok.setText(String.valueOf(intent.getIntExtra("datumRok",0)));
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

    public void zmenitZaznam(View view){
        if(!(vstupDatumDen.getText().toString().isEmpty()
                ||vstupDatumMesic.getText().toString().isEmpty()
                ||vstupDatumRok.getText().toString().isEmpty())) {
            intent = getIntent();
            String typ;
            int datumDen;
            int datumMesic;
            int datumRok;
            double castka;
            String kategorieVstup;

            if (prepinac.isChecked()) {
                typ = "Výdaj";
            } else {
                typ = "Příjem";
            }

            datumDen = Integer.parseInt(vstupDatumDen.getText().toString());
            datumMesic = Integer.parseInt(vstupDatumMesic.getText().toString());
            datumRok = Integer.parseInt(vstupDatumRok.getText().toString());

            String castkaString = vstupCastka.getText().toString();
            if (castkaString.isEmpty()) {
                castka = 0;
            } else {
                castka = Double.parseDouble(castkaString);
            }

            kategorieVstup = kategorie.getSelectedItem().toString();

            zaznamDBoperation.updateZaznam(intent.getLongExtra("id", 0), typ, datumDen, datumMesic, datumRok, castka, kategorieVstup);
            Intent intentOverview = new Intent(UpdateDetails.this, Overview.class);
            startActivity(intentOverview);
        }else {
            Toast.makeText(this, "Error.", Toast.LENGTH_LONG).show();
        }
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