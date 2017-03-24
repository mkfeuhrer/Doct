package com.neeraj.example.doct;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Disease extends AppCompatActivity {
    String[] disease,probability;
    //EditText text;
    Button findDoctor;
    private List<Disease_type> diseaseList;
    private RecyclerView recyclerView;
    private DiseaseAdapter diseaseAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease);
        findDoctor=(Button)findViewById(R.id.findDoctor);
        findDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Disease.this,Search.class);
                startActivity(intent);
            }
        });
        //text=(EditText)findViewById(R.id.text);
        disease=new String[100];
        probability=new String[100];
        diseaseList=new ArrayList<>();
        prepareData();
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        diseaseAdapter=new DiseaseAdapter(diseaseList);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(diseaseAdapter);

    }
    private void prepareData(){
        String data= getIntent().getStringExtra("json");
        try {
            JSONObject jsono = new JSONObject(data);
            JSONArray jarray = jsono.getJSONArray("conditions");
            //text.setText("DISEASE             :        PROBABILITY     \n" );
            //text.setTextSize(20);

            for(int i=0;i<jarray.length();i++)
            {
                JSONObject job=jarray.getJSONObject(i);
                String dis=job.getString("name");
                String prob=job.getString("probability");
                disease[i]=dis;
                probability[i]=prob;
                Disease_type d=new Disease_type();
                d.setDISEASE(dis);
                d.setPROB(prob);
                diseaseList.add(d);
                //text.append(disease[i]+"   : "+probability[i]+"\n");
                System.out.println(disease[i]+" : "+probability[i]);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
