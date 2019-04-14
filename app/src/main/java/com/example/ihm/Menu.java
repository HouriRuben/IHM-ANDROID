package com.example.ihm;

public class Menu {
    private String nomMenu;
    private String entree;
    private String plat;
    private String dessert;
    private Double prix;
    private Integer calories;

    public Menu(String nomMenu, String entree, String plat, String dessert, double prix, int calories){
        this.nomMenu = nomMenu;
        this.entree = entree;
        this.plat = plat;
        this.dessert = dessert;
        this.prix = prix;
        this.calories = calories;

    }
}
