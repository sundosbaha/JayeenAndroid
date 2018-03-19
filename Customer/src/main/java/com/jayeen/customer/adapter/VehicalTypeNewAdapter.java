package com.jayeen.customer.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jayeen.customer.R;
import com.jayeen.customer.fragments.UberMapFragment;
import com.jayeen.customer.newmodels.VehicleTypeModel;
import com.jayeen.customer.parse.RecycleOnItemClick;
import com.jayeen.customer.utils.AndyUtils;
import com.jayeen.customer.utils.AppLog;

import java.util.ArrayList;
import java.util.List;

public class VehicalTypeNewAdapter extends RecyclerView.Adapter<VehicalTypeNewAdapter.ViewHolder> {


    List<VehicleTypeModel> mItems;
    View v;

    RecycleOnItemClick mItemClickListener;
    Context scontext;
//    UberMapFragment fragment;

    public VehicalTypeNewAdapter(Context context, ArrayList<VehicleTypeModel> listType, RecycleOnItemClick itemClickListener) {
        super();
        mItems = listType;
        this.scontext = context;
//        this.fragment = fragment;
        this.mItemClickListener = itemClickListener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_vehicaltype, viewGroup, false);
        v.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final ViewHolder viewHolder = new ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(v, viewHolder.getLayoutPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        VehicleTypeModel movie = mItems.get(i);
        viewHolder.tvType.setText(movie.name + "");
        viewHolder.ivIcon.setTag(i);
        AndyUtils.Picasso(this.scontext, movie.icon, viewHolder.ivIcon);
        if (mItems.get(i).isSelected) {
            viewHolder.ivSelectService.setVisibility(View.INVISIBLE);
            viewHolder.ivSelectService.setImageResource(R.drawable.selected_service);
            viewHolder.tvType.setTextColor(scontext.getResources().getColor(R.color.white));
            viewHolder.tvTypemin.setTextColor(scontext.getResources().getColor(R.color.white));
            viewHolder.mainlayveh1.setBackgroundResource(R.drawable.gradient_bookselect_btn);
        } else {
            viewHolder.ivSelectService.setVisibility(View.INVISIBLE);
            viewHolder.tvType.setTextColor(scontext.getResources().getColor(R.color.colorPrimary));
            viewHolder.mainlayveh1.setBackgroundResource(R.drawable.gradient_bookunselect_btn);
            viewHolder.tvTypemin.setTextColor(scontext.getResources().getColor(R.color.colorPrimary));
        }

        if (i == 0) {
            viewHolder.divider.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvType, tvTypemin;
        LinearLayout mainlayveh1;
        public ImageView ivIcon, ivSelectService, divider, ivIcon2api;

        public ViewHolder(View itemView) {
            super(itemView);
            tvType = (TextView) itemView.findViewById(R.id.tvType2api);
            tvTypemin = (TextView) itemView.findViewById(R.id.tvTypeminapi);
            ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon2api);
            divider = (ImageView) itemView.findViewById(R.id.car_line);
            mainlayveh1 = (LinearLayout) itemView.findViewById(R.id.mainlayveh1);
            ivSelectService = (ImageView) itemView
                    .findViewById(R.id.ivSelectService2api);
        }
    }

    public void UpdateETA(ViewHolder mViewHolder, String min) {
        if (mViewHolder != null)
            mViewHolder.tvTypemin.setText(min);
        else
            AppLog.Log("Adapter", "ViewHolder Null");
    }
}
