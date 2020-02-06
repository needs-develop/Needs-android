package com.example.project_basic;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.multidex.MultiDex;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static com.example.project_basic.SubActivity.fragmentNumber;

public class FavoritesFragment extends Fragment {
    ListView free_listView;
    Favorites_Adapter favorites_adapter;
    ArrayList<FavoritesList> list_itemArrayList = null;

    ImageView free_write;

    FirebaseFirestore db = FirebaseFirestore.getInstance();//firestore객체
    EditText free_edit;

    String spinnerValue;
    Spinner spinner;
    TextView spinnerView;

    static int f_spinnerNumber = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard,container,false);
        MultiDex.install(getActivity());


        spinner = v.findViewById(R.id.free_spinner);
        spinnerView = v.findViewById(R.id.free_spinner_text);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerValue = parent.getItemAtPosition(position).toString();
                spinnerView.setText(spinnerValue);
                f_spinnerNumber = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        fragmentNumber = 0;

        free_listView = (ListView)v.findViewById(R.id.free_list);

        list_itemArrayList = new ArrayList<FavoritesList>();

        db.collection("freeData").orderBy("day", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i = 1;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String number = Integer.toString(i);
                                list_itemArrayList.add(new FavoritesList(number,document.getData().get("title").toString() ,
                                        document.getData().get("content").toString(), document.getData().get("write").toString() ,
                                        document.getData().get("day").toString(),document.getData().get("visit_num").toString(),
                                        document.getData().get("good_num").toString(),document.getData().get("document_name").toString()));
                                i = Integer.parseInt(number);
                                i++;
                            }
                            favorites_adapter= new Favorites_Adapter(getActivity(),list_itemArrayList);
                            free_listView.setAdapter(favorites_adapter);

                            free_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent(getActivity(),HomeFreeContent.class);

                                    String title = list_itemArrayList.get(position).getBtn_title();
                                    String content = list_itemArrayList.get(position).getContent();
                                    String day = list_itemArrayList.get(position).getBtn_date();
                                    String conId = list_itemArrayList.get(position).getBtn_writer();
                                    String goodNum = list_itemArrayList.get(position).getContent_good();
                                    String visitString = list_itemArrayList.get(position).getBtn_visitnum();
                                    String documentName = list_itemArrayList.get(position).getDocument_name();

                                    int visitInt = Integer.parseInt(visitString);
                                    visitInt = visitInt +1;
                                    visitString = Integer.toString(visitInt);

                                    intent.putExtra("title",title);
                                    intent.putExtra("content",content);
                                    intent.putExtra("day",day);
                                    intent.putExtra("id",conId);
                                    intent.putExtra("good",goodNum);
                                    intent.putExtra("visitnum",visitString);
                                    intent.putExtra("documentName",documentName);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            Log.d("태그", "Error getting documents: ", task.getException());
                        }
                    }
                });

        free_write = (ImageView)v.findViewById(R.id.free_write);
        free_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("태그","클릭");
                Intent intent = new Intent(getActivity(), FavoritesWrite.class);
                //이부분 응용잘하기 후
                startActivity(intent);
            }
        });

        free_edit = v.findViewById(R.id.free_edit);
        free_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String search = s.toString();
                if(search.length() > 0) {
                    free_listView.setFilterText(search);
                }
                else {
                    free_listView.clearTextFilter();
                }
            }
        });

        return v;
    }

}
