package com.example.simplemusic.dialog;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.simplemusic.R;

public class Mp3DownLoadChooseDialog extends Activity implements View.OnClickListener {
    //消息ID
    private static final int DOWNLOAD_REQUESTCODE = 1;
    private RadioGroup mgroup;
    private RadioButton mnormal;
    private RadioButton mhight;
    private RadioButton mreal;
    private int mtype = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_mp3_download_choose);
        findViewById(R.id.download_confirm).setOnClickListener(this);
        mtype = getIntent().getIntExtra("types",1);
        //初始化按钮
        initRadioGroup();
    }

    public void initRadioGroup(){

        mgroup = (RadioGroup)findViewById(R.id.rg_admit);
        mnormal = (RadioButton)findViewById(R.id.rb_normal);
        mhight = (RadioButton)findViewById(R.id.rb_hight);
        mreal = (RadioButton)findViewById(R.id.rb_real);
        if(mtype == 1){
            mhight.setVisibility(View.GONE);
            mreal.setVisibility(View.GONE);
        }
        if(mtype == 3){
            mreal.setVisibility(View.GONE);
        }
        if(mtype == 5){
            mhight.setVisibility(View.GONE);
        }

        mgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_normal:
                        mtype = 1;
                        break;
                    case R.id.rb_hight:
                        mtype = 2;
                        break;
                    case R.id.rb_real:
                        mtype = 3;
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.download_confirm){
            Intent intent = new Intent();
            intent.putExtra("quality", mtype);
            setResult(DOWNLOAD_REQUESTCODE, intent);
            finish();
        }
    }
}
