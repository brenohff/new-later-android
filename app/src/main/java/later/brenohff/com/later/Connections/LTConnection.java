package later.brenohff.com.later.Connections;

import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by breno on 07/08/2017.
 */

public class LTConnection {
        private static final String API_BASE = "http://10.0.2.2:8080/";
//    private static final String API_BASE = "http://later-backend.herokuapp.com/";
        public static final String SOCKET = "ws://192.168.0.22:8080/event/websocket";
//    public static final String SOCKET = "ws://later-backend.herokuapp.com/event/websocket";

    private static Retrofit builder = new Retrofit.Builder()
            .baseUrl(API_BASE)
            .client(new OkHttpClient())
//            .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create()))
            .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create()))
            .build();

    public static <S> S createService(Class<S> serviceClass) {
        return builder.create(serviceClass);
    }
}
