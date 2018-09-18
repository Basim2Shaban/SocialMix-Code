package com.basm.socialmix.firebase;

import com.basm.socialmix.Chat;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Basim on 07/03/2018.
 */

public interface MyMethod {
    String finalMassage = null;


     void Query_about_FinalMassage(DatabaseReference reference , Chat.MyHolder holder);
}
