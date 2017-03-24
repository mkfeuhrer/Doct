package com.neeraj.example.doct;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class Search extends AppCompatActivity {
    Button search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        search=(Button)findViewById(R.id.search);

        final Spinner spinner = (Spinner) findViewById(R.id.searchdoctor);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        //String text = spinner.getSelectedItem().toString();
        //System.out.println(text);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = spinner.getSelectedItem().toString().toLowerCase();
                System.out.println(text);
                String url="https://www.google.co.in/maps/search/"+text;
                Intent bi=new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(bi);
            }
        });

    }
}
