package com.neeraj.example.doct;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Disease extends AppCompatActivity {
    String[] disease, probability, disease_id;
    //EditText text;
   Button findDoctor;
    private List<Disease_type> diseaseList;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private DiseaseAdapter diseaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease);
   /*     findDoctor = (Button) findViewById(R.id.findDoctor);
        findDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Disease.this, Search.class);
                startActivity(intent);
            }
        });*/
        //text=(EditText)findViewById(R.id.text);
        disease = new String[100];
        probability = new String[100];
        disease_id = new String[100];
        diseaseList = new ArrayList<>();
        prepareData();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        diseaseAdapter = new DiseaseAdapter(diseaseList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(diseaseAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                progressBar.setVisibility(View.VISIBLE);
                Disease_type disease_type = diseaseList.get(position);
                String ur = "https://api.infermedica.com/v2/conditions/";
                ur = ur + disease_type.getID();
                new Disease.QuestionAsynTask().execute(ur);
                //Intent intent=new Intent(starQuestion.this,starQuestionDisplay.class);
                //intent.putExtra("position",position);
                //startActivity(intent);
                //finish();
                //Toast.makeText(getApplicationContext(), question.getQUESTION() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void prepareData() {
        String data = getIntent().getStringExtra("json");
        try {
            JSONObject jsono = new JSONObject(data);
            JSONArray jarray = jsono.getJSONArray("conditions");
            //text.setText("DISEASE             :        PROBABILITY     \n" );
            //text.setTextSize(20);

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject job = jarray.getJSONObject(i);
                String dis_id = job.getString("id");
                String dis = job.getString("name");
                String prob = job.getString("probability");
                disease_id[i] = dis_id;
                disease[i] = dis;
                probability[i] = prob;
                Disease_type d = new Disease_type();
                d.setDISEASE(dis);
                d.setPROB(prob);
                d.setID(dis_id);
                diseaseList.add(d);
                //text.append(disease[i]+"   : "+probability[i]+"\n");
                System.out.println(disease_id[i] + " : " + disease[i] + " : " + probability[i]);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    String data;

    public class QuestionAsynTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                System.out.println("in");
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httppost = new HttpGet(params[0]);
                httppost.setHeader("App-Id", "6b4495d7");
                httppost.setHeader("App-Key", "d0eb66d855924bcae1d5b497896d0ea3");
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    data = EntityUtils.toString(entity);
                    System.out.println(data + "data");

                    //Toast.makeText(MainActivity.this, "data added", Toast.LENGTH_SHORT).show();
                    //editText.setText(data);
                }
                return true;
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result == false) {
                Toast.makeText(Disease.this, "Connection Failed", Toast.LENGTH_SHORT).show();
            } else {

                //   Toast.makeText(SymptomActivity.this, "True", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(Disease.this, DiseaseDetails.class);
                intent.putExtra("json", data);
                startActivity(intent);
            }


        }
    }
}
