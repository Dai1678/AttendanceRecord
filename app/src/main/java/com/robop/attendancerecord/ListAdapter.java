package com.robop.attendancerecord;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ListAdapter extends ArrayAdapter<ListItemModel>  {

    private int resource;
    private List<ListItemModel> listItems;
    private LayoutInflater layoutInflater;

    ListAdapter(@NonNull Context context, @LayoutRes int resource, List<ListItemModel> listItems) {
        super(context, resource, listItems);

        this.resource = resource;
        this.listItems = listItems;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private static class ViewHolder {
        private TextView classNumHolder;    //◯限
        private TextView subjectNameHolder; //教科名
        private TextView attendNumHolder;   //出席回数
        private TextView absentNumHolder;   //欠席回数
        private TextView lateNumHolder;     //遅刻回数
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent){
        ViewHolder viewHolder;

        if (convertView == null){
            convertView = this.layoutInflater.inflate(this.resource, null);
            viewHolder = new ViewHolder();

            viewHolder.classNumHolder = convertView.findViewById(R.id.classNum);
            viewHolder.subjectNameHolder =  convertView.findViewById(R.id.subjectName);
            viewHolder.attendNumHolder = convertView.findViewById(R.id.attendRecord);
            viewHolder.absentNumHolder = convertView.findViewById(R.id.absentRecord);
            viewHolder.lateNumHolder = convertView.findViewById(R.id.lateRecord);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ListItemModel listItem = this.listItems.get(position);     //ListView内に表示する情報

        viewHolder.classNumHolder.setText(position+1 + "限");
        viewHolder.subjectNameHolder.setText(listItem.getSubjectName());
        viewHolder.attendNumHolder.setText("出席 " + String.valueOf(listItem.getAttendNum()) + "回");
        viewHolder.absentNumHolder.setText("欠席 " + String.valueOf(listItem.getAbsentNum()) + "回");
        viewHolder.lateNumHolder.setText("遅刻 " + String.valueOf(listItem.getLateNum()) + "回");

        return convertView;
    }
}