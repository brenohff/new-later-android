package later.brenohff.com.later.Fragments;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.java_websocket.WebSocket;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import later.brenohff.com.later.Activities.MainActivity;
import later.brenohff.com.later.Adapters.CommentAdapter;
import later.brenohff.com.later.Connections.LTConnection;
import later.brenohff.com.later.Connections.LTRequests;
import later.brenohff.com.later.Memory.LTMainData;
import later.brenohff.com.later.Models.LTChat;
import later.brenohff.com.later.Models.LTEvent;
import later.brenohff.com.later.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentFragment extends Fragment {

    private Context context;
    private LTEvent event;
    private List<LTChat> ltChats;
    private CommentAdapter adapter;

    private static final String TAG = "COMMENT-FRAGMENT";
    private StompClient mStompClient;
    private Gson mGson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
    private RecyclerView recyclerView;
    private EditText etMessage;
    private ImageButton btSend;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            event = (LTEvent) getArguments().getSerializable("event");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity.setBottomBarInvisible();
        getChats();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mStompClient != null) {
            mStompClient.disconnect();

        }
        MainActivity.setBottomBarVisible();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        context = view.getContext();

        castFields(view);

        return view;
    }

    private void castFields(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.comentarios_recyclerView);
        etMessage = (EditText) view.findViewById(R.id.fragment_comentario_comentario_texto);
        btSend = (ImageButton) view.findViewById(R.id.fragment_comentario_bt_enviar);

        setColor(btSend, R.color.floatingButtonColor);
        setColor(etMessage, R.color.white);

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = etMessage.getText().toString();
                if (mStompClient != null) {
                    if (LTMainData.getInstance().getUser() != null) {
                        if (!("").equals(message)) {
                            sendMessge();
                            etMessage.setText("");
                        } else {
                            Toast.makeText(context, "Insira um comentário.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Faça login para comentar!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Conexão não estabelecida.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void conectSocket() {
        mStompClient = Stomp.over(WebSocket.class, LTConnection.SOCKET);

        mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.d(TAG, "You'r connected.");
                            break;
                        case ERROR:
                            Log.e(TAG, "Stomp connection error", lifecycleEvent.getException());
                            Toast.makeText(context, "Erro ao conectar com o chat", Toast.LENGTH_SHORT).show();
                            break;
                        case CLOSED:
                            Toast.makeText(context, "Chat encerrado", Toast.LENGTH_SHORT).show();
                    }
                });

        mStompClient.topic("/topic/event/" + event.getId().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d(TAG, "Received " + topicMessage.getPayload());
                    updateRecycler(mGson.fromJson(topicMessage.getPayload(), LTChat.class));
                });

        mStompClient.connect();
    }

    private void getChats() {
        LTRequests requests = LTConnection.createService(LTRequests.class);
        Call<List<LTChat>> call = requests.getChatByEventId(event.getId().toString());
        call.enqueue(new Callback<List<LTChat>>() {
            @Override
            public void onResponse(Call<List<LTChat>> call, Response<List<LTChat>> response) {
                if (response.isSuccessful()) {
                    ltChats = response.body();
                    mountRecycler(ltChats);

                    conectSocket();
                } else {
                    Toast.makeText(context, "Erro ao obter conversas.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<LTChat>> call, Throwable t) {
                Toast.makeText(context, "Falha ao obter conversas, verifique conexão.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessge() {
        if (mStompClient != null) {
            LTChat chat = new LTChat();
            chat.setUser(LTMainData.getInstance().getUser());
            chat.setType(LTChat.MessageType.CHAT);
            chat.setContent(etMessage.getText().toString());
            chat.setEventId(event.getId().toString());

            String builder = new GsonBuilder().create().toJson(chat, LTChat.class);

            mStompClient.send("/live/event/" + event.getId().toString() + "/sendMessage", builder)
                    .compose(tFlowable -> tFlowable
                            .unsubscribeOn(Schedulers.newThread())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread()))
                    .subscribe(aVoid -> {
                        Toast.makeText(context, "STOMP echo send successfully", Toast.LENGTH_SHORT).show();
                    }, throwable -> {
                        Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(context, "Não foi possível enviar chat, verifique conexão.", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateRecycler(LTChat chat) {
        ltChats.add(chat);
        mountRecycler(ltChats);
    }

    private void mountRecycler(List<LTChat> ltChats) {
        adapter = new CommentAdapter(ltChats);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    //region SET BACKGROUND ROUNDED COLORS
    private void setColor(ImageButton imageView, int color) {

        Drawable background = imageView.getBackground();

        if (background instanceof ShapeDrawable)
            ((ShapeDrawable) background).getPaint().setColor(ContextCompat.getColor(context, color));
        else if (background instanceof GradientDrawable)
            ((GradientDrawable) background).setColor(ContextCompat.getColor(context, color));
        else if (background instanceof ColorDrawable)
            ((ColorDrawable) background).setColor(ContextCompat.getColor(context, color));
    }

    private void setColor(EditText imageView, int color) {
        Drawable background = imageView.getBackground();

        if (background instanceof ShapeDrawable)
            ((ShapeDrawable) background).getPaint().setColor(ContextCompat.getColor(context, color));
        else if (background instanceof GradientDrawable)
            ((GradientDrawable) background).setColor(ContextCompat.getColor(context, color));
        else if (background instanceof ColorDrawable)
            ((ColorDrawable) background).setColor(ContextCompat.getColor(context, color));
    }

    //endregion
}
