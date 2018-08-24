package later.brenohff.com.later.Fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

import later.brenohff.com.later.Activities.MainActivity;
import later.brenohff.com.later.Connections.LTConnection;
import later.brenohff.com.later.Connections.LTRequests;
import later.brenohff.com.later.Models.LTEvent;
import later.brenohff.com.later.Others.LocationUtil;
import later.brenohff.com.later.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by breno.franco on 23/08/2018.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback, LocationUtil.LocationListenerUtil {

    private static View view;
    private GoogleMap mMap;
    private Context context;

    private Map<String, LTEvent> mMarkers = new HashMap<>();

    private MarkerOptions userMarker;
    private LocationUtil locationUtil;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_maps, container, false);
        } catch (InflateException e) {
            Log.d("MapsFragment", e.getMessage());
        }

        context = Objects.requireNonNull(view).getContext();
        locationUtil = new LocationUtil(context, this);

        getEvents();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void getEvents() {
        LTRequests requests = LTConnection.createService(LTRequests.class);
        Call<List<LTEvent>> call = requests.getPublic();
        call.enqueue(new Callback<List<LTEvent>>() {
            @Override
            public void onResponse(@NonNull Call<List<LTEvent>> call, @NonNull Response<List<LTEvent>> response) {
                for (LTEvent event : Objects.requireNonNull(response.body())) {
                    setEventsMarkers(event);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<LTEvent>> call, @NonNull Throwable t) {

            }
        });
    }

    public void setUserLocation(double lat, double lng) {
        if (userMarker == null) {
            userMarker = new MarkerOptions().position(new LatLng(lat, lng)).title("Sua posição");
            userMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.outline_person_pin_circle_black_48));
            if (mMap != null)
                mMap.addMarker(userMarker);
        }

        if (mMap != null)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 12));
    }

    public void setEventsMarkers(LTEvent event) {
        if (mMap != null) {
            Marker mkr = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(event.getLat(), event.getLon()))
                    .title(event.getTitle()));
            mMarkers.put(mkr.getId(), event);
            mMap.setOnInfoWindowClickListener(marker -> {
                //TODO TERMINAR ESSA PARTE!
//                EventFragment eventFragment = new EventFragment();
//                Bundle bundle = new Bundle();
//                eventFragment.setArguments(bundle);
//                ((MainActivity) context).pushFragmentWithStack(eventFragment, "EventFragment");
                Toast.makeText(context, "Função em desenvolvimento", Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public void availableLocation() {
        setUserLocation(locationUtil.getLatitude(), locationUtil.getLongitude());
    }

    @Override
    public void onStart() {
        super.onStart();
        locationUtil.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        locationUtil.resume();
    }

    @Override
    public void onStop() {
        super.onStop();
        locationUtil.stop();
    }

    @Override
    public void onPause() {
        super.onPause();
        locationUtil.pause();
    }
}
