package later.brenohff.com.later.Fragments;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
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
import later.brenohff.com.later.R;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    private Context context;
    private LTUser user;

    private Button bt_logout, bt_criarEvento;
    private CircleImageView userImage;
    private TextView userName, userEmail;

    private LocationManager locationManager;
    private AlertDialog alert;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = view.getContext();

        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        bt_logout = (Button) view.findViewById(R.id.logout);
        bt_criarEvento = (Button) view.findViewById(R.id.criar_evento);
        userImage = (CircleImageView) view.findViewById(R.id.profile_fragment_imagem);
        userName = (TextView) view.findViewById(R.id.profile_fragment_nome);
        userEmail = (TextView) view.findViewById(R.id.profile_fragment_email);

        if (LTMainData.getInstance().getUser() != null) {
            user = LTMainData.getInstance().getUser();
            setUserInfo();
        }

        bt_logout.setOnClickListener(this);
        bt_criarEvento.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logout:
                LoginManager.getInstance().logOut();
                ((MainActivity) context).pushFragmentWithNoStack(new LoginFragment(), "LoginFragment");
                break;
            case R.id.criar_evento:
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                break;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
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
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alert = alertDialogBuilder.create();
        alert.show();
    }
}
