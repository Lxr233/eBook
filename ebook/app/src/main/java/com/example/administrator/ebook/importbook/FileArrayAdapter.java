package com.example.administrator.ebook.importbook;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.ebook.R;

import java.util.List;

/**
 * Created by Lxr on 2016/6/3.
 */
public class FileArrayAdapter extends ArrayAdapter<Item> {

    private Context c;
    private int id;
    private List<Item>items;

    public FileArrayAdapter(Context context, int textViewResourceId,
                            List<Item> objects) {
        super(context, textViewResourceId, objects);
        c = context;
        id = textViewResourceId;
        items = objects;
    }
    public Item getItem(int i)
    {
        return items.get(i);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(id, null);
        }


        final Item o = items.get(position);
        if (o != null) {
            TextView t1 = (TextView) v.findViewById(R.id.file_name);
            TextView t2 = (TextView) v.findViewById(R.id.file_msg);

                       /* Take the ImageView from layout and set the city's image */
            ImageView img = (ImageView) v.findViewById(R.id.file_img);
            switch (o.getType()){
                case "file":
                    img.setImageResource(R.drawable.file);
                    break;
                case "txt":
                    img.setImageResource(R.drawable.txt);
                    break;
                case "epub":
                    img.setImageResource(R.drawable.epub);
                    break;
                case "directory":
                    img.setImageResource(R.drawable.folder);
                    break;
                case "directory_up":
                    img.setImageResource(R.drawable.folder);
                    break;
                default:
                    break;
            }

            t1.setText(o.getName());
            t2.setText(o.getMsg());



        }
        return v;
    }
}