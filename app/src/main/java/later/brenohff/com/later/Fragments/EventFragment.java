package later.brenohff.com.later.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionButton;

import later.brenohff.com.later.Activities.MainActivity;
import later.brenohff.com.later.Models.LTEvent;
import later.brenohff.com.later.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment implements View.OnClickListener {

    private Context context;
    private LTEvent event;

    private FloatingActionButton btComment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            event = (LTEvent) getArguments().getSerializable("event");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        context = view.getContext();

        castFields(view);

        return view;
    }

    private void castFields(View view) {
        btComment = (FloatingActionButton) view.findViewById(R.id.fragment_event_floating_coment);

        btComment.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_event_floating_coment:
                CommentFragment commentFragment = new CommentFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("event", event);
                commentFragment.setArguments(bundle);
                ((MainActivity) context).pushFragmentWithStack(commentFragment, "CommentFragment");
                break;
        }
    }
}
