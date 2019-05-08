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

    public Menu(Menu menu) {
        this.nomMenu = menu.getNomMenu();
        this.entree = menu.getEntree();
        this.plat = menu.getPlat();
        this.dessert = menu.getDessert();
        this.prix = menu.getPrix();
        this.calories = menu.getCalories();
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
}
