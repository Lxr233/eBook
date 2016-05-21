package com.example.administrator.ebook;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.administrator.ebook.R;

/**
 * Created by Lxr on 2016/5/21.
 */
public class BaseActivity extends FragmentActivity {

    private LinearLayout containterLayout;

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);

        imageView = (ImageView)findViewById(R.id.delete_img);
    }

    public void showItem(){
        imageView.setVisibility(View.VISIBLE);
    }

    public void hideItem(){
        imageView.setVisibility(View.GONE);
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
