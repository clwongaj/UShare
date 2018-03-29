package lix5.ushare;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static lix5.ushare.MainActivity.DROPOFF_PLACE_AUTOCOMPLETE_REQUEST_CODE;
import static lix5.ushare.MainActivity.PICKUP_PLACE_AUTOCOMPLETE_REQUEST_CODE;

public class CreateActivity extends AppCompatActivity {
    private EditText seats;
    private ImageView add, remove;
    private ImageButton taxiButton, carButton;
    private CheckBox boys, girls, isRequest;
    private TextView createPickup, createDropoff, createTime;
    private String TAG = "Create Autocomplete";
    String date_time = "";
    int mYear, mMonth, mDay, mHour, mMinute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        seats = (EditText) findViewById(R.id.number_of_seats);
        add = (ImageView) findViewById(R.id.add_seat);
        remove = (ImageView) findViewById(R.id.remove_seat);
        taxiButton = (ImageButton) findViewById(R.id.taxi_button);
        carButton = (ImageButton) findViewById(R.id.car_button);
        boys = (CheckBox) findViewById(R.id.boys);
        girls = (CheckBox) findViewById(R.id.girls);
        isRequest = (CheckBox) findViewById(R.id.isRequest);

        setupRouteInputBar();


        seats.setFilters(new InputFilter[]{new InputFilterMinMax("1", "10")});
        add.setOnClickListener(v->{
            if (!String.valueOf(seats.getText()).equals("10")) {
                seats.setText(String.valueOf(Integer.parseInt(seats.getText().toString()) + 1));
                remove.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove_circle_outline_black_18dp));
                if (String.valueOf(seats.getText()).equals("10"))
                    add.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_circle_outline_gray_18dp));
            }
        });
        remove.setOnClickListener(v->{
            if (!String.valueOf(seats.getText()).equals("1")){
                seats.setText(String.valueOf(Integer.parseInt(seats.getText().toString())-1));
                add.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_circle_outline_black_18dp));
                if (String.valueOf(seats.getText()).equals("1"))
                 remove.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove_circle_outline_gray_18dp));
            }
        });


    }

    private void setupRouteInputBar() {
        // Setup autocomplete
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setCountry("HK").build();
        createPickup = (TextView) findViewById(R.id.create_pick_up);
        createPickup.setOnClickListener(v->{
            try {
                Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).setFilter(typeFilter).build(CreateActivity.this);
                startActivityForResult(intent, PICKUP_PLACE_AUTOCOMPLETE_REQUEST_CODE);
            } catch (GooglePlayServicesRepairableException e) {
                // TODO: Handle the error.
            } catch (GooglePlayServicesNotAvailableException e) {
                // TODO: Handle the error.
            }
        });
        createDropoff = (TextView) findViewById(R.id.create_drop_off);
        createDropoff.setOnClickListener(v->{
            try {
                Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).setFilter(typeFilter).build(CreateActivity.this);
                startActivityForResult(intent, DROPOFF_PLACE_AUTOCOMPLETE_REQUEST_CODE);
            } catch (GooglePlayServicesRepairableException e) {
                // TODO: Handle the error.
            } catch (GooglePlayServicesNotAvailableException e) {
                // TODO: Handle the error.
            }
        });
        createTime = (TextView) findViewById(R.id.create_time);
        createTime.setOnClickListener(v-> datePicker());

        // Set icons
        createPickup.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Drawable img = CreateActivity.this.getResources().getDrawable(
                                R.drawable.current_location);
                        img.setBounds(0, 0, img.getIntrinsicWidth() * createPickup.getMeasuredHeight() / img.getIntrinsicHeight(), createPickup.getMeasuredHeight());
                        createPickup.setCompoundDrawables(img, null, null, null);
                        createPickup.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
        createDropoff.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Drawable img = CreateActivity.this.getResources().getDrawable(
                                R.drawable.destination);
                        img.setBounds(0, 0, img.getIntrinsicWidth() * createDropoff.getMeasuredHeight() / img.getIntrinsicHeight(), createDropoff.getMeasuredHeight());
                        createDropoff.setCompoundDrawables(img, null, null, null);
                        createDropoff.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
        createTime.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Drawable img = CreateActivity.this.getResources().getDrawable(
                                R.drawable.clock);
                        img.setBounds(0, 0, img.getIntrinsicWidth() * createTime.getMeasuredHeight() / img.getIntrinsicHeight(), createTime.getMeasuredHeight());
                        createTime.setCompoundDrawables(img, null, null, null);
                        createTime.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
    }

    public void onTypeSelected(View view){
        if(view.getId()==R.id.taxi_button){
            taxiButton.setImageDrawable(getResources().getDrawable(R.drawable.taxi_sign_icon));
            carButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_car_gray_48dp));
        }
        if(view.getId()==R.id.car_button){
            carButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_car_black_48dp));
            taxiButton.setImageDrawable(getResources().getDrawable(R.drawable.taxi_sign_gray));
            //TODO if no car plate -> isRequest.setChecked(true);
        }
    }

    private void datePicker() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this, (DatePickerDialog.OnDateSetListener) (DatePicker view, int year, int monthOfYear, int dayOfMonth) -> {
            Date date = new Date(year-1900, monthOfYear, dayOfMonth);
            timePicker(date);
        }, mYear, mMonth, mDay);
        dpd.getDatePicker().setMinDate(c.getTimeInMillis());
        c.add(Calendar.YEAR, 1);
        long afterOneYearInMillis = c.getTimeInMillis();
        dpd.getDatePicker().setMaxDate(afterOneYearInMillis);
        dpd.show();

    }

    private void timePicker(Date date) {
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, 30); // Set the dialog init time 30 minutes after
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        c.add(Calendar.MINUTE, -30);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (TimePicker view, int hourOfDay, int minute) -> {
            mHour = hourOfDay;
            mMinute = minute;
            date.setHours(hourOfDay);
            date.setMinutes(minute);
            if(date.before(c.getTime()))
                new AlertDialog.Builder(CreateActivity.this).setMessage("The pick up time can only be future.").setPositiveButton("OK", (dialog, which) -> datePicker()).show();
            else
                date_time = (date.getYear()+1900 == c.get(Calendar.YEAR) ? new SimpleDateFormat("EE, dd MMMM, HH:mm").format(date) : new SimpleDateFormat("EE, dd MMMM yyyy, HH:mm").format(date));
            createTime.setText(date_time);

        }, mHour, mMinute, true);
        timePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICKUP_PLACE_AUTOCOMPLETE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    CharSequence result = place.getAddress();//.toString() + place.getName();
                    createPickup.setText(result);
                    Log.i(TAG, "Place: " + place.getName());
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    // TODO: Handle the error.
                    Log.i(TAG, status.getStatusMessage());

                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
                break;
            case DROPOFF_PLACE_AUTOCOMPLETE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    CharSequence result = place.getAddress();//.toString() + place.getName();
                    createDropoff.setText(result);
                    Log.i(TAG, "Place: " + place.getName());
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    // TODO: Handle the error.
                    Log.i(TAG, status.getStatusMessage());

                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
                break;
        }
    }

    public void onCheckboxClicked(View view) {
        if(view.getId()==R.id.boys)
            girls.setChecked(false);
        if(view.getId()==R.id.girls)
            boys.setChecked(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent();
            intent = new Intent(CreateActivity.this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public class InputFilterMinMax implements InputFilter {
        private int min, max;

        public InputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public InputFilterMinMax(String min, String max) {
            this.min = Integer.parseInt(min);
            this.max = Integer.parseInt(max);
        }
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                int input = Integer.parseInt(dest.toString() + source.toString());
                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException nfe) { }
            return "";
        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }

}