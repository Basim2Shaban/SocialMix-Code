package com.basm.socialmix;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by basm on 11/12/2017.
 */

public class ClassPigerAdepter extends FragmentPagerAdapter {
    public ClassPigerAdepter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 :
            {
                Home home = new Home();
                return home ;
            }
            case 1 :
            {
                Chat chat = new Chat();
                return chat ;
            }
            case 2 :
            {
                AllUsers users = new AllUsers();
                return users ;
            }
            default:
                return null ;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position){
        switch (position) {
            case 0:
                {
                return "HOME";
            }
            case 1:
            {
                return "Chat";
            }
            case 2:
            {
                return "Users";
            }
            default:
                return null ;
        }

    }
}
