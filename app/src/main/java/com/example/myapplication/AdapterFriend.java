package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AdapterFriend extends ArrayAdapter<FriendDTO> {
    public AdapterFriend(@NonNull Context context, int resource) {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_friend, null);

            viewHolder = new ViewHolder();
            viewHolder.bo = (TextView)convertView.findViewById(R.id.txtvBo);
            viewHolder.name = (TextView)convertView.findViewById(R.id.txtvName);
            viewHolder.fuid = (TextView)convertView.findViewById(R.id.txtvFuid);
            viewHolder.now = (ImageView)convertView.findViewById(R.id.imgvNow);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        FriendDTO friendDTO = getItem(position);
        viewHolder.name.setText(friendDTO.getName());
        viewHolder.fuid.setText("UID : " + friendDTO.getFuid());
        viewHolder.bo.setText(friendDTO.getWalk() + " 보");
        viewHolder.now.setImageResource((friendDTO.getLoc().equals("0,0") ? R.drawable.runwalk2 : R.drawable.runwalk));

        return convertView;
    }

    @Nullable
    @Override
    public FriendDTO getItem(int position) {
        return super.getItem(position);
    }


    private class ViewHolder{
        TextView name;
        TextView fuid;
        TextView bo;
        ImageView now;
    }
}
