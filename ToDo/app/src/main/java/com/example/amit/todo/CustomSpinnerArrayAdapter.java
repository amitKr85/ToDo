package com.example.amit.todo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomSpinnerArrayAdapter extends ArrayAdapter<CharSequence> {
    Context mContext;
    public CustomSpinnerArrayAdapter(Context context,int resourceId,CharSequence[] list){
        super(context,resourceId,list);
        mContext=context;
    }

    /**
     * this method will set up the view for drop down items
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        return makeAndReturnView(position,convertView,parent);
    }

    /**
     * this method will set up the selected item
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        return makeAndReturnView(position,convertView,parent);
    }

    /**
     * as code for both getDropDownView() and getView() were same so made this single method to work for both
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    private View makeAndReturnView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View rootView=convertView;
        if(rootView==null){
            LayoutInflater inflater=LayoutInflater.from(mContext);
            rootView=inflater.inflate(R.layout.custom_spinner_item,parent,false);
        }

        // setting text into textview
        String item=(String)getItem(position);
        TextView textView=rootView.findViewById(R.id.custom_spinner_item_text_view);
        textView.setText(item);

        //setting background color of drop down
        if(item.equals(mContext.getString(R.string.very_important)))
            rootView.setBackgroundColor(mContext.getResources().getColor(R.color.very_important));
        else if(item.equals(mContext.getString(R.string.important)))
            rootView.setBackgroundColor(mContext.getResources().getColor(R.color.important));
        else if(item.equals(mContext.getString(R.string.normal)))
            rootView.setBackgroundColor(mContext.getResources().getColor(R.color.normal));
        else if(item.equals(mContext.getString(R.string.should_do)))
            rootView.setBackgroundColor(mContext.getResources().getColor(R.color.should_do));
        else if(item.equals(mContext.getString(R.string.do_if_possible)))
            rootView.setBackgroundColor(mContext.getResources().getColor(R.color.do_if_possible));

        return rootView;
    }
}
