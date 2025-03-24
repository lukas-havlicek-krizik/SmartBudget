package com.example.smartbudget;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
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
        textView = findViewById(R.id.textView5);
        listView = findViewById(R.id.listView);
        zaznamDBoperations = new ZaznamOperations(this);
        zaznamDBoperations.open();
        rok = LocalDate.now().getYear();

        List<Zaznam> values = zaznamDBoperations.getVydajeZaRok(String.valueOf(rok));

        ArrayAdapter<Zaznam> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, values);
        listView.setAdapter(adapter);
        textView.setText("Útrata za rok " + rok);
    }
    public void changeScreen(View view){
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
}