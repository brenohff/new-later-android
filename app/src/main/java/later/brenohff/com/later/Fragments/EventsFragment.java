package later.brenohff.com.later.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import later.brenohff.com.later.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment {

    private Context context;

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        context = view.getContext();

        recyclerView = (RecyclerView) view.findViewById(R.id.events_recyclerView);

        return view;
    }

}
