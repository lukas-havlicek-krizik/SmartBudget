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

public class UpdateDetails extends AppCompatActivity {
    EditText vstupDatumDen, vstupDatumMesic, vstupDatumRok, vstupCastka;
    Spinner kategorie;
    Switch prepinac;
    ArrayAdapter<CharSequence> adapter;
    private ZaznamOperations zaznamDBoperation;
    Intent intent;
    SharedPreferences spL;
    String zbyvajiciLimitPref, nastavenyLimitPref, aktualMesic;
    double zbyvajiciLimitCislo, puvodniZbyLimit;
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
        puvodniZbyLimit = zbyvajiciLimitCislo;
    }

    public void changeScreen(View view){
        Intent intentOverview = new Intent(UpdateDetails.this, Overview.class);
        startActivity(intentOverview);
    }
    public void smazatZaznam(View view){
        intent = getIntent();
        long defaultniHodnota = 0;
        String typ;
        int datumMesic;
        int datumRok;

        if (prepinac.isChecked()) {
            typ = "Výdaj";
        } else {
            typ = "Příjem";
        }


        datumMesic = Integer.parseInt(vstupDatumMesic.getText().toString());
        datumRok = Integer.parseInt(vstupDatumRok.getText().toString());

        long idZIntent = intent.getLongExtra("id",defaultniHodnota);
        zaznamDBoperation.deleteZaznam(idZIntent);

        if(typ.equals("Výdaj")
                &&intent.getIntExtra("datumMesic",0)==LocalDate.now().getMonthValue()
                &&intent.getStringExtra("typ").equals("Výdaj")
                &&datumMesic==LocalDate.now().getMonthValue()
                &&datumRok == LocalDate.now().getYear()){

            zbyvajiciLimitCislo += intent.getDoubleExtra("castka", 0);

            if(zbyvajiciLimitCislo>Double.parseDouble(nastavenyLimitPref)){
                zbyvajiciLimitCislo=2000;
            }

            SharedPreferences.Editor spE = spL.edit();
            spE.putString("zbyvajiciLimit", String.valueOf(zbyvajiciLimitCislo));
            spE.commit();
            Toast.makeText(this, "Záznam smazán.\nZbývající limit: " + zbyvajiciLimitCislo, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Záznam smazán.", Toast.LENGTH_LONG).show();
        }

        Intent intentOverview = new Intent(UpdateDetails.this, Overview.class);
        startActivity(intentOverview);
    }

    public void zmenitZaznam(View view){
        if(!(vstupDatumDen.getText().toString().isEmpty()
                ||vstupDatumMesic.getText().toString().isEmpty()
                ||vstupDatumRok.getText().toString().isEmpty())
            &&(Integer.parseInt(vstupDatumRok.getText().toString())<=LocalDate.now().getYear())
            &&(Integer.parseInt(vstupDatumRok.getText().toString())>=2020)) {
            intent = getIntent();
            String typ;
            int datumDen;
            int datumMesic;
            int datumRok;
            double castka;
            String kategorieVstup;
            double rozdil;

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
            rozdil = Math.abs(intent.getDoubleExtra("castka", 0) - castka);

            //datum bylo aktualni - uživatel mění pouze částku - částku zvyšuje
            if(typ.equals("Výdaj")
                    &&datumMesic==LocalDate.now().getMonthValue()
                    &&datumRok == LocalDate.now().getYear()
                    &&intent.getDoubleExtra("castka", 0)<castka
                    &&typ.equals(intent.getStringExtra("typ"))
                    &&intent.getIntExtra("datumMesic",0)==LocalDate.now().getMonthValue()) {

                zbyvajiciLimitCislo -= rozdil;

            //datum bylo aktualni - uživatel mění pouze částku - částku snižuje
            }else if(typ.equals("Výdaj")
                    &&datumMesic==LocalDate.now().getMonthValue()
                    &&datumRok == LocalDate.now().getYear()
                    &&intent.getDoubleExtra("castka", 0)>castka
                    &&typ.equals(intent.getStringExtra("typ"))
                    &&intent.getIntExtra("datumMesic",0)==LocalDate.now().getMonthValue()){

                zbyvajiciLimitCislo += rozdil;

                if(zbyvajiciLimitCislo>Double.parseDouble(nastavenyLimitPref)){
                    zbyvajiciLimitCislo=2000;
                }

            //uživatel mění aktuální příjem nebo starý záznam na aktuální výdaj - odečítá se celá částka od limitu
            }else if(typ.equals("Výdaj")
                    &&datumMesic==LocalDate.now().getMonthValue()
                    &&datumRok == LocalDate.now().getYear()
                    &&((intent.getStringExtra("typ").equals("Příjem")
                               &&intent.getIntExtra("datumMesic",0)==LocalDate.now().getMonthValue()
                               &&intent.getIntExtra("datumRok",0)==LocalDate.now().getYear())
                       ||
                       (intent.getIntExtra("datumMesic",0)!=LocalDate.now().getMonthValue()
                               ||intent.getIntExtra("datumRok",0)!=LocalDate.now().getYear()))){

                zbyvajiciLimitCislo -= castka;

            //uživatel mění výdaj na příjem - odebraná částka se opět přidává k limitu
            }else if(typ.equals("Příjem")
                    &&intent.getIntExtra("datumMesic",0)==LocalDate.now().getMonthValue()
                    &&intent.getIntExtra("datumRok",0)==LocalDate.now().getYear()
                    &&intent.getStringExtra("typ").equals("Výdaj")){

                zbyvajiciLimitCislo += castka;
                if(zbyvajiciLimitCislo>Double.parseDouble(nastavenyLimitPref)){
                    zbyvajiciLimitCislo=2000;
                }

            }else if(typ.equals("Výdaj")
                    &&intent.getIntExtra("datumMesic",0)==LocalDate.now().getMonthValue()
                    &&intent.getIntExtra("datumRok",0)==LocalDate.now().getYear()
                    &&intent.getStringExtra("typ").equals("Výdaj")
                    &&(datumMesic!=LocalDate.now().getMonthValue()||datumRok!=LocalDate.now().getYear())){

                zbyvajiciLimitCislo += castka;
                if(zbyvajiciLimitCislo>Double.parseDouble(nastavenyLimitPref)){
                    zbyvajiciLimitCislo=2000;
                }

            }

            //je-li rok menší než aktuální
            if((Integer.parseInt(vstupDatumRok.getText().toString())<LocalDate.now().getYear())) {
                if (puvodniZbyLimit != zbyvajiciLimitCislo) {
                    SharedPreferences.Editor spE = spL.edit();
                    spE.putString("zbyvajiciLimit", String.valueOf(zbyvajiciLimitCislo));
                    spE.commit();
                    Toast.makeText(this, "Záznam upraven.\nZbývající limit: " + zbyvajiciLimitCislo, Toast.LENGTH_LONG).show();
                }

                zaznamDBoperation.updateZaznam(intent.getLongExtra("id", 0), typ, datumDen, datumMesic, datumRok, castka, kategorieVstup);
                Intent intentOverview = new Intent(UpdateDetails.this, Overview.class);
                startActivity(intentOverview);
            //pokud je rok aktuální
            }else if((Integer.parseInt(vstupDatumRok.getText().toString())==LocalDate.now().getYear())
                        &&!(Integer.parseInt(vstupDatumDen.getText().toString())>LocalDate.now().getDayOfMonth()
                            &&Integer.parseInt(vstupDatumMesic.getText().toString())==LocalDate.now().getMonthValue())
                        &&!(Integer.parseInt(vstupDatumMesic.getText().toString())>LocalDate.now().getMonthValue())) {
                if (puvodniZbyLimit != zbyvajiciLimitCislo) {
                    SharedPreferences.Editor spE = spL.edit();
                    spE.putString("zbyvajiciLimit", String.valueOf(zbyvajiciLimitCislo));
                    spE.commit();
                    Toast.makeText(this, "Záznam upraven.\nZbývající limit: " + zbyvajiciLimitCislo, Toast.LENGTH_LONG).show();
                }

                zaznamDBoperation.updateZaznam(intent.getLongExtra("id", 0), typ, datumDen, datumMesic, datumRok, castka, kategorieVstup);
                Intent intentOverview = new Intent(UpdateDetails.this, Overview.class);
                startActivity(intentOverview);
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
    protected void onPause() {
        zaznamDBoperation.close();
        super.onPause();
    }
}