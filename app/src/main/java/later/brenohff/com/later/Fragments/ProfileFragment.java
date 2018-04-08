package later.brenohff.com.later.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Profile;

import later.brenohff.com.later.Activities.MainActivity;
import later.brenohff.com.later.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = view.getContext();

        if (((MainActivity) context).isUserLogged()) {
            ((MainActivity) context).showToast("Oii " + Profile.getCurrentProfile().getFirstName() + " ;)");
        }

        return view;
    }

}
