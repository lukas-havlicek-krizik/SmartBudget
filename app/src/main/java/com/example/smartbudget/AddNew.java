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
    ImageButton btnDeleteImg, btnShowImg;

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
        btnShowImg = findViewById(R.id.btnShowImg);

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        zaznamDBoperation = new ZaznamOperations(this);
        zaznamDBoperation.open();

        vstupDatumDen.setText(String.valueOf(LocalDate.now().getDayOfMonth()));
        vstupDatumMesic.setText(String.valueOf(LocalDate.now().getMonthValue()));
        vstupDatumRok.setText(String.valueOf(LocalDate.now().getYear()));

        adapter = ArrayAdapter.createFromResource(AddNew.this, R.array.spinnerKategoriePrijem, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner.setAdapter(adapter);

        prepinac.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked) {
                    adapter = ArrayAdapter.createFromResource(AddNew.this, R.array.spinnerKategoriePrijem, R.layout.spinner_item);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown);
                    spinner.setAdapter(adapter);
                }else{
                    adapter = ArrayAdapter.createFromResource(AddNew.this, R.array.spinnerKategorieVydej, R.layout.spinner_item);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown);
                    spinner.setAdapter(adapter);
                }
            }
        });

        spL = getSharedPreferences("limits",MODE_PRIVATE);
        nastavenyLimitPref = spL.getString("nastavenyLimit",String.valueOf(0));
        aktualMesic = spL.getString("aktualMesic",String.valueOf(LocalDate.now().getMonthValue()));

        if(!aktualMesic.equals(String.valueOf(LocalDate.now().getMonthValue()))){
            SharedPreferences.Editor spE= spL.edit();
            spE.putString("zbyvajiciLimit",nastavenyLimitPref);
            spE.putString("aktualMesic",String.valueOf(LocalDate.now().getMonthValue()));
            spE.commit();
        }

        if(Integer.parseInt(nastavenyLimitPref)!=0) {
            zbyvajiciLimitPref = spL.getString("zbyvajiciLimit", nastavenyLimitPref);
            zbyvajiciLimitCislo = Double.parseDouble(zbyvajiciLimitPref);
        }

        obrazek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePickDialog();
            }
        });

        btnDeleteImg.setVisibility(View.INVISIBLE);
        btnShowImg.setVisibility(View.INVISIBLE);


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
        btnShowImg.setVisibility(View.INVISIBLE);
    }

    public void ukazFoto(View view){
        Intent intentPhoto = new Intent(AddNew.this, ShowPhoto.class);
        intentPhoto.putExtra("obrazek",imageUri.toString());
        startActivity(intentPhoto);
    }
    public void addZaznam(View view) {
        if(!(vstupDatumDen.getText().toString().isEmpty()
                ||vstupDatumMesic.getText().toString().isEmpty()
                ||vstupDatumRok.getText().toString().isEmpty())
            &&(Integer.parseInt(vstupDatumRok.getText().toString())<=LocalDate.now().getYear())
            &&(Integer.parseInt(vstupDatumRok.getText().toString())>=2020)
            &&!vstupCastka.getText().toString().isEmpty()
            &&Integer.parseInt(nastavenyLimitPref)!=0) {
            String typ;
            int datumDen;
            int datumMesic;
            int datumRok;
            double castka;
            String kategorie;

            if (prepinac.isChecked()) {
                typ = getString(R.string.switch_databaseVydaj);
            } else {
                typ = getString(R.string.switch_databasePrijem);
            }

            datumDen = Integer.parseInt(vstupDatumDen.getText().toString());
            datumMesic = Integer.parseInt(vstupDatumMesic.getText().toString());
            datumRok = Integer.parseInt(vstupDatumRok.getText().toString());
            castka = Double.parseDouble(vstupCastka.getText().toString());
            kategorie = spinner.getSelectedItem().toString();
            imageUriString = (imageUri != null) ? imageUri.toString() : "";

            //podmínka - je napsaný rok menší než aktuální? - ano -> provede se
            if(datumRok<LocalDate.now().getYear()
                &&jePlatneDatum(datumDen,datumMesic,datumRok)) {

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
                btnShowImg.setVisibility(View.INVISIBLE);

                if (typ.equals(getString(R.string.switch_databaseVydaj)) && datumMesic == LocalDate.now().getMonthValue()
                                        && datumRok == LocalDate.now().getYear()) {
                    zbyvajiciLimitCislo -= castka;
                    SharedPreferences.Editor spE = spL.edit();
                    spE.putString("zbyvajiciLimit", String.valueOf(zbyvajiciLimitCislo));
                    spE.commit();
                    Toast.makeText(this, getString(R.string.toast_zaznamPridatLimit) + " " + zbyvajiciLimitCislo, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, getString(R.string.toast_zaznamPridat), Toast.LENGTH_LONG).show();
                }
            //podmínka pro aktuální rok - není měsíc větší než aktuální? není den v měsíci větší než aktuální? - není -> provede se
            }else if((datumRok==LocalDate.now().getYear())
                    &&!(datumDen>LocalDate.now().getDayOfMonth()
                    &&datumMesic==LocalDate.now().getMonthValue())
                    &&!(datumMesic>LocalDate.now().getMonthValue())
                    &&jePlatneDatum(datumDen,datumMesic,datumRok)) {

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
                btnShowImg.setVisibility(View.INVISIBLE);

                if (typ.equals(getString(R.string.switch_databaseVydaj)) && datumMesic == LocalDate.now().getMonthValue()
                                        && datumRok == LocalDate.now().getYear()) {
                    zbyvajiciLimitCislo -= castka;
                    SharedPreferences.Editor spE = spL.edit();
                    spE.putString("zbyvajiciLimit", String.valueOf(zbyvajiciLimitCislo));
                    spE.commit();
                    Toast.makeText(this, getString(R.string.toast_zaznamPridatLimit) + " " + zbyvajiciLimitCislo, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, getString(R.string.toast_zaznamPridat), Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this, getString(R.string.toast_chybneDatum), Toast.LENGTH_LONG).show();
            }

        }else {
            if(Integer.parseInt(nastavenyLimitPref)==0){
                Toast.makeText(this, getString(R.string.toast_nastavSvujLimit), Toast.LENGTH_LONG).show();
            }else if ((Integer.parseInt(vstupDatumRok.getText().toString())<=LocalDate.now().getYear())
                       ||(Integer.parseInt(vstupDatumRok.getText().toString())>=2020)){
                Toast.makeText(this, getString(R.string.toast_chybnyRok), Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(this, getString(R.string.toast_vyplnitVsechnaPole), Toast.LENGTH_LONG).show();
            }
        }
    }
    public boolean jePlatneDatum(int den, int mesic, int rok) {

        if (mesic < 1 || mesic > 12) return false;

        int maxDnu;

        switch (mesic) {
            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                maxDnu = 31; break;
            case 4: case 6: case 9: case 11:
                maxDnu = 30; break;
            case 2:
                if(rok % 4 == 0) {
                    maxDnu = 29;
                }else{
                    maxDnu = 28;
                }
                break;
            default:
                return false;
        }

        return den >= 1 && den <= maxDnu;
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
        String[] moznosti = {getString(R.string.vyber_kamera), getString(R.string.vyber_galerie)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.vyber_nadpis));
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
                        Toast.makeText(this, getString(R.string.toast_povoleniKameraUloziste), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(this, getString(R.string.toast_povoleniUlozisteOnly), Toast.LENGTH_LONG).show();
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
                btnShowImg.setVisibility(View.VISIBLE);
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
                        btnShowImg.setVisibility(View.VISIBLE);
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