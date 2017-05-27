package com.neeraj.example.doct;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DiseaseDetails extends AppCompatActivity {

    TextView tname,tcategory,tprevalence,tacuteness,tseverity,thint;
    String name,category="",prevalence,acuteness,severity,hint;
    Button bfind;
    String text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.disease_details);
        tname=(TextView)findViewById(R.id.name);
        tcategory=(TextView)findViewById(R.id.category);
        tprevalence=(TextView)findViewById(R.id.prevalence);
        tacuteness=(TextView)findViewById(R.id.acuteness);
        tseverity=(TextView)findViewById(R.id.severity);
        thint=(TextView)findViewById(R.id.hint);
        bfind=(Button)findViewById(R.id.find);
        bfind.setVisibility(View.INVISIBLE);

        String data=getIntent().getStringExtra("json");
        try {
            JSONObject jobj=new JSONObject(data);
            name=jobj.getString("name");
            prevalence=jobj.getString("prevalence");
            acuteness=jobj.getString("acuteness");
            severity=jobj.getString("severity");
            JSONObject jobj1=jobj.getJSONObject("extras");
            hint=jobj1.getString("hint");
            JSONArray jar=jobj.getJSONArray("categories");
            category=jar.getString(0);
            /*for(int i=0;i<jar.length();i++)
            {
                category+=jar.getString(i)+", ";
            }*/
            //name=jobj.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tname.setText(name);
        System.out.println(category);
        tcategory.setText(category);
        tprevalence.setText(prevalence);
        tacuteness.setText(acuteness);
        tseverity.setText(severity);
        thint.setText(hint);
        text = (category.substring(0,category.length()-1))+"ist";
        bfind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 text = (category.substring(0,category.length()-1))+"ist";
                System.out.println(text);
                String url="https://www.google.co.in/maps/search/"+text;
                Intent bi=new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(bi);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.find_doctor, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        System.out.println("hello from item");
      switch (item.getItemId())
      {
          case R.id.find:
              System.out.println("the value of text is "+text);
              String url="https://www.google.co.in/maps/search/"+text;
              Intent bi=new Intent(Intent.ACTION_VIEW, Uri.parse(url));
              startActivity(bi);
              return true;

          case R.id.home:
              startActivity(new Intent(this,MainActivityNav.class));
              return true;
      }
        return super.onOptionsItemSelected(item);
    }


}
