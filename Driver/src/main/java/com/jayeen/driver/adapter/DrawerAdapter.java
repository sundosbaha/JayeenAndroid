package com.jayeen.driver.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.jayeen.driver.R;
import com.jayeen.driver.model.ApplicationPages;
import com.jayeen.driver.utills.AndyUtils;

/**
 * @author Kishan H Dhamat
 */
public class DrawerAdapter extends BaseAdapter {
    private ArrayList<ApplicationPages> arrayListApplicationPages;
    private ViewHolder holder;
    private LayoutInflater inflater;
    Context ctx;

    public DrawerAdapter(Context context,
                         ArrayList<ApplicationPages> arrayListApplicationPages) {
        this.arrayListApplicationPages = arrayListApplicationPages;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ctx = context;
    }

    @Override
    public int getCount() {
        return arrayListApplicationPages.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.drawer_item, parent, false);
            holder = new ViewHolder();
            holder.tvMenuItem = (TextView) convertView
                    .findViewById(R.id.tvMenuItem);
            holder.ivMenuImage = (ImageView) convertView
                    .findViewById(R.id.ivMenuImage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvMenuItem.setText(arrayListApplicationPages.get(position).getTitle());
        if (position == 0) {
            holder.ivMenuImage.setImageResource(R.drawable.nav_profile);
        }
//        else if (position == 1) {
//            holder.ivMenuImage.setImageResource(R.drawable.ic_wallet_colored);
//        }
        else if (position == 1) {
            holder.ivMenuImage.setImageResource(R.drawable.ub__nav_history);
        }
        else if (position == 2) {
            holder.ivMenuImage.setImageResource(R.drawable.promotion);
        } else if (position == 3) {
            holder.ivMenuImage.setImageResource(R.drawable.share_ic);
        } else if (position == 4) {
            holder.ivMenuImage.setImageResource(R.drawable.instant_trip_icon);
        }else if (position == (arrayListApplicationPages.size() - 1)) {
            holder.ivMenuImage.setImageResource(R.drawable.ub__nav_logout);
        } else {
            if (TextUtils.isEmpty(arrayListApplicationPages.get(position)
                    .getIcon())) {
                holder.ivMenuImage.setImageResource(R.drawable.taxi);
            } else {
                AndyUtils.Picasso(this.ctx, arrayListApplicationPages.get(position).getIcon(), holder.ivMenuImage, R.drawable.ic_launcher);

            }
        }
        return convertView;
    }

    class ViewHolder {
        public TextView tvMenuItem;
        public ImageView ivMenuImage;
    }

}
