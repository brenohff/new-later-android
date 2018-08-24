package later.brenohff.com.later.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import later.brenohff.com.later.Activities.MainActivity;
import later.brenohff.com.later.Connections.LTConnection;
import later.brenohff.com.later.Connections.LTRequests;
import later.brenohff.com.later.Memory.LTMainData;
import later.brenohff.com.later.Models.LTUser;
import later.brenohff.com.later.Others.SaveUserOnDevice;
import later.brenohff.com.later.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by breno.franco on 23/08/2018.
 */
public class LoginFragment extends Fragment {

    private Context context;

    private LTUser user;

    private Button login_facebook;
    private ImageView login_background;
    private CallbackManager callbackManager = CallbackManager.Factory.create();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        context = view.getContext();

        login_facebook = (Button) view.findViewById(R.id.login_btFacebook);
        login_background = (ImageView) view.findViewById(R.id.login_background);

        //TODO DESCOMENTAR ESSA LINHA.
//        Picasso.get().load(R.drawable.background).resize(500, 500).into(login_background);

        login_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginFacebook();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loginFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile, email, user_friends, user_birthday"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getUserInfo(loginResult);
            }

            @Override
            public void onCancel() {
                ((MainActivity) context).showToast("Login cancelado.");
            }

            @Override
            public void onError(FacebookException error) {
                ((MainActivity) context).showToast("Erro ao realizar login");
            }
        });
    }

    private void getUserInfo(LoginResult loginResult) {
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                user = new LTUser();
                try {
                    user.setBirthday(object.getString("birthday"));
                    user.setEmail(object.getString("email"));
                    user.setId(object.getString("id"));
                    user.setName(object.getString("name"));
                    user.setGender(object.getString("gender"));
                    user.setLink(object.getString("link"));
                    user.setImage("https://graph.facebook.com/" + user.getId() + "/picture");
                    user.setImage_long("https://graph.facebook.com/" + user.getId() + "/picture?type=large");

                    saveUser(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,gender,birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void saveUser(final LTUser user) {
        LTRequests requests = LTConnection.createService(LTRequests.class);
        Call<Void> call = requests.registrarUsuario(user);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    LTMainData.getInstance().setUser(user);
                    ((MainActivity) context).showToast("Bem vindo, " + user.getName());
                    ((MainActivity) context).pushFragmentWithNoStack(new ProfileFragment(), "ProfileFragment");
                    SaveUserOnDevice saveUserOnDevice = new SaveUserOnDevice(user);
                    saveUserOnDevice.saveUser(context);
                } else if (response.code() == 409) {
                    LTMainData.getInstance().setUser(user);
                    ((MainActivity) context).showToast("Olá novamente, " + user.getName());
                    ((MainActivity) context).pushFragmentWithNoStack(new ProfileFragment(), "ProfileFragment");
                    SaveUserOnDevice saveUserOnDevice = new SaveUserOnDevice(user);
                    saveUserOnDevice.saveUser(context);
                } else {
                    LoginManager.getInstance().logOut();
                    ((MainActivity) context).showToast("Não foi possível realizar login.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                ((MainActivity) context).showToast("Erro ao realizar login.");
            }
        });
    }

}
