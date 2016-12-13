package com.xiong.wlanconmmunition.service;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiong.wlanconmmunition.MemberInfo;
import com.xiong.wlanconmmunition.R;

import java.util.ArrayList;

/**
 * Created by eshion on 16-9-26.
 */
public class MemberAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<MemberInfo> memberInfos = null;

    public MemberAdapter(Context mContext, ArrayList<MemberInfo> memberInfos) {
        this.mContext = mContext;
        this.memberInfos = memberInfos;
    }

    public void setList(ArrayList<MemberInfo> members){
        this.memberInfos = members;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return memberInfos.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return memberInfos.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
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
        View v = convertView;
        if(v == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.signalmember, null);
        }
        ImageView icon  = (ImageView) v.findViewById(R.id.membericon);
        TextView name = (TextView) v.findViewById(R.id.membername);
        TextView ip = (TextView) v.findViewById(R.id.memberip);

        icon.setImageDrawable(memberInfos.get(position).iCon);
        name.setText(memberInfos.get(position).name);
        ip.setText(memberInfos.get(position).ip);
        return v;
    }
}
