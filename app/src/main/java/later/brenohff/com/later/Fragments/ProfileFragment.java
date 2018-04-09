package later.brenohff.com.later.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import later.brenohff.com.later.Activities.MainActivity;
import later.brenohff.com.later.Memory.LTMainData;
import later.brenohff.com.later.Models.LTUser;
import later.brenohff.com.later.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    private Context context;
    private LTUser user;

    private Button bt_logout;
    private CircleImageView userImage;
    private TextView userName, userEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = view.getContext();

        bt_logout = (Button) view.findViewById(R.id.logout);
        userImage = (CircleImageView) view.findViewById(R.id.profile_fragment_imagem);
        userName = (TextView) view.findViewById(R.id.profile_fragment_nome);
        userEmail = (TextView) view.findViewById(R.id.profile_fragment_email);

        if (LTMainData.getInstance().getUser() != null) {
            user = LTMainData.getInstance().getUser();
            setUserInfo();
        }

        bt_logout.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logout:
                LoginManager.getInstance().logOut();
                ((MainActivity) context).pushFragmentWithNoStack(new LoginFragment(), "LoginFragment");
                break;

        }
    }

    private void setUserInfo() {
        Picasso.get().load(user.getImage_long()).into(userImage);
        userName.setText(user.getName());
        userEmail.setText(user.getEmail());
    }
}
