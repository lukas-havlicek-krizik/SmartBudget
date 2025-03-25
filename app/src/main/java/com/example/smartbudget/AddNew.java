package com.example.smartbudget;

import android.content.Intent;
import android.content.SharedPreferences;
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

import java.time.LocalDate;

public class AddNew extends AppCompatActivity {
    Spinner spinner;
    Switch prepinac;
    ArrayAdapter<CharSequence> adapter;
    private ZaznamOperations zaznamDBoperation;
    EditText vstupDatumDen;
    EditText vstupDatumMesic;
    EditText vstupDatumRok;
    EditText vstupCastka;
    SharedPreferences spL;
    String zbyvajiciLimitPref;
    String nastavenyLimitPref;
    double zbyvajiciLimitCislo;
    String aktualMesic;
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
        vstupDatumDen = findViewById(R.id.vstupDatumDen);
        vstupDatumMesic = findViewById(R.id.vstupDatumMesic);
        vstupDatumRok = findViewById(R.id.vstupDatumRok);
        vstupCastka = findViewById(R.id.vstupCastka);
        zaznamDBoperation = new ZaznamOperations(this);
        zaznamDBoperation.open();

        vstupDatumDen.setText(String.valueOf(LocalDate.now().getDayOfMonth()));
        vstupDatumMesic.setText(String.valueOf(LocalDate.now().getMonthValue()));
        vstupDatumRok.setText(String.valueOf(LocalDate.now().getYear()));

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
        spL = getSharedPreferences("limits",MODE_PRIVATE);
        nastavenyLimitPref = spL.getString("nastavenyLimit",String.valueOf(1000));
        aktualMesic = spL.getString("aktualMesic",String.valueOf(LocalDate.now().getMonthValue()));

        if(!aktualMesic.equals(String.valueOf(LocalDate.now().getMonthValue()))){
            SharedPreferences.Editor spE= spL.edit();
            spE.putString("zbyvajiciLimit",nastavenyLimitPref);
            spE.putString("aktualMesic",String.valueOf(LocalDate.now().getMonthValue()));
            spE.commit();
        }

        zbyvajiciLimitPref = spL.getString("zbyvajiciLimit",nastavenyLimitPref);
        zbyvajiciLimitCislo = Double.parseDouble(zbyvajiciLimitPref);
    }
    public void changeScreen(View view){
        Intent intentMain = new Intent(AddNew.this, MainActivity.class);
        startActivity(intentMain);
    }

    public void addZaznam(View view) {
        if(!(vstupDatumDen.getText().toString().isEmpty()
                ||vstupDatumMesic.getText().toString().isEmpty()
                ||vstupDatumRok.getText().toString().isEmpty())
            &&(Integer.parseInt(vstupDatumRok.getText().toString())<=LocalDate.now().getYear())
            &&(Integer.parseInt(vstupDatumRok.getText().toString())>=2020)) {
            String typ;
            int datumDen;
            int datumMesic;
            int datumRok;
            double castka;
            String kategorie;

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

            kategorie = spinner.getSelectedItem().toString();


            if((Integer.parseInt(vstupDatumRok.getText().toString())<LocalDate.now().getYear())) {
                zaznamDBoperation.addZaznam(typ, datumDen, datumMesic, datumRok, castka, kategorie);

                vstupDatumDen.setText(String.valueOf(LocalDate.now().getDayOfMonth()));
                vstupDatumMesic.setText(String.valueOf(LocalDate.now().getMonthValue()));
                vstupDatumRok.setText(String.valueOf(LocalDate.now().getYear()));
                vstupCastka.setText("");
                prepinac.setChecked(false);

                if (typ.equals("Výdaj") && datumMesic == LocalDate.now().getMonthValue()
                                        && datumRok == LocalDate.now().getYear()) {
                    zbyvajiciLimitCislo -= castka;
                    SharedPreferences.Editor spE = spL.edit();
                    spE.putString("zbyvajiciLimit", String.valueOf(zbyvajiciLimitCislo));
                    spE.commit();
                    Toast.makeText(this, "Záznam přidán.\nZbývající limit: " + zbyvajiciLimitCislo, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Záznam přidán.", Toast.LENGTH_LONG).show();
                }

            }else if((Integer.parseInt(vstupDatumRok.getText().toString())==LocalDate.now().getYear())
                    &&!(Integer.parseInt(vstupDatumDen.getText().toString())>LocalDate.now().getDayOfMonth()
                    &&Integer.parseInt(vstupDatumMesic.getText().toString())==LocalDate.now().getMonthValue())
                    &&!(Integer.parseInt(vstupDatumMesic.getText().toString())>LocalDate.now().getMonthValue())) {

                zaznamDBoperation.addZaznam(typ, datumDen, datumMesic, datumRok, castka, kategorie);

                vstupDatumDen.setText(String.valueOf(LocalDate.now().getDayOfMonth()));
                vstupDatumMesic.setText(String.valueOf(LocalDate.now().getMonthValue()));
                vstupDatumRok.setText(String.valueOf(LocalDate.now().getYear()));
                vstupCastka.setText("");
                prepinac.setChecked(false);

                if (typ.equals("Výdaj") && datumMesic == LocalDate.now().getMonthValue()
                                        && datumRok == LocalDate.now().getYear()) {
                    zbyvajiciLimitCislo -= castka;
                    SharedPreferences.Editor spE = spL.edit();
                    spE.putString("zbyvajiciLimit", String.valueOf(zbyvajiciLimitCislo));
                    spE.commit();
                    Toast.makeText(this, "Záznam přidán.\nZbývající limit: " + zbyvajiciLimitCislo, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Záznam přidán.", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this, "Error.", Toast.LENGTH_LONG).show();
            }

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
    protected void onDestroy() {
        zaznamDBoperation.close();
        super.onDestroy();
    }
}