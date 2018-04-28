package later.brenohff.com.later.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import later.brenohff.com.later.Adapters.EventsAdapter;
import later.brenohff.com.later.Connections.LTConnection;
import later.brenohff.com.later.Connections.LTRequests;
import later.brenohff.com.later.Models.LTEvent;
import later.brenohff.com.later.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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

        castFields(view);

        return view;
    }

    private void castFields(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.events_recyclerView);

        getEvents();
    }

    private void getEvents() {
        LTRequests requests = LTConnection.createService(LTRequests.class);
        Call<List<LTEvent>> call = requests.getPublic();
        call.enqueue(new Callback<List<LTEvent>>() {
            @Override
            public void onResponse(Call<List<LTEvent>> call, Response<List<LTEvent>> response) {
                if (response.isSuccessful()) {
                    List<LTEvent> ltEvents = response.body();
                    mountRecycler(ltEvents);
                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<LTEvent>> call, Throwable t) {
                Toast.makeText(context, "Não foi possível conectar ao servidor.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void mountRecycler(List<LTEvent> ltEvents) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new EventsAdapter(ltEvents));
    }
}
