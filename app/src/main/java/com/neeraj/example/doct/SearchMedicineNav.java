package com.neeraj.example.doct;

import android.content.Context;
import android.content.Intent;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SearchMedicineNav extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ArrayList<String> first = new ArrayList<String>();
    ImageView imageView;
    Button submit;
    ProgressBar progressBar;
    TextView display,display2,display3,des;
    String data="",id;
    String[] Symptom_names,id_array;
    int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_medicine_nav);
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
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
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
                    if (v != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.getIndeterminateDrawable().setColorFilter(0xFFFFFFFF, android.graphics.PorterDuff.Mode.MULTIPLY);
                    imageView.setVisibility((View.INVISIBLE));
                    //   Toast.makeText(SearchMedicineNav.this, "please wait while we process your data", Toast.LENGTH_SHORT).show();
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
                    new SearchMedicineNav.QuestionAsynTask().execute(ur);
                }
                else
                {
                    Toast.makeText(SearchMedicineNav.this, "please enter a medicine name", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(SearchMedicineNav.this,"Connection Failed", Toast.LENGTH_SHORT).show();
            } else {
                //  Toast.makeText(SearchMedicineNav.this, "True", Toast.LENGTH_SHORT).show();
                //String text = "0123hello9012hello8901hello7890";
                String word = "</th></tr><tr><th>Description</th><td>";
                int idx=data.indexOf(word)+word.length();
                int till = data.indexOf("<", idx);
                String finalData = data.substring(idx, till);
                System.out.println(finalData); // prints "4"
                progressBar.setVisibility(View.INVISIBLE);
                //display.setText(finalData);
                //display.setVisibility(View.VISIBLE);
                //imageView.setVisibility(View.INVISIBLE);
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
        getMenuInflater().inflate(R.menu.search_medicine_nav, menu);
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
