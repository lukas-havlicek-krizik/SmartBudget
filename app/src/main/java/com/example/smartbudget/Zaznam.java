package com.example.smartbudget;


public class Zaznam {
    private int id, datumDen, datumMesic, datumRok;
    private String typ, kategorie, obrazek;
    private double castka;

    public long getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getTyp(){
        return typ;
    }
    public void setTyp(String typ){
        this.typ = typ;
    }
    public int getDatumDen(){
        return datumDen;
    }
    public void setDatumDen(int datumDen){
        this.datumDen = datumDen;
    }
    public int getDatumMesic(){
        return datumMesic;
    }
    public void setDatumMesic(int datumMesic){
        this.datumMesic = datumMesic;
    }
    public int getDatumRok(){
        return datumRok;
    }
    public void setDatumRok(int datumRok){
        this.datumRok = datumRok;
    }
    public double getCastka(){
        return castka;
    }
    public void setCastka(double castka){
        this.castka = castka;
    }
    public String getKategorie(){
        return kategorie;
    }
    public void setKategorie(String kategorie){
        this.kategorie = kategorie;
    }
    public String getObrazek(){
        return obrazek;
    }
    public void setObrazek(String obrazek){
        this.obrazek = obrazek;
    }

    @Override
    public String toString(){
        return datumDen + "/" + datumMesic +  "/" + datumRok + " " + " | " + typ + " - " + kategorie + " | " + "\n"+ castka + " Kč";
    }
}
