package com.diastock.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomMenuAdapter extends ArrayAdapter<MenuDataModel> implements View.OnClickListener {

    private ArrayList<MenuDataModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtAttributes;
        TextView txtMenuid;
        ImageView imageView;
    }

    public CustomMenuAdapter(ArrayList<MenuDataModel> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext = context;

    }

    @Override
    public void onClick(View v) {

        /*
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        MenuDataModel dataModel=(MenuDataModel)object;

        switch (v.getId())
        {
            case R.id.item_info:
                Snackbar.make(v, "Release date " +dataModel.getFeature(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }
        */
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        MenuDataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtAttributes= (TextView) convertView.findViewById(R.id.attributes);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.txtMenuid = (TextView) convertView.findViewById(R.id.number);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image);
            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }


        lastPosition = position;

        viewHolder.txtName.setText(dataModel.getName());
        viewHolder.txtAttributes.setText(dataModel.getAttributes());
        viewHolder.txtMenuid.setText(dataModel.getNumber());
        viewHolder.imageView.setImageResource(dataModel.getImage());
        // Return the completed view to render on screen
        return convertView;
    }
}

