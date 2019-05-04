package com.example.ihm;

public class MenuPlanified {
    private String nomMenu;
    private String entree;
    private String plat;
    private String dessert;
    private Double prix;
    private Integer calories;
    private Integer year;
    private Integer month;
    private Integer day;

    public MenuPlanified(String nomMenu, String entree, String plat, String dessert, double prix, int calories,int year,int month,int day) {
        this.nomMenu = nomMenu;
        this.entree = entree;
        this.plat = plat;
        this.dessert = dessert;
        this.prix = prix;
        this.calories = calories;
        this.year = year;
        this.month = month;
        this.day = day;


    }

    public String getNomMenu() {
        return nomMenu;
    }

    public String getEntree() {
        return entree;
    }

    public String getPlat() {
        return plat;
    }

    public String getDessert() {
        return dessert;
    }

    public Double getPrix() {
        return prix;
    }

    public Integer getCalories() {
        return calories;
    }

    public Integer getYear() {
        return year;
    }

    public Integer getMonth() {
        return month;
    }

    public Integer getDay() {
        return day;
    }

    public String dateToString(){
        return Integer.toString(day)+"/"+Integer.toString(month)+"/"+Integer.toString(year);
    }
}
