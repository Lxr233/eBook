package com.example.administrator.ebook;


import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.view.WindowManager;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.ebook.database.BookData;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lxr on 2016/4/12.
 */
public class FragmentBook extends Fragment {

    private GridView gridView;
    private List<Map<String, Object>> data;
    Map<View, Integer> viewMap;
    private WindowManager windowManager;
    private int screenHeight,screenWidth;
    private int gridViewItemWidth,gridViewItemHeight;
    private ScrollView scrollView;
    private MyAdapter adapter;
    private BookData bookData;
    private List<BookData> bookList;


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view=inflater.inflate(R.layout.fragment_book, container, false);

        SQLiteDatabase db = Connector.getDatabase();
        gridView = (GridView)view.findViewById(R.id.fragment_book_gridview);
        scrollView = (ScrollView)view.findViewById(R.id.fragment_book_scroll);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        initDatabase();
    }

    private void initDatabase(){
        bookData = new BookData();
        bookData.setImg(1);
        bookData.save();
        BookData bookData2 = new BookData();
        bookData2.setImg(2);
        bookData2.save();
        BookData bookData3 = new BookData();
        bookData3.setImg(3);
        bookData3.save();
        DataSupport.delete(BookData.class, 2);
    }

    private void init(){
        //获取屏幕大小
        windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels ;

        System.out.println(screenWidth);
        gridViewItemWidth = (int)((screenWidth-dptopx(getContext(),80)) / (3*1.1));
        gridViewItemHeight = (int)(gridViewItemWidth*1.4);
        System.out.println(gridViewItemWidth);
        gridView.setColumnWidth((int)(gridViewItemWidth*1.1));
        viewMap = new HashMap<View, Integer>();

        sharedPreferences = getActivity().getSharedPreferences("temp", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //第一次登陆时的初始化
        if(!sharedPreferences.contains("times")){
            editor.putInt("times",1);
            
            editor.commit();
        }

        data = getData();
        adapter = new MyAdapter(getContext());
        gridView.setAdapter(adapter);
        scrollView.smoothScrollTo(0, 20);
        gridView.setOnDragListener(new mGridViewDragListen());

    }


    private List<Map<String, Object>> getData()
    {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> contentList= new ArrayList<Map<String, Object>>();
        Map<String, Object> map;
        for(int i=0;i<2;i++) {
            map = new HashMap<String, Object>();
            map.put("img", R.drawable.book3);
            map.put("name", "DATE A LIVE 9");
            map.put("msg", "已读 0%");
            map.put("type", 0); //0表示单本书,1表示书集合
            list.add(map);
            map = new HashMap<String, Object>();
            map.put("img", R.drawable.book1);
            map.put("name", "DATE A LIVE 10");
            map.put("msg", "已读 100%");
            map.put("type", 0); //0表示单本书,1表示书集合
            list.add(map);
            map = new HashMap<String, Object>();
            map.put("img", R.drawable.book2);
            map.put("name", "DATE A LIVE 11");
            map.put("msg", "已读 0%");
            map.put("type", 0); //0表示单本书,1表示书集合
            list.add(map);
        }
//
//        for(int i=0;i<2;i++){
//            map = new HashMap<String, Object>();
//            map.put("img", R.drawable.book2);
//            map.put("name", "DATE A LIVE 10");
//            map.put("msg", "已读 100%");
//            contentList.add(map);
//        }
//        for(int i=0;i<1;i++){
//            map = new HashMap<String, Object>();
//            map.put("img", R.drawable.book3);
//            map.put("name", "DATE A LIVE 10");
//            map.put("msg", "已读 100%");
//            contentList.add(map);
//        }

//        map = new HashMap<String, Object>();
//        map.put("img", R.drawable.book2);
//        map.put("name", "DATE A LIVE ");
//        map.put("msg", "共 2本");
//        map.put("type", 1); //0表示单本书,1表示书集合
//        map.put("content",contentList);
//        list.add(map);

//        for(int i=0;i<5;i++)
//        {
//            map = new HashMap<String, Object>();
//            map.put("img", R.drawable.bookset);
//            map.put("name", "DATE A LIVE");
//            map.put("msg", "总共 5本");
//            map.put("type", 1); //0表示单本书,1表示书集合
//            list.add(map);
//        }
        return list;
    }

    static class ViewHolderBook{
        TextView name,msg;
        ImageView img;
    }
    static class ViewHolderBookSet{
        TextView name,msg;
        GridLayout gridLayout;
    }

    public class MyAdapter extends BaseAdapter
    {
        private final int TYPE_BOOK=0,TYPE_BOOKSET=1,TYPE_COUNT=2;
        private LayoutInflater mInflater = null;
        private MyAdapter(Context context)
        {
            //根据context上下文加载布局，这里的是Demo17Activity本身，即this
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            //How many items are in the data set represented by this Adapter.
            //在此适配器中所代表的数据集中的条目数
            return data.size();
        }

        /** 该方法返回多少个不同的布局*/
        @Override
        public int getViewTypeCount() {
            // TODO Auto-generated method stub
            return TYPE_COUNT;
        }
        /** 根据position返回相应的Item*/
        @Override
        public int getItemViewType(int position) {
            // TODO Auto-generated method stub
            return (int)data.get(position).get("type");
        }

        @Override
        public Object getItem(int position) {
            // Get the data item associated with the specified position in the data set.
            //获取数据集中与指定索引对应的数据项
            return position;
        }

        @Override
        public long getItemId(int position) {
            //Get the row id associated with the specified position in the list.
            //获取在列表中与指定索引对应的行id
            return position;
        }

        //Get a View that displays the data at the specified position in the data set.
        //获取一个在数据集中指定索引的视图来显示数据
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            int type=getItemViewType(position);
            ViewHolderBook viewHolderBook=null;
            ViewHolderBookSet viewHolderBookSet=null;
            if(convertView==null){
                switch (type) {
                    case TYPE_BOOK:
                        viewHolderBook=new ViewHolderBook();
                        convertView=mInflater.inflate(R.layout.book, null);
                        viewHolderBook.name=(TextView) convertView.findViewById(R.id.book_name);
                        viewHolderBook.msg=(TextView) convertView.findViewById(R.id.book_msg);
                        viewHolderBook.img = (ImageView)convertView.findViewById(R.id.book_img);
                        convertView.setTag(viewHolderBook);
                        break;

                    case TYPE_BOOKSET:
                        viewHolderBookSet=new ViewHolderBookSet();
                        convertView=mInflater.inflate(R.layout.bookset, null);
                        viewHolderBookSet.name=(TextView) convertView.findViewById(R.id.bookset_name);
                        viewHolderBookSet.msg=(TextView) convertView.findViewById(R.id.bookset_msg);
                        viewHolderBookSet.gridLayout = (GridLayout)convertView.findViewById(R.id.bookset_gridlayout);
                        convertView.setTag( viewHolderBookSet);
                        break;
                }
            }
            else{
                switch (type) {
                    case TYPE_BOOK:
                        viewHolderBook=(ViewHolderBook) convertView.getTag();
                        break;
                    case TYPE_BOOKSET:
                        viewHolderBookSet=(ViewHolderBookSet) convertView.getTag();
                        break;
                }
            }


            switch (type){
                case TYPE_BOOK:
                    viewHolderBook.name.setText((String) data.get(position).get("name"));
                    viewHolderBook.msg.setText((String) data.get(position).get("msg"));
                    ViewGroup.LayoutParams imgParams = viewHolderBook.img.getLayoutParams();
                    imgParams.width = gridViewItemWidth-2;
                    imgParams.height = gridViewItemHeight;
                    viewHolderBook.img.setLayoutParams(imgParams);
                    viewHolderBook.img.setImageBitmap(
                            decodeSampledBitmapFromResource(getResources(), (Integer) data.get(position).get("img"), gridViewItemWidth, gridViewItemHeight));

                    convertView.setOnLongClickListener(new View.OnLongClickListener() {

                        // 定义接口的方法，长按View时会被调用到
                        public boolean onLongClick(View v) {
                            ClipData.Item item = new ClipData.Item(String.valueOf(position));
                            ClipData data = new ClipData("book-" + String.valueOf(position), new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
                            //ClipData data = ClipData.newPlainText("position", "1");
                            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                            v.startDrag(data, shadowBuilder, v, 0);
                            v.setAlpha(0.3f);
                            return false;
                        }
                    });
                    convertView.setOnDragListener(new mBookDragListen());
                    viewMap.put(convertView,position);
                    //convertView.setOnTouchListener(new mTouchListen());
                    break;
                case TYPE_BOOKSET:
                    viewHolderBookSet.name.setText((String) data.get(position).get("name"));
                    viewHolderBookSet.msg.setText((String) data.get(position).get("msg"));

                    ViewGroup.LayoutParams Params = viewHolderBookSet.gridLayout.getLayoutParams();
                    Params.width = gridViewItemWidth-2;
                    Params.height = gridViewItemHeight;
                    viewHolderBookSet.gridLayout.setLayoutParams(Params);

                    List<Map<String, Object>> contentList= new ArrayList<Map<String, Object>>();
                    contentList = (ArrayList<Map<String, Object>>)data.get(position).get("content");

                    int contentWidth = (gridViewItemWidth - 3*dptopx(getContext(),5))/2;
                    int contentHeight =(int) (contentWidth *1.4);
                    //为gridlayout添加子view
                    for(int i=0,len = contentList.size();i<len;i++){

                        if(i==4)
                            break;
                        ImageView contentImg = new ImageView(getContext());

                        contentImg.setImageBitmap(
                                decodeSampledBitmapFromResource(getResources(), (Integer)contentList.get(i).get("img"), contentWidth, contentHeight));
                        //指定子组件所在行
                        GridLayout.Spec rowSpec;
                        if(i<2){
                            rowSpec = GridLayout.spec(0);
                        }
                        else{
                            rowSpec = GridLayout.spec(1);
                        }
                        //指定子组件所在列
                        GridLayout.Spec columnSpec = GridLayout.spec(i%2);
                        GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec,columnSpec);
                        params.setGravity(Gravity.START);
                        params.width = contentWidth;
                        params.height = contentHeight;
                        params.leftMargin = dptopx(getContext(),5);
                        params.topMargin = dptopx(getContext(),7);
                        viewHolderBookSet.gridLayout.addView(contentImg,params);

                    }
                    viewMap.put(convertView, position);
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            System.out.println("danji");
                            FragmentBookSet fragment = FragmentBookSet.newInstance(position);
                            FragmentManager manager = getFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            transaction.add(R.id.drawer_layout, fragment,"bookset");
                            transaction.commit();
                        }
                    });
                    convertView.setOnDragListener(new mBookDragListen());


                    break;
            }





            return convertView;
        }

    }

