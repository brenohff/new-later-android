package later.brenohff.com.later.Others;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveUserOnDevice {

    private static final String PREFS_NAME = "LoadedBefore";

    private String user_id;

    public SaveUserOnDevice(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void saveUser(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("id", getUser_id());
        editor.apply();

//        editor.putString("id", user.getId());
//        editor.putString("name", user.getName());
//        editor.putString("email", user.getEmail());
//        editor.putString("birthday", user.getBirthday());
//        editor.putString("gender", user.getGender());
//        editor.putString("image", user.getImage());
//        editor.putString("image_long", user.getImage_long());
//        editor.putString("link", user.getLink());
    }

    public static SaveUserOnDevice loadSavedUser(Context context) {

        SharedPreferences userPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String user_id = userPrefs.getString("id", null);

        if (user_id == null) {
            return null;
        } else {
            return new SaveUserOnDevice(user_id);
        }

//        LTUser user = new LTUser();
//        user.setId(userPrefs.getString("id", null));
//        user.setName(userPrefs.getString("name", null));
//        user.setEmail(userPrefs.getString("email", null));
//        user.setBirthday(userPrefs.getString("birthday", null));
//        user.setGender(userPrefs.getString("gender", null));
//        user.setImage(userPrefs.getString("image", null));
//        user.setImage_long(userPrefs.getString("image_long", null));
//        user.setLink(userPrefs.getString("link", null));

    }

    public static void removeSavedUser(Context context) {
        SharedPreferences userPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userPrefs.edit();
        editor.remove("id");
        editor.apply();

//        editor.remove("name");
//        editor.remove("email");
//        editor.remove("birthday");
//        editor.remove("gender");
//        editor.remove("image");
//        editor.remove("image_long");
//        editor.remove("link");
    }
}
