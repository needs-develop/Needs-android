package com.tester.Needs.Common;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tester.Needs.Main.FavoritesFragment;
import com.tester.Needs.R;


public class FavoritesFunc extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.free_list, new FavoritesFragment());
        fragmentTransaction.commit();
          /*한가지 값 불러오기(firestore에서)
        DocumentReference docRef = db.collection("data").document("hello_one");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("태그", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("태그", "No such document");
                    }
                } else {
                    Log.d("태그", "get failed with ", task.getException());
                }
            }
        });*/

    }
}
