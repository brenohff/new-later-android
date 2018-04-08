package later.brenohff.com.later.Activities;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import later.brenohff.com.later.Fragments.LoginFragment;
import later.brenohff.com.later.Fragments.ProfileFragment;
import later.brenohff.com.later.R;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFB();
        showFBInfo();

        bottomMenu = (BottomNavigationView) findViewById(R.id.bottom_nav_view);

        bottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_conta:
                        if (isUserLogged()) {
                            changeFragment(new ProfileFragment(), "ProfileFragment");
                        } else {
                            changeFragment(new LoginFragment(), "LoginFragment");
                        }
                        break;
                    case R.id.nav_categorias:
                        showToast("Categorias");
                        break;
                    case R.id.nav_eventos:
                        showToast("Eventos");
                        break;
                    case R.id.nav_mapa:
                        showToast("Mapa");
                        break;
                }
                return true;
            }
        });

        setFragment(1);
        changeStatusBarColor(R.color.background);
    }

    //region FRAGMENT

    private void setFragment(Integer position) {
        switch (position) {
            case 1:
                bottomMenu.setSelectedItemId(R.id.nav_conta);
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;

        }
    }

    public void pushFragmentWithStack(String tag, Fragment fragment) {
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

    public boolean isUserLogged() {
        if (AccessToken.getCurrentAccessToken() != null) {
            return true;
        }
        return false;
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
}
