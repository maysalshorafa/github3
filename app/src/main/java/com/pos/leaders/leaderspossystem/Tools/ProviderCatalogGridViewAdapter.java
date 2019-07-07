package com.pos.leaders.leaderspossystem.Tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pos.leaders.leaderspossystem.Models.Provider;
import com.pos.leaders.leaderspossystem.R;

import java.util.List;

/**
 * Created by Win8.1 on 7/7/2019.
 */


public class ProviderCatalogGridViewAdapter extends BaseAdapter {
    private Context context;
    private List<Provider> providers;
    private LayoutInflater inflater;

    public ProviderCatalogGridViewAdapter(Context context, List<Provider> providers) {
        this.context = context;
        this.providers = providers;
    }


    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return providers.size();
    }



    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Provider getItem(int position) {
        return providers.get(position);
    }



    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return (long) providers.get(position).getProviderId();
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
            gridView=inflater.inflate(R.layout.grid_view_item_custmer,null);
        }

        TextView tvCustmerName=(TextView)gridView.findViewById(R.id.custmerGridView_TVCustmerName);
        TextView tvCustmerLastName=(TextView)gridView.findViewById(R.id.custmerGridView_TVCustmerLastName);

        //TextView tvFullName=(TextView)gridView.findViewById(R.id.custmerGridView_TVFullName);
        TextView tvPhoneNumber=(TextView)gridView.findViewById(R.id.custmerGridView_TVPhoneNumber);

        tvCustmerName.setText(providers.get(position).getFirstName());
        //   tvFullName.setText(providers.get(position).getFullName());
        tvCustmerLastName.setText(providers.get(position).getLastName());
        tvPhoneNumber.setText(providers.get(position).getPhoneNumber());

        return gridView;
    }



}
