package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AdapterComment extends ArrayAdapter<CommentDTO> {
    public AdapterComment(@NonNull Context context, int resource) {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_comment, null);

            viewHolder = new ViewHolder();
            viewHolder.txtvName = (TextView)convertView.findViewById(R.id.txtvName);
            viewHolder.txtvComment = (TextView)convertView.findViewById(R.id.txtvComment);
            viewHolder.txtvTime = (TextView)convertView.findViewById(R.id.txtvTime);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        CommentDTO commentDTO = getItem(position);
        viewHolder.txtvName.setText(commentDTO.getName());
        viewHolder.txtvComment.setText(commentDTO.getTxt());
        viewHolder.txtvTime.setText(commentDTO.getTime());

        return convertView;
    }
    private class ViewHolder{
        TextView txtvName;
        TextView txtvComment;
        TextView txtvTime;
    }
}
