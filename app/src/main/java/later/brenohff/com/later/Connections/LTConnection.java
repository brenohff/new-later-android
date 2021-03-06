package later.brenohff.com.later.Connections;

import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by breno on 07/08/2017.
 */

public class LTConnection {

//    private static final String API_BASE = "http://10.0.2.2:8080/";
//    public static final String SOCKET = "ws://10.0.2.2:8080/event/websocket";
//
//    private static final String API_BASE = "http://later-backend.herokuapp.com/";
//    public static final String SOCKET = "ws://later-backend.herokuapp.com/event/websocket";
//
//    private static final String API_BASE = "http://192.168.0.22:8080/";
//    public static final String SOCKET = "ws://192.168.0.22:8080/event/websocket";

    private static final String API_BASE = "http://142.93.192.165:8080//";
    public static final String SOCKET = "ws://142.93.192.165:8080//event/websocket";

    private static Retrofit builder = new Retrofit.Builder()
            .baseUrl(API_BASE)
            .client(new OkHttpClient().newBuilder()
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build())
            .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create()))
            .build();

    public static <S> S createService(Class<S> serviceClass) {
        return builder.create(serviceClass);
    }
}
