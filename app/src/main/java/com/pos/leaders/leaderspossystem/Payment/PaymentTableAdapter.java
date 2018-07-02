package com.pos.leaders.leaderspossystem.Payment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pos.leaders.leaderspossystem.R;
import com.pos.leaders.leaderspossystem.Tools.Util;

import java.util.ArrayList;

/**
 * Created by KARAM on 02/07/2018.
 */

public class PaymentTableAdapter extends ArrayAdapter<PaymentTable> {

    private ArrayList<PaymentTable> paymentTables;
    private int resource;
    private LayoutInflater inflater;
    private Context context;
    int bgColor =0;

    public PaymentTableAdapter(Context context,int resource, ArrayList<PaymentTable> paymentTables) {
        super(context, resource, paymentTables);
        this.context = context;
        this.paymentTables = paymentTables;
        this.resource = resource;
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(resource, null);
            holder.tvDue = (TextView) convertView.findViewById(R.id.list_multi_currencies_payment_due);
            holder.tvTendered = (TextView) convertView.findViewById(R.id.list_multi_currencies_payment_tendered);
            holder.tvChange = (TextView) convertView.findViewById(R.id.list_multi_currencies_payment_change);
            holder.tvMethod = (TextView) convertView.findViewById(R.id.list_multi_currencies_payment_method);
            holder.tvCurrency = (TextView) convertView.findViewById(R.id.list_multi_currencies_payment_currency);
            holder.tvDelete = (TextView) convertView.findViewById(R.id.list_multi_currencies_payment_delete);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // Get the data item for this position
        final PaymentTable paymentTable = getItem(position);

        holder.tvDue.setText(Util.makePrice(paymentTable.getDue()));
        if(!Double.isNaN(paymentTable.getTendered()))
            holder.tvTendered.setText(Util.makePrice(paymentTable.getTendered()));
        else
            holder.tvDelete.setVisibility(View.INVISIBLE);

        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(paymentTable);
            }
        });

        if(paymentTable.getChange()>0)
            holder.tvChange.setText(Util.makePrice(paymentTable.getChange()));
        else
            holder.tvChange.setText("");

        holder.tvMethod.setText(paymentTable.getPaymentMethod().toString());

        holder.tvCurrency.setText(paymentTable.getCurrency().getType());

        return convertView;
    }

    public class ViewHolder {
        TextView tvDue;
        TextView tvTendered;
        TextView tvChange;
        TextView tvMethod;
        TextView tvCurrency;
        TextView tvDelete;
    }
}
