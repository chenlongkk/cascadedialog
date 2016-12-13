package com.qiyu.wbg.cascadeview.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qiyu.wbg.cascadeview.CascadeData;
import com.qiyu.wbg.cascadeview.CascadeDialog;
import com.qiyu.wbg.cascadeview.CascadeSelectListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CascadeData cascadeData = null;
        try
        {
            InputStream inputStream = getResources().getAssets().open("data.json");
            Gson gson = new Gson();
            cascadeData = gson.fromJson(new InputStreamReader(inputStream),CascadeData.class);
        }catch (IOException e){

        }




        final CascadeDialog dialog;
        Bundle bundle = new Bundle();
        bundle.putSerializable("cascade_data",(Serializable) cascadeData);
        bundle.putInt("level",2);

        final List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(1);
        list.add(0);
        bundle.putSerializable("selected_data",(Serializable) list);

        dialog = new CascadeDialog.CascadeDialogBuilder()
                .setDataSource(cascadeData)
                .setLevel(2)
                .setSelectData(list).build();

        dialog.setSelectedListener(new CascadeSelectListener() {
            @Override
            public void onSelect(List<Integer> res) {
                StringBuilder sb = new StringBuilder();
                for(Integer i : res){
                    sb.append("select:").append(i);
                }
//                list.clear();
//                list.addAll(res);
                Toast.makeText(MainActivity.this,sb.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                dialog.setSelectedData(list);
                dialog.show(getSupportFragmentManager(),"cascade");
            }
        });
    }
}
