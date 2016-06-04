package com.example.administrator.ebook.importbook;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.ebook.R;
import com.example.administrator.ebook.database.BookData;
import com.example.administrator.ebook.database.BookSetContent;

import org.litepal.crud.DataSupport;

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
                    if(o.getHasImport()){
                        Toast.makeText(getApplicationContext(), "不能重复导入", Toast.LENGTH_SHORT).show();
                    }
                    else if(o.getType().equals("txt"))
                        onFileClick(o);
                    else
                        Toast.makeText(getApplicationContext(), "目前不支持导入该格式文件", Toast.LENGTH_SHORT).show();
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
                    String fileName = ff.getName();
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
                    boolean hasImport;
                    List<BookData> bookDataList = DataSupport.where("path = ?", ff.getAbsolutePath()).find(BookData.class);
                    List<BookSetContent> bookSetContentList = DataSupport.where("path = ?", ff.getAbsolutePath()).find(BookSetContent.class);
                    if(!bookDataList.isEmpty()||!bookSetContentList.isEmpty()){
                        hasImport = true;//表示已经导入
                    }
                    else
                        hasImport = false;

                    if(prefix.equals("txt"))
                        fls.add(new Item(ff.getName(),msg,  ff.getAbsolutePath(),"txt",hasImport));
                    else if(prefix.equals("epub"))
                        fls.add(new Item(ff.getName(),msg,  ff.getAbsolutePath(),"epub",hasImport));
                    else
                        fls.add(new Item(ff.getName(),msg,  ff.getAbsolutePath(),"file",hasImport));
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

        Intent intent = new Intent();
//        intent.putExtra("GetType", o.getType());
        intent.putExtra("GetFilePath", o.getPath());
        intent.putExtra("GetFileName", o.getName());
        setResult(RESULT_OK, intent);
        finish();
    }

}
