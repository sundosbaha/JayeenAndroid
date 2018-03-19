package com.jayeen.driver.component;



import com.jayeen.driver.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;


public class MyFontEdittextView extends EditText {

	private static final String TAG = "TextView";

	private Typeface typeface;

	public MyFontEdittextView(Context context) {
		super(context);
	}

	public MyFontEdittextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setHintTextColor(getResources().getColor(R.color.color_hint_light));
		setCustomFont(context, attrs);
	}

	public MyFontEdittextView(Context context, AttributeSet attrs, int defStyle) {
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
				typeface = Typeface.createFromAsset(ctx.getAssets(),"fonts/OPENSANS-REGULAR.ttf");
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		setTypeface(typeface);
		return true;
	}

}