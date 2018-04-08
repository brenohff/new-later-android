package later.brenohff.com.later.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import later.brenohff.com.later.Models.LTUser;
import later.brenohff.com.later.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private Context context;

    private LTUser user;

    private Button login_facebook;
    private CallbackManager callbackManager = CallbackManager.Factory.create();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        context = view.getContext();

        login_facebook = (Button) view.findViewById(R.id.login_btFacebook);
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
                    user.setFace_id(object.getString("id"));
                    user.setName(object.getString("name"));
                    user.setGender(object.getString("gender"));
                    user.setLink(object.getString("link"));
                    user.setImage("https://graph.facebook.com/" + user.getFace_id() + "/picture");
                    user.setImage_long("https://graph.facebook.com/" + user.getFace_id() + "/picture?type=large");

                    ((MainActivity) context).pushFragmentWithNoStack(new ProfileFragment(), "ProfileFragment");
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

}
