package com.example.administrator.ebook;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.example.administrator.ebook.R;

/**
 * Created by Lxr on 2016/5/21.
 */
public class BaseActivity extends FragmentActivity {

    private LinearLayout containterLayout;

    private ImageView imageView,importBook;
    private FloatingActionButton floatBt;
    private PopupWindow mPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);

        imageView = (ImageView)findViewById(R.id.delete_img);
        floatBt = (FloatingActionButton)findViewById(R.id.popuBt);
        View popupView = getLayoutInflater().inflate(R.layout.popupwindow, null);
        mPopupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mPopupWindow.setAnimationStyle(R.style.anim_menu_bottombar);
        importBook = (ImageView)mPopupWindow.getContentView().findViewById(R.id.importbook);

        initListener();

    }

    private void initListener(){
        importBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("导入");
            }
        });
        floatBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.showAtLocation(findViewById(R.id.base_layout), Gravity.BOTTOM, 0, 0);
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 0.7f;
                getWindow().setAttributes(lp);
                mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        WindowManager.LayoutParams lp = getWindow().getAttributes();
                        lp.alpha = 1f;
                        getWindow().setAttributes(lp);
                    }
                });
            }
        });
    }


    @Override
    public void setContentView(int layoutResID) {
        containterLayout = (LinearLayout)findViewById(R.id.containerLayout);
        containterLayout.removeAllViews();
        View childLayout = this.getLayoutInflater().inflate(layoutResID, null);
        ViewGroup.LayoutParams childParams =  new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        containterLayout.addView(childLayout, childParams);
    }
}
