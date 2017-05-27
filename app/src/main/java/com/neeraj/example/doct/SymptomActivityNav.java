package com.neeraj.example.doct;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
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

public class SymptomActivityNav extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    RadioButton male, female;
    Button button, tap;
    ProgressBar progressBar;
    ArrayList<String> first = new ArrayList<String>();
    ArrayList<String> dispSymp = new ArrayList<String>();
    ArrayAdapter<String> adapter1;
    EditText editText, ageText;
    ListView list;
    int sizeOfId = 0;
    String sex = "male";
    String symp;
    AutoCompleteTextView acTextView;
    String[] dispArr;
    int age = 0;
    protected static final int REQUEST_OK = 1;
    String[] Symptom_names, id_array, id;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom_nav);
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
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        list = (ListView) findViewById(R.id.list);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);
        button = (Button) findViewById(R.id.button);
        tap = (Button) findViewById(R.id.tap);
        ageText = (EditText) findViewById(R.id.ageText);
        Symptom_names = new String[10000];
        id_array = new String[10000];
        id = new String[100];
        SQLiteDatabase db = openOrCreateDatabase("doct", MODE_PRIVATE, null);
        db.execSQL("create table if not exists symptoms(id varchar,name varchar);");
        String query = "select * from symptoms";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String temp = cursor.getString(0).toLowerCase();
                String temp1 = cursor.getString(1).toLowerCase();
                String reciv = cursor.getString(1).toLowerCase();
                first.add(reciv);
                Symptom_names[i] = temp1;
                System.out.println(Symptom_names[i]);
                id_array[i] = temp;
                i++;
            } while (cursor.moveToNext());
        }
        final String[] arrayOfStrings;
        arrayOfStrings = first.toArray(new String[first.size()]);
        System.out.println(i + "");
        acTextView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
        acTextView.setThreshold(2);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arrayOfStrings);
        acTextView.setAdapter(adapter);
        //parseIntent();
        setAdapter();
        findViewById(R.id.btn_speak).setOnClickListener(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!male.isChecked() && !female.isChecked()) || ageText.getText().toString().equals(""))
                    Toast.makeText(SymptomActivityNav.this, "Please enter your valid credentials ", Toast.LENGTH_SHORT).show();

                else {

                    // String symp = acTextView.getText().toString();
                    age = Integer.parseInt(ageText.getText().toString());
                    // System.out.println(symp);
                    /*int flag = 0;
                    for (int j = 0; j < i; j++) {
                        if (Symptom_names[j].equals(symp)) {
                            flag = 1;
                            id[sizeOfId] = id_array[j];
                            sizeOfId++;
                            System.out.println(id);
                            break;
                        }
                    }*/
                    // if (flag == 1) {
                    if (v != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                    progressBar.setVisibility(View.VISIBLE);
                    //       Toast.makeText(SymptomActivityNav.this, "Please wait for a few moments while we process your data ", Toast.LENGTH_SHORT).show();
                    if (male.isChecked()) {
                        sex = "male";
                    } else if (female.isChecked()) {
                        sex = "female";
                    }
                    String ur = "https://api.infermedica.com/v2/diagnosis";
                    button.setClickable(false);
                    new SymptomActivityNav.QuestionAsynTask().execute(ur);

                  /*  else {
                        Toast.makeText(SymptomActivityNav.this, "Please enter a valid symptom", Toast.LENGTH_SHORT).show();
                    }*/

                }
            }
        });
        tap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!acTextView.getText().toString().equals("")) {
                    symp = acTextView.getText().toString();
                    //System.out.println(symp);
                    int flag = 0;
                    for (int j = 0; j < i; j++) {
                        if (Symptom_names[j].equals(symp)) {
                            dispSymp.add(symp);
                            flag = 1;
                            id[sizeOfId] = id_array[j];
                            sizeOfId++;
                            System.out.println(id);
                            acTextView.setText("");
                            setAdapter();
                            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,arrayOfStrings);
                            break;
                        }

                    }
                    if (flag == 0) {
                        //Toast.makeText(SymptomActivityNav.this, "Please enter a valid symptom", Toast.LENGTH_SHORT).show();
                        if (v != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                        progressBar.setVisibility(View.VISIBLE);
                        String ur = "https://api.infermedica.com/v2/parse";
                        new SymptomActivityNav.QuestionAsynTask1().execute(ur);
                    }

                } else {
                    Toast.makeText(SymptomActivityNav.this, "Please enter a symptom", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    void setAdapter() {
        dispArr = dispSymp.toArray(new String[dispSymp.size()]);
        adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dispArr);
        list.setAdapter(adapter1);

    }

    /*void parseIntent() {
        String temp = getIntent().getStringExtra("json");
        try {
            JSONObject job = new JSONObject(temp);
            JSONArray jar = job.getJSONArray("mentions");
            for (int i = 0; i < jar.length(); i++) {
                JSONObject job1 = jar.getJSONObject(i);
                String name = job1.getString("name");
                String id1 = job1.getString("id");
                id[sizeOfId] = id1;
                sizeOfId++;
                dispSymp.add(name);
                //setAdapter();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }*/

    String data;

    @Override
    public void onClick(View v) {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
        try {
            startActivityForResult(i, REQUEST_OK);
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing speech to text engine.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OK && resultCode == RESULT_OK) {
            ArrayList<String> thingsYouSaid = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            acTextView.setText(thingsYouSaid.get(0));
        }
    }

    public class QuestionAsynTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                System.out.println("in");
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(params[0]);
                httppost.setHeader("App-Id", "6b4495d7");
                httppost.setHeader("App-Key", "d0eb66d855924bcae1d5b497896d0ea3");
                JSONObject json = new JSONObject();
                json.put("sex", sex);
                json.put("age", age);
                JSONArray ja = new JSONArray();
                for (int k = 0; k < sizeOfId; k++) {
                    JSONObject json1 = new JSONObject();
                    json1.put("id", id[k]);
                    json1.put("choice_id", "present");
                    ja.put(json1);
                }
                json.put("evidence", ja);
                StringEntity se = new StringEntity(json.toString());
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httppost.setEntity(se);
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    data = EntityUtils.toString(entity);
                    System.out.println(data + "data");
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
                Toast.makeText(SymptomActivityNav.this, "Connection Failed", Toast.LENGTH_SHORT).show();
            } else {

                //   Toast.makeText(SymptomActivityNav.this, "True", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(SymptomActivityNav.this, Disease.class);
                intent.putExtra("json", data);
                startActivity(intent);
                //finish();
                //editText.setText(data);
                //long seed = System.nanoTime();
                // Collections.shuffle(quesList, new Random(seed));
                // buttonOnCreate();
            }
        }


    }

    String data1 = "";

    public class QuestionAsynTask1 extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                System.out.println("in");
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(params[0]);
                httppost.setHeader("App-Id", "6b4495d7");
                httppost.setHeader("App-Key", "d0eb66d855924bcae1d5b497896d0ea3");
                JSONObject json = new JSONObject();
                json.put("text", symp);
                StringEntity se = new StringEntity(json.toString());
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httppost.setEntity(se);
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    data1 = EntityUtils.toString(entity);
                    System.out.println(data1 + "data1");
                }
                return true;
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            //button.setClickable(true);
            super.onPostExecute(result);
            if (result == false) {
                Toast.makeText(SymptomActivityNav.this, "Connection Failed", Toast.LENGTH_SHORT).show();
            } else {
                //Intent intent = new Intent(SymptomActivityNav.this, SymptomActivityNav.class);
                //intent.putExtra("json", data1);
                //startActivity(intent);
                acTextView.setText("");
                progressBar.setVisibility(View.INVISIBLE);
                String temp = data1;
                try {
                    JSONObject job = new JSONObject(temp);
                    JSONArray jar = job.getJSONArray("mentions");
                    for (int i = 0; i < jar.length(); i++) {
                        JSONObject job1 = jar.getJSONObject(i);
                        String name = job1.getString("name");
                        String id1 = job1.getString("id");
                        id[sizeOfId] = id1;
                        sizeOfId++;
                        dispSymp.add(name);
                        setAdapter();

                    }
                    if(jar.length()==0)
                    {
                        Toast.makeText(SymptomActivityNav.this, "Sorry, No symtoms matched. Please try again or Select symptoms from the dropdown textbox.", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
        getMenuInflater().inflate(R.menu.symptom_activity_nav, menu);
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
            Intent i=new Intent(this,SearchDoctorNav.class);
            startActivity(i);
            // Handle the camera action
        } else if (id == R.id.find_disease) {
            Intent i=new Intent(this,SymptomActivityNav.class);
            startActivity(i);

        } else if (id == R.id.search_medicine) {
            Intent i=new Intent(this,SearchMedicineNav.class);
            startActivity(i);

        } else if (id == R.id.help) {
            Intent i=new Intent(this,Help.class);
            startActivity(i);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
