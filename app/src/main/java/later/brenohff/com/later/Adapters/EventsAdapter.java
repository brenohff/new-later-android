package later.brenohff.com.later.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import later.brenohff.com.later.Activities.MainActivity;
import later.brenohff.com.later.Fragments.EventFragment;
import later.brenohff.com.later.Models.LTEvent;
import later.brenohff.com.later.Others.ResizeAnimation;
import later.brenohff.com.later.R;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private Context context;
    private List<LTEvent> eventList;

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
        holder.local.setText(event.getLocale());
        holder.hora.setText(event.getDate() + " - " + event.getHour());

        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventFragment eventFragment = new EventFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("event", event);
                eventFragment.setArguments(bundle);
                ((MainActivity) context).pushFragmentWithStack(eventFragment, "EventFragment");
            }
        });

        holder.coracao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Liked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {

        private ImageView background;
        private TextView nome, hora, local;
        private CardView cardview;
        private ImageView coracao;
        private LinearLayout info, data;

        public EventViewHolder(View itemView) {
            super(itemView);

            nome = (TextView) itemView.findViewById(R.id.nome);
            hora = (TextView) itemView.findViewById(R.id.hora);
            local = (TextView) itemView.findViewById(R.id.local);
            background = (ImageView) itemView.findViewById(R.id.viewHolderEventBackground);
            cardview = (CardView) itemView.findViewById(R.id.cardview);
            coracao = (ImageView) itemView.findViewById(R.id.coracao);
            info = (LinearLayout) itemView.findViewById(R.id.infor);
            data = (LinearLayout) itemView.findViewById(R.id.data);
            nome = (TextView) itemView.findViewById(R.id.nome);
        }
    }
}