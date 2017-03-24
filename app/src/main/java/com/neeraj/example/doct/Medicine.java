package com.neeraj.example.doct;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Medicine extends AppCompatActivity {
    //AutoCompleteTextView
    ArrayList<String> first = new ArrayList<String>();
    ImageView imageView;
    Button submit;
    TextView display,display2,display3,des;
    String data="",id;
    String[] Symptom_names,id_array;
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);
        submit=(Button)findViewById(R.id.button);
        des=(TextView)findViewById(R.id.des);
        des.setVisibility(View.INVISIBLE);
        imageView=(ImageView)findViewById(R.id.imageView);
        display=(TextView)findViewById(R.id.diplay);
        display2=(TextView)findViewById(R.id.diplay2);
        display3=(TextView)findViewById(R.id.diplay3);
        display.setVisibility(View.INVISIBLE);
        display2.setVisibility(View.INVISIBLE);
        display3.setVisibility(View.INVISIBLE);
        Symptom_names=new String[10000];
        id_array=new String[10000];
        //id=new String[100];

        SQLiteDatabase db=openOrCreateDatabase("doct",MODE_PRIVATE,null);
        db.execSQL("create table if not exists medicine(id varchar,name varchar);");
        String query="select * from medicine";
        Cursor cursor=db.rawQuery(query,null);
        if(cursor.moveToFirst())
        {
            do
            {
                String temp=cursor.getString(0).toLowerCase();
                String temp1=cursor.getString(1).toLowerCase();
                String reciv=cursor.getString(1).toLowerCase();
                first.add(reciv);
                Symptom_names[i] = temp1;
                //androidBooks
                System.out.println(Symptom_names[i]);
                id_array[i] = temp;
                i++;
            }while(cursor.moveToNext());

        }
        final String[] arrayOfStrings;

        arrayOfStrings = first.toArray(new String[first.size()]);
        System.out.println(i+"");
        final AutoCompleteTextView acTextView = (AutoCompleteTextView)findViewById(R.id.autocomplete);
        acTextView.setThreshold(2);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,arrayOfStrings);
        acTextView.setAdapter(adapter);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!acTextView.getText().toString().equals("")) {
                    Toast.makeText(Medicine.this, "please wait while we process your data", Toast.LENGTH_SHORT).show();
                    String symp = acTextView.getText().toString();
                    System.out.println(symp);
                    int flag = 0;
                    for (int j = 0; j < id_array.length; j++) {
                        if (Symptom_names[j].equals(symp)) {
                            flag = 1;
                            id = id_array[j];
                            System.out.println(id);
                            break;
                        }
                    }
                    //System.out.println(id);
                    submit.setClickable(false);
                    String ur = "https://www.drugbank.ca/drugs/" + id + "#interactions";
                    new QuestionAsynTask().execute(ur);
                }
                else
                {
                    Toast.makeText(Medicine.this, "please enter a medicine name", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    public class QuestionAsynTask extends AsyncTask<String, Void,Boolean>
    {

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                //Toast.makeText(MainActivity.this,"try",Toast.LENGTH_SHORT).show();
                //System.out.println("try status");
                //c1=1;
                URL url=new URL(params[0]);

                //c1=1;
                HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                //conn.setConnectTimeout();
                int status=conn.getResponseCode();
                //c2=1;
                //System.out.println(status+" status");
                //c2=status;
                if (status==HttpURLConnection.HTTP_OK) {
                    InputStream is=conn.getInputStream();
                    BufferedReader br=new BufferedReader(new InputStreamReader(is));
                    String line;
                    data="";
                    while((line=br.readLine())!=null)
                    {
                        data+=line;
                    }
                    System.out.println(data);
                    Log.d("abc",data+"");
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } /*catch (JSONException e) {
                e.printStackTrace();
            }*/
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            submit.setClickable(true);
            super.onPostExecute(result);
            if (result == false) {
                Toast.makeText(Medicine.this,"Connection Failed", Toast.LENGTH_SHORT).show();
            } else {
              //  Toast.makeText(Medicine.this, "True", Toast.LENGTH_SHORT).show();
                //String text = "0123hello9012hello8901hello7890";
                String word = "</th></tr><tr><th>Description</th><td>";
                int idx=data.indexOf(word)+word.length();
                int till = data.indexOf("<", idx);
                String finalData = data.substring(idx, till);
                System.out.println(finalData); // prints "4"

                //display.setText(finalData);
                //display.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                word = "/categories/";
                idx=data.indexOf(word)+word.length()+13;
                till = data.indexOf("<", idx);
                finalData = data.substring(idx, till);
                display.setText("1. "+finalData);
                des.setVisibility(View.VISIBLE);
                display.setVisibility(View.VISIBLE);
                System.out.println(finalData);
                idx+=47+finalData.length();
                till = data.indexOf("<", idx);
                finalData = data.substring(idx, till);
                System.out.println(finalData);
                display2.setText("2. "+finalData);
                display2.setVisibility(View.VISIBLE);
                idx+=47+finalData.length();
                till = data.indexOf("<", idx);
                finalData = data.substring(idx, till);
                System.out.println(finalData);
                display3.setText("3. "+finalData);
                display3.setVisibility(View.VISIBLE);
                System.out.println(data.lastIndexOf(word)); // prints "22"
            }
        }


    }
}
