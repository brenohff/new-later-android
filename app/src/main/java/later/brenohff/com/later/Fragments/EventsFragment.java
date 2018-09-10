package later.brenohff.com.later.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import later.brenohff.com.later.Activities.MainActivity;
import later.brenohff.com.later.Adapters.EventsAdapter;
import later.brenohff.com.later.Connections.LTConnection;
import later.brenohff.com.later.Connections.LTRequests;
import later.brenohff.com.later.Models.LTEvent;
import later.brenohff.com.later.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsFragment extends Fragment {

    private Context context;

    private RecyclerView recyclerView;
    private AlertDialog alertDialog;

    @Override
    public void onStop() {
        super.onStop();
        if (alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        context = view.getContext();

        castFields(view);

        return view;
    }

    private void castFields(View view) {
        recyclerView = view.findViewById(R.id.events_recyclerView);

        alertDialog = ((MainActivity) context).alertDialog(alertDialog, context, "Buscando eventos...");
        getEvents();
    }

    private void getEvents() {
        LTRequests requests = LTConnection.createService(LTRequests.class);
        Call<List<LTEvent>> call = requests.getPublic();
        call.enqueue(new Callback<List<LTEvent>>() {
            @Override
            public void onResponse(@NonNull Call<List<LTEvent>> call, @NonNull Response<List<LTEvent>> response) {
                if (response.isSuccessful()) {
                    mountRecycler(response.body());
                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<LTEvent>> call, @NonNull Throwable t) {
                Toast.makeText(context, "Não foi possível conectar ao servidor.", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });

    }

    private void mountRecycler(List<LTEvent> ltEvents) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new EventsAdapter(ltEvents));

        (new Handler()).postDelayed(() -> alertDialog.dismiss(), 2000);
    }
}
