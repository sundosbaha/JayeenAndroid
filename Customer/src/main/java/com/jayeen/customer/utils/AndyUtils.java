package com.jayeen.customer.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.jayeen.customer.R;
import com.jayeen.customer.interfaces.OnProgressCancelListener;
import com.jayeen.customer.newmodels.WalkerDetail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.wangyuwei.loadingview.LoadingView;


@SuppressLint("NewApi")
public class AndyUtils {
    static float density = 1;
    private static ProgressDialog mProgressDialog;
    private static Dialog mDialog;
    private static OnProgressCancelListener progressCancelListener;
    private int driverId = 0;
    private LoadingView imageview;
    static TextView tvDriverName, tvRateStar, tvTaxiModel, tvTaxiNo;
    static CircularImageView ivDriverPhoto;

    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        return display.getWidth();
    }

    public static void showSimpleProgressDialog(Context context, String title,
                                                String msg, boolean isCancelable) {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(context, title, msg);
                mProgressDialog.setCancelable(isCancelable);
            }

            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }

        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();
        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showSimpleProgressDialog(Context context) {
        showSimpleProgressDialog(context, null, "Loading...", false);
    }


    public static void removeSimpleProgressDialog() {
        try {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();

        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void showNetworkErrorMessage(final Context context) {
        Builder dlg = new Builder(context);
        dlg.setCancelable(false);
        dlg.setTitle("Error");
        dlg.setMessage("Network error has occured. Please check the network status of your phone and retry");
        dlg.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dlg.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ((Activity) context).finish();
                System.exit(0);
            }
        });
        dlg.show();
    }

    public static void showOkDialog(String title, String msg, Activity act) {
        Builder dialog = new Builder(act);
        if (title != null) {

            TextView dialogTitle = new TextView(act);
            dialogTitle.setText(title);
            dialogTitle.setPadding(10, 10, 10, 10);
            dialogTitle.setGravity(Gravity.CENTER);
            dialogTitle.setTextColor(Color.WHITE);
            dialogTitle.setTextSize(20);
            dialog.setCustomTitle(dialogTitle);

        }
        if (msg != null) {
            dialog.setMessage(msg);
        }
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dlg = dialog.show();
        TextView messageText = (TextView) dlg
                .findViewById(android.R.id.message);
        messageText.setGravity(Gravity.CENTER);

    }

    public static float getDisplayMetricsDensity(Context context) {
        density = context.getResources().getDisplayMetrics().density;

        return density;
    }

    public static int getPixel(Context context, int p) {
        if (density != 1) {
            return (int) (p * density + 0.5);
        }
        return p;
    }

    public static void Picasso(Context context, String url, ImageView view, int errorRes) {
        if(url !=null && url.isEmpty())
        Picasso.with(context).load(errorRes).error(errorRes)
                .into(view);
        else
            Picasso.with(context).load(url).error(errorRes)
                    .into(view);

    }
    public static void Picasso(Context context, ImageView view, int width,int height,String sLat,String sLng,String eLat,String eLng,int errorRes) {
        String url=String.format(Const.ServiceType.STATIC_MAP_URL,width,height,sLat,sLng,eLat,eLng,sLat,sLng,eLat,eLng);
        if(url.isEmpty())
        Picasso.with(context).load(errorRes).error(errorRes)
                .into(view);
        else
            Picasso.with(context).load(url).error(errorRes)
                    .into(view);

    }

    public static void Picasso(Context context, String url, ImageView view) {
        Picasso.with(context).load(url)
                .into(view);
    }

    public static Animation FadeAnimation(float nFromFade, float nToFade) {
        Animation fadeAnimation = new AlphaAnimation(nToFade, nToFade);

        return fadeAnimation;
    }

    public static Animation inFromRightAnimation() {
        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);

        return inFromRight;
    }

    public static Animation inFromLeftAnimation() {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);

        return inFromLeft;
    }

    public static Animation inFromBottomAnimation() {
        Animation inFromBottom = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);

        return inFromBottom;
    }

    public static Animation outToLeftAnimation() {
        Animation outToLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);

        return outToLeft;
    }

    public static Animation outToRightAnimation() {
        Animation outToRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);

        return outToRight;
    }

    public static Animation outToBottomAnimation() {
        Animation outToBottom = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f);

        return outToBottom;
    }

    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivity = (ConnectivityManager) activity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean eMailValidation(String emailstring) {
        if (null == emailstring || emailstring.length() == 0) {
            return false;
        }
        Pattern emailPattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher emailMatcher = emailPattern.matcher(emailstring);
        return emailMatcher.matches();
    }


    public static String urlBuilderForGetMethod(Map<String, String> g_map) {

        StringBuilder sbr = new StringBuilder();
        int i = 0;
        if (g_map.containsKey("url")) {
            sbr.append(g_map.get("url"));
            g_map.remove("url");
        }
        for (String key : g_map.keySet()) {
            if (i != 0) {
                sbr.append("&");
            }
            sbr.append(key + "=" + g_map.get(key));
            i++;
        }
        System.out.println("Builder url = " + sbr.toString());
        return sbr.toString();
    }

    public static int isValidate(String... fields) {
        if (fields == null) {
            return 0;
        }

        for (int i = 0; i < fields.length; i++) {
            if (TextUtils.isEmpty(fields[i])) {
                return i;
            }

        }
        return -1;
    }
    /**
     * @param msg used to return datatype
     * */
    public static void showToast(String msg, int coordinatorLayout, Activity activity) {
        try {
            Snackbar.make(activity.findViewById(coordinatorLayout), "" + msg, Snackbar.LENGTH_LONG).show();
        } catch (Exception e) {
            AppLog.Log("AndyUtils", "" + e.getMessage());
        }

    }

    public static String UppercaseFirstLetters(String str) {
        boolean prevWasWhiteSp = true;
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (Character.isLetter(chars[i])) {
                if (prevWasWhiteSp) {
                    chars[i] = Character.toUpperCase(chars[i]);
                }
                prevWasWhiteSp = false;
            } else {
                prevWasWhiteSp = Character.isWhitespace(chars[i]);
            }
        }
        return new String(chars);
    }


    public static void buttonEffect(ImageButton button, final int alpha) {

        button.setOnTouchListener(new OnTouchListener() {

            @SuppressWarnings("deprecation")
            public boolean onTouch(View v, MotionEvent event) {
                ImageButton btn = (ImageButton) v;
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN: {

                        if (Build.VERSION.SDK_INT > 15) {
                            btn.setImageAlpha(alpha);

                        } else {
                            btn.setAlpha(alpha);
                        }

                        break;
                    }
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:

                        if (Build.VERSION.SDK_INT > 15) {

                            btn.setImageAlpha(255);
                        } else {
                            btn.setAlpha(255);
                        }

                        break;
                }
                return false;
            }
        });

    }

    public static final SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    }

    public static String convertToUTF8(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static void showCustomProgressDialog(Context context, String title,
                                                boolean iscancelable,
                                                OnProgressCancelListener progressCancelListener) {
        if (mDialog != null && mDialog.isShowing())
            return;
        AndyUtils.progressCancelListener = progressCancelListener;

        mDialog = new Dialog(context, R.style.MyDialog);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.progress_bar_cancel);
        LoadingView imageView = (LoadingView) mDialog.findViewById(R.id.ivProgressBar);
        imageView.setExternalRadius(40);
        imageView.setInternalRadius(40);
        imageView.setDuration(100);
        imageView.start();
        if (!TextUtils.isEmpty(title)) {
            TextView tvTitle = (TextView) mDialog.findViewById(R.id.tvTitle);
            tvTitle.setText(title);
        }
        mDialog.findViewById(R.id.progress_walker_layout).setVisibility(View.GONE);
//        RelativeLayout includeDriver = (RelativeLayout) mDialog.findViewById(R.id.includeDriver);
//        includeDriver.setVisibility(View.GONE);
        Button btnCancel = (Button) mDialog.findViewById(R.id.btnCancel);
        if (AndyUtils.progressCancelListener == null) {
            btnCancel.setVisibility(View.GONE);
        } else {
            btnCancel.setVisibility(View.VISIBLE);
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (AndyUtils.progressCancelListener != null) {
                    AndyUtils.progressCancelListener.onProgressCancel();
                }
            }
        });
        mDialog.show();
    }

    public static void updateDriverDetailsProgress(Context context, WalkerDetail driver) {
        if (driver != null) {
            if (ivDriverPhoto != null)
                if (driver.picture != null && !driver.picture.isEmpty()) {
                    Picasso.with(context)
                            .load(driver.picture)
                            .placeholder(R.drawable.default_user)
                            .into(ivDriverPhoto);
                }
            if (tvDriverName != null)
                tvDriverName.setText(driver.firstName);
            if (tvRateStar != null)
                tvRateStar.setText(driver.rating + "");
            if (tvTaxiModel != null)
                tvTaxiModel.setText(driver.carModel);
            if (tvTaxiNo != null)
                tvTaxiNo.setText(driver.carNumber);
        }
    }

    public static void showCustomProgressDialog(Context context, String title,
                                                boolean iscancelable,
                                                OnProgressCancelListener progressCancelListener, WalkerDetail driver) {
        if (mDialog != null && mDialog.isShowing())
            return;
        AndyUtils.progressCancelListener = progressCancelListener;
        mDialog = new Dialog(context, R.style.MyDialog);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.progress_bar_cancel);
        LoadingView imageView = (LoadingView) mDialog.findViewById(R.id.ivProgressBar);
        imageView.setExternalRadius(40);
        imageView.setInternalRadius(40);
        imageView.setDuration(100);
        imageView.start();

        mDialog.findViewById(R.id.progress_walker_layout).setVisibility(driver != null ? View.VISIBLE : View.GONE);
        tvDriverName = (TextView) mDialog.findViewById(R.id.tvDriverName);
        tvRateStar = (TextView) mDialog.findViewById(R.id.tvRateStar);
        tvTaxiModel = (TextView) mDialog.findViewById(R.id.tvTaxiModel);
        tvTaxiNo = (TextView) mDialog.findViewById(R.id.tvTaxiNo);
        ivDriverPhoto = (CircularImageView) mDialog.findViewById(R.id.ivDriverPhoto);
        LinearLayout driver_details = (LinearLayout) mDialog.findViewById(R.id.driver_details);

        if (!TextUtils.isEmpty(title)) {
            TextView tvTitle = (TextView) mDialog.findViewById(R.id.tvTitle);
            tvTitle.setText(title);
        }
        Button btnCancel = (Button) mDialog.findViewById(R.id.btnCancel);
        if (AndyUtils.progressCancelListener == null) {
            btnCancel.setVisibility(View.GONE);
            driver_details.setVisibility(View.GONE);
        } else {
            btnCancel.setVisibility(View.VISIBLE);
            driver_details.setVisibility(View.VISIBLE);
            updateDriverDetailsProgress(context, driver);
//            tvDriverName.setText(driver.firstName);
//            tvRateStar.setText(driver.rating + "");
//            tvTaxiModel.setText(driver.carModel);
//            tvTaxiNo.setText(driver.carNumber);
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AndyUtils.progressCancelListener != null) {
                    AndyUtils.progressCancelListener.onProgressCancel();
                }
            }
        });
        final AnimationDrawable frameAnimation = (AnimationDrawable) imageView
                .getBackground();
        mDialog.setCancelable(iscancelable);
        imageView.post(new Runnable() {

            @Override
            public void run() {
                if (frameAnimation != null) {
                    frameAnimation.start();
                    frameAnimation.setOneShot(false);
                }
            }
        });
        mDialog.show();
    }

    public void UpdateDriverData() {

    }

    public static void Errorcancel() {

        if (AndyUtils.progressCancelListener != null) {
            AndyUtils.progressCancelListener.onProgressCancel();
        }
    }

    public static void removeCustomProgressDialog() {
        try {
            AndyUtils.progressCancelListener = null;
            if (mDialog != null) {
                // if (mProgressDialog.isShowing()) {
                mDialog.dismiss();
                mDialog = null;
                // }
            }
        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();

        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showCustomProgressRequestDialog(Context context,
                                                       String title, boolean iscancelable,
                                                       OnProgressCancelListener progressCancelListener) {
        if (mDialog != null && mDialog.isShowing())
            return;
        AndyUtils.progressCancelListener = progressCancelListener;

        mDialog = new Dialog(context, R.style.MyDialog);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.progress_bar_cancel);
        mDialog.findViewById(R.id.progress_walker_layout).setVisibility(View.GONE);
        LoadingView imageView = (LoadingView) mDialog.findViewById(R.id.ivProgressBar);
        imageView.setExternalRadius(40);
        imageView.setInternalRadius(40);
        imageView.setDuration(100);
        imageView.start();
        if (!TextUtils.isEmpty(title)) {
            TextView tvTitle = (TextView) mDialog.findViewById(R.id.tvTitle);
            tvTitle.setText(title);
        }
        Button btnCancel = (Button) mDialog.findViewById(R.id.btnCancel);
        if (AndyUtils.progressCancelListener == null) {
            btnCancel.setVisibility(View.GONE);

        } else {
            btnCancel.setVisibility(View.VISIBLE);

        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AndyUtils.progressCancelListener != null) {
                    AndyUtils.progressCancelListener.onProgressCancel();
                }
            }
        });
        mDialog.show();
    }

    public static void removeCustomProgressRequestDialog() {
        try {
            AndyUtils.progressCancelListener = null;
            if (mDialog != null) {
                // if (mProgressDialog.isShowing()) {
                mDialog.dismiss();
                mDialog = null;
                // }
            }
        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();
        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String encodeTobase64(Bitmap image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        byte[] bs = outputStream.toByteArray();
        String imageEncoded = Base64.encodeBytes(bs);
        return imageEncoded;
    }

    public static void showErrorToast(int id, Context ctx, Activity activity) {
        String msg = "";
        switch (id) {
            case 401:
                msg = ctx.getResources().getString(R.string.error_401);
                break;
            case 402:
                msg = ctx.getResources().getString(R.string.error_402);
                break;
            case 403:
                msg = ctx.getResources().getString(R.string.error_403);
                break;
            case 404:
                msg = ctx.getResources().getString(R.string.error_404);
                break;
            case 405:
                msg = ctx.getResources().getString(R.string.error_405);
                break;
            case 406:
                msg = ctx.getResources().getString(R.string.error_406);
                break;
            case 407:
                msg = ctx.getResources().getString(R.string.error_407);
                break;
            case 408:
                msg = ctx.getResources().getString(R.string.error_408);
                break;
            case 409:
                msg = ctx.getResources().getString(R.string.error_409);
                break;
            case 410:
                msg = ctx.getResources().getString(R.string.error_410);
                break;
            case 411:
                msg = ctx.getResources().getString(R.string.error_411);
                break;
            case 412:
                msg = ctx.getResources().getString(R.string.error_412);
                break;
            case 413:
                msg = ctx.getResources().getString(R.string.error_413);
                break;
            case 414:
                msg = ctx.getResources().getString(R.string.error_414);
                break;
            case 415:
                msg = ctx.getResources().getString(R.string.error_415);
                break;
            case 416:
                msg = ctx.getResources().getString(R.string.error_416);
                break;
            case 417:
                msg = ctx.getResources().getString(R.string.error_417);
                break;
            default:
                break;
        }
        AndyUtils.showToast(msg, R.id.coordinatorLayout, activity);
    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(displayMetrics);
        view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels,
                displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    private static Bitmap getBitmap(VectorDrawableCompat vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap getBitmap(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return BitmapFactory.decodeResource(context.getResources(), drawableId);
        } else if (drawable instanceof VectorDrawableCompat) {
            return getBitmap((VectorDrawableCompat) drawable);
        } else if (drawable instanceof VectorDrawable) {
            return getBitmap((VectorDrawable) drawable);
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }
}
