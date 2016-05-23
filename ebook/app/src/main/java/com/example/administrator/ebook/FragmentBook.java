package com.example.administrator.ebook;


import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.LruCache;
import android.util.ArrayMap;
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
import com.example.administrator.ebook.database.BookSetContent;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import android.os.Handler;
/**
 * Created by Lxr on 2016/4/12.
 */
public class FragmentBook extends Fragment {

    private boolean isFirst = true;
    private GridView gridView;
    //    private List<Map<String, Object>> data;
    Map<View, Integer> viewMap;
    private WindowManager windowManager;
    private int screenHeight,screenWidth;
    private int gridViewItemWidth,gridViewItemHeight;
    private ScrollView scrollView;
    private double tabHeight;
    private ImageView delImg;

    public static MyAdapter adapter;

    public static List<BookData> bookDataList ;
    private LruCache<String, Bitmap> mMemoryCache;
    private double moveY,mUpScrollBorder,mDownScrollBorder;



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
        initCache();
    }


    private void initCache(){
        // 获取到可用内存的最大值，使用内存超出这个值会引起OutOfMemory异常。
        // LruCache通过构造函数传入缓存值，以KB为单位。
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // 使用最大可用内存值的1/4作为缓存的大小。
        int cacheSize = maxMemory / 4;
        System.out.println("cachesize is" + cacheSize);
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // 重写此方法来衡量每张图片的大小，默认返回图片数量。
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    //resId是图片资源的id
    public void loadBookBitmap(int resId,ImageView imageView , int position ) {
        final String imageKey = String.valueOf(resId)+String.valueOf(0);
        final Bitmap bitmap = getBitmapFromMemCache(imageKey);
        ViewGroup.LayoutParams imgParams = imageView.getLayoutParams();
        imgParams.width = gridViewItemWidth-2;
        imgParams.height = gridViewItemHeight;
        imageView.setLayoutParams(imgParams);
        imageView.setTag(String.valueOf(position));
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            new BookImgTask(imageView,gridViewItemWidth,gridViewItemHeight).execute(resId,0); //0代表是书架中的大图，1代表书集合中的小图
        }
    }

    public void loadBookSetBitmap(int resId,ViewHolderBookSet viewHolderBookSet, int contentWidth , int contentHeight,int position, int i ) {
        final String imageKey = String.valueOf(resId)+String.valueOf(1);
        final Bitmap bitmap = getBitmapFromMemCache(imageKey);

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

        ImageView contentImg = new ImageView(getContext());

        if (bitmap != null) {
            contentImg.setImageBitmap(bitmap);
        } else {
            new BookImgTask(contentImg ,contentWidth ,contentHeight).execute(resId,1);
        }

        viewHolderBookSet.gridLayout.addView(contentImg, params);

    }

    private class BookImgTask extends AsyncTask<Integer, Integer, Bitmap>  {
        private int mPosition;
        private ImageView imageView;
        private int width,height;
        public BookImgTask( ImageView imageView ,int width , int height) {
            this.imageView = imageView;
            this.width = width;
            this.height = height;
        }
        @Override
        protected Bitmap doInBackground(Integer... params) {
            Bitmap bitmap;
            bitmap =  Singleton.getInstance().decodeSampledBitmapFromResource
                    (getResources(),params[0], width, height);
            addBitmapToMemoryCache(String.valueOf(params[0])+String.valueOf(params[1]), bitmap);
            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }


    private void initDatabase(){
        sharedPreferences = getActivity().getSharedPreferences("temp", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        BookData bookData;


        //第一次登陆时的初始化
        if(!sharedPreferences.contains("times")){
            editor.putInt("times",1);
            editor.commit();
            for(int i =1 ; i<=4;i++){
                bookData = new BookData();
                bookData.setImg(R.drawable.book1);
                bookData.setName("DATA A LIVE 9");
                bookData.setMsg("已读 0%");
                bookData.setType(0);
                bookData.save();
                bookDataList.add(bookData);

                bookData = new BookData();
                bookData.setImg(R.drawable.book2);
                bookData.setName("DATA A LIVE 10");
                bookData.setMsg("已读 50%");
                bookData.setType(0);
                bookData.save();
                bookDataList.add(bookData);

                bookData = new BookData();
                bookData.setImg(R.drawable.book3);
                bookData.setName("DATA A LIVE 11");
                bookData.setMsg("已读 100%");
                bookData.setType(0);
                bookData.save();
                bookDataList.add(bookData);
            }
        }
        else{
            bookDataList =  DataSupport.findAll(BookData.class);
            System.out.println("database is " + bookDataList);
        }
    }

    private void init(){

        delImg = (ImageView)getActivity().findViewById(R.id.delete_img);
        delImg.setTranslationX(100f);
        //获取屏幕大小
        windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels ;

        tabHeight = dptopx(getContext(),30);
        mDownScrollBorder = (screenHeight-tabHeight)*5/6;
        mUpScrollBorder = (screenHeight-tabHeight)*1/6;

        gridViewItemWidth = (int)((screenWidth-dptopx(getContext(),80)) / (3*1.1));
        gridViewItemHeight = (int)(gridViewItemWidth*1.4);

        gridView.setColumnWidth((int) (gridViewItemWidth * 1.1));


        //初始化单例类，保存一些全局的变量
        Singleton s1 = Singleton.getInstance();
        s1.setGridViewItemHeight(gridViewItemHeight);
        s1.setGridViewItemWidth(gridViewItemWidth);
        s1.setScreenHeight(screenHeight);
        s1.setScreenWidth(screenWidth);

        viewMap = new HashMap<View, Integer>();
        bookDataList = new ArrayList<BookData>();


        adapter = new MyAdapter(getContext());
        gridView.setAdapter(adapter);
        scrollView.smoothScrollTo(0, 20);
        scrollView.setOnDragListener(new mScrollViewDragListen());
        delImg.setOnDragListener(new mDeleteImageViewDragListen());
//        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
//                System.out.println("item 被长按" + position);
//                return false;
//            }
//        });
    }




    static class ViewHolderBook{
        TextView name,msg;
        ImageView img;
        int position;
    }
    static class ViewHolderBookSet{
        TextView name,msg;
        GridLayout gridLayout;
        int position;
    }

    public class MyAdapter extends BaseAdapter
    {
        private final int TYPE_BOOK=0,TYPE_BOOKSET=1,TYPE_COUNT=2;
        private LayoutInflater mInflater = null;
        private MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return bookDataList.size();
        }
        @Override
        public int getViewTypeCount() {
            return TYPE_COUNT;
        }
        @Override
        public int getItemViewType(int position) {
            return bookDataList.get(position).getType();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            BookData bookData = bookDataList.get(position);

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
                    viewHolderBook.name.setText(bookData.getName());
                    viewHolderBook.msg.setText(bookData.getMsg());

                    //使用缓存并异步加载图片
                    viewHolderBook.position = position;
                    loadBookBitmap(bookData.getImg(), viewHolderBook.img , position);

                    convertView.setOnLongClickListener(new View.OnLongClickListener() {

                        // 定义接口的方法，长按View时会被调用到
                        public boolean onLongClick(View v) {
                            ClipData.Item item = new ClipData.Item(String.valueOf(position));
                            ClipData data = new ClipData("book-" + String.valueOf(position), new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
                            //ClipData data = ClipData.newPlainText("position", "1");
                            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                            v.startDrag(data, shadowBuilder, v, 0);
                            v.setAlpha(0.3f);
//                            delImg.setVisibility(View.VISIBLE);
                            delImg.animate().translationX(0f).setDuration(300);

                            return false;
                        }
                    });
                    convertView.setOnDragListener(new mBookDragListen());
                    viewMap.put(convertView,position);
                    //convertView.setOnTouchListener(new mTouchListen());
                    break;
                case TYPE_BOOKSET:
                    viewHolderBookSet.name.setText(bookData.getName());
                    viewHolderBookSet.msg.setText(bookData.getMsg());
                    viewHolderBookSet.position = position;

                    ViewGroup.LayoutParams Params = viewHolderBookSet.gridLayout.getLayoutParams();
                    Params.width = gridViewItemWidth-2;
                    Params.height = gridViewItemHeight;
                    viewHolderBookSet.gridLayout.setLayoutParams(Params);

                    List<BookSetContent> contentList= new ArrayList<BookSetContent>();
                    contentList = bookData.getContentList();


                    int contentWidth = (gridViewItemWidth - 3*dptopx(getContext(),5))/2;
                    int contentHeight =(int) (contentWidth *1.4);

                    //为了避免出现因为复用convertView导致的图片重复出现的bug，先将gridlayout的子view置空
                    viewHolderBookSet.gridLayout.removeAllViews();

                    //为gridlayout添加子view
                    for(int i=0,len = contentList.size();i<len;i++){
                        if(i==4)
                            break;
                        //使用缓存并异步加载图片
                        loadBookSetBitmap(contentList.get(i).getImg(), viewHolderBookSet, contentWidth, contentHeight ,position,i );

                    }
                    viewMap.put(convertView, position);
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FragmentBookSet fragment = FragmentBookSet.newInstance(position);
                            FragmentManager manager = getFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            transaction.add(R.id.drawer_layout, fragment, "bookset");
                            transaction.commit();
                        }
                    });
                    convertView.setOnLongClickListener(new View.OnLongClickListener() {

                        // 定义接口的方法，长按View时会被调用到
                        public boolean onLongClick(View v) {
                            ClipData.Item item = new ClipData.Item(String.valueOf(position));
                            ClipData data = new ClipData("bookset-" + String.valueOf(position), new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
                            //ClipData data = ClipData.newPlainText("position", "1");
                            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                            v.startDrag(data, shadowBuilder, v, 0);
                            v.setAlpha(0.3f);
//                            delImg.setVisibility(View.VISIBLE);
                            delImg.animate().translationX(0f).setDuration(300);

                            return false;
                        }
                    });
                    convertView.setOnDragListener(new mBookDragListen());


                    break;
            }
            return convertView;
        }

    }


    private Handler mHandler = new Handler();
    private Runnable mScrollRunnable = new Runnable() {

        @Override
        public void run() {
            int scrollY;
            if(moveY > mDownScrollBorder){
                scrollY = 10;
                mHandler.postDelayed(mScrollRunnable, 25);
            }else if(moveY < mUpScrollBorder){
                scrollY = -10;
                mHandler.postDelayed(mScrollRunnable, 25);
            }else{
                scrollY = 0;
                mHandler.removeCallbacks(mScrollRunnable);
            }
            scrollView.smoothScrollBy(0,scrollY);
        }
    };




    private class mDeleteImageViewDragListen implements View.OnDragListener  {
        int eventPosition;
        int viewPosition;
        int viewType ;
        public boolean onDrag(View v, DragEvent event) {
            final int action = event.getAction();
            String[] label;
            String type;
            switch(action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    label = event.getClipDescription().getLabel().toString().split("-");
                    type = label[0];
                    if(type.equals("bookset") ){
                        viewType =1;
                    }
                    else if(type.equals("book")){
                        viewType = 0;
                    }
                    else
                        viewType = 2;
                    System.out.println("type:"+viewType);
                    eventPosition = Integer.parseInt(label[1]);
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    delImg.setImageResource(R.drawable.intrash);
                    delImg.animate().scaleX(1.2f);
                    delImg.animate().scaleY(1.2f);
                    return(true);
                case DragEvent.ACTION_DRAG_LOCATION:
                    return(true);
                case DragEvent.ACTION_DRAG_EXITED:
                    delImg.setImageResource(R.drawable.trash);
                    delImg.animate().scaleX(1f);
                    delImg.animate().scaleY(1f);
                    return(true);
                case DragEvent.ACTION_DROP:
                    if(viewType==2){
                        int p = Integer.parseInt(event.getClipData().getItemAt(0).getText().toString());
                        BookData bookData = bookDataList.get(p);
                        bookData.setContentCount(bookData.getContentCount()-1);
                        bookData.setMsg("共 "+bookData.getContentCount()+"本");
                        FragmentBookSet.notifyContent(eventPosition);
                    }
                    else {
                        DataSupport.delete(BookData.class, bookDataList.get(eventPosition).getId());
                        if(viewType==1){
                            DataSupport.deleteAll(BookSetContent.class, "bookdata_id = ?",String.valueOf(bookDataList.get(eventPosition).getId() ));
                        }
                        bookDataList.remove(eventPosition);

                    }
                    adapter.notifyDataSetChanged();

                    return(true);
                case DragEvent.ACTION_DRAG_ENDED:
                    delImg.setImageResource(R.drawable.trash);
                    delImg.animate().scaleX(1f);
                    delImg.animate().scaleY(1f);
                    return true;
                // 收到一个未知的action type
                default:
                    Log.e("DragDrop Example", "Unknown action type received by OnDragListener.");
                    break;
            }
            return false;
        }
    }
    private class mScrollViewDragListen implements View.OnDragListener{
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
                    moveY = event.getY();
                    mHandler.post(mScrollRunnable);
                    if(moveY < mDownScrollBorder && moveY > mUpScrollBorder){
                        mHandler.removeCallbacks(mScrollRunnable);
                    }
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
            return false;
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
                    if(bookDataList.get(viewPosition).getType()==0){
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
                    if(bookDataList.get(viewPosition).getType()==0){
                        if(!(viewMap.get(v) == eventPosition)){
                            ImageView img = (ImageView)v.findViewById(R.id.book_img);
                            img.animate().scaleX(1.0f);
                            img.animate().scaleY(1.0f);

                            img.setImageResource(bookDataList.get(viewMap.get(v)).getImg());
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
                    if(bookDataList.get(viewPosition).getType()==0){
                        //如果放下的位置跟拖拽的view的位置一样，则将拖拽的view恢复原本的透明度
                        if(viewPosition == eventPosition){
                            v.setAlpha(1f);
                        }
                        else{
//                                BookData bookData = DataSupport.find(BookData.class, bookDataList.get(viewPosition).getId());
                            BookData bookData = bookDataList.get(viewPosition);


                            BookSetContent content = new BookSetContent();
                            content.setImg(bookDataList.get(viewPosition).getImg());
                            content.setName(bookDataList.get(viewPosition).getName());
                            content.setMsg(bookDataList.get(viewPosition).getMsg());
                            content.setBookdata_id(bookDataList.get(viewPosition).getId());
                            content.save();


                            content = new BookSetContent();
                            content.setImg(bookDataList.get(eventPosition).getImg());
                            content.setName(bookDataList.get(eventPosition).getName());
                            content.setMsg(bookDataList.get(eventPosition).getMsg());
                            content.setBookdata_id(bookDataList.get(viewPosition).getId());
                            content.save();

                            bookData.setName("未命名分组");
                            bookData.setMsg("共 2本");
                            bookData.setType(1);
                            bookData.setContentCount(2);
                            bookData.save();

//                                bookDataList.set(viewPosition, bookData);

                            DataSupport.delete(BookData.class, bookDataList.get(eventPosition).getId());
                            bookDataList.remove(eventPosition);
//                            viewMap = new HashMap<View, Integer>();

                        }
                    }
                    else{
                        //放下后恢复书籍集合的原本大小
                        GridLayout gridLayout = (GridLayout)v.findViewById(R.id.bookset_gridlayout);
                        gridLayout.setPivotX(0);
                        gridLayout.setPivotY(0);
                        gridLayout.animate().scaleX(1.0f);
                        gridLayout.animate().scaleY(1.0f);
                        //update 对应数据
//                            BookData bookData = DataSupport.find(BookData.class, bookDataList.get(viewPosition).getId());
                        BookData bookData = bookDataList.get(viewPosition);

                        BookSetContent content = new BookSetContent();
                        content.setImg(bookDataList.get(eventPosition).getImg());
                        content.setName(bookDataList.get(eventPosition).getName());
                        content.setMsg(bookDataList.get(eventPosition).getMsg());
                        content.setBookdata_id(bookData.getId());
                        content.save();


                        bookData.setContentCount(bookData.getContentList().size());
                        bookData.setMsg("共 " + bookData.getContentCount() + "本");
                        bookData.save();
//                            bookDataList.set(viewPosition, bookData);
                        DataSupport.delete(BookData.class, bookDataList.get(eventPosition).getId());


                        bookDataList.remove(eventPosition);
                    }
                    adapter.notifyDataSetChanged();
                    System.out.println("size is "+bookDataList.size());
                    return(true);
                case DragEvent.ACTION_DRAG_ENDED:
                    delImg.animate().translationX(100f).setDuration(300);
//                    delImg.setVisibility(View.GONE);
                    v.setAlpha(1.0f);
                    return true;
                // 收到一个未知的action type
                default:
                    Log.e("DragDrop Example", "Unknown action type received by OnDragListener.");
                    break;
            }
            return false;
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dptopx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }








}