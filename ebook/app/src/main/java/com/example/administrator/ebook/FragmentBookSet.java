package com.example.administrator.ebook;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.ebook.database.BookData;
import com.example.administrator.ebook.database.BookSetContent;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentBookSet extends Fragment {
    private EditText et;
    private LinearLayout linearLayout;
    private List<BookSetContent> contentList = new ArrayList<BookSetContent>();
    private GridView gridView;
    private int p; //存储传过来的bookdatalist的位置
    private WindowManager windowManager;
    private int screenHeight,screenWidth;
    private int gridViewItemWidth,gridViewItemHeight;
    private MyBookSetAdapter adapter;
    private String bookSetName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookset, container, false);
        et =  (EditText)view.findViewById(R.id.fragment_bookset_et);
        linearLayout = (LinearLayout)view.findViewById(R.id.fragment_bookset_layout);
        gridView = (GridView)view.findViewById(R.id.fragment_bookset_gridview);
        if (getArguments() != null) {
            Integer mParam1 = getArguments().getInt("position");
            p = mParam1;
            System.out.println("position is "+p);
        }

        init();
        getDatabase();
        initBackground();
        initListener();
        return view;

    }

    private void init(){
        Singleton s1 = Singleton.getInstance();
        screenWidth = s1.getScreenWidth();
        screenHeight = s1.getScreenHeight() ;
        gridViewItemHeight = s1.getGridViewItemHeight();
        gridViewItemWidth = s1.getGridViewItemWidth();

        gridView.setColumnWidth((int) (gridViewItemWidth * 1.1));
        adapter = new MyBookSetAdapter(getContext());
        gridView.setAdapter(adapter);



    }

    private void initListener(){
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("单击");
                if (et.hasFocus()) {
                    System.out.println("已经获得焦点"+et.getText().toString());
                    et.clearFocus();//取消焦点
                    ((InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(getActivity()
                                            .getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);//关闭输入法
                    if(!et.getText().toString().equals(FragmentBook.bookDataList.get(p).getName())){
                        BookData bookData = FragmentBook.bookDataList.get(p);
                        bookData.setName(et.getText().toString());
                        bookData.save();
                        FragmentBook.adapter.notifyDataSetChanged();
                    }
                }
                else{
                    FragmentManager manager = getFragmentManager();
                    Fragment fragment = manager.findFragmentByTag("bookset");
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.remove(fragment);
                    transaction.commit();
                }

            }
        });
//        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                if(!hasFocus){
//
//                }
//            }
//        });
    }

    private void initBackground(){
        //设置毛玻璃效果的背景
        Bitmap bitmap = getWindowBitmap(getActivity().getWindow().getDecorView());
        bitmap = blur(bitmap, 10);
        linearLayout.setBackground(new BitmapDrawable(getResources(), bitmap));
    }

    private void getDatabase(){
        SQLiteDatabase db = Connector.getDatabase();
        FragmentBook.bookDataList =  DataSupport.findAll(BookData.class);
        contentList = FragmentBook.bookDataList.get(p).getContentList();
        et.setText(FragmentBook.bookDataList.get(p).getName());

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    static class ViewHolderBook{
        TextView name,msg;
        ImageView img;
    }




    private Bitmap getWindowBitmap(View view) {
        // 获取windows中最顶层的view
        view.buildDrawingCache();

        // 获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeights = rect.top;
//        Display display = getActivity().getWindowManager().getDefaultDisplay();

        // 获取屏幕宽和高
        int widths = screenWidth;
        int heights = screenHeight;

        // 允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);

        // 去掉状态栏
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(), 0,
                statusBarHeights, widths, heights - statusBarHeights);

        // 销毁缓存信息
        view.destroyDrawingCache();
        return compress(bmp, widths, heights - statusBarHeights);
    }

    private static Bitmap compress(Bitmap bkg ,int width ,int height) {
        float scaleFactor = 20;// 图片缩放比例；
        Matrix matrix = new Matrix();
        matrix.postScale(1 / scaleFactor, 1 / scaleFactor);
        Bitmap bitmapBlur = Bitmap.createBitmap(bkg, 0, 0, width, height, matrix, true);
        return bitmapBlur;
    }


    //进行模糊运算
    private Bitmap blur(Bitmap overlay, int radius) {

        RenderScript rs = RenderScript.create(getContext());
        Allocation overlayAlloc = Allocation.createFromBitmap(rs, overlay);
        ScriptIntrinsicBlur blur =
                ScriptIntrinsicBlur.create(rs, overlayAlloc.getElement());
        blur.setInput(overlayAlloc);
        blur.setRadius(radius);
        blur.forEach(overlayAlloc);
        overlayAlloc.copyTo(overlay);

        rs.destroy();
        return overlay;
    }

    public static FragmentBookSet newInstance(int p) {
        FragmentBookSet fragment = new FragmentBookSet();
        Bundle args = new Bundle();
        args.putInt("position", p);
        fragment.setArguments(args);
        return fragment;
    }


    public class MyBookSetAdapter extends BaseAdapter {
        private LayoutInflater mInflater = null;

        private MyBookSetAdapter(Context context) {
            //根据context上下文加载布局，这里的是Demo17Activity本身，即this
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            //How many items are in the data set represented by this Adapter.
            //在此适配器中所代表的数据集中的条目数
            return contentList.size();
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

            ViewHolderBook viewHolderBook = null;
            if (convertView == null) {
                viewHolderBook = new ViewHolderBook();
                convertView = mInflater.inflate(R.layout.book, null);
                viewHolderBook.name = (TextView) convertView.findViewById(R.id.book_name);
                viewHolderBook.msg = (TextView) convertView.findViewById(R.id.book_msg);
                viewHolderBook.img = (ImageView) convertView.findViewById(R.id.book_img);
                convertView.setTag(viewHolderBook);

            } else {
                viewHolderBook = (ViewHolderBook) convertView.getTag();

            }
            viewHolderBook.name.setText(contentList.get(position).getName());
            viewHolderBook.msg.setText(contentList.get(position).getMsg());
            ViewGroup.LayoutParams imgParams = viewHolderBook.img.getLayoutParams();
            imgParams.width = gridViewItemWidth - 2;
            imgParams.height = gridViewItemHeight;
            viewHolderBook.img.setLayoutParams(imgParams);
            viewHolderBook.img.setImageBitmap(
                    Singleton.getInstance().decodeSampledBitmapFromResource(getResources(), contentList.get(position).getImg(), gridViewItemWidth, gridViewItemHeight));
            return convertView;
        }
    }
}