package later.brenohff.com.later.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.Switch;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import later.brenohff.com.later.Activities.MainActivity;
import later.brenohff.com.later.Models.LTEvent;
import later.brenohff.com.later.Others.MonetaryMask;
import later.brenohff.com.later.R;

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
    private List<String> categoriesList;
    private Switch modo;
    private boolean isPrivate = false;

    private MaterialEditText titulo_et, descricao_et, valor_et;


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

        modo.setOnClickListener(this);
        data_texto.setOnClickListener(this);
        hora_texto.setOnClickListener(this);
        local_texto.setOnClickListener(this);
        bt_calendario.setOnClickListener(this);
        bt_register.setOnClickListener(this);
        bt_upload.setOnClickListener(this);
        bt_hora.setOnClickListener(this);
        bt_local.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_event_register_switch:
                break;

            case R.id.fragment_event_register_local:
                break;

            case R.id.fragment_event_register_localizacao:
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

            case R.id.fragment_event_register_register:
                break;

            case R.id.fragment_event_register_uploadImage:
                break;
        }
    }

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
}
