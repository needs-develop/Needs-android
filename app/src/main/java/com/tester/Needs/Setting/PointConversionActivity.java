package com.tester.Needs.Setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tester.Needs.R;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.O)
public class PointConversionActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid;
    String point;
    TextView curPoint;
    EditText editText; //Point to switch

    Instant nowUtc = Instant.now();
    ZoneId asiaSeoul = ZoneId.of("Asia/Seoul");
    ZonedDateTime nowAsiaSeoul = ZonedDateTime.ofInstant(nowUtc, asiaSeoul);

    String year = String.valueOf(nowAsiaSeoul.getYear());
    String month = String.valueOf(nowAsiaSeoul.getMonthValue());
    String day1 = String.valueOf(nowAsiaSeoul.getDayOfMonth());
    String hour = String.valueOf(nowAsiaSeoul.getHour());
    String minute = String.valueOf(nowAsiaSeoul.getMinute());
    String second = String.valueOf(nowAsiaSeoul.getSecond());

    String fullDay = year + "/" + month + "/" + day1 + " " + hour + ":" + minute + ":" + second;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_conversion);

        if (month.length() == 1) {
            month = "0" + month;
            fullDay = year + "/" + month + "/" + day1 + " " + hour + ":" + minute + ":" + second;
        }
        if (day1.length() == 1) {
            day1 = "0" + day1;
            fullDay = year + "/" + month + "/" + day1 + " " + hour + ":" + minute + ":" + second;
        }


        if (hour.length() == 1) {
            hour = "0" + hour;
            fullDay = year + "/" + month + "/" + day1 + " " + hour + ":" + minute + ":" + second;
        }

        if (minute.length() == 1) {
            minute = "0" + minute;
            fullDay = year + "/" + month + "/" + day1 + " " + hour + ":" + minute + ":" + second;
        }

        if (second.length() == 1) {
            second = "0" + second;
            fullDay = year + "/" + month + "/" + day1 + " " + hour + ":" + minute + ":" + second;
        }

        curPoint = findViewById(R.id.curPoint);
        uid = user.getUid();

        db.collection("user")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (uid.equals(document.getData().get("id_uid").toString())) {
                                    point = document.getData().get("id_point").toString();
                                    curPoint.setText(point);
                                    break;
                                }
                            }
                        } else {
                        }
                    }
                });

        Button button = findViewById(R.id.conversion);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage();
            }
        });

        editText = (EditText) findViewById(R.id.editText);
    }

    private void showMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("포인트 전환 확인");
        builder.setMessage("포인트 전환을 진행하시겠습니까?\n전환완료시 취소할 수 없습니다.");
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        builder.setPositiveButton("전환하기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int curPoint = Integer.parseInt(point);
                int conPoint = Integer.parseInt(editText.getText().toString());
                curPoint = curPoint - conPoint;
                if (curPoint >= 0) {
                    point = Integer.toString(curPoint);

                    db.collection("user").document(uid)
                            .update(
                                    "id_point", point
                            );

                    Map<String, Object> member = new HashMap<>();
                    member.put("day", fullDay);
                    member.put("point", "-" + editText.getText().toString());
                    member.put("type", "사용");

                    db.collection("user").document(uid).collection("pointHistory")
                            .add(member)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                }
                            });

                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "포인트 전환 완료", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "포인트 부족", Toast.LENGTH_LONG).show();
                }
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
