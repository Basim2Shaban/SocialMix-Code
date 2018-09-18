package com.basm.socialmix.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Basim on 14/02/2018.
 */

public class ChatListners {

    private static final DatabaseReference getChat = FirebaseDatabase.getInstance().getReference().child("chat");
    private static final FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    private static final DatabaseReference  mCurrentUserChatsRef = getChat.child(mCurrentUser.getUid());


/*

     mCurrentUserChatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });
        mCurrentUserChatsRef.addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });
*/
}


