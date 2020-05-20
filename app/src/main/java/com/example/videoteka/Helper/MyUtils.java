package com.example.videoteka.Helper;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class MyUtils {
    public MyUtils() {
    }
    public static String[] FragmentTipString = {"MOJI FILMOVI","SPREMLJENI FILMOVI"};

    public static void zamjeniFragment(FragmentManager fm, int lokacijaID, Fragment fragment, Boolean addToBackStack) {
        FragmentManager fragmentManager = fm;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentManager.getBackStackEntryCount();
        fragmentTransaction.replace(lokacijaID,fragment);
        if (addToBackStack)
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
