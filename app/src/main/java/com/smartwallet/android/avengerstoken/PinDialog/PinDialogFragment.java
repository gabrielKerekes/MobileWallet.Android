package com.mobilewallet.android.avengerstoken.PinDialog;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mobilewallet.android.R;

/**
 * Created by GabrielK on 20-Feb-17.
 */

public class PinDialogFragment extends DialogFragment
{
    private PinDialogResultListener resultListener;

    private EditText pinEditText;

    public static PinDialogFragment newInstance(Context context) {
        PinDialogFragment newInstance = new PinDialogFragment();

        if(context instanceof PinDialogResultListener) {
            newInstance.setResultListener((PinDialogResultListener) context);
        }

        return newInstance;
    }

    public void setResultListener(PinDialogResultListener resultListener){
        this.resultListener = resultListener;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_pin, container, false);

        Button mOkButton = (Button) view.findViewById(R.id.okButton);
        Button mCancelButton = (Button) view.findViewById(R.id.cancelButton);

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(resultListener != null) {
                    String pin = pinEditText.getText().toString();
                    resultListener.onPinDialogOkButtonClicked(pin);
                    PinDialogFragment.this.dismiss();
                }
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(resultListener != null) {
                    resultListener.onPinDialogCancelButtonClicked();
                    PinDialogFragment.this.dismiss();
                }
            }
        });

        pinEditText = (EditText) view.findViewById(R.id.pin_edit_text);

        return view;
    }
}
