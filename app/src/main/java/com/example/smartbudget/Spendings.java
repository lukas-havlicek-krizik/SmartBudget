package com.example.smartbudget;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalDate;
import java.util.List;

public class Spendings extends AppCompatActivity {
    private ZaznamOperations zaznamDBoperations;
    ListView listView;
    TextView textView;
    int rok;
    SharedPreferences sp;
    String rokPref;
    Button dalsiRok, minulyRok;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_spendings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        dalsiRok = findViewById(R.id.dalsiRok);
        minulyRok = findViewById(R.id.predchoziRok);
        textView = findViewById(R.id.textView5);
        listView = findViewById(R.id.listView);
        zaznamDBoperations = new ZaznamOperations(this);
        zaznamDBoperations.open();
        rok = LocalDate.now().getYear();

        sp = getSharedPreferences("package",MODE_PRIVATE);
        rokPref = sp.getString("aktualRok",String.valueOf(rok));

        List<Zaznam> values = zaznamDBoperations.getVydajeZaRok(rokPref);

        ArrayAdapter<Zaznam> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, values);
        listView.setAdapter(adapter);
        textView.setText("Útrata za rok " + rokPref);

        if(Integer.parseInt(rokPref)==rok){
            dalsiRok.setVisibility(View.INVISIBLE);
        }else{
            dalsiRok.setVisibility(View.VISIBLE);
        }

        if(Integer.parseInt(rokPref)==2020){
            minulyRok.setVisibility(View.INVISIBLE);
        }else{
            minulyRok.setVisibility(View.VISIBLE);
        }
    }
    public void changeScreen(View view){
        SharedPreferences.Editor spE= sp.edit();
        spE.putString("aktualRok",String.valueOf(rok));
        spE.commit();
        Intent intentMain = new Intent(Spendings.this, MainActivity.class);
        startActivity(intentMain);
    }
    @Override
    protected void onResume() {
        zaznamDBoperations.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        zaznamDBoperations.close();
        super.onPause();
    }

    public void zmenaRoku(View view){
        if(view.getId()==R.id.predchoziRok){
            int pozadovanyRok = (Integer.parseInt(rokPref)-1);
            String pozadovanyRokString = String.valueOf(pozadovanyRok);
            SharedPreferences.Editor spE= sp.edit();
            spE.putString("aktualRok",pozadovanyRokString);
            spE.commit();
            this.recreate();
        }else if(view.getId()==R.id.dalsiRok&&(Integer.parseInt(rokPref)+1<=rok)){
            int pozadovanyRok = (Integer.parseInt(rokPref)+1);
            String pozadovanyRokString = String.valueOf(pozadovanyRok);
            SharedPreferences.Editor spE= sp.edit();
            spE.putString("aktualRok",pozadovanyRokString);
            spE.commit();
            this.recreate();
        }
    }
}