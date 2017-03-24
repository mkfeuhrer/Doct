package com.neeraj.example.doct;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Disease extends AppCompatActivity {
    String[] disease,probability;
    EditText text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease);
        text=(EditText)findViewById(R.id.text);
        disease=new String[100];
        probability=new String[100];
        String data= getIntent().getStringExtra("json");
        try {
            JSONObject jsono = new JSONObject(data);
            JSONArray jarray = jsono.getJSONArray("conditions");
            text.setText("   Disease    :    Probability     \n" );
            for(int i=0;i<jarray.length();i++)
            {
                JSONObject job=jarray.getJSONObject(i);
                String dis=job.getString("name");
                String prob=job.getString("probability");
                disease[i]=dis;
                probability[i]=prob;
                text.append(disease[i]+" : "+probability[i]+"\n");
                System.out.println(disease[i]+" : "+probability[i]);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
