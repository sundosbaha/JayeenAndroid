/**
 *
 */
package com.jayeen.customer.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView.PinnedSectionListAdapter;
import com.jayeen.customer.HistoryActivityNew;
import com.jayeen.customer.R;
import com.jayeen.customer.ScheduleListActivity;
import com.jayeen.customer.newmodels.SchedulehistoryModel;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;


public class ScheduleHistoryAdapterNew extends BaseAdapter implements
        PinnedSectionListAdapter {

    private Activity activity;
    private List<SchedulehistoryModel.Request> list;
    private LayoutInflater inflater;
    private ViewHolder holder;
    public static final int TYPE_ITEM = 0;
    public static final int TYPE_SEPARATOR = 1;
    private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;
    private DecimalFormat df;
    TreeSet<Integer> mSeparatorsSet;
    SimpleDateFormat simpleDateFormat;
    private SectionViewHolder sectionHolder;


    public ScheduleHistoryAdapterNew(Activity activity, List<SchedulehistoryModel.Request> historyList,
                                     TreeSet<Integer> mSeparatorsSet) {
        // TODO Auto-generated constructor stub
        this.activity = activity;
        this.list = historyList;

        df = new DecimalFormat("00");
        this.mSeparatorsSet = mSeparatorsSet;

        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int type = getItemViewType(position);
        boolean iseditable = false;
        switch (type) {
            case TYPE_ITEM:
                convertView = inflater.inflate(R.layout.item_layout_schedule, parent,
                        false);
                holder = new ViewHolder();
                holder.tv_pickup = (TextView) convertView.findViewById(R.id.tv_pickupaddr);
                holder.tv_drop = (TextView) convertView.findViewById(R.id.tv_dropaddr);
                holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
                holder.btn_cancel = (Button) convertView.findViewById(R.id.btncancel_ride);
                final SchedulehistoryModel.Request history = list.get(position);
                holder.tv_pickup.setText(history.pickupAddress.trim().length() == 0 ? activity.getString(R.string.text_addr_notavailable) : history.pickupAddress.replace("\n", ""));
                holder.tv_drop.setText(history.droppAddress.trim().length() == 0 ? activity.getString(R.string.text_addr_notavailable) : history.droppAddress.replace("\n", ""));
                holder.tv_pickup.setSelected(true);
                holder.tv_drop.setSelected(true);
                holder.tv_date.setText(history.scheduleDate);
                holder.tv_time.setText(history.scheduleTime);
                holder.tv_type.setText(history.carType);
                holder.tv_type.setTag(position);
                holder.btn_cancel.setTag(history.id);
                if (history.isCancelled) {
                    holder.btn_cancel.setText(activity.getResources().getString(R.string.text_ride_canceled));
                    holder.btn_cancel.setEnabled(false);
                    holder.btn_cancel.setTextColor(activity.getResources().getColor(R.color.gray));
                }
                holder.btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (((Button) v).getText().toString().equalsIgnoreCase(
                                activity.getResources().getString(R.string.text_cancel_ride))) {
//                            ((ScheduleListActivity) activity).CancelBooking(v.getTag().toString());
                            ((HistoryActivityNew) activity).cancelBooking(v.getTag().toString());
                            holder.btn_cancel.setText(activity.getResources().getString(R.string.text_ride_canceled));
                            holder.btn_cancel.setEnabled(false);
                            holder.btn_cancel.setTextColor(activity.getResources().getColor(R.color.gray));
                        }
                    }
                });
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


        switch (type) {
            case TYPE_ITEM:
                break;
            case TYPE_SEPARATOR:
                Date currentDate = new Date();
                Date returnDate = new Date();
                SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                String date = df1.format(currentDate);
//                if (sectionHolder == null) {
//                    sectionHolder = new SectionViewHolder();
//                    convertView = inflater.inflate(R.layout.history_date_layout, parent, false);
//
//                    sectionHolder.tv = (TextView) convertView.findViewById(R.id.tvDate);
//                    convertView.setTag(sectionHolder);
//                }
                if (list.get(position).scheduleDate.equals(date)) {
                    sectionHolder.tv
                            .setBackgroundResource(R.drawable.history_header_line_green);
                    sectionHolder.tv.setTextColor(activity.getResources().getColor(
                            R.color.white));
                    sectionHolder.tv.setText(activity
                            .getString(R.string.text_today));
                } else if (list.get(position).scheduleDate
                        .equals(getYesterdayDateString())) {
                    sectionHolder.tv
                            .setBackgroundResource(R.drawable.history_header_line_white);
                    sectionHolder.tv.setTextColor(activity.getResources().getColor(
                            R.color.color_text));
                    sectionHolder.tv.setText(activity
                            .getString(R.string.text_yesterday));
                } else {

                    sectionHolder.tv
                            .setBackgroundResource(R.drawable.history_header_line_white);
                    sectionHolder.tv.setTextColor(activity.getResources().getColor(
                            R.color.color_text));
                    try {
                        returnDate = df1.parse(list.get(position).scheduleDate);

                    } catch (ParseException e) {
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

        TextView tv_pickup, tv_drop, tv_date, tv_time, tv_type;
        Button btn_cancel;

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
}
