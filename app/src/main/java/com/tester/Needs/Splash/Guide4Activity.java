package com.tester.Needs.Splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tester.Needs.Main.MainActivity;
import com.tester.Needs.Main.SubActivity;
import com.tester.Needs.R;

public class Guide4Activity extends AppCompatActivity {
    ImageView nextButton;
    TextView txtLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide4);

        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        txtLink = (TextView) this.findViewById(R.id.txtLink);
        txtLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://icons8.com"));
                startActivity(intent);
            }
        });
    }
}
