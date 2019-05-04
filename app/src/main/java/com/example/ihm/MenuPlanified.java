package com.example.ihm;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MenuPlanified extends Menu {
    private Menu menu;
    private LocalDate date;

    public MenuPlanified(Menu menu , LocalDate date) {
        super(menu);
        this.date = date;

    }

    public Menu getMenu() {
        return menu;
    }

    public LocalDate getDate() {
        return date;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String dateToString(){
        LocalDate localDate = LocalDate.of(this.date.getYear(), this.date.getMonth().getValue(), this.date.getDayOfMonth());
        return localDate.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM, yyyy", Locale.FRENCH));
    }
}
