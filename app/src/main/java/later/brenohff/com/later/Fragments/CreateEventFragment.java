package later.brenohff.com.later.Fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.Switch;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import later.brenohff.com.later.Activities.MainActivity;
import later.brenohff.com.later.Connections.LTConnection;
import later.brenohff.com.later.Connections.LTRequests;
import later.brenohff.com.later.Memory.LTMainData;
import later.brenohff.com.later.Models.LTCategory;
import later.brenohff.com.later.Models.LTEvent;
import later.brenohff.com.later.Others.MonetaryMask;
import later.brenohff.com.later.R;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by breno.franco on 23/08/2018.
 */
public class CreateEventFragment extends Fragment implements View.OnClickListener {

    private Context context;

    private MonetaryMask monetaryMask;

    private TextView data_texto, hora_texto, local_texto;
    private TagView tagView;
    private List<Tag> tagList;
    private List<LTCategory> categoriesList;
    private MaterialEditText titulo_et, descricao_et, valor_et;

    private Double lat, lon;
    private int PLACE_PICKER_REQUEST = 1;
    private final int SELECT_PHOTO = 2;
    private Uri imageUri;

    private AlertDialog progressDialog;


    @Override
    public void onStop() {
        super.onStop();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);
        context = view.getContext();

        castFields(view);

