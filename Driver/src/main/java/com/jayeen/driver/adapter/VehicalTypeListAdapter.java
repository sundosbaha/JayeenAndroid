/**
 *
 */
package com.jayeen.driver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.jayeen.driver.R;
import com.jayeen.driver.fragment.RegisterFragment;
import com.jayeen.driver.newmodels.VehicleTypesModel;
import com.jayeen.driver.utills.AndyUtils;

import java.util.List;

public class VehicalTypeListAdapter extends BaseAdapter {

    private List<VehicleTypesModel.Type> listVehicalType;
    private LayoutInflater inflater;
    private ViewHolder holder;
    public static int seletedPosition = 0;
    RegisterFragment regFrag;
    Context scontext;

    public VehicalTypeListAdapter(Context context, List<VehicleTypesModel.Type> listVehicalType, RegisterFragment mapfrag) {
        this.listVehicalType = listVehicalType;
        this.regFrag = mapfrag;
        this.scontext = context;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return listVehicalType.size();
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
            convertView = inflater.inflate(R.layout.list_item_type, parent,
                    false);
            holder = new ViewHolder();
            holder.tvType = (TextView) convertView.findViewById(R.id.tvType);
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
            holder.ivSelectService = (ImageView) convertView.findViewById(R.id.ivSelectService);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvType.setText(listVehicalType.get(position).mName + "");
        holder.ivIcon.setTag(position);
        AndyUtils.Picasso(this.scontext,listVehicalType.get(position).mIcon, holder.ivIcon);
        if (listVehicalType.get(position).isSelected) {
            holder.ivSelectService.setVisibility(View.VISIBLE);
            holder.ivSelectService.setImageResource(R.drawable.selected_service);
        } else {
            holder.ivSelectService.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    private class ViewHolder {
        TextView tvType;
        ImageView ivIcon, ivSelectService;
    }

}
