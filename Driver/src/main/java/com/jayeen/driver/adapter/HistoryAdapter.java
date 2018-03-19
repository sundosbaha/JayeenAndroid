/**
 *
 */
package com.jayeen.driver.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView.PinnedSectionListAdapter;
import com.jayeen.driver.R;
import com.jayeen.driver.newmodels.Request;
import com.jayeen.driver.utills.AndyUtils;
import com.jayeen.driver.widget.MyFontTextView;
import com.jayeen.driver.widget.MyFontTextViewBold;
import com.jayeen.driver.widget.MyFontTextViewTitle;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.TreeSet;

public class HistoryAdapter extends BaseAdapter implements
        PinnedSectionListAdapter {

    private Activity activity;
    private ArrayList<Request> list;
    private LayoutInflater inflater;
    private ViewHolder holder;
    public static final int TYPE_ITEM = 0;
    public static final int TYPE_SEPARATOR = 1;
    private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;
    private DecimalFormat df, twodigitdecimalformat;
    TreeSet<Integer> mSeparatorsSet;
    SimpleDateFormat simpleDateFormat;
    private SectionViewHolder sectionHolder;
    private Date currentDate = new Date();
    private Date returnDate = new Date();


    public HistoryAdapter(Activity activity, ArrayList<Request> historyList, TreeSet<Integer> mSeparatorsSet) {
        this.activity = activity;
        this.list = historyList;
        df = new DecimalFormat("00");
        twodigitdecimalformat = new DecimalFormat("0.00");
        this.mSeparatorsSet = mSeparatorsSet;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case TYPE_ITEM:
                    convertView = inflater.inflate(R.layout.history_item, parent,
                            false);
                    holder = new ViewHolder();
                    holder.tvName = (MyFontTextViewBold) convertView
                            .findViewById(R.id.tvHistoryName);
                    holder.tvDate = (MyFontTextViewTitle) convertView
                            .findViewById(R.id.tvHistoryDate);
                    holder.tvCost = (MyFontTextViewTitle) convertView
                            .findViewById(R.id.tvHistoryCost);
                    holder.ivIcon = (ImageView) convertView
                            .findViewById(R.id.ivHistoryIcon);
                    convertView.setTag(holder);
                    break;
                case TYPE_SEPARATOR:
                    sectionHolder = new SectionViewHolder();
                    convertView = inflater.inflate(R.layout.history_date_layout,
                            parent, false);
                    sectionHolder.tv = (TextView) convertView
                            .findViewById(R.id.tvDate);
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
                Request history = list.get(position);
                holder.tvName.setText(history.mOwner.mFirst_name + " " + history.mOwner.last_name);
                holder.tvDate.setText(formatTime(history.mDate));
                holder.tvCost.setText(activity.getResources().getString(R.string.costarica_currency) + twodigitdecimalformat.format(history.mTotal));
                AndyUtils.Picasso(activity,history.mOwner.mPicture,holder.ivIcon,R.drawable.user);
                break;
            case TYPE_SEPARATOR:
                Log.i(activity.getString(R.string.text_return_date), "" + list.get(position).mDate);
                SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                String date = df1.format(currentDate);

                if (list.get(position).mDate.equals(date)) {
                    sectionHolder.tv
                            .setBackgroundResource(R.drawable.history_header_line_blue);
                    sectionHolder.tv.setTextColor(activity.getResources().getColor(
                            R.color.white));
                    sectionHolder.tv.setText(activity
                            .getString(R.string.text_today));
                } else if (list.get(position).mDate
                        .equals(getYesterdayDateString())) {
                    sectionHolder.tv
                            .setBackgroundResource(R.drawable.history_header_line_white);
                    sectionHolder.tv.setTextColor(activity.getResources().getColor(
                            R.color.color_blue));
                    sectionHolder.tv.setText(activity
                            .getString(R.string.text_yesterday));
                } else {
                    sectionHolder.tv
                            .setBackgroundResource(R.drawable.history_header_line_white);
                    sectionHolder.tv.setTextColor(activity.getResources().getColor(
                            R.color.color_blue));

                    try {
                        returnDate = df1.parse(list.get(position).mDate);

                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    SimpleDateFormat pinnedDate = new SimpleDateFormat(
                            "dd-MMM-yyyy");
                    Log.i("New Date", pinnedDate.format(returnDate));
                    sectionHolder.tv.setText(pinnedDate.format(returnDate));

                }

                break;

        }

        return convertView;
    }

    private class ViewHolder {
        MyFontTextView tvBio;
        MyFontTextViewBold tvName;
        MyFontTextViewTitle tvCost, tvDate;
        ImageView ivIcon;
    }
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

    private String formatTime(String strDate) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        Date time = new Date();
        try {
            time = sf.parse(strDate);
            Log.i("Return Time", "" + time.getHours() + ":" + time.getMinutes());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time.getTime());
        return sdf.format(cal.getTime());

    }

    private String getYesterdayDateString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return dateFormat.format(cal.getTime());
    }

    Comparator<Date> myComparator = new Comparator<Date>() {
        public int compare(Date currentDate, Date returnDate) {
            return currentDate.compareTo(returnDate);
        }
    };

}
