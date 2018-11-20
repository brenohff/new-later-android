package later.brenohff.com.later.Fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
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
import later.brenohff.com.later.Enums.UserType;
import later.brenohff.com.later.Memory.LTMainData;
import later.brenohff.com.later.Models.LTUser;
import later.brenohff.com.later.Others.SaveUserOnDevice;
import later.brenohff.com.later.R;

/**
 * Created by breno.franco on 23/08/2018.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    private Context context;
    private LTUser user;

    private CircleImageView userImage;
    private TextView userName, userEmail;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = view.getContext();

        userImage = view.findViewById(R.id.profile_fragment_image);
        userName = view.findViewById(R.id.profile_fragment_name);
        userEmail = view.findViewById(R.id.profile_fragment_email);

        Button bt_logout = view.findViewById(R.id.profile_fragment_logout);
        Button bt_criarEvento = view.findViewById(R.id.profile_fragment_create_event);
        Button bt_my_events = view.findViewById(R.id.profile_fragment_my_events);
        Button bt_approve_event = view.findViewById(R.id.profile_fragment_approve_event);

        bt_my_events.setOnClickListener(this);
        bt_logout.setOnClickListener(this);
        bt_criarEvento.setOnClickListener(this);

        if (LTMainData.getInstance().getUser() != null) {
            user = LTMainData.getInstance().getUser();
            setUserInfo();
        }

        if (user.getUserType() != null && user.getUserType().equals(UserType.ADMIN)) {
            bt_approve_event.setVisibility(View.VISIBLE);
            bt_approve_event.setOnClickListener(this);
        }

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
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
                if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Por favor, conceda permissão para que o app possa obter sua localização e acessar suas fotos.", Toast.LENGTH_LONG).show();
                } else {
                    ((MainActivity) context).pushFragmentWithStack(new CreateEventFragment(), "CreateEventFragment");
                }
                break;
            case R.id.profile_fragment_my_events:
                ((MainActivity) context).pushFragmentWithStack(new MyEventsFragment(), "MyEventsFragment");
                break;
        }
    }

    private void setUserInfo() {
        Picasso.get().load(user.getImage_long()).into(userImage);
        userName.setText(user.getName());
        userEmail.setText(user.getEmail());
    }

}
