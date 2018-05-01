package later.brenohff.com.later.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import later.brenohff.com.later.Models.LTChat;
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
        LTChat chat = ltChats.get(position);
        if (chat.getType() == LTChat.MessageType.CHAT) {
            Picasso.get().load(chat.getUser().getImage()).into(holder.profilePicture);
            holder.profileName.setText(chat.getUser().getName());
            holder.chatComment.setText(chat.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return ltChats.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView profilePicture;
        private TextView profileName, chatComment, chatDate;

        public CommentViewHolder(View itemView) {
            super(itemView);

            profilePicture = (CircleImageView) itemView.findViewById(R.id.item_comentario_foto);
            profileName = (TextView) itemView.findViewById(R.id.item_comentario_nome);
            chatComment = (TextView) itemView.findViewById(R.id.item_comentario_texto);
            chatDate = (TextView) itemView.findViewById(R.id.item_comentario_data);
        }
    }
}
