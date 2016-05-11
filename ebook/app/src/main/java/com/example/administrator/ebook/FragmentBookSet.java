package com.example.administrator.ebook;

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
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentBookSet extends Fragment {
    EditText et;
    LinearLayout linearLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookset, container, false);
        et =  (EditText)view.findViewById(R.id.fragment_bookset_et);
        linearLayout = (LinearLayout)view.findViewById(R.id.fragment_bookset_layout);

        //设置毛玻璃效果的背景
        Bitmap bitmap = getWindowBitmap(getActivity().getWindow().getDecorView());
        bitmap = blur(bitmap, 10);
        linearLayout.setBackground(new BitmapDrawable(getResources(), bitmap));
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                Fragment fragment = manager.findFragmentByTag("bookset");
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.remove(fragment);
                transaction.commit();
            }
        });
        if (getArguments() != null) {
            Integer mParam1 = getArguments().getInt("position");

//            et.setText(String.valueOf(mParam1));
        }
        return view;

    }

    public void onResume(){
        super.onResume();

    }
    private Bitmap getWindowBitmap(View view) {
        // 获取windows中最顶层的view
        view.buildDrawingCache();

        // 获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeights = rect.top;
        Display display = getActivity().getWindowManager().getDefaultDisplay();

        // 获取屏幕宽和高
        int widths = display.getWidth();
        int heights = display.getHeight();

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
        matrix.postScale(1/scaleFactor, 1/scaleFactor);
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

}