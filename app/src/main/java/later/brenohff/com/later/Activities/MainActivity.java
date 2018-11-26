package later.brenohff.com.later.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.roughike.bottombar.BottomBar;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import later.brenohff.com.later.Connections.LTConnection;
import later.brenohff.com.later.Connections.LTRequests;
import later.brenohff.com.later.Fragments.CategoriesFragment;
import later.brenohff.com.later.Fragments.EventsFragment;
import later.brenohff.com.later.Fragments.LoginFragment;
import later.brenohff.com.later.Fragments.MapsFragment;
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
    private LocationManager locationManager;
    private android.support.v7.app.AlertDialog alert;
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

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        } else {
            popFragment(1);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (alert != null) {
            alert.dismiss();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setPermissions();

        if (AccessToken.getCurrentAccessToken() != null) {
            getUserByID();
        } else {
            changeFragment(new LoginFragment(), "LoginFragment");
        }

        initFB();
        showFBInfo();

        bottomMenu = findViewById(R.id.bottom_nav_view);

        bottomMenu.setOnTabSelectListener(tabId -> {
            switch (tabId) {
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
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Por favor, conceda permissão para que o app possa obter sua localização.", Toast.LENGTH_LONG).show();
                    } else {
                        changeFragment(new MapsFragment(), "MapsFragment");
                    }
                    break;
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

    //region BOTTOM BAR
    public static void setBottomBarInvisible() {
        bottomMenu.setVisibility(View.GONE);
    }

    public static void setBottomBarVisible() {
        bottomMenu.setVisibility(View.VISIBLE);
    }
    //endregion

    //region PERMISSION

    private void setPermissions() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        showGPSDisabledAlertToUser();
                    }
                } else {
                    Toast.makeText(this, "Permissão negada para esta função.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void showGPSDisabledAlertToUser() {
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("O GPS está desativado. Deseja ativá-lo?")
                .setCancelable(false)
                .setPositiveButton("Ativar GPS",
                        (dialog, id) -> startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1));
        alertDialogBuilder.setNegativeButton("Cancelar",
                (dialog, id) -> dialog.cancel());
        alert = alertDialogBuilder.create();
        alert.show();
    }
    //endregion

    private void getUserByID() {
        if (SaveUserOnDevice.loadSavedUser(this) != null) {
            LTRequests request = LTConnection.createService(LTRequests.class);
            Call<LTUser> call = request.getUserByFaceID(Objects.requireNonNull(SaveUserOnDevice.loadSavedUser(this)).getUser_id());
            call.enqueue(new Callback<LTUser>() {
                @Override
                public void onResponse(@NonNull Call<LTUser> call, @NonNull Response<LTUser> response) {
                    if (response.isSuccessful()) {
                        LTMainData.getInstance().setUser(response.body());
                    }else{
                        showToast("Não foi possível obter usuário, tente novamente.");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<LTUser> call, @NonNull Throwable t) {
                    showToast("Erro ao conectar com o servidor, tente novamente.");
                }
            });
        } else {
            showToast("Não foi possível carregar usuário, faça login novamente.");
            LoginManager.getInstance().logOut();
            SaveUserOnDevice.removeSavedUser(this);
            changeFragment(new LoginFragment(), "LoginFragment");
        }
    }

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
        } catch (PackageManager.NameNotFoundException ignored) {

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public int getWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        return metrics.widthPixels;
    }

    public AlertDialog alertDialog(Context context, String message) {
        AlertDialog alertDialog = new SpotsDialog.Builder()
                .setContext(context)
                .setMessage(message)
                .setCancelable(false)
                .setTheme(R.style.AlertDialogStyle)
                .build();
        alertDialog.show();

        return alertDialog;
    }


}
