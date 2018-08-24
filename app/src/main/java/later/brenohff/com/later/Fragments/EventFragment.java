package later.brenohff.com.later.Fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import later.brenohff.com.later.Activities.MainActivity;
import later.brenohff.com.later.Connections.LTConnection;
import later.brenohff.com.later.Connections.LTRequests;
import later.brenohff.com.later.Models.LTCategory;
import later.brenohff.com.later.Models.LTEvent;
import later.brenohff.com.later.Others.ExpandableTextView;
import later.brenohff.com.later.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by breno.franco on 23/08/2018.
 */
public class EventFragment extends Fragment implements View.OnClickListener {

    private Context context;
    private LTEvent event;

    private TagView tagView;
    private List<Tag> tagList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            event = (LTEvent) getArguments().getSerializable("event");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        context = view.getContext();

        castFields(view);

        return view;
    }

    private void castFields(View view) {
        FloatingActionButton btComment = view.findViewById(R.id.fragment_event_floating_coment);
        ImageView imageView = view.findViewById(R.id.fragment_event_image);
        ExpandableTextView event_description = view.findViewById(R.id.fragment_event_description);
        ImageView event_map = view.findViewById(R.id.fragment_event_map);
        TextView event_locale = view.findViewById(R.id.fragment_event_location);
        tagView = view.findViewById(R.id.fragment_event_tag_group);
        CircleImageView event_user_image = view.findViewById(R.id.fragment_event_user_foto);
        TextView event_user_name = view.findViewById(R.id.fragment_event_user_nome);
        TextView event_user_email = view.findViewById(R.id.fragment_event_user_email);

        int width = ((MainActivity) context).getWidth();
        int height = (int) (((MainActivity) context).getWidth() * 0.6);

        imageView.getLayoutParams().width = width;
        imageView.getLayoutParams().height = height;
        Picasso.get().load(event.getImage()).into(imageView);

        event_description.setText(event.getDescription());
        event_locale.setText(event.getLocale());

        String staticMapUrl = "http://maps.google.com/maps/api/staticmap?center="
                + event.getLat() + "," + event.getLon()
                + "&markers=icon:|" + event.getLat() + "," + event.getLon()
                + "&zoom=" + 17 + "&size=480x200&sensor=false";

        getCategories();

        Picasso picasso = Picasso.get();
        picasso.setIndicatorsEnabled(false);

        picasso.load(staticMapUrl).resize(480, 200).into(event_map);
        event_map.setOnClickListener(v -> {
            String uri = String.format(Locale.getDefault(), "geo:%f,%f", event.getLat(), event.getLon());
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            context.startActivity(intent);
        });

        Picasso.get().load(event.getUser().getImage()).into(event_user_image);
        event_user_name.setText(event.getUser().getName());
        event_user_email.setText(event.getUser().getEmail());

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

    private void getCategories() {
        LTRequests requests = LTConnection.createService(LTRequests.class);
        Call<List<LTCategory>> call = requests.getCategories();
        call.enqueue(new Callback<List<LTCategory>>() {
            @Override
            public void onResponse(@NonNull Call<List<LTCategory>> call, @NonNull Response<List<LTCategory>> response) {
                if (response.isSuccessful()) {

                    tagList = new ArrayList<>();

                    for (LTCategory category : event.getCategories()) {
                        Tag tag = new Tag(category.getName().toUpperCase());
                        tagList.add(tag);
                    }

                    mountaTagView();

                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<LTCategory>> call, @NonNull Throwable t) {
                Toast.makeText(context, "Não foi possível conectar ao servidor.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mountaTagView() {
        tagView.addTags(tagList);

        for (int i = 0; i < tagList.size(); i++) {
            if (tagView.getChildAt(i) != null) {
                tagView.getChildAt(i).setBackgroundColor(Color.parseColor(event.getCategories().get(i).getBaseColor()));
            }
        }
    }
}
