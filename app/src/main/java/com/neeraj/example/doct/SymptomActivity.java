package com.neeraj.example.doct;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

public class SymptomActivity extends AppCompatActivity {
    RadioButton male,female;
    Button button,tap;
    //AutoCompleteTextView autoCompleteTextView;
    ArrayList<String> first = new ArrayList<String>();
    EditText editText,ageText;
    int sizeOfId=0;
    String sex="male";

    int age=0;

    Spinner spinner;
    String[] Symptom_names,id_array,id;
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom);
        male=(RadioButton)findViewById(R.id.male);
        female=(RadioButton)findViewById(R.id.female);
        //spinner=(Spinner)findViewById(R.id.spinner);
        //editText=(EditText)findViewById(R.id.editText);
        button=(Button)findViewById(R.id.button);
        tap=(Button)findViewById(R.id.tap);
        ageText=(EditText)findViewById(R.id.ageText);
       // age=Integer.parseInt(ageText.getText().toString());
        //autoCompleteTextView=(AutoCompleteTextView)findViewById(R.id.symptomtextview);
        Symptom_names=new String[10000];
        id_array=new String[10000];
        id=new String[100];

        SQLiteDatabase db=openOrCreateDatabase("doct",MODE_PRIVATE,null);
        db.execSQL("create table if not exists symptoms(id varchar,name varchar);");
        String query="select * from symptoms";
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
       // System.out.println("LIST OF COURSE NAME  " + first);
        final String[] arrayOfStrings;

        arrayOfStrings = first.toArray(new String[first.size()]);
        System.out.println(i+"");
        final AutoCompleteTextView acTextView = (AutoCompleteTextView)findViewById(R.id.autocomplete);
        acTextView.setThreshold(2);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,arrayOfStrings);
        acTextView.setAdapter(adapter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( (!male.isChecked() && !female.isChecked() )|| ageText.getText().toString().equals("") || Symptom_names[0].equals(""))
                    Toast.makeText(SymptomActivity.this, "Please enter your valid credentials ", Toast.LENGTH_SHORT).show();

                else {

                    String symp = acTextView.getText().toString();
                    age=Integer.parseInt(ageText.getText().toString());
                    System.out.println(symp);
                    int flag = 0;
                    for (int j = 0; j < i; j++) {
                        if (Symptom_names[j].equals(symp)) {
                            flag = 1;
                            id[sizeOfId] = id_array[j];
                            sizeOfId++;
                            System.out.println(id);
                            break;
                        }
                    }
                    if (flag == 1) {
                        Toast.makeText(SymptomActivity.this, "Please wait for a few moments while we process your data ", Toast.LENGTH_SHORT).show();
                        if (male.isChecked()) {
                            sex = "male";
                        } else if (female.isChecked()) {
                            sex = "female";
                        }
                        String ur = "https://api.infermedica.com/v2/diagnosis";
                        button.setClickable(false);
                        new QuestionAsynTask().execute(ur);
                    }
                    else {
                        Toast.makeText(SymptomActivity.this, "Please enter a valid symptom", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        tap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!acTextView.getText().toString().equals("")) {
                    String symp = acTextView.getText().toString();
                    //System.out.println(symp);
                    int flag = 0;
                    for (int j = 0; j <i; j++) {
                        if (Symptom_names[j].equals(symp)) {
                            flag = 1;
                            id[sizeOfId] = id_array[j];
                            sizeOfId++;
                            System.out.println(id);
                            acTextView.setText("");
                            break;
                        }

                    }
                    if(flag==0)
                    {
                        Toast.makeText(SymptomActivity.this, "Please enter a valid symptom", Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    Toast.makeText(SymptomActivity.this, "Please enter a symptom", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    String data;
    public class QuestionAsynTask extends AsyncTask<String, Void,Boolean>
    {

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                System.out.println("in");
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(params[0]);
                httppost.setHeader("App-Id", "6b4495d7");
                httppost.setHeader("App-Key", "d0eb66d855924bcae1d5b497896d0ea3");
                JSONObject json = new JSONObject();
                json.put("sex",sex);
                json.put("age",age);
                JSONArray ja=new JSONArray();
                for(int k=0;k<sizeOfId;k++) {
                    JSONObject json1 = new JSONObject();
                    json1.put("id", id[k]);
                    json1.put("choice_id", "present");
               /* JSONObject json2 = new JSONObject();
                json2.put("id","s_488");
                json2.put("choice_id","present");
                JSONObject json3 = new JSONObject();
                json3.put("id","s_418");
                json3.put("choice_id","present");*/
                    ja.put(json1);
                }
                //ja.put(json2);
                //ja.put(json3);
                json.put("evidence",ja);
                StringEntity se = new StringEntity( json.toString());
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httppost.setEntity(se);
                //String postMessage=

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    data = EntityUtils.toString(entity);
                    System.out.println(data+"data");

                    //Toast.makeText(MainActivity.this, "data added", Toast.LENGTH_SHORT).show();
                    //editText.setText(data);
                }
                return true;
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }/* catch (JSONException e) {
                e.printStackTrace();
            }*/ catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            button.setClickable(true);
            super.onPostExecute(result);
            if (result == false) {
                 Toast.makeText(SymptomActivity.this,"Connection Failed", Toast.LENGTH_SHORT).show();
            } else {

             //   Toast.makeText(SymptomActivity.this, "True", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(SymptomActivity.this,Disease.class);
                intent.putExtra("json",data);
                startActivity(intent);
                //finish();
                //editText.setText(data);
                //long seed = System.nanoTime();
                // Collections.shuffle(quesList, new Random(seed));
                // buttonOnCreate();
            }
        }


    }
}
