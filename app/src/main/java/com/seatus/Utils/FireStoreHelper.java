package com.seatus.Utils;

import android.support.annotation.WorkerThread;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by rah on 15-Nov-17.
 */

public class FireStoreHelper {

    public static void deleteQueryBatch(final Query query) throws Exception {
        new Thread(() -> {
            try {


            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
//        return querySnapshot.getDocuments();
    }
}
