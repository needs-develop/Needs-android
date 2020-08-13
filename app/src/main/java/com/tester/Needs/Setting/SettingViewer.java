package com.tester.Needs.Setting;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tester.Needs.R;

public class SettingViewer extends LinearLayout
{
    TextView setting_text;
    ImageView setting_image;


    public SettingViewer(Context context) {
        super(context);
        init(context);
    }
    public SettingViewer(Context context, @Nullable AttributeSet attrs){
        super(context,attrs);
        init(context);
    }

    public void init(Context context){

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.activity_itemsetting,this,true);

        setting_text = (TextView)findViewById(R.id.setting_text);
        setting_image = (ImageView)findViewById(R.id.setting_image);
    }

    public void setItem(SettingItem settingItem)
    {
        setting_text.setText(settingItem.getText());
        setting_image.setImageResource(settingItem.getImage());
    }

}
