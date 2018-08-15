package later.brenohff.com.later.Connections;

import java.util.List;

import later.brenohff.com.later.Models.LTCategory;
import later.brenohff.com.later.Models.LTChat;
import later.brenohff.com.later.Models.LTEvent;
import later.brenohff.com.later.Models.LTUser;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by breno on 07/08/2017.
 */

public interface LTRequests {

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ///// EVENTS
    ////////////////////////////////////////////////////////////////////////////////////////////////////
   @Multipart
    @POST("events/saveEvent")
    Call<Void> registerEvent(
            @Part("event") RequestBody event,
            @Part MultipartBody.Part file
    );

    @GET("events/getEventsByUser")
    Call<List<LTEvent>> getEventsByUser(@Query("user_id") String user_id);

    @GET("events/getPublic")
    Call<List<LTEvent>> getPublic();

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ///// USERS
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    @POST("users/saveUser")
    Call<Void> registrarUsuario(@Body LTUser user);

    @GET("users/getUser")
    Call<LTUser> getUserByFaceID(@Query("id") String id);

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ///// CATEGORIES
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    @GET("categories/getAll")
    Call<List<LTCategory>> getCategories();

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ///// COMMENTS
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    @GET("chat/getChatByEventId")
    Call<List<LTChat>> getChatByEventId(@Query("eventId") String eventId);


}
