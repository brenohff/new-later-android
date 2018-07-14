package later.brenohff.com.later.Others;

import android.content.Context;
import android.content.SharedPreferences;

import later.brenohff.com.later.Models.LTUser;

public class SaveUserOnDevice {

    public static final String PREFS_NAME = "LoadedBefore";

    private LTUser user;

    public SaveUserOnDevice(LTUser user) {
        this.user = user;
    }

    public LTUser getUser() {
        return user;
    }

    public void setUser(LTUser user) {
        this.user = user;
    }

    public void saveUser(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("name", user.getName());
        editor.putString("email", user.getEmail());
        editor.putString("birthday", user.getBirthday());
        editor.putString("gender", user.getGender());
        editor.putString("id", user.getId());
        editor.putString("image", user.getImage());
        editor.putString("image_long", user.getImage_long());
        editor.putString("link", user.getLink());

        editor.apply();
    }

    public static SaveUserOnDevice loadSavedUser(Context context) {

        SharedPreferences userPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        LTUser user = new LTUser();
        user.setName(userPrefs.getString("name", null));
        user.setEmail(userPrefs.getString("email", null));
        user.setBirthday(userPrefs.getString("birthday", null));
        user.setGender(userPrefs.getString("gender", null));
        user.setId(userPrefs.getString("id", null));
        user.setImage(userPrefs.getString("image", null));
        user.setImage_long(userPrefs.getString("image_long", null));
        user.setLink(userPrefs.getString("link", null));

        if (user.getId() == null || user.getName() == null && user.getEmail() == null) {
            return null;
        } else {
            return new SaveUserOnDevice(user);
        }
    }

    public static void removeSavedUser(Context context) {
        SharedPreferences userPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userPrefs.edit();
        editor.remove("name");
        editor.remove("email");
        editor.remove("birthday");
        editor.remove("gender");
        editor.remove("id");
        editor.remove("image");
        editor.remove("image_long");
        editor.remove("link");
        editor.apply();
    }
}