        return view;
    }

    private void castFields(View view) {
        tagView = view.findViewById(R.id.tag_group);
        ImageButton bt_calendario = view.findViewById(R.id.fragment_event_register_calendario);
        ImageButton bt_hora = view.findViewById(R.id.fragment_event_register_horario);
        ImageButton bt_local = view.findViewById(R.id.fragment_event_register_localizacao);
        Button bt_register = view.findViewById(R.id.fragment_event_register_register);
        Button bt_upload = view.findViewById(R.id.fragment_event_register_uploadImage);

        Switch modo = view.findViewById(R.id.fragment_event_register_switch);

        data_texto = view.findViewById(R.id.fragment_event_register_data);
        hora_texto = view.findViewById(R.id.fragment_event_register_hora);
        local_texto = view.findViewById(R.id.fragment_event_register_local);
        titulo_et = view.findViewById(R.id.fragment_event_register_titulo);
        descricao_et = view.findViewById(R.id.fragment_event_register_descricao);
        valor_et = view.findViewById(R.id.fragment_event_register_valor);

        monetaryMask = new MonetaryMask(valor_et);
        valor_et.addTextChangedListener(monetaryMask);


        /*
         * OnClickListener
         */
        modo.setOnClickListener(this);
        data_texto.setOnClickListener(this);
        hora_texto.setOnClickListener(this);
        local_texto.setOnClickListener(this);
        bt_calendario.setOnClickListener(this);
        bt_register.setOnClickListener(this);
        bt_upload.setOnClickListener(this);
        bt_hora.setOnClickListener(this);
        bt_local.setOnClickListener(this);

        /*
         * Chamada de métodos.
         */
        getCategories();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_event_register_switch:
                break;

            case R.id.fragment_event_register_local:
                startPlacePickerActivity();
                break;

            case R.id.fragment_event_register_localizacao:
                startPlacePickerActivity();
                break;

            case R.id.fragment_event_register_data:
                datePicker();
                break;

            case R.id.fragment_event_register_calendario:
                datePicker();
                break;

            case R.id.fragment_event_register_hora:
                timePicker();
                break;

            case R.id.fragment_event_register_horario:
                timePicker();
                break;

            case R.id.fragment_event_register_uploadImage:
                startImagePickerActivity();
                break;

            case R.id.fragment_event_register_register:
                if (validateFields()) {
                    saveEvent();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            displaySelectedPlaceFromPlacePicker(data);
        }
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK) {
            imageUri = data.getData();
//            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
        }
    }

    private boolean validateFields() {
        boolean b = true;

        if (titulo_et.getText().toString().isEmpty()) {
            b = false;
            titulo_et.setError("Insira um título");
        }

        if (descricao_et.getText().toString().isEmpty()) {
            b = false;
            descricao_et.setError("Insira uma descrição");
        }

        if (local_texto.getText().toString().equals("Local")) {
            b = false;
            local_texto.setError("Insira uma local");
        }

        if (valor_et.getText().toString().isEmpty()) {
            b = false;
            valor_et.setError("Insira uma valor!");
        }

        if (data_texto.getText().toString().equals("Data")) {
            b = false;
            data_texto.setError("Escolha uma data!");
        }

        if (hora_texto.getText().toString().equals("Horário")) {
            b = false;
            hora_texto.setError("Escolha um horário!");
        }

        if (imageUri == null) {
            b = false;
            Toast.makeText(context, "Insira uma foto.", Toast.LENGTH_SHORT).show();
        }

        if (categoriesList.isEmpty()) {
            b = false;
            Toast.makeText(context, "Escolha pelo menos 1 categoria", Toast.LENGTH_SHORT).show();
        }

        return b;
    }

    private void getCategories() {
        LTRequests requests = LTConnection.createService(LTRequests.class);
        Call<List<LTCategory>> call = requests.getCategories();
        call.enqueue(new Callback<List<LTCategory>>() {
            @Override
            public void onResponse(@NonNull Call<List<LTCategory>> call, @NonNull Response<List<LTCategory>> response) {
                if (response.isSuccessful()) {

                    tagList = new ArrayList<>();

                    List<LTCategory> ltCategories = response.body();
                    for (LTCategory category : Objects.requireNonNull(ltCategories)) {
                        Tag tag = new Tag(category.getName().toUpperCase());
                        tagList.add(tag);
                    }

                    mountaTagView(ltCategories);

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

    private void mountaTagView(final List<LTCategory> ltCategories) {
        final boolean[] isChecked = new boolean[tagList.size()];
        categoriesList = new ArrayList<>();
        tagView.addTags(tagList);

        for (int i = 0; i < isChecked.length; i++) {
            isChecked[i] = false;
            if (tagView.getChildAt(i) != null) {
                tagView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
            }
        }

        tagView.setOnTagClickListener((tag, i) -> {
            if (!isChecked[i]) {
                isChecked[i] = true;
                for (LTCategory category : ltCategories) {
                    if (tag.text.equals(category.getName().toUpperCase())) {
                        tagView.getChildAt(i).setBackgroundColor(Color.parseColor(category.getBaseColor()));
                        categoriesList.add(category);
                    }
                }
            } else {
                isChecked[i] = false;
                tagView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                for (LTCategory category : ltCategories) {
                    if (tag.text.equals(category.getName().toUpperCase())) {
                        categoriesList.remove(category);
                    }
                }
            }
        });
    }

    private void saveEvent() {
        LTEvent ltEvent = new LTEvent();
        ltEvent.setCategories(categoriesList);
        ltEvent.setStatus("avaliacao");
        ltEvent.setPrivate(false);
        ltEvent.setLat(lat);
        ltEvent.setLon(lon);
        ltEvent.setTitle(titulo_et.getText().toString());
        ltEvent.setDescription(descricao_et.getText().toString());
        ltEvent.setLocale(local_texto.getText().toString());
        ltEvent.setPrice(monetaryMask.valorSemMascara(valor_et.getText().toString()));
        ltEvent.setDate(data_texto.getText().toString());
        ltEvent.setHour(hora_texto.getText().toString());
        ltEvent.setUser(LTMainData.getInstance().getUser());

        //Create file from image path
        File originalFile = new File(getPath(imageUri));

        //Parsing oject to json
        Gson gson = new Gson();
        String eventJson = gson.toJson(ltEvent);

        //Create multipart
        RequestBody eventPart = RequestBody.create(MultipartBody.FORM, eventJson);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), originalFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", originalFile.getName(), requestBody);

        progressDialog = new SpotsDialog.Builder()
                .setContext(context)
                .setMessage("Registrando evento")
                .setCancelable(false)
                .build();
        progressDialog.show();

        LTRequests requests = LTConnection.createService(LTRequests.class);
        Call<Void> call = requests.registerEvent(eventPart, body);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(context, "Evento criado com sucesso.", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(context, response.code() + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();
            }
        });
    }

    //region IMAGE PICKER

    private void startImagePickerActivity() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PHOTO);
    }

    //endregion

    //region PLACE PICKER
    private void startPlacePickerActivity() {
        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();

        try {
            Intent intent = intentBuilder.build(Objects.requireNonNull(getActivity()));
            startActivityForResult(intent, PLACE_PICKER_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displaySelectedPlaceFromPlacePicker(Intent data) {
        Place placeSelected = PlacePicker.getPlace(data, context);

        lat = placeSelected.getLatLng().latitude;
        lon = placeSelected.getLatLng().longitude;

        local_texto.setText(placeSelected.getAddress());
    }
    //endregion

    //region DATE AND TIME PICKER

    private void datePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                (view, year, monthOfYear, dayOfMonth) -> data_texto.setText(String.format(Locale.getDefault(), "%s/%s/%s",
                        (dayOfMonth < 10) ? "0" + Integer.toString(dayOfMonth) : Integer.toString(dayOfMonth),
                        (monthOfYear + 1 < 10) ? "0" + Integer.toString(monthOfYear + 1) : Integer.toString(monthOfYear + 1),
                        year)),
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setVersion(DatePickerDialog.Version.VERSION_1);
        dpd.setAccentColor(context.getResources().getColor(R.color.floatingButtonColor));
        dpd.setMinDate(now);
        dpd.show(((MainActivity) Objects.requireNonNull(getContext())).getFragmentManager(), "");
    }

    private void timePicker() {
        Calendar now = Calendar.getInstance();

        TimePickerDialog tpd = TimePickerDialog.newInstance(
                (view, hourOfDay, minute, second) -> hora_texto.setText(String.format(Locale.getDefault(), "%s:%s",
                        (hourOfDay < 10) ? "0" + Integer.toString(hourOfDay) : Integer.toString(hourOfDay),
                        (minute < 10) ? "0" + Integer.toString(minute) : Integer.toString(minute))),
                now.getTime().getHours(),
                now.getTime().getMinutes(),
                now.getTime().getSeconds(),
                false
        );
        tpd.setAccentColor(context.getResources().getColor(R.color.floatingButtonColor));
        tpd.show(((MainActivity) Objects.requireNonNull(getContext())).getFragmentManager(), "");

    }

    //endregion

    private String getPath(Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = Objects.requireNonNull(getActivity()).getContentResolver().query(uri, filePathColumn, null, null, null);
        Objects.requireNonNull(cursor).moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }


}
