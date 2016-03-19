package org.glucosio.android.tools;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;


public class NotDismissableEditText extends EditText {

    public NotDismissableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        return true;
        //return super.onKeyPreIme(keyCode, event);
    }
}
