package com.example.ihm;

import java.util.Comparator;

public class dateComparator implements Comparator<MenuPlanified> {
    public int compare(MenuPlanified a1,MenuPlanified a2){
        if(a1.dateCompare()==a2.dateCompare())
            return 0;
        else if(a1.dateCompare()>a2.dateCompare())
            return 1;
        else
            return -1;
    }
}
