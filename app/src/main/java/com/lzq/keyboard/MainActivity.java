package com.lzq.keyboard;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.hjq.toast.ToastUtils;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity implements KeyboardView.OnItemClickListener, View.OnFocusChangeListener, View.OnClickListener {
    private static final String TAG=MainActivity.class.getSimpleName();
    private KeyboardView mKeyboardView;
    private EditText mEdt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEdt= findViewById(R.id.main_edt);
        mKeyboardView=findViewById(R.id.main_keyboard_view);
        mKeyboardView.setOnKeyboardClickListener(this);
        mEdt.setInputType(InputType.TYPE_NULL);
        mEdt.setOnFocusChangeListener(this);
        mEdt.setOnClickListener(this);
    }






    @Override
    public void onKeyClick(View v, int key) {
        Log.d(TAG, "----onKeyClick: "+key);
        mEdt.setText(mEdt.getText().append(String.valueOf(key)));
    }

    @Override
    public void onDeleteClick() {
        Log.d(TAG, "----onDeleteClick ");
        mEdt.setText(TextUtils.isEmpty(mEdt.getText().toString())?"":
                mEdt.getText().toString().trim().substring(0,mEdt.getText().toString().trim().length()-1));
    }

    @Override
    public void onPointClick() {
        Log.d(TAG, "----onPointClick");
        mEdt.getText().append('.');
    }

    @Override
    public void onHideClick(View v) {
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus){
            if (mKeyboardView.isShow()==false){
                mKeyboardView.show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_edt:
                KeyboardUtils.hideSoftInput(this);
                if (mKeyboardView.isShow()==false){
                    mKeyboardView.show();
                }
                break;
        }
    }
}
