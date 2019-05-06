package com.example.ihm;

public class MenuPlanified extends Menu{
    private Integer year;
    private Integer month;
    private Integer day;

    public MenuPlanified(String nomMenu, String entree, String plat, String dessert, double prix, int calories,int year,int month,int day) {
        super(nomMenu, entree, plat, dessert, prix, calories);
        this.year = year;
        this.month = month;
        this.day = day;
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

    public int dateCompare() {
        return (year*2000)+(month*100)+day;
    }
}
