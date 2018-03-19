package com.jayeen.driver.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.jayeen.driver.R;

/**
 * Created by user on 4/1/2016.
 */
public class MyFontTextViewCustom extends TextView {
    private static final String TAG = "TextView";

    private Typeface typeface;
    public MyFontTextViewCustom(Context context) {
        super(context);
    }
    public MyFontTextViewCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public MyFontTextViewCustom(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.app);
        String customFont = a.getString(R.styleable.app_customFont);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    private boolean setCustomFont(Context ctx, String asset) {
        try {
            if (typeface == null) {
                Log.i(TAG, "asset:: " + "fonts/" + asset);
                typeface = Typeface.createFromAsset(ctx.getAssets(),
                        "fonts/OPENSANS-REGULAR.ttf");
//                        "fonts/SIFONN_BASIC.otf");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Could not get typeface: " + e.getMessage());
            return false;
        }

        setTypeface(typeface);
        return true;
    }
}
