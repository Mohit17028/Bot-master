package com.app.sample.fchat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.sample.fchat.adapter.SliderAdapter;

public class SliderActivity extends AppCompatActivity {

    private ViewPager slideViewPager;
    private LinearLayout dotLayout;
    private TextView[]dotstv;
    private int[] layouts ={R.layout.slide_layout, R.layout.slide_layout2, R.layout.slide_layout3};
    private SliderAdapter sliderAdapter;
    private Button btnSkip, btnNext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!isFirstTimeStartApp()){
            //Start Chat activity Directly
            setFirstTimeStartStatus(false);
            Intent intent = new Intent(getApplicationContext(), ActivitySelectFriend.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_slider);

        slideViewPager = (ViewPager) findViewById(R.id.view_pager);
        dotLayout = (LinearLayout) findViewById(R.id.dots_layout);
        btnSkip = (Button)findViewById(R.id.btn_skip);
        btnNext = (Button)findViewById(R.id.btn_next);

        sliderAdapter = new SliderAdapter(this, layouts);
        slideViewPager.setAdapter(sliderAdapter);

        slideViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == layouts.length-1){
                    //Last page
                    btnNext.setText("Start");
                    btnSkip.setVisibility(View.GONE);
                }
                else{

//                    btnNext.setText("Start");
                    btnSkip.setVisibility(View.VISIBLE);
                }
                setDotStatus(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setDotStatus(0);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFirstTimeStartStatus(false);
                Intent intent = new Intent(getApplicationContext(), ActivitySelectFriend.class);
                startActivity(intent);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPage = slideViewPager.getCurrentItem()+1;
                if (currentPage < layouts.length){
                    //move to next page
                    slideViewPager.setCurrentItem(currentPage);
                }
                else {
                    //Start chat activity
                    setFirstTimeStartStatus(false);
                    Intent intent = new Intent(getApplicationContext(), ActivitySelectFriend.class);
                    startActivity(intent);
                }
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", false)) {
            // <---- run your one time code here
            // mark first time has runned.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }

    }

    //    added by himani
    private void setDotStatus(int page){
        dotLayout.removeAllViews();
        dotstv = new TextView[layouts.length];
        for (int i=0; i<dotstv.length; i++){
            dotstv[i] = new TextView(this);
            dotstv[i].setText(Html.fromHtml("&#8226"));
            dotstv[i].setTextSize(30);
            dotstv[i].setTextColor(Color.parseColor("#a9b4bb"));
            dotLayout.addView(dotstv[i]);
        }
        //Get current dot active
        if (dotstv.length > 0){
            dotstv[page].setTextColor(Color.parseColor("#ffffff"));
        }
    }

    private boolean isFirstTimeStartApp(){
        SharedPreferences ref = getApplicationContext().getSharedPreferences("IntroSliderApp", Context.MODE_PRIVATE);
        return ref.getBoolean("FirstTimeStartFlag",true);
    }


    private void setFirstTimeStartStatus(boolean stt){
        SharedPreferences ref = getApplicationContext().getSharedPreferences("IntroSliderApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = ref.edit();
        editor.putBoolean("FirstTimeStartFlag",stt);
        editor.commit();
    }


}
