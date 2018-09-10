package later.brenohff.com.later.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import later.brenohff.com.later.Adapters.CategoriesAdapter;
import later.brenohff.com.later.Connections.LTConnection;
import later.brenohff.com.later.Connections.LTRequests;
import later.brenohff.com.later.Models.LTCategory;
import later.brenohff.com.later.Others.ItemClickSupport;
import later.brenohff.com.later.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriesFragment extends Fragment {

    private Context context;

    private RecyclerView recyclerView;
    private Parcelable recyclerViewState;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        context = view.getContext();

        castFields(view);

        return view;
    }

    private void castFields(View view) {
        recyclerView = view.findViewById(R.id.categories_recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        getCategories();
    }

    private void getCategories() {
        LTRequests requests = LTConnection.createService(LTRequests.class);
        Call<List<LTCategory>> call = requests.getCategories();
        call.enqueue(new Callback<List<LTCategory>>() {
            @Override
            public void onResponse(@NonNull Call<List<LTCategory>> call, @NonNull Response<List<LTCategory>> response) {
                if (response.isSuccessful()) {
                    List<LTCategory> ltCategories = response.body();
                    recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
                    mountRecycler(Objects.requireNonNull(ltCategories));

                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<LTCategory>> call, @NonNull Throwable t) {
                Toast.makeText(context, "Não foi possível conectar ao servidor.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mountRecycler(final List<LTCategory> ltCategories) {
        if (!ltCategories.isEmpty()) {
            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
            recyclerView.setHasFixedSize(true);
            ItemClickSupport.addTo(recyclerView).setOnItemClickListener((recyclerView, position, v) ->
                    Toast.makeText(context, ltCategories.get(position).getName(), Toast.LENGTH_SHORT).show());
            recyclerView.setAdapter(new CategoriesAdapter(ltCategories));
        }
    }
}
