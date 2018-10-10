package com.app.sample.fchat.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import junit.framework.Test;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context, int[] layouts){
        this.context = context;
        this.slide_layout = layouts;
    }

    //Arrays
//    public int[] slide_images = {
//            R.drawable.two,
//            R.drawable.doc_mother,
//            R.drawable.eat_healthy,
//            R.drawable.mother_child
//    };
//
    public int[] slide_layout;//={
//            R.layout.slide_layout,
//            R.layout.slide_layout2,
//            R.layout.slide_layout3
//    };
//
//    public int[] slide_bg = {
//            R.drawable.bg2,
//            R.drawable.bg2,
//            R.drawable.eat_healthy,
//            R.drawable.mother_child
//    };
//
//    public String[] slide_text = {
//
//            "Something about ASHA and what this app does. How will it be helpul. More Illustrative images will be next." ,
//            "Something else about healthcare and bla bla bla...",
//            "Slide 3",
//            "Slide 4"
//
//    };


    @Override
    public int getCount() {
        return slide_layout.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (RelativeLayout)object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(slide_layout[position],container,false);


//        ImageView slideImageView = (ImageView)view.findViewById(R.id.slide_imageView);
//        TextView slide_txt = (TextView)view.findViewById(R.id.slide_txt_view);
//
////        view.setBackground(slide_bg[position]);
//        slideImageView.setImageResource(slide_images[position]);
//        slide_txt.setText(slide_text[position]);


        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }
}

