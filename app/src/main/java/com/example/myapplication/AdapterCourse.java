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

public class AdapterCourse extends ArrayAdapter<CourseDTO> {
    public AdapterCourse(@NonNull Context context, int resource) {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_course, null);

            viewHolder = new ViewHolder();
            viewHolder.title = (TextView)convertView.findViewById(R.id.txtvTitle);
            viewHolder.time = (TextView)convertView.findViewById(R.id.txtvTime);
            viewHolder.km = (TextView)convertView.findViewById(R.id.txtvKm);
            viewHolder.slope = (ImageView)convertView.findViewById(R.id.imgvSlop);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        CourseDTO courseDTO = getItem(position);
        viewHolder.title.setText(courseDTO.getTitle());
        viewHolder.time.setText(String.format("%.2f 분", courseDTO.getKm()/5.0 * 60.0));
        viewHolder.km.setText("" + courseDTO.getKm() + " km");
        viewHolder.slope.setVisibility((courseDTO.getFlat() ? View.VISIBLE : View.INVISIBLE));

        return convertView;
    }
    private class ViewHolder{
        TextView title;
        TextView time;
        TextView km;
        ImageView slope;
    }
}
