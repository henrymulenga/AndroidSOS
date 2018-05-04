package com.henrymulenga.androidsos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.henrymulenga.androidsos.R;
import com.henrymulenga.androidsos.models.UsefulEmergencyNumber;

import java.util.List;


/**
 * Created by Henry Mulenga.
 */

public class UsefulNumbersListAdapter extends ArrayAdapter<UsefulEmergencyNumber> {
    public UsefulNumbersListAdapter(Context context, List<UsefulEmergencyNumber> items)
    {
        super(context, R.layout.activity_list_hospitals, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;


        if(convertView == null) {
            // inflate the GridView item layout
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.single_list_item_hospital, parent, false);

            // initialize the view holder
            viewHolder = new ViewHolder();
            viewHolder.brand = (TextView) convertView.findViewById(R.id.textName);
            viewHolder.usefulNumber = (TextView) convertView.findViewById(R.id.textDetails);
            viewHolder.imageViewProduct = (ImageView) convertView.findViewById(R.id.productImage);

            convertView.setTag(viewHolder);
        }else{
            // recycle the already inflated view
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // update the item view
        UsefulEmergencyNumber item = getItem(position);
        viewHolder.usefulNumber.setText(item.getName() + " (" +item.getNumber() + ")");

        return convertView;
    }

    private static class ViewHolder {
        TextView brand;
        TextView usefulNumber;
        ImageView imageViewProduct;
    }
}
