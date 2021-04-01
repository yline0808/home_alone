package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AdapterNotice extends ArrayAdapter<NoticeDTO> {
    public AdapterNotice(@NonNull Context context, int resource) {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_coummunity, null);

            viewHolder = new ViewHolder();
            viewHolder.txtvTitle = (TextView)convertView.findViewById(R.id.txtvTitle);
            viewHolder.txtvSummary = (TextView)convertView.findViewById(R.id.txtvSummary);
            viewHolder.txtvTime = (TextView)convertView.findViewById(R.id.txtvTime);
            viewHolder.txtvCommentCnt = (TextView)convertView.findViewById(R.id.txtvCommentCnt);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        NoticeDTO noticeDTO = getItem(position);
        viewHolder.txtvTitle.setText(noticeDTO.getTitle());
        viewHolder.txtvSummary.setText(noticeDTO.getSummary());
        viewHolder.txtvTime.setText(noticeDTO.getTime());
        viewHolder.txtvCommentCnt.setText(""+noticeDTO.getCommentCnt());

        return convertView;
    }

    @Nullable
    @Override
    public NoticeDTO getItem(int position) {
        return super.getItem(position);
    }

    private class ViewHolder{
        TextView txtvTitle;
        TextView txtvSummary;
        TextView txtvTime;
        TextView txtvCommentCnt;
    }
}
