package com.example.main.simplemp3_2.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.main.simplemp3_2.R;
import java.util.ArrayList;



public class FolderAdapter extends BaseAdapter {
    private static String TAG = "FolderAdapter";
    private ArrayList<String> fileStr;
    private LayoutInflater inflate_folder;

    public FolderAdapter(Context context,ArrayList<String> fileStr){
        inflate_folder = LayoutInflater.from(context);
        this.fileStr = fileStr;
    }

    public static class ViewHolder{
        TextView txv_folderName;
        ImageButton btn_setting;

        public ViewHolder(ImageButton btn_setting,TextView txv_folderName){
            this.btn_setting = btn_setting;
            this.txv_folderName = txv_folderName;
            btn_setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "onClick: ~~~~~~~~~~~~~~~~~~~~~~~~~~");
                }
            });
        }
    }

    @Override
    public int getCount() {
        return fileStr.size();
    }

    @Override
    public Object getItem(int i) {
        return 0;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View converView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(converView == null){
            converView = inflate_folder.inflate(R.layout.item_folder,null);
            TextView txv_folderName = converView.findViewById(R.id.txv_folderName);
            ImageButton btn_setting = converView.findViewById(R.id.btn_setting);
            viewHolder = new ViewHolder(btn_setting,txv_folderName);
            converView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) converView.getTag();
        }

        viewHolder.txv_folderName.setText(fileStr.get(position));

        return converView;
    }


}
