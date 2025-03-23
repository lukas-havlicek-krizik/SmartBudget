package com.example.smartbudget;


public class Zaznam {
    private int id;
    private String typ;
    private int datumDen;
    private int datumMesic;
    private int datumRok;
    private double castka;
    private String kategorie;

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

    @Override
    public String toString(){
        return datumDen + "/" + datumMesic +  "/" + datumRok + " " + " | " + typ + " - " + kategorie + " | " + "\n"+ castka + " Kč";
    }
}
