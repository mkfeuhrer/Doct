package com.neeraj.example.doct;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivityNav extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    SharedPreferences prefs = null;
    Button button,symptom,butmed,searchdoc,help;
    EditText editText;
    ProgressBar progressBar;
    String data,data1;
    int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Toast.makeText(MainActivityNav.this, "Please make sure you have a working Internet Connection", Toast.LENGTH_LONG).show();
        button=(Button)findViewById(R.id.button);
        symptom=(Button)findViewById(R.id.symptom);
        symptom.setVisibility(View.INVISIBLE);
        help=(Button)findViewById(R.id.help);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        butmed=(Button)findViewById(R.id.butmed);
        searchdoc=(Button)findViewById(R.id.searchdoc);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivityNav.this,Help.class);
                startActivity(intent);
            }
        });
        searchdoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivityNav.this,SearchDoctorNav.class);
                startActivity(intent);
            }
        });
        butmed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivityNav.this,SearchMedicineNav.class);
                startActivity(intent);
            }
        });
        prefs = getSharedPreferences("com.neeraj.example.doct", MODE_PRIVATE);


        symptom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag=1;
                String ur="https://api.infermedica.com/v2/symptoms";
                new QuestionAsynTask().execute(ur);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String ur="https://www.opentdb.com/api.php?amount=10&category="+cat+"&type=multiple";
                //System.out.println("button click");
                //String ur="https://api.infermedica.com/v2/diagnosis";
                //new QuestionAsynTask().execute(ur);
                Intent intent=new Intent(MainActivityNav.this,SymptomActivityNav.class);
                startActivity(intent);
                //finish();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();

        if (prefs.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            Toast.makeText(MainActivityNav.this, "Preparing the app for its first time use...please bear with us", Toast.LENGTH_SHORT).show();
            String ur="https://api.infermedica.com/v2/symptoms";
            new QuestionAsynTask().execute(ur);
            String ur1="https://rxnav.nlm.nih.gov/REST/interaction/interaction.json?rxcui=341248";
            new MedicineAsynTask().execute(ur1);
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean("firstrun", false).commit();
        }
        else
        {

            SQLiteDatabase db=openOrCreateDatabase("doct",MODE_PRIVATE,null);
            db.execSQL("create table if not exists symptoms(id varchar,name varchar);");
            String query="select * from symptoms";
            Cursor cursor=db.rawQuery(query,null);
            if(!cursor.moveToFirst())
            {
                Toast.makeText(MainActivityNav.this, "Preparing the app...please bear with us", Toast.LENGTH_SHORT).show();
                String ur="https://api.infermedica.com/v2/symptoms";
                new QuestionAsynTask().execute(ur);
            }
            db.execSQL("create table if not exists medicine(id varchar,name varchar);");
            String query1="select * from medicine";
            Cursor cursor1=db.rawQuery(query1,null);
            if(!cursor1.moveToFirst())
            {
                Toast.makeText(MainActivityNav.this, "Preparing the app...please bear with us", Toast.LENGTH_SHORT).show();
                String ur1="https://rxnav.nlm.nih.gov/REST/interaction/interaction.json?rxcui=341248";
                //progressBar.setVisibility(View.VISIBLE);
                new MedicineAsynTask().execute(ur1);
            }

        }
    }
    public class QuestionAsynTask extends AsyncTask<String, Void,Boolean>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                System.out.println("in");
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httppost = new HttpGet(params[0]);
                httppost.setHeader("App-Id", "6b4495d7");
                httppost.setHeader("App-Key", "d0eb66d855924bcae1d5b497896d0ea3");
                /*JSONObject json = new JSONObject();
                json.put("sex","male");
                json.put("age",30);
                JSONArray ja=new JSONArray();
                JSONObject json1 = new JSONObject();
                json1.put("id","s_1193");
                json1.put("choice_id","present");
                JSONObject json2 = new JSONObject();
                json2.put("id","s_488");
                json2.put("choice_id","present");
                JSONObject json3 = new JSONObject();
                json3.put("id","s_418");
                json3.put("choice_id","present");
                ja.put(json1);
                ja.put(json2);
                ja.put(json3);
                json.put("evidence",ja);
                StringEntity se = new StringEntity( json.toString());
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httppost.setEntity(se);
*/
                //String postMessage=

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    data = EntityUtils.toString(entity);
                    System.out.println(data+"data");

                    //Toast.makeText(MainActivityNav.this, "data added", Toast.LENGTH_SHORT).show();
                    //editText.setText(data);
                }
                return true;
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }/* catch (JSONException e) {
                e.printStackTrace();
            }*/
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result == false) {
                Toast.makeText(MainActivityNav.this,"Connection Failed", Toast.LENGTH_SHORT).show();
            } else {
                SQLiteDatabase db=openOrCreateDatabase("doct",MODE_PRIVATE,null);
                db.execSQL("create table if not exists symptoms(id varchar,name varchar);");
                try {
                    JSONArray jarray = new JSONArray(data);
                    for(int i=0;i<jarray.length();i++)
                    {

                        JSONObject object = jarray.getJSONObject(i);
                        //System.out.println("after array");
                        String id=object.getString("id");
                        String name=object.getString("name");
                        System.out.println(id+" : "+name+" ");
                        if (name.matches(".*'.*")||name.matches(".*,.*")) {
                            // Do nothing
                        }
                        else {
                            db.execSQL("insert into symptoms values ('" + id + "','" + name + "');");
                        }
                        //Toast.makeText(MainActivityNav.this, "star added", Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //progressBar.setVisibility(View.INVISIBLE);
                //Toast.makeText(MainActivityNav.this, "Just a few more moments....", Toast.LENGTH_SHORT).show();
                //editText.setText(data);

                //long seed = System.nanoTime();
                // Collections.shuffle(quesList, new Random(seed));
                // buttonOnCreate();
            }
        }


    }
    public class MedicineAsynTask extends AsyncTask<String, Void,Boolean>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);
        }


        @Override
        protected Boolean doInBackground(String... params) {
            try {
                //Toast.makeText(MainActivityNav.this,"try",Toast.LENGTH_SHORT).show();
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
                    data1="";
                    while((line=br.readLine())!=null)
                    {
                        data1+=line;
                    }
                    System.out.println(data1);
                    Log.d("abc",data1+"");
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } /*catch (JSONException e) {
                e.printStackTrace();
            }*/
            return false;
        }
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result == false) {
                Toast.makeText(MainActivityNav.this,"Connection Failed", Toast.LENGTH_SHORT).show();
            } else {
                SQLiteDatabase db=openOrCreateDatabase("doct",MODE_PRIVATE,null);
                db.execSQL("create table if not exists medicine(id varchar,name varchar);");
                try {
                    JSONObject jobj=new JSONObject(data1);
                    JSONArray jarray=jobj.getJSONArray("interactionTypeGroup");
                    for(int i=0;i<jarray.length();i++)
                    {

                        JSONObject object = jarray.getJSONObject(i);
                        JSONArray jarray1=object.getJSONArray("interactionType");
                        for(int j=0;j<jarray1.length();j++)
                        {
                            JSONObject job1=jarray1.getJSONObject(j);
                            JSONArray jarray2=job1.getJSONArray("interactionPair");
                            for(int k=0;k<jarray2.length();k++)
                            {
                                JSONObject job2=jarray2.getJSONObject(k);
                                JSONArray jarray3=job2.getJSONArray("interactionConcept");
                                for(int l=0;l<jarray3.length();l++)
                                {
                                    JSONObject job3=jarray3.getJSONObject(l);
                                    JSONObject job4=job3.getJSONObject("sourceConceptItem");
                                    String temp1=job4.getString("id");
                                    String temp2=job4.getString("name");
                                    System.out.println(temp1+" : "+temp2);
                                    if (temp2.matches(".*'.*")||temp2.matches(".*,.*")) {
                                        // Do something
                                    }
                                    else {
                                        db.execSQL("insert into medicine values ('" + temp1 + "','" + temp2 + "');");
                                    }
                                }
                            }

                        }
                        //System.out.println("after array");

                        /*String id=object.getString("id");
                        String name=object.getString("name");
                        System.out.println(id+" : "+name+" ");
                        if (name.matches(".*'.*")||name.matches(".*,.*")) {
                            // Do something
                        }
                        else {
                            db.execSQL("insert into symptoms values ('" + id + "','" + name + "');");
                        }*/
                        //Toast.makeText(MainActivityNav.this, "star added", Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(MainActivityNav.this, "You are ready to go", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                //editText.setText(data);
                // long seed = System.nanoTime();
                // Collections.shuffle(quesList, new Random(seed));
                // buttonOnCreate();
            }
        }


    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.search_doctor) {
            Intent i=new Intent(MainActivityNav.this,SearchDoctorNav.class);
            startActivity(i);
            // Handle the camera action
        } else if (id == R.id.find_disease) {
            Intent i=new Intent(MainActivityNav.this,SymptomActivityNav.class);
            startActivity(i);

        } else if (id == R.id.search_medicine) {
            Intent i=new Intent(MainActivityNav.this,SearchMedicineNav.class);
            startActivity(i);

        } else if (id == R.id.help) {
            Intent i=new Intent(MainActivityNav.this,Help.class);
            startActivity(i);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
