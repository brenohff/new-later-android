package later.brenohff.com.later.Others;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

/**
 * Created by breno on 17/04/2017.
 */

public final class ConfigFirebase {

    private static DatabaseReference database;
    private static FirebaseAuth auth;
    private static FirebaseStorage storage;
    private static FirebaseDatabase firebaseDatabase;

    public static FirebaseDatabase getFirebaseDatabase() {
        if (firebaseDatabase == null) {
            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.setPersistenceEnabled(true);
        }
        return firebaseDatabase;
    }

    public static DatabaseReference getDatabase() {
        if (database == null)
            database = FirebaseDatabase.getInstance().getReference();

        return database;
    }

    public static FirebaseAuth getAuth() {
        if (auth == null)
            auth = FirebaseAuth.getInstance();

        return auth;
    }

    public static FirebaseStorage getStorage() {
        if (storage == null)
            storage = FirebaseStorage.getInstance();

        return storage;
    }

}