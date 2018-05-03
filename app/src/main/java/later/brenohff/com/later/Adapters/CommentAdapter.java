package later.brenohff.com.later.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import later.brenohff.com.later.Memory.LTMainData;
import later.brenohff.com.later.Models.LTChat;
import later.brenohff.com.later.Models.LTUser;
import later.brenohff.com.later.R;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<LTChat> ltChats;
    private Context context;

    public CommentAdapter(List<LTChat> ltChats) {
        this.ltChats = ltChats;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_comment, parent, false);
        context = view.getContext();

        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        LTUser userLogged = LTMainData.getInstance().getUser();
        LTChat chat = ltChats.get(position);
        String[] username = chat.getUser().getName().split(" ");

        if (userLogged != null && chat.getUser().getId().equals(userLogged.getId())) {
            holder.layout_left.setVisibility(View.GONE);
            holder.layout_right.setVisibility(View.VISIBLE);

            Picasso.get().load(chat.getUser().getImage()).into(holder.profilePicture2);
            holder.profileName2.setText(username[0]);
            holder.chatComment2.setText(chat.getContent());
        } else {
            holder.layout_right.setVisibility(View.GONE);
            holder.layout_left.setVisibility(View.VISIBLE);

            Picasso.get().load(chat.getUser().getImage()).into(holder.profilePicture);
            holder.profileName.setText(username[0]);
            holder.chatComment.setText(chat.getContent());

        }
    }

    @Override
    public int getItemCount() {
        return ltChats.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout layout_left, layout_right;
        private CircleImageView profilePicture, profilePicture2;
        private TextView profileName, profileName2, chatComment, chatComment2, chatDate, chatDate2;

        public CommentViewHolder(View itemView) {
            super(itemView);

            layout_left = (LinearLayout) itemView.findViewById(R.id.item_comentario_layout_left);
            layout_right = (LinearLayout) itemView.findViewById(R.id.item_comentario_layout_right);

            profilePicture = (CircleImageView) itemView.findViewById(R.id.item_comentario_foto);
            profilePicture2 = (CircleImageView) itemView.findViewById(R.id.item_comentario_foto2);
            profileName = (TextView) itemView.findViewById(R.id.item_comentario_nome);
            profileName2 = (TextView) itemView.findViewById(R.id.item_comentario_nome2);
            chatComment = (TextView) itemView.findViewById(R.id.item_comentario_texto);
            chatComment2 = (TextView) itemView.findViewById(R.id.item_comentario_texto2);
            chatDate = (TextView) itemView.findViewById(R.id.item_comentario_data);
            chatDate2 = (TextView) itemView.findViewById(R.id.item_comentario_data2);
        }
    }
}
