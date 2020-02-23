package com.tester.Needs.Splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.tester.Needs.Main.MainActivity;
import com.tester.Needs.Main.SubActivity;
import com.tester.Needs.R;

public class Guide4Activity extends AppCompatActivity {
    ImageView nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide4);

        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Guide4Activity.this, SubActivity.class));
            }
        });
    }
}
