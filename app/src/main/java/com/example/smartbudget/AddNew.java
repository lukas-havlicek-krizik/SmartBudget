package com.example.smartbudget;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.documentfile.provider.DocumentFile;
import java.time.LocalDate;

public class AddNew extends AppCompatActivity {
    Spinner spinner;
    Switch prepinac;
    ArrayAdapter<CharSequence> adapter;
    private ZaznamOperations zaznamDBoperation;
    EditText vstupDatumDen, vstupDatumMesic, vstupDatumRok, vstupCastka;
    SharedPreferences spL;
    String zbyvajiciLimitPref, nastavenyLimitPref, aktualMesic;
    double zbyvajiciLimitCislo;
    private ImageView obrazek;
    String imageUriString;

    private static final int CAMERA_REQUEST_CODE = 100,
                             STORAGE_REQUEST_CODE = 101,
                             IMAGE_PICK_CAMERA_CODE = 102,
                             IMAGE_PICK_GALLERY_CODE = 103;

    private String[] cameraPermissions;
    private String[] storagePermissions;
    Uri imageUri;
    ImageButton btnDeleteImg;

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
        obrazek = findViewById(R.id.imageView);
        btnDeleteImg = findViewById(R.id.btnDeleteImg);

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

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

        obrazek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePickDialog();
            }
        });

        btnDeleteImg.setVisibility(View.INVISIBLE);


    }
    public void changeScreen(View view){
        Intent intentMain = new Intent(AddNew.this, MainActivity.class);
        startActivity(intentMain);
    }
    public void smazatFoto(View view){
        imageUriString = "";
        imageUri = null;
        obrazek.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_camera));
        btnDeleteImg.setVisibility(View.INVISIBLE);
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
            imageUriString = (imageUri != null) ? imageUri.toString() : "";

            if((Integer.parseInt(vstupDatumRok.getText().toString())<LocalDate.now().getYear())) {
                zaznamDBoperation.addZaznam(typ, datumDen, datumMesic, datumRok, castka, kategorie, imageUriString);

                vstupDatumDen.setText(String.valueOf(LocalDate.now().getDayOfMonth()));
                vstupDatumMesic.setText(String.valueOf(LocalDate.now().getMonthValue()));
                vstupDatumRok.setText(String.valueOf(LocalDate.now().getYear()));
                vstupCastka.setText("");
                prepinac.setChecked(false);
                imageUriString = "";
                imageUri = null;
                obrazek.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_camera));
                btnDeleteImg.setVisibility(View.INVISIBLE);

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

                zaznamDBoperation.addZaznam(typ, datumDen, datumMesic, datumRok, castka, kategorie, imageUriString);

                vstupDatumDen.setText(String.valueOf(LocalDate.now().getDayOfMonth()));
                vstupDatumMesic.setText(String.valueOf(LocalDate.now().getMonthValue()));
                vstupDatumRok.setText(String.valueOf(LocalDate.now().getYear()));
                vstupCastka.setText("");
                prepinac.setChecked(false);
                imageUriString = "";
                imageUri = null;
                obrazek.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_camera));
                btnDeleteImg.setVisibility(View.INVISIBLE);

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
    private boolean checkStoragePermissions(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermissions(){
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }
    private boolean checkCameraPermissions(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private void requestCameraPermissions(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }
    private void imagePickDialog() {
        String[] moznosti = {"Kamera", "Galerie"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Vyberte obrázek z");
        builder.setItems(moznosti, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    if(!checkCameraPermissions()){
                        requestCameraPermissions();;
                    }else{
                        //již povoleno
                        vybratZKamera();
                    }
                } else if(which==1){
                    if(!checkStoragePermissions()){
                        requestStoragePermissions();
                    }else{
                        //již povoleno
                        vybratZGalerie();
                    }
                }
            }
        });
        builder.create().show();
    }
    private void vybratZKamera(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Image title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image description");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intentKamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentKamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intentKamera, IMAGE_PICK_CAMERA_CODE);
    }
    private void vybratZGalerie() {
        Intent intentGalerie = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intentGalerie.setType("image/*");
        intentGalerie.addCategory(Intent.CATEGORY_OPENABLE); // Otevře pouze otevřitelné soubory
        startActivityForResult(intentGalerie, IMAGE_PICK_GALLERY_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //výsledek povolení/zakázání kamery/uloziste
        switch(requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    //pokud povoleno, vrátí "true", pokud ne, vrátí "false"
                    boolean kameraPovolena = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean ulozistePovoleno = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if(kameraPovolena && ulozistePovoleno){
                        //oboje povoleno
                        if (!checkCameraPermissions()) {
                            requestCameraPermissions();
                        } else {
                            vybratZKamera();
                        }
                    }else{
                        Toast.makeText(this, "Musíte povolit přístup ke kameře a úložišti.", Toast.LENGTH_LONG).show();
                    }
                }
            }break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean ulozistePovoleno = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(ulozistePovoleno){
                        //uloziste povoleno
                        vybratZGalerie();
                    }else{
                        Toast.makeText(this, "Musíte povolit přístup k úložišti.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                // Získání obrázku z kamery
                obrazek.setImageURI(imageUri);
                btnDeleteImg.setVisibility(View.VISIBLE);
            } else if (requestCode == IMAGE_PICK_GALLERY_CODE && data != null) {
                // Získání obrázku z galerie
                imageUri = data.getData();
                if (imageUri != null) {
                    DocumentFile documentFile = DocumentFile.fromSingleUri(this, imageUri);
                    if (documentFile != null && documentFile.exists()) {
                        getContentResolver().takePersistableUriPermission(
                                imageUri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );
                        obrazek.setImageURI(imageUri);
                        btnDeleteImg.setVisibility(View.VISIBLE);
                    }
                }
            }
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