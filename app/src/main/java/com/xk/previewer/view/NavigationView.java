package com.xk.previewer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.xk.previewer.R;

import java.util.HashMap;

/**
 * @author xuekai
 * @date 2025/01/11
 */
public class NavigationView extends LinearLayout implements View.OnClickListener {

    int fragmentRootId;
    FragmentManager fragmentManager;

    HashMap<View, Fragment> items = new HashMap<>();

    public NavigationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }


    private void addTab(String tab, int resId, Fragment fragment, OnClickListener listener) {
        View tabLayout = LayoutInflater.from(getContext()).inflate(R.layout.tab_layout, null);
        ((ImageView) tabLayout.findViewById(R.id.icon)).setImageResource(resId);
        ((TextView) tabLayout.findViewById(R.id.title)).setText(tab);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = 10;
        layoutParams.leftMargin = 10;
        layoutParams.rightMargin = 10;
        addView(tabLayout, layoutParams);
        if (listener != null) {
            tabLayout.setOnClickListener(listener);
        } else {
            tabLayout.setOnClickListener(this);
        }
        if (fragment != null) {
            if (items.isEmpty()) {
                tabLayout.setSelected(true);
                fragmentManager.beginTransaction().replace(fragmentRootId, fragment).commit();
            }
            items.put(tabLayout, fragment);
        }
    }

    public void addTab(String tab, int resId, Fragment fragment) {
        addTab(tab, resId, fragment, null);
    }

    public void addTab(String tab, int resId, OnClickListener listener) {
        addTab(tab, resId, null, listener);
    }


    @Override
    public void onClick(View view) {
        for (View item : items.keySet()) {
            item.setSelected(false);
        }
        view.setSelected(true);
        fragmentManager.beginTransaction().replace(fragmentRootId, items.get(view)).commit();
    }

    public void init(FragmentManager supportFragmentManager, int fragmentRootId) {
        this.fragmentManager = supportFragmentManager;
        this.fragmentRootId = fragmentRootId;
    }
}