//    private class mTouchListen implements View.OnTouchListener{
//        public boolean onTouch(View v,MotionEvent ev){
//            switch(ev.getAction()){
//                case MotionEvent.ACTION_DOWN:
//                    System.out.println("touch x down is "+(int) ev.getX());
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    System.out.println("touch x move is "+(int) ev.getX());
//                    break;
//                case MotionEvent.ACTION_UP:
//                    System.out.println("touch x up is "+(int) ev.getX());
//                    break;
//            }
//            return false;
//        }
//
//
//    }


    private class mGridViewDragListen implements View.OnDragListener{
        // 这是系统向侦听器发送拖动事件时将会调用的方法
        public boolean onDrag(View v, DragEvent event) {

            // 定义一个变量，用于保存收到事件的action类型
            final int action = event.getAction();


            // 处理所有需要的事件
            switch(action) {

                case DragEvent.ACTION_DRAG_STARTED:
                    return true;


                case DragEvent.ACTION_DRAG_ENTERED:
                    return(true);



                case DragEvent.ACTION_DRAG_LOCATION:
                    return(true);


                case DragEvent.ACTION_DRAG_EXITED:
                    return(true);



                case DragEvent.ACTION_DROP:
                    return(true);



                case DragEvent.ACTION_DRAG_ENDED:
                    System.out.println("grid"+event.getResult());
                    return true;



                // 收到一个未知的action type
                default:
                    Log.e("DragDrop Example", "Unknown action type received by OnDragListener.");

                    break;
            }
            return true;
        }
    }
    private class mBookDragListen implements View.OnDragListener  {
        int eventPosition;
        int viewPosition;
        int viewType ;
        // 这是系统向侦听器发送拖动事件时将会调用的方法
        public boolean onDrag(View v, DragEvent event) {

            // 定义一个变量，用于保存收到事件的action类型
            final int action = event.getAction();
            viewPosition = viewMap.get(v);

            String[] label;
            String type;
            // 处理所有需要的事件
            switch(action) {

                case DragEvent.ACTION_DRAG_STARTED:
                    label = event.getClipDescription().getLabel().toString().split("-");
                    type = label[0];
                    eventPosition = Integer.parseInt(label[1]);
                    //若拖拽的控件是book控件，才会进行监听之后的动作
                    if (type.equals("book")){
                        return true;
                    }
                    //若拖拽的控件是bookset控件，则不会监听之后的动作
                    else
                        return false;



                case DragEvent.ACTION_DRAG_ENTERED:
                    System.out.println("view is"+(int)data.get(viewPosition).get("type"));
                    if((int)data.get(viewPosition).get("type")==0){
                        if(!(viewMap.get(v) == eventPosition)){
                            ImageView img = (ImageView)v.findViewById(R.id.book_img);
                            img.setImageResource(R.drawable.book_border);
                            img.setPivotX(0);
                            img.setPivotY(0);
                            img.animate().scaleX(1.05f);
                            img.animate().scaleY(1.05f);
                        }
                    }
                    else{
                        GridLayout gridLayout = (GridLayout)v.findViewById(R.id.bookset_gridlayout);
                        gridLayout.setPivotX(0);
                        gridLayout.setPivotY(0);
                        gridLayout.animate().scaleX(1.05f);
                        gridLayout.animate().scaleY(1.05f);
                    }



                    return(true);



                    case DragEvent.ACTION_DRAG_LOCATION:
                        return(true);



                    case DragEvent.ACTION_DRAG_EXITED:
                        if((int)data.get(viewPosition).get("type")==0){
                            if(!(viewMap.get(v) == eventPosition)){
                                ImageView img = (ImageView)v.findViewById(R.id.book_img);
                                img.animate().scaleX(1.0f);
                                img.animate().scaleY(1.0f);

                                img.setImageResource((Integer)data.get(viewMap.get(v)).get("img"));
                            }
                        }
                        else{
                            GridLayout gridLayout = (GridLayout)v.findViewById(R.id.bookset_gridlayout);
                            gridLayout.setPivotX(0);
                            gridLayout.setPivotY(0);
                            gridLayout.animate().scaleX(1.0f);
                            gridLayout.animate().scaleY(1.0f);
                        }

                        return(true);



                    case DragEvent.ACTION_DROP:
                        //如果放下的位置跟拖拽的view的位置一样，则将拖拽的view恢复原本的透明度
                        if((int)data.get(viewPosition).get("type")==0){
                            if(viewPosition == eventPosition){
                                v.setAlpha(1f);
                            }
                            else{
                                List<Map<String, Object>> contentList= new ArrayList<Map<String, Object>>();
                                Map<String, Object> map;

                                map = new HashMap<String, Object>();
                                map.put("img", data.get(viewPosition).get("img"));
                                map.put("name", data.get(viewPosition).get("name"));
                                map.put("msg", "已读 100%");
                                contentList.add(map);

                                map = new HashMap<String, Object>();
                                map.put("img", data.get(eventPosition).get("img"));
                                map.put("name", data.get(eventPosition).get("name"));
                                map.put("msg", "已读 100%");
                                contentList.add(map);

                                map = new HashMap<String, Object>();
                                map.put("img", R.drawable.book1);
                                map.put("name", "未命名分组");
                                map.put("msg", "共 2本");
                                map.put("type", 1); //0表示单本书,1表示书集合
                                map.put("content", contentList);
                                data.set(viewPosition, map);
                                data.remove(eventPosition);
//                            viewMap = new HashMap<View, Integer>();

                            }
                        }
                        else{
                            List<Map<String, Object>> contentList = (ArrayList<Map<String, Object>>)data.get(viewPosition).get("content");
                            Map<String, Object> map = new HashMap<String, Object>();;
                            map.put("img", data.get(eventPosition).get("img"));
                            map.put("name", data.get(eventPosition).get("name"));
                            map.put("msg", "已读 100%");
                            contentList.add(map);
                            data.remove(eventPosition);

                        }
                        adapter.notifyDataSetChanged();

                        return(true);



                    case DragEvent.ACTION_DRAG_ENDED:
                        v.setAlpha(1.0f);
                        return true;



                    // 收到一个未知的action type
                    default:
                        Log.e("DragDrop Example", "Unknown action type received by OnDragListener.");
                        break;
                }
            return true;
            }
        }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dptopx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }


}