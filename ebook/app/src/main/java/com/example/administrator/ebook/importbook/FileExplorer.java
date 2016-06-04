package com.example.administrator.ebook.importbook;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.ebook.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Lxr on 2016/6/3.
 */
public class FileExplorer extends Activity {

    ListView listView;

    private File currentDir;
    private FileArrayAdapter adapter;
    private TextView pathTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.fileexplorer);
        listView = (ListView)findViewById(R.id.file_list);
        pathTv = (TextView)findViewById(R.id.file_path);

        File root = new File("/mnt/sdcard/");
        inflateListView(root);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Item o = adapter.getItem(position);
                if (o.getType().equalsIgnoreCase("directory") || o.getType().equalsIgnoreCase("directory_up")) {
                    currentDir = new File(o.getPath());
                    inflateListView(currentDir);
                } else {
                    onFileClick(o);
                }
            }
        });

    }
    private void inflateListView(File f){
        pathTv.setText("路径："+f.getAbsolutePath());
        File[] dirs = f.listFiles();

        List<Item> dir = new ArrayList<Item>();
        List<Item>fls = new ArrayList<Item>();
        try {
            for (File ff : dirs){
                if(ff.isDirectory()){
                    File[] fbuf = ff.listFiles();
                    int buf = 0;
                    if(fbuf != null){
                        buf = fbuf.length;
                    }
                    else buf = 0;
                    String num_item = String.valueOf(buf);
                    num_item+="个文件";

                    //String formated = lastModDate.toString();
                    dir.add(new Item(ff.getName(),num_item,ff.getAbsolutePath(),"directory"));

                }
                else
                {

//
//                    String fileCode=(String)System.getProperties().get("file.encoding");
                    String fileName = ff.getName();
//                    fileName = new String (fileName.getBytes(fileCode),fileCode);
                    String prefix=fileName.substring(fileName.lastIndexOf(".") + 1);
                    System.out.println("后缀"+prefix);

                    String size;
                    if(ff.length()<1024)
                        size = ff.length()+"Byte";
                    else if(ff.length()/1024<1024)
                        size = ff.length()/1024+"KB";
                    else
                        size = ff.length()/1024/1024+"MB";

                    String msg;
                    msg = "类型:"+prefix+" / 大小:"+size;

                    if(prefix.equals("txt"))
                        fls.add(new Item(ff.getName(),msg,  ff.getAbsolutePath(),"txt"));
                    else if(prefix.equals("epub"))
                        fls.add(new Item(ff.getName(),msg,  ff.getAbsolutePath(),"epub"));
                    else
                        fls.add(new Item(ff.getName(),msg,  ff.getAbsolutePath(),"file"));
                }
            }
        }
        catch(Exception e) {

        }
        Collections.sort(dir);
        Collections.sort(fls);
        dir.addAll(fls);
        System.out.println(dir);
        if(!f.getName().equalsIgnoreCase("sdcard"))
            dir.add(0,new Item("..","返回上级",f.getParent(),"directory_up"));
        adapter = new FileArrayAdapter(FileExplorer.this, R.layout.file_view,dir);
        listView.setAdapter(adapter);
    }

    private void onFileClick(Item o)
    {
        //Toast.makeText(this, "Folder Clicked: "+ currentDir, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra("GetPath", currentDir.toString());
        intent.putExtra("GetFileName", o.getPath());
        setResult(RESULT_OK, intent);
        finish();
    }

}
