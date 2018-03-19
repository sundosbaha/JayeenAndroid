package com.jayeen.driver.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.jayeen.driver.R;
import com.jayeen.driver.model.CountryModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 9/15/2016.
 */
public class CountryAdapter extends BaseAdapter implements Filterable {

    Context ctx = null;
    List<CountryModel> listarray = null;
    List<CountryModel> Fullarray = null;
    private LayoutInflater mInflater = null;

    public CountryAdapter(Activity activty, List<CountryModel> list) {
        this.ctx = activty;
        mInflater = activty.getLayoutInflater();
        this.listarray = list;
        this.Fullarray = list;
    }

    @Override
    public int getCount() {

        return listarray.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_conutry, null);

            holder.titlename = (TextView) convertView.findViewById(R.id.textView_titllename);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CountryModel datavalue = listarray.get(position);
        holder.titlename.setText(datavalue.phone_code + "   " + datavalue.name);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new ValueFilter();
    }

    private static class ViewHolder {
        TextView titlename;
    }


    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.toString().trim().length() > 0) {
                ArrayList<CountryModel> filterList = new ArrayList<CountryModel>();
                for (int i = 0; i < Fullarray.size(); i++) {
                    if ((Fullarray.get(i).name.toUpperCase()).startsWith(constraint.toString().toUpperCase())) {
                        CountryModel country = Fullarray.get(i);
                        filterList.add(country);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = Fullarray.size();
                results.values = Fullarray;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            listarray = (ArrayList<CountryModel>) results.values;
            notifyDataSetChanged();
        }

    }
}
