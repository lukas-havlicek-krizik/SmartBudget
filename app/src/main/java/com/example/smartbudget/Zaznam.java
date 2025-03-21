package com.example.smartbudget;

import java.time.LocalDate;

public class Zaznam {
    private int id;
    private String typ;
    private LocalDate datum;
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
    public LocalDate getDatum(){
        return datum;
    }
    public void setDatum(LocalDate datum){
        this.datum = datum;
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
    public void setId(String kategorie){
        this.kategorie = kategorie;
    }

    @Override
    public String toString(){
        return datum + " " + " |" + typ + "| " + " "+ castka + " Kc ... " + kategorie;
    }
}
