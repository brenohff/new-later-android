package later.brenohff.com.later.Fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.Switch;
import com.soundcloud.android.crop.Crop;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import later.brenohff.com.later.Activities.MainActivity;
import later.brenohff.com.later.Connections.LTConnection;
import later.brenohff.com.later.Connections.LTRequests;
import later.brenohff.com.later.Memory.LTMainData;
import later.brenohff.com.later.Models.LTCategory;
import later.brenohff.com.later.Models.LTEvent;
import later.brenohff.com.later.Others.MonetaryMask;
import later.brenohff.com.later.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateEventFragment extends Fragment implements View.OnClickListener {

    private Context context;

    private LTEvent ltEvent;
    private MonetaryMask monetaryMask;

    private Button bt_register, bt_upload;
    private ImageButton bt_calendario, bt_hora, bt_local;
    private TextView data_texto, hora_texto, local_texto;
    private TagView tagView;
    private List<Tag> tagList;
    private List<LTCategory> categoriesList;
    private Switch modo;
    private MaterialEditText titulo_et, descricao_et, valor_et;
    private boolean isPrivate = false;

    private Double lat, lon;
    private int PLACE_PICKER_REQUEST = 1;
    private final int SELECT_PHOTO = 2;

    private Uri eventImage = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);
        context = view.getContext();

        castFields(view);

        return view;
    }

    private void castFields(View view) {
        tagView = (TagView) view.findViewById(R.id.tag_group);
        bt_calendario = (ImageButton) view.findViewById(R.id.fragment_event_register_calendario);
        bt_hora = (ImageButton) view.findViewById(R.id.fragment_event_register_horario);
        bt_local = (ImageButton) view.findViewById(R.id.fragment_event_register_localizacao);
        bt_register = (Button) view.findViewById(R.id.fragment_event_register_register);
        bt_upload = (Button) view.findViewById(R.id.fragment_event_register_uploadImage);

        modo = (Switch) view.findViewById(R.id.fragment_event_register_switch);

        data_texto = (TextView) view.findViewById(R.id.fragment_event_register_data);
        hora_texto = (TextView) view.findViewById(R.id.fragment_event_register_hora);
        local_texto = (TextView) view.findViewById(R.id.fragment_event_register_local);
        titulo_et = (MaterialEditText) view.findViewById(R.id.fragment_event_register_titulo);
        descricao_et = (MaterialEditText) view.findViewById(R.id.fragment_event_register_descricao);
        valor_et = (MaterialEditText) view.findViewById(R.id.fragment_event_register_valor);

        monetaryMask = new MonetaryMask(valor_et);
        valor_et.addTextChangedListener(monetaryMask);


        /**
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

        /**
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
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                beginCrop(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
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

//        if (local_texto.getText().toString().equals("Local")) {
//            b = false;
//            local_texto.setError("Insira uma local");
//        }

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
            public void onResponse(Call<List<LTCategory>> call, Response<List<LTCategory>> response) {
                if (response.isSuccessful()) {

                    tagList = new ArrayList<Tag>();

                    List<LTCategory> ltCategories = response.body();
                    for (LTCategory category : ltCategories) {
                        Tag tag = new Tag(category.getName().toUpperCase());
                        tagList.add(tag);
                    }

                    mountaTagView(ltCategories);

                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<LTCategory>> call, Throwable t) {
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

        tagView.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int i) {
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
            }
        });
    }

    private void saveEvent() {
        ltEvent = new LTEvent();
        ltEvent.setCategories(categoriesList);
        ltEvent.setStatus("avaliacao");
        ltEvent.setPrivate(isPrivate);
        ltEvent.setLat(lat);
        ltEvent.setLon(lon);
        ltEvent.setTitle(titulo_et.getText().toString());
        ltEvent.setDescription(descricao_et.getText().toString());
        ltEvent.setLocale(local_texto.getText().toString());
        ltEvent.setPrice(monetaryMask.valorSemMascara(valor_et.getText().toString()));
        ltEvent.setDate(data_texto.getText().toString());
        ltEvent.setHour(hora_texto.getText().toString());
        ltEvent.setUser(LTMainData.getInstance().getUser());

        Random r = new Random();
        ltEvent.setImage(categoriesList.get(r.nextInt(categoriesList.size())).getUrl());

        final LTRequests requests = LTConnection.createService(LTRequests.class);
        Call<Void> call = requests.registerEvent(ltEvent);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Evento inserido com sucesso.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Não foi possível inserir evento - " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Erro ao conectar com o servidor.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //region IMAGE PICKER

    private void beginCrop(Uri source) {
        eventImage = Uri.fromFile(new File(context.getCacheDir(), "cropped"));
        Crop.of(source, eventImage).asSquare().start(context, this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Log.d("CREATEEVENT", "");
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(context, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void startImagePickerActivity() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    //endregion

    //region PLACE PICKER
    private void startPlacePickerActivity() {
        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();

        try {
            Intent intent = intentBuilder.build(getActivity());
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
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        data_texto.setText(String.format(Locale.getDefault(), "%s/%s/%s",
                                (dayOfMonth < 10) ? "0" + Integer.toString(dayOfMonth) : Integer.toString(dayOfMonth),
                                (monthOfYear + 1 < 10) ? "0" + Integer.toString(monthOfYear + 1) : Integer.toString(monthOfYear + 1),
                                year));
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setVersion(DatePickerDialog.Version.VERSION_1);
        dpd.setAccentColor(context.getResources().getColor(R.color.floatingButtonColor));
        dpd.setMinDate(now);
        dpd.show(((MainActivity) getContext()).getFragmentManager(), "");
    }

    private void timePicker() {
        Calendar now = Calendar.getInstance();

        TimePickerDialog tpd = TimePickerDialog.newInstance(
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                        hora_texto.setText(String.format(Locale.getDefault(), "%s:%s",
                                (hourOfDay < 10) ? "0" + Integer.toString(hourOfDay) : Integer.toString(hourOfDay),
                                (minute < 10) ? "0" + Integer.toString(minute) : Integer.toString(minute)));
                    }
                },
                now.getTime().getHours(),
                now.getTime().getMinutes(),
                now.getTime().getSeconds(),
                false
        );
        tpd.setAccentColor(context.getResources().getColor(R.color.floatingButtonColor));
        tpd.show(((MainActivity) getContext()).getFragmentManager(), "");

    }

    //endregion

    //region UPLOAD IMAGE
    //endregion


}
