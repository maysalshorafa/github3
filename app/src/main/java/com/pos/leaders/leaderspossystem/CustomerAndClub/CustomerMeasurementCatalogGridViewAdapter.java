package com.pos.leaders.leaderspossystem.CustomerAndClub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pos.leaders.leaderspossystem.DataBaseAdapter.CustomerMeasurementAdapter.MeasurementsDetailsDBAdapter;
import com.pos.leaders.leaderspossystem.Models.CustomerMeasurement.CustomerMeasurement;
import com.pos.leaders.leaderspossystem.R;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Win8.1 on 12/20/2017.
 */

public class CustomerMeasurementCatalogGridViewAdapter extends BaseAdapter {
    private Context context;
    private List<CustomerMeasurement> customerMeasurements;
    private LayoutInflater inflater;
    private int resource;
    int bgColor =0;
    public CustomerMeasurementCatalogGridViewAdapter(Context context, int resource, List<CustomerMeasurement> customerMeasurements) {
        this.context = context;
        this.resource = resource;
        this.context = context;
        this.customerMeasurements = customerMeasurements;
        bgColor=0;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return customerMeasurements.size();
    }



    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public CustomerMeasurement getItem(int position) {
        return customerMeasurements.get(position);
    }



    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return (long) customerMeasurements.get(position).getCustomerMeasurementId();
    }



    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridView=convertView;

        if(convertView==null){
            inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView=inflater.inflate(R.layout.grid_view_item_customer_measurement,null);
        }
        final TextView tvMeasurementId=(TextView)gridView.findViewById(R.id.customerMeasurementGridView_TVMeasurementId);
        final TextView tvNoOfMeasurement=(TextView)gridView.findViewById(R.id.customerMeasurementGridView_TVNoOfMeasurement);
        final TextView tvMeasurementVisitDate=(TextView)gridView.findViewById(R.id.customerMeasurementGridView_TVMeasurementVisitDate);

        tvMeasurementId.setText((position+1)+""); //MeasurementId
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        tvMeasurementVisitDate.setText(format.format(customerMeasurements.get(position).getVisitDate()));
        MeasurementsDetailsDBAdapter measurementsDetailsDBAdapter = new MeasurementsDetailsDBAdapter(context);
        measurementsDetailsDBAdapter.open();
        int noOfMeasurement = measurementsDetailsDBAdapter.getMeasurementDetailsByMeasurementsId(customerMeasurements.get(position).getCustomerMeasurementId());
        tvNoOfMeasurement.setText(noOfMeasurement+" ");
        if(bgColor%2==0){
            gridView.setBackgroundColor(context.getResources().getColor(R.color.backgroundColor));
        }
        bgColor++;
        return gridView;
    }

}
