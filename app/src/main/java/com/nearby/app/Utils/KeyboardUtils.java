package com.nearby.app.Utils;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import static com.nearby.app.ChatRoomActivity.mRootContainer;

public class KeyboardUtils  {
    public static void hideKeyboard(Activity activity, View view){
        InputMethodManager mInputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    public static void showSnackbar(String message){
        if (mRootContainer != null){
            Snackbar.make(mRootContainer, message, Snackbar.LENGTH_SHORT).show();
        }
    }
}
