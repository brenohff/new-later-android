package later.brenohff.com.later.Fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import later.brenohff.com.later.Activities.MainActivity;
import later.brenohff.com.later.Memory.LTMainData;
import later.brenohff.com.later.Models.LTUser;
import later.brenohff.com.later.Others.SaveUserOnDevice;
import later.brenohff.com.later.R;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by breno.franco on 23/08/2018.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    private Context context;
    private LTUser user;

    private CircleImageView userImage;
    private TextView userName, userEmail;

    private LocationManager locationManager;
    private AlertDialog alert;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = view.getContext();

        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        userImage = view.findViewById(R.id.profile_fragment_image);
        userName = view.findViewById(R.id.profile_fragment_name);
        userEmail = view.findViewById(R.id.profile_fragment_email);
        Button bt_logout = view.findViewById(R.id.profile_fragment_logout);
        Button bt_criarEvento = view.findViewById(R.id.profile_fragment_create_event);
        Button bt_my_events = view.findViewById(R.id.profile_fragment_my_events);

        if (LTMainData.getInstance().getUser() != null) {
            user = LTMainData.getInstance().getUser();
            setUserInfo();
        }

        bt_my_events.setOnClickListener(this);
        bt_logout.setOnClickListener(this);
        bt_criarEvento.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profile_fragment_logout:
                LoginManager.getInstance().logOut();
                LTMainData.getInstance().setUser(null);
                SaveUserOnDevice.removeSavedUser(context);
                ((MainActivity) context).pushFragmentWithNoStack(new LoginFragment(), "LoginFragment");
                break;
            case R.id.profile_fragment_create_event:
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                break;
            case R.id.profile_fragment_my_events:
                ((MainActivity) context).pushFragmentWithStack(new MyEventsFragment(), "MyEventsFragment");
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        ((MainActivity) context).pushFragmentWithStack(new CreateEventFragment(), "CreateEventFragment");
                    } else {
                        showGPSDisabledAlertToUser();
                    }
                } else {
                    Toast.makeText(context, "Permissão negada para esta função.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (alert != null) {
            alert.dismiss();
        }
    }

    private void setUserInfo() {
        Picasso.get().load(user.getImage_long()).into(userImage);
        userName.setText(user.getName());
        userEmail.setText(user.getEmail());
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("O GPS está desativado. Deseja ativá-lo?")
                .setCancelable(false)
                .setPositiveButton("Ativar GPS",
                        (dialog, id) -> startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1));
        alertDialogBuilder.setNegativeButton("Cancelar",
                (dialog, id) -> dialog.cancel());
        alert = alertDialogBuilder.create();
        alert.show();
    }
}
