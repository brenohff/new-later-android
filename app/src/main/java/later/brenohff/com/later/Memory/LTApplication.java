package later.brenohff.com.later.Memory;

import android.app.Application;
import android.content.Context;

public class LTApplication extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();

        LTApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return LTApplication.context;
    }
}
