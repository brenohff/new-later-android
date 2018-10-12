package later.brenohff.com.later.Fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import later.brenohff.com.later.Activities.MainActivity;
import later.brenohff.com.later.Adapters.MyEventsAdapter;
import later.brenohff.com.later.Connections.LTConnection;
import later.brenohff.com.later.Connections.LTRequests;
import later.brenohff.com.later.Enums.EventStatus;
import later.brenohff.com.later.Memory.LTMainData;
import later.brenohff.com.later.Models.LTEvent;
import later.brenohff.com.later.Others.ItemClickSupport;
import later.brenohff.com.later.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Cre
 */
public class MyEventsFragment extends Fragment {

    private Context context;

    private AlertDialog alertDialog;
    private RecyclerView recyclerView;
    private TextView not_found_message;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_events, container, false);
        context = view.getContext();

        recyclerView = view.findViewById(R.id.my_events_recyclerView);
        not_found_message = view.findViewById(R.id.my_events_not_found);

        alertDialog = ((MainActivity) context).alertDialog(context, "Buscando eventos...");
        getMyEvents();

        return view;
    }

    private void getMyEvents() {
        LTRequests requests = LTConnection.createService(LTRequests.class);
        Call<List<LTEvent>> call = requests.getEventsByUser(LTMainData.getInstance().getUser().getId());
        call.enqueue(new Callback<List<LTEvent>>() {
            @Override
            public void onResponse(@NonNull Call<List<LTEvent>> call, @NonNull Response<List<LTEvent>> response) {
                if (response.isSuccessful()) {
                    inflateRecyclerView(Objects.requireNonNull(response.body()));
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(context, "Erro ao buscar eventos.", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<LTEvent>> call, @NonNull Throwable t) {
                Toast.makeText(context, "Falha ao buscar eventos.", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });

    }

    private void inflateRecyclerView(List<LTEvent> eventList) {
        if (eventList.size() > 0) {
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            ItemClickSupport.addTo(recyclerView).setOnItemClickListener((recyclerView, position, v) ->
                    showFilterPopup(v, eventList.get(position)));
            recyclerView.setAdapter(new MyEventsAdapter(eventList));
        } else {
            recyclerView.setVisibility(View.GONE);
            not_found_message.setVisibility(View.VISIBLE);
        }
    }

    private void showFilterPopup(View v, LTEvent event) {
        PopupMenu popup = new PopupMenu(context, v);
        popup.getMenuInflater().inflate(R.menu.popup_filter, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.popup_view:
                    EventFragment eventFragment = new EventFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("event", event);
                    eventFragment.setArguments(bundle);
                    ((MainActivity) context).pushFragmentWithStack(eventFragment, "EventFragment");
                    return true;

                case R.id.popup_edit:
                    EditEventFragment editEventFragment= new EditEventFragment();
                    Bundle editBundle = new Bundle();
                    editBundle.putSerializable("event", event);
                    editEventFragment.setArguments(editBundle);
                    ((MainActivity) context).pushFragmentWithStack(editEventFragment, "EditEventFragment");
                    return true;

                case R.id.popup_delete:
                    deleteEvent();
                    return true;

                default:
                    return false;
            }
        });

        popup.show();
    }

    private void deleteEvent() {
        alertDialog = ((MainActivity) context).alertDialog(context, "Cancelando evento...");

        LTRequests requests = LTConnection.createService(LTRequests.class);
        Call<Void> call = requests.changeEventStatus(EventStatus.CANCELADO);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Evento cancelado com sucesso", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(context, "Erro ao cancelar evento.", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(context, "Falha ao cancelar evento.", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });
    }

}
