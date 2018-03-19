/**
 *
 */
package com.jayeen.customer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView.PinnedSectionListAdapter;
import com.jayeen.customer.R;
import com.jayeen.customer.component.MyFontHistoryTextView;
import com.jayeen.customer.component.MyFontTextView;
import com.jayeen.customer.component.MyTitleFontTextView;
import com.jayeen.customer.newmodels.HistoryModel;
import com.jayeen.customer.utils.AndyUtils;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeSet;


/**
 * @author Kishan H Dhamat
 */
public class HistoryAdapter extends BaseAdapter implements
        PinnedSectionListAdapter {

    private Activity activity;
    private ArrayList<HistoryModel.Request> list;
    //	private ArrayList<HistoryModel.Request> list1;
    private LayoutInflater inflater;
    private ViewHolder holder;
    public static final int TYPE_ITEM = 0;
    public static final int TYPE_SEPARATOR = 1;
    private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;
    private DecimalFormat df;
    TreeSet<Integer> mSeparatorsSet;
    SimpleDateFormat simpleDateFormat;
    private SectionViewHolder sectionHolder;
    //    width of google map static image
    int width = Resources.getSystem().getDisplayMetrics().widthPixels;

    /**
     * historyActivity
     *
     * @param historyList
     */
    public HistoryAdapter(Activity activity, ArrayList<HistoryModel.Request> historyList,
                          TreeSet<Integer> mSeparatorsSet) {
        // TODO Auto-generated constructor stub
        this.activity = activity;
        this.list = historyList;
//        decimalFormat = new DecimalFormat("0.00");
        df = new DecimalFormat("00");
        this.mSeparatorsSet = mSeparatorsSet;

        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        // notifyDataSetInvalidated();
        // notifyDataSetChanged();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return list.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case TYPE_ITEM:
                    convertView = inflater.inflate(R.layout.history_item_new, parent, false);
                    holder = new ViewHolder();
                    holder.tvName = (MyFontHistoryTextView) convertView.findViewById(R.id.tvHistoryName);
                    holder.tvTypeName = (MyFontTextView) convertView.findViewById(R.id.li_history_type_name);
                    holder.tvPaymentType = (MyFontTextView) convertView.findViewById(R.id.li_history_payment);
                    holder.tvDate = (MyTitleFontTextView) convertView.findViewById(R.id.tvHistoryDate);
                    holder.tvCost = (MyTitleFontTextView) convertView.findViewById(R.id.tvHistoryCost);
                    holder.ivIcon = (CircularImageView) convertView.findViewById(R.id.ivHistoryIcon);
                    holder.ivType = (ImageView) convertView.findViewById(R.id.li_history_type);
                    holder.ivPath = (ImageView) convertView.findViewById(R.id.li_history_path);
                    convertView.setTag(holder);
                    break;
                case TYPE_SEPARATOR:
                    sectionHolder = new SectionViewHolder();
                    convertView = inflater.inflate(R.layout.history_date_layout, parent, false);
                    sectionHolder.tv = (TextView) convertView.findViewById(R.id.tvDate);
                    convertView.setTag(sectionHolder);
                    break;
            }

        } else {
            switch (type) {
                case TYPE_ITEM:
                    holder = (ViewHolder) convertView.getTag();
                    break;
                case TYPE_SEPARATOR:
                    sectionHolder = (SectionViewHolder) convertView.getTag();
                    break;

            }
        }

        switch (type) {
            case TYPE_ITEM:
                HistoryModel.Request request = list.get(position);
                holder.tvName.setText(request.walker.firstName + " " + request.walker.lastName);
                holder.tvPaymentType.setText(request.paymentType == 1 ? "CASH" : "CARD");
                holder.tvDate.setText(formatDate(request.date) + " at " + formatTime(request.date));
                holder.tvTypeName.setText(request.walker.type);
                holder.tvCost.setText(activity.getString(R.string.payment_unit) + Double.parseDouble(request.total));
                AndyUtils.Picasso(this.activity, request.walker.picture, holder.ivIcon, R.drawable.default_user);
                AndyUtils.Picasso(this.activity, request.typeIcon, holder.ivType, R.drawable.default_user);
                AndyUtils.Picasso(this.activity, holder.ivPath, width,((int)(width*0.24)>300?120:(int)(width*0.24)), request.startLat, request.startLong, request.endLat, request.endLong, R.drawable.default_user);
                break;
            case TYPE_SEPARATOR:
                Date currentDate = new Date();
                Date returnDate = new Date();
                SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                String date = df1.format(currentDate);
                if (list.get(position).date.equals(date)) {
                    sectionHolder.tv.setBackgroundResource(R.drawable.history_header_line_green);
                    sectionHolder.tv.setTextColor(activity.getResources().getColor(R.color.white));
                    sectionHolder.tv.setText(activity
                            .getString(R.string.text_today));
                } else if (list.get(position).date.equals(getYesterdayDateString())) {
                    sectionHolder.tv.setBackgroundResource(R.drawable.history_header_line_white);
                    sectionHolder.tv.setTextColor(activity.getResources().getColor(R.color.color_text));
                    sectionHolder.tv.setText(activity
                            .getString(R.string.text_yesterday));
                } else {
                    sectionHolder.tv.setBackgroundResource(R.drawable.history_header_line_white);
                    sectionHolder.tv.setTextColor(activity.getResources().getColor(R.color.color_text));
                    try {
                        returnDate = df1.parse(list.get(position).date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    SimpleDateFormat pinnedDate = new SimpleDateFormat("dd-MMM-yyyy");
                    sectionHolder.tv.setText(pinnedDate.format(returnDate));
                }
                break;

        }

        return convertView;
    }

    private class ViewHolder {
        MyFontHistoryTextView tvName;
        MyFontTextView tvTypeName, tvPaymentType;
        MyTitleFontTextView tvCost, tvDate;
        CircularImageView ivIcon;
        ImageView ivType, ivPath;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.hb.views.PinnedSectionListView.PinnedSectionListAdapter#
     * isItemViewTypePinned(int)
     */
    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == TYPE_SEPARATOR;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    class SectionViewHolder {
        TextView tv;
    }

    private String getYesterdayDateString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return dateFormat.format(cal.getTime());
    }

    private String formatTime(String strDate) {
        Log.i("String Date", strDate);
        Date time = new Date();
        try {
            time = simpleDateFormat.parse(strDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time.getTime());
        return sdf.format(cal.getTime());

    }

    private String formatDate(String strDate) {
        Log.i("String Date", strDate);
        Date time = new Date();
        try {
            time = simpleDateFormat.parse(strDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time.getTime());
        return sdf.format(cal.getTime());

    }
}
