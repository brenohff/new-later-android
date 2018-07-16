package later.brenohff.com.later.Fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.ads.formats.NativeAd;
import com.squareup.picasso.Picasso;

import later.brenohff.com.later.Activities.MainActivity;
import later.brenohff.com.later.Models.LTEvent;
import later.brenohff.com.later.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment implements View.OnClickListener {

    private Context context;
    private LTEvent event;
    private String[] meses = {"jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez"};

    private FloatingActionButton btComment;
    private ImageView event_image;
    private TextView event_title;

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
        event_image = (ImageView) view.findViewById(R.id.fragment_event_viewpager);
        event_title = (TextView) view.findViewById(R.id.event_title);

        event_image.getLayoutParams().height = (int) (((MainActivity) context).getWidth() * 0.6);
        String date[] = event.getDate().split("/");

        Picasso.get().load(event.getImage()).into(event_image);
        event_title.setText(event.getTitle());

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

    private void setColor(ImageView imageView, int color) {
        Drawable background = imageView.getBackground();
        if (background instanceof GradientDrawable)
            ((GradientDrawable) background).setColor(color);
    }

    private void setColor(LinearLayout imageView, int color) {
        Drawable background = imageView.getBackground();

        if (background instanceof ShapeDrawable)
            ((ShapeDrawable) background).getPaint().setColor(color);
        else if (background instanceof GradientDrawable)
            ((GradientDrawable) background).setColor(color);
        else if (background instanceof ColorDrawable)
            ((ColorDrawable) background).setColor(color);

    }
}
