package later.brenohff.com.later.Activities;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import later.brenohff.com.later.Connections.LTConnection;
import later.brenohff.com.later.Connections.LTRequests;
import later.brenohff.com.later.Fragments.CategoriesFragment;
import later.brenohff.com.later.Fragments.EventsFragment;
import later.brenohff.com.later.Fragments.LoginFragment;
import later.brenohff.com.later.Fragments.ProfileFragment;
import later.brenohff.com.later.Memory.LTMainData;
import later.brenohff.com.later.Models.LTUser;
import later.brenohff.com.later.Others.SaveUserOnDevice;
import later.brenohff.com.later.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static BottomBar bottomMenu;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Precione novamente para sair...", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            popFragment(1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (AccessToken.getCurrentAccessToken() != null) {
            if (SaveUserOnDevice.loadSavedUser(this) != null) {
                LTMainData.getInstance().setUser(Objects.requireNonNull(SaveUserOnDevice.loadSavedUser(this)).getUser());
                changeFragment(new ProfileFragment(), "ProfileFragment");
            } else {
                showToast("Não foi possível carregar usuário, faça login novamente.");
                LoginManager.getInstance().logOut();
                SaveUserOnDevice.removeSavedUser(this);
                changeFragment(new LoginFragment(), "LoginFragment");
            }
        } else {
            changeFragment(new LoginFragment(), "LoginFragment");
        }

        initFB();
        showFBInfo();

        bottomMenu = (BottomBar) findViewById(R.id.bottom_nav_view);

        bottomMenu.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                switch (tabId){
                    case R.id.nav_conta:
                        if (LTMainData.getInstance().getUser() != null) {
                            changeFragment(new ProfileFragment(), "ProfileFragment");
                        } else {
                            changeFragment(new LoginFragment(), "LoginFragment");
                        }
                        break;
                    case R.id.nav_categorias:
                        changeFragment(new CategoriesFragment(), "CategoriesFragment");
                        break;
                    case R.id.nav_eventos:
                        changeFragment(new EventsFragment(), "EventsFragment");
                        break;
                    case R.id.nav_mapa:
                        showToast("Mapa");
                        break;
                }
            }
        });

        setFragment(3);
        changeStatusBarColor(R.color.background);
    }

    //region FRAGMENT

    public void setFragment(Integer position) {
        switch (position) {
            case 1:
                bottomMenu.selectTabWithId(R.id.nav_conta);
                break;
            case 2:
                bottomMenu.selectTabWithId(R.id.nav_categorias);
                break;
            case 3:
                bottomMenu.selectTabWithId(R.id.nav_eventos);
                break;
            case 4:
                break;

        }
    }

    public void pushFragmentWithStack(Fragment fragment, String tag) {
        this.getSupportFragmentManager().beginTransaction()
        .setCustomAnimations(R.animator.fragment_slide_left_enter,
            R.animator.fragment_slide_left_exit,
            R.animator.fragment_slide_right_enter,
            R.animator.fragment_slide_right_exit)
                .replace(R.id.main_container, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    public void pushFragmentWithNoStack(Fragment fragment, String tag) {
        this.getSupportFragmentManager().beginTransaction()
        .setCustomAnimations(R.animator.fragment_slide_left_enter,
            R.animator.fragment_slide_left_exit,
            R.animator.fragment_slide_right_enter,
            R.animator.fragment_slide_right_exit)
                .replace(R.id.main_container, fragment, tag)
                .commit();
    }

    public void popFragment(Integer qtd) {
        final FragmentManager mFragmentManager = this.getSupportFragmentManager();
        for (int i = 0; i < qtd; i++) {
            mFragmentManager.popBackStack();
        }
    }

    private void changeFragment(Fragment fragment, String tag) {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            pushFragmentWithNoStack(fragment, tag);
        } else {
            for (int i = 0; i <= getSupportFragmentManager().getBackStackEntryCount(); i++) {
                if (i == getSupportFragmentManager().getBackStackEntryCount()) {
                    pushFragmentWithNoStack(fragment, tag);
                } else {
                    popFragment(1);
                }
            }
        }
    }

    //endregion

    private void changeStatusBarColor(Integer color) {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            final Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(color));
        }
    }

    public void showToast(String value) {
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
    }

    private void initFB() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

    private void showFBInfo() {
        try {
            PackageInfo info = this.getPackageManager().getPackageInfo("later.brenohff.com.later", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    //region PERMISSIONS

    //endregion

    //region BOTTOM BAR
    public static void setBottomBarInvisible() {
        bottomMenu.setVisibility(View.GONE);
    }

    public static void setBottomBarVisible() {
        bottomMenu.setVisibility(View.VISIBLE);
    }
    //endregion

    public int getWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels;

        return width;

    }

}
