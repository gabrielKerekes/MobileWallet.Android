package com.mobilewallet.android.avengerstoken.PinDialog;

/**
 * Created by GabrielK on 20-Feb-17.
 */

public interface PinDialogResultListener
{
    void onPinDialogOkButtonClicked(String pin);
    void onPinDialogCancelButtonClicked();
}
