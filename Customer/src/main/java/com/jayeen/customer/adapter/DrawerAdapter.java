package com.jayeen.customer.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//import com.androidquery.AQuery;
//import com.androidquery.callback.ImageOptions;
import com.squareup.picasso.Picasso;
import com.jayeen.customer.R;
import com.jayeen.customer.models.ApplicationPages;
import com.jayeen.customer.utils.AndyUtils;

import java.util.ArrayList;


/**
 * @author Hardik A Bhalodi
 */
public class DrawerAdapter extends BaseAdapter {

    private String items[];
    private ViewHolder holder;
    private LayoutInflater inflater;
    ArrayList<ApplicationPages> listMenu;
    private Context ctx;
//	private AQuery aQuery;
//	private ImageOptions imageOptions;

    public DrawerAdapter(Context context, ArrayList<ApplicationPages> listMenu) {
        // TODO Auto-generated constructor stub
        this.listMenu = listMenu;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.ctx = context;
//		aQuery = new AQuery(context);
//		imageOptions = new ImageOptions();
//		imageOptions.fileCache = true;
//		imageOptions.memCache = true;
//		imageOptions.fallback = R.drawable.ic_launcher;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listMenu.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
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
        if (position == 0) {
            holder.ivMenuImage.setImageResource(R.drawable.nav_profile);
        } else if (position == 1) {
            holder.ivMenuImage.setImageResource(R.drawable.nav_payment);
        }/* else if (position == 2) {
            holder.ivMenuImage.setImageResource(R.drawable.ic_wallet);
        }*/ else if (position == 2) {
            holder.ivMenuImage.setImageResource(R.drawable.ub__nav_history);
        } else if (position == 3) {
            holder.ivMenuImage.setImageResource(R.drawable.nav_referral);
        } else if (position == 4) {
            holder.ivMenuImage.setImageResource(R.drawable.sos_red);
        } else if (position == 5) {
            holder.ivMenuImage.setImageResource(R.drawable.promotion);
        } else if (position == (listMenu.size() - 1)) {
            holder.ivMenuImage.setImageResource(R.drawable.ub__nav_logout);
        } else {
            if (TextUtils.isEmpty(listMenu.get(position).getIcon())) {
                holder.ivMenuImage.setImageResource(R.drawable.fare_info);
            } else {
                AndyUtils.Picasso(this.ctx, listMenu.get(position).getIcon(), holder.ivMenuImage, R.drawable.fare_info);
            }
        }
        holder.tvMenuItem.setText(listMenu.get(position).getTitle());

        return convertView;
    }

    class ViewHolder {
        public TextView tvMenuItem;
        public ImageView ivMenuImage;
    }

}
