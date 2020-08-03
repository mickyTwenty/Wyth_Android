package com.seatus.Views;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seatus.R;
import com.seatus.Utils.AppConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by rohail on 3/28/2018.
 */

public class NumberPicker extends FrameLayout {

    Context context;
    @BindView(R.id.btn_seat_minus)
    ImageView btnSeatMinus;
    @BindView(R.id.txt_seats)
    TextView txtNumber;
    @BindView(R.id.btn_seat_plus)
    ImageView btnSeatPlus;

    MutableLiveData<Integer> livedata;

    public NumberPicker(Context context) {
        super(context);
        this.context = context;
        initUi();
    }

    public NumberPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initUi();
    }

    private void initUi() {
        inflate(getContext(), R.layout.view_numberpicker, this);
        ButterKnife.bind(this);
        livedata = new MutableLiveData<>();
        livedata.setValue(0);
    }

    @OnClick({R.id.btn_seat_minus, R.id.btn_seat_plus})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_seat_minus:
                int seat = Integer.parseInt(txtNumber.getText().toString());
                if (seat > 1) {
                    seat--;
                    txtNumber.setText(String.format("%02d", seat));
                    livedata.setValue(seat);
                }
                break;
            case R.id.btn_seat_plus:
                int available_seat = Integer.parseInt(txtNumber.getText().toString());
                if (available_seat < AppConstants.MAX_SEAT_LIMIT) {
                    available_seat++;
                    txtNumber.setText(String.format("%02d", available_seat));
                    livedata.setValue(available_seat);
                }
        }
    }

    public MutableLiveData<Integer> getObservable() {
        return livedata;
    }

    public int getSelectedNumber() {
        return Integer.parseInt(txtNumber.getText().toString());
    }

    public void setNumber(int number) {
        txtNumber.setText(String.format("%02d", number));
    }
}
