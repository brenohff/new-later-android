package later.brenohff.com.later.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import later.brenohff.com.later.Activities.MainActivity;
import later.brenohff.com.later.Connections.LTConnection;
import later.brenohff.com.later.Connections.LTRequests;
import later.brenohff.com.later.Fragments.EventFragment;
import later.brenohff.com.later.Memory.LTMainData;
import later.brenohff.com.later.Models.LTEvent;
import later.brenohff.com.later.Models.LTUser;
import later.brenohff.com.later.Models.LTUserEvent;
import later.brenohff.com.later.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private Context context;
    private List<LTEvent> eventList;
    private boolean liked = false;

    public EventsAdapter(List<LTEvent> eventList) {
        this.eventList = eventList;
    }


    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_event, parent, false);
        context = view.getContext();
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventViewHolder holder, int position) {
        final LTEvent event = eventList.get(position);
        Picasso.get().load(event.getImage()).into(holder.background);
        holder.nome.setText(event.getTitle());
        holder.hora.setText(event.getDate() + " - " + event.getHour());

//        holder.local.setText(event.getLocale());
//
        if (holder.whoGo != null) {
            if (event.getFavorites().isEmpty()) {
                holder.whoGo.setVisibility(View.GONE);
            } else {
                Integer size = event.getFavorites().size();

                if (size == 0) {
                    holder.whoGo.setVisibility(View.GONE);
                } else if (size == 1) {
                    Picasso.get().load(event.getFavorites().get(0).getImage()).into(holder.profile1);
                    holder.profile2.setVisibility(View.GONE);
                    holder.profile3.setVisibility(View.GONE);
                } else if (size == 2) {
                    Picasso.get().load(event.getFavorites().get(0).getImage()).into(holder.profile1);
                    Picasso.get().load(event.getFavorites().get(1).getImage()).into(holder.profile2);
                    holder.profile3.setVisibility(View.GONE);
                } else if (size >= 3) {
                    Picasso.get().load(event.getFavorites().get(0).getImage()).into(holder.profile1);
                    Picasso.get().load(event.getFavorites().get(1).getImage()).into(holder.profile2);
                    Picasso.get().load(event.getFavorites().get(2).getImage()).into(holder.profile3);
                }
            }
        }

        if (LTMainData.getInstance().getUser() != null) {
            for (LTUser user : event.getFavorites()) {
                if (user.getId().equals(LTMainData.getInstance().getUser().getId())) {
                    liked = true;
                    holder.coracao.setImageDrawable(context.getDrawable(R.drawable.ic_icons8_heart_outline_filled_100));
                }
            }
        }

        holder.cardview.setOnClickListener(view -> {
            EventFragment eventFragment = new EventFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("event", event);
            eventFragment.setArguments(bundle);
            ((MainActivity) context).pushFragmentWithStack(eventFragment, "EventFragment");
        });

        holder.coracao.setOnClickListener(view -> {
            if (LTMainData.getInstance().getUser() != null) {
                if (liked) {
                    deleteFavoriteEvent(event.getId());
                    holder.coracao.setImageDrawable(context.getDrawable(R.drawable.ic_icons8_heart_outline_100));
                } else {
                    saveFavoriteEvent(event.getId());
                    holder.coracao.setImageDrawable(context.getDrawable(R.drawable.ic_icons8_heart_outline_filled_100));
                }
                liked = !liked;
            } else {
                Toast.makeText(context, "É necessário realizar login para inserir evento aos favoritos.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveFavoriteEvent(final Long event_id) {
        LTRequests requests = LTConnection.createService(LTRequests.class);
        Call<Void> call = requests.saveFavoritesEvents(new LTUserEvent(event_id, LTMainData.getInstance().getUser().getId()));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Evento adicionado aos favoritos.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Não foi possível salvar evento como favorito", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(context, "Falha ao salvar evento como favorito", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteFavoriteEvent(final Long event_id) {
        LTRequests requests = LTConnection.createService(LTRequests.class);
        Call<Void> call = requests.deleteFavoritesEvents(new LTUserEvent(event_id, LTMainData.getInstance().getUser().getId()));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Evento removido dos favoritos.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Não foi possível remover evento como favorito", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(context, "Falha ao remover evento como favorito", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {

        private ImageView background;
        private TextView nome, hora, local;
        private CardView cardview;
        private ImageView coracao;

        private LinearLayout whoGo;
        private CircleImageView profile1, profile2, profile3;

        EventViewHolder(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.nome);
            hora = itemView.findViewById(R.id.hora);
            local = itemView.findViewById(R.id.local);
            background = itemView.findViewById(R.id.viewHolderEventBackground);
            cardview = itemView.findViewById(R.id.cardview);
            coracao = itemView.findViewById(R.id.coracao);
            nome = itemView.findViewById(R.id.nome);

            whoGo = itemView.findViewById(R.id.whoGo);
            profile1 = itemView.findViewById(R.id.profile_image);
            profile2 = itemView.findViewById(R.id.profile_image2);
            profile3 = itemView.findViewById(R.id.profile_image3);
        }
    }
}