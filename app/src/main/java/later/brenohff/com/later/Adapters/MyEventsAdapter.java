package later.brenohff.com.later.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import later.brenohff.com.later.Models.LTEvent;
import later.brenohff.com.later.R;

public class MyEventsAdapter extends RecyclerView.Adapter<MyEventsAdapter.MyEventsViewHolder> {

    private List<LTEvent> eventList;

    public MyEventsAdapter(List<LTEvent> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public MyEventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_my_events, parent, false);

        return new MyEventsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyEventsViewHolder holder, int position) {
        holder.bindView(eventList.get(position));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    class MyEventsViewHolder extends RecyclerView.ViewHolder {

        private TextView title;

        MyEventsViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.viewholder_my_events_title);
        }

        private void bindView(LTEvent event) {
            title.setText(event.getTitle());
        }
    }
}
