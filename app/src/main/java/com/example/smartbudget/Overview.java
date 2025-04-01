package com.example.smartbudget;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalDate;
import java.util.List;

public class Overview extends AppCompatActivity {
    private ZaznamOperations zaznamDBoperations;
    ListView listView;
    CheckBox checkBox;
    int aktualMesic, aktualRok;
    boolean checked;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_overview);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        listView = findViewById(R.id.listView);
        checkBox = findViewById(R.id.checkBox);

        sp = getSharedPreferences("overview",MODE_PRIVATE);
        checked = sp.getBoolean("pouzeAktualMesic",true);

        checkBox.setChecked(checked);

        aktualMesic = LocalDate.now().getMonthValue();
        aktualRok = LocalDate.now().getYear();

        zaznamDBoperations = new ZaznamOperations(this);
        zaznamDBoperations.open();


        List<Zaznam> values = zaznamDBoperations.getAllZaznamy(checkBox.isChecked(), aktualMesic, aktualRok);
        ArrayAdapter<Zaznam> adapter = new ArrayAdapter<>(this, R.layout.listview_list_item, values);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Zaznam selectedZaznam = (Zaznam) parent.getItemAtPosition(position);
                Intent intentDetails = new Intent(Overview.this, UpdateDetails.class);

                intentDetails.putExtra("id", selectedZaznam.getId());
                intentDetails.putExtra("typ", selectedZaznam.getTyp());
                intentDetails.putExtra("datumDen", selectedZaznam.getDatumDen());
                intentDetails.putExtra("datumMesic", selectedZaznam.getDatumMesic());
                intentDetails.putExtra("datumRok", selectedZaznam.getDatumRok());
                intentDetails.putExtra("castka", selectedZaznam.getCastka());
                intentDetails.putExtra("kategorie", selectedZaznam.getKategorie());
                intentDetails.putExtra("obrazek",selectedZaznam.getObrazek());

                startActivity(intentDetails);
            }
        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor spE= sp.edit();
                spE.putBoolean("pouzeAktualMesic",!checked);
                spE.commit();

                finish();
                startActivity(getIntent());
            }
        });
    }

    public void changeScreen(View view){
        Intent intentMain = new Intent(Overview.this, MainActivity.class);
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
}