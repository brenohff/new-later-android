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
                holder.expand();
            }
        });
        holder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventFragment eventFragment = new EventFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("event", event);
                eventFragment.setArguments(bundle);
                ((MainActivity) context).pushFragmentWithStack(eventFragment, "EventFragment");
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
        private LinearLayout info, whoGo, data;

        private boolean b = true;
        final int animationDuration = 500;
        private ResizeAnimation animation = null;

        public EventViewHolder(View itemView) {
            super(itemView);

            nome = (TextView) itemView.findViewById(R.id.nome);
            hora = (TextView) itemView.findViewById(R.id.hora);
            local = (TextView) itemView.findViewById(R.id.local);
            background = (ImageView) itemView.findViewById(R.id.viewHolderEventBackground);
            cardview = (CardView) itemView.findViewById(R.id.cardview);
            coracao = (ImageView) itemView.findViewById(R.id.coracao);
            info = (LinearLayout) itemView.findViewById(R.id.infor);
            whoGo = (LinearLayout) itemView.findViewById(R.id.whoGo);
            data = (LinearLayout) itemView.findViewById(R.id.data);
            nome = (TextView) itemView.findViewById(R.id.nome);

            hideInfo();
        }

        private void expand() {
            if (b) {
                showInfo();
                b = false;
            } else {
                hideInfo();
                b = true;
            }
        }

        private void hideInfo() {
            int newHeigth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, context.getResources().getDisplayMetrics());
            animation = new ResizeAnimation(cardview, newHeigth);
            animation.setDuration(animationDuration);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    nome.animate().translationY(nome.getHeight() * 2).setDuration(animationDuration);
                    coracao.animate().alpha(0.0f).setDuration(300);
                    info.animate().alpha(0.0f).setDuration(300);
                    data.animate().alpha(0.0f).setDuration(300);
                    whoGo.animate().alpha(0.0f).setDuration(300);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            cardview.startAnimation(animation);
        }

        private void showInfo() {
            int newHeigth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, context.getResources().getDisplayMetrics());
            animation = new ResizeAnimation(cardview, newHeigth);
            animation.setDuration(animationDuration);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    nome.animate().translationY(-nome.getHeight() / 10).setDuration(300);
                    coracao.animate().alpha(1.0f).setDuration(animationDuration);
                    info.animate().alpha(1.0f).setDuration(animationDuration);
                    data.animate().alpha(1.0f).setDuration(animationDuration);
                    whoGo.animate().alpha(1.0f).setDuration(animationDuration);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            cardview.startAnimation(animation);
        }
    }
}

//region animação cardview
    /*

        private CardView cardview;
        private ImageView coracao;
        private RelativeLayout relativeLayout;
        private LinearLayout info, whoGo, data;
        private TextView nome;
        private boolean b = true;
        final int animationDuration = 500;
        private ResizeAnimation animation = null;

        cardview = (CardView) findViewById(R.id.cardview);
        coracao = (ImageView) findViewById(R.id.coracao);
        info = (LinearLayout) findViewById(R.id.infor);
        whoGo = (LinearLayout) findViewById(R.id.whoGo);
        data = (LinearLayout) findViewById(R.id.data);
        nome = (TextView) findViewById(R.id.nome);
        relativeLayout = (RelativeLayout) findViewById(R.id.relative);

        hideInfo();

        cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expand();
            }
        });

    private void expand() {
        if (b) {
            showInfo();
            b = false;
        } else {
            hideInfo();
            b = true;
        }
    }

    private void hideInfo() {
        int newHeigth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
        animation = new ResizeAnimation(cardview, newHeigth);
        animation.setDuration(animationDuration);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                nome.animate().translationY(nome.getHeight() * 2).setDuration(animationDuration);
                coracao.animate().alpha(0.0f).setDuration(300);
                info.animate().alpha(0.0f).setDuration(300);
                data.animate().alpha(0.0f).setDuration(300);
                whoGo.animate().alpha(0.0f).setDuration(300);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        cardview.startAnimation(animation);
    }

    private void showInfo() {
        int newHeigth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, getResources().getDisplayMetrics());

        animation = new ResizeAnimation(cardview, newHeigth);
        animation.setDuration(animationDuration);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                nome.animate().translationY(-nome.getHeight() / 10).setDuration(300);
                coracao.animate().alpha(1.0f).setDuration(animationDuration);
                info.animate().alpha(1.0f).setDuration(animationDuration);
                data.animate().alpha(1.0f).setDuration(animationDuration);
                whoGo.animate().alpha(1.0f).setDuration(animationDuration);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        cardview.startAnimation(animation);
    }

    */
//endregion
