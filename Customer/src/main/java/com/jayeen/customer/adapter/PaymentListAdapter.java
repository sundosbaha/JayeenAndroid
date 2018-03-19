/**
 *
 */
package com.jayeen.customer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.jayeen.customer.CustomerApplication;
import com.jayeen.customer.R;
import com.jayeen.customer.newmodels.CardsModel;
import com.jayeen.customer.parse.AsyncTaskCompleteListener;
import com.jayeen.customer.parse.TaxiParseContent;
import com.jayeen.customer.parse.VolleyHttpRequest;
import com.jayeen.customer.utils.AndyUtils;
import com.jayeen.customer.utils.AppLog;
import com.jayeen.customer.utils.Const;

import java.util.HashMap;
import java.util.List;

import static com.jayeen.customer.CustomerApplication.requestQueue;

public class PaymentListAdapter extends BaseAdapter implements
        AsyncTaskCompleteListener, ErrorListener {
    private LayoutInflater inflater;
    private ViewHolder holder;
    private List<CardsModel.Payment> listCard;
    private int selectedPosition;
    private Context context;
    private TaxiParseContent parseContent;

    public PaymentListAdapter(Activity context, List<CardsModel.Payment> listCard,
                              int defaultCard) {
        this.listCard = listCard;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        selectedPosition = defaultCard;
//		requestQueue = Volley.newRequestQueue(context);
        parseContent = new TaxiParseContent(context);
    }

    @Override
    public int getCount() {
        return listCard.size();
    }

    @Override
    public Object getItem(int position) {
        return listCard.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.view_payment_list_item,
                    parent, false);
            holder = new ViewHolder();
            holder.ivCard = (ImageView) convertView.findViewById(R.id.ivCard);
            holder.tvNo = (TextView) convertView.findViewById(R.id.tvNo);
            holder.rdCardSelection = (RadioButton) convertView
                    .findViewById(R.id.rdCardSelection);
            convertView.setTag(holder);
            holder.rdCardSelection.setTag(position);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final CardsModel.Payment card = listCard.get(position);
        final int cardId = card.id;

        holder.tvNo.setText("*****" + card.lastFour);
        String type = card.cardType;

        if (type.equalsIgnoreCase(Const.VISA)) {
            holder.ivCard.setImageResource(R.drawable.ub__creditcard_visa);
        } else if (type.equalsIgnoreCase(Const.MASTERCARD)) {
            holder.ivCard
                    .setImageResource(R.drawable.ub__creditcard_mastercard);
        } else if (type.equalsIgnoreCase(Const.AMERICAN_EXPRESS)) {
            holder.ivCard.setImageResource(R.drawable.ub__creditcard_amex);
        } else if (type.equalsIgnoreCase(Const.DISCOVER)) {
            holder.ivCard.setImageResource(R.drawable.ub__creditcard_discover);
        } else if (type.equalsIgnoreCase(Const.DINERS_CLUB)) {
            holder.ivCard.setImageResource(R.drawable.ub__creditcard_discover);
        } else {
            holder.ivCard.setImageResource(R.drawable.ub__nav_payment);
        }

        if (selectedPosition == cardId)
            holder.rdCardSelection.setChecked(true);
        else
            holder.rdCardSelection.setChecked(false);

        if (card.isDefault) {
            holder.rdCardSelection.setChecked(true);
            CustomerApplication.preferenceHelper.putDefaultCard(cardId);
            CustomerApplication.preferenceHelper.putDefaultCardNo(card.lastFour);
            CustomerApplication.preferenceHelper.putDefaultCardType(card.cardType);
        } else
            holder.rdCardSelection.setChecked(false);

        holder.rdCardSelection.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton rd = (RadioButton) v;
                if (rd.isChecked()) {
                    AppLog.Log("PaymentAdapater", "checked Id " + cardId);
                    selectedPosition = cardId;
                    CustomerApplication.preferenceHelper.putDefaultCard(cardId);
                    CustomerApplication.preferenceHelper.putDefaultCardNo(card.lastFour);
                    CustomerApplication.preferenceHelper.putDefaultCardType(card.cardType);
                    notifyDataSetChanged();
                    setDefaultCard(cardId);
                } else {
                    AppLog.Log("PaymentAdapater", "unchecked Id " + cardId);
                }
                Intent i = new Intent(Const.CART_CHANGE_RECEIVER);
                context.sendBroadcast(i);
            }
        });

        return convertView;
    }

    private class ViewHolder {
        public ImageView ivCard;
        public TextView tvNo;
        public RadioButton rdCardSelection;
    }

    private void setDefaultCard(int cardId) {
        AndyUtils.showCustomProgressDialog(context,
                context.getString(R.string.text_changing_default_card), true,
                null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.DEFAULT_CARD);
        map.put(Const.Params.ID, String.valueOf(CustomerApplication.preferenceHelper.getUserId()));
        map.put(Const.Params.TOKEN, String.valueOf(CustomerApplication.preferenceHelper.getSessionToken()));
        map.put(Const.Params.DEFAULT_CARD_ID, String.valueOf(cardId));

        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                Const.ServiceCode.DEFAULT_CARD, this, this));
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        AndyUtils.removeCustomProgressDialog();
        listCard.clear();
        AppLog.Log("PaymentAdapter", "CardSelection reponse : " + response);
        listCard = parseContent.parseCards(response, listCard);
        notifyDataSetChanged();
    }
/**
 * To get the number of cards in Wallet screen for validation
 * */
    public int getNumberofCards() {
        return listCard == null ? 0 : listCard.size();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        // TODO Auto-generated method stub
        AppLog.Log(Const.TAG, error.getMessage());
    }
}
