package later.brenohff.com.later.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import later.brenohff.com.later.Models.LTCategory;
import later.brenohff.com.later.R;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    private List<LTCategory> ltCategories;
    private Context context;

    public CategoriesAdapter(List<LTCategory> ltCategories) {
        this.ltCategories = ltCategories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_category, parent, false);
        context = view.getContext();

        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        LTCategory category = ltCategories.get(position);
        Picasso.get().load(category.getUrl()).into(holder.background);
        holder.title.setText(category.getName());
    }

    @Override
    public int getItemCount() {
        return ltCategories.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        private ImageView background;
        private TextView title;

        public CategoryViewHolder(View itemView) {
            super(itemView);

            background = (ImageView) itemView.findViewById(R.id.viewHolderCategoryBackground);
            title = (TextView) itemView.findViewById(R.id.viewHolderCategoryText);
        }
    }

}
