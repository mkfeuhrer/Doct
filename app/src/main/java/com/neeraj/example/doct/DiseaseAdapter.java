package com.neeraj.example.doct;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by neera on 3/24/2017.
 */

public class DiseaseAdapter extends RecyclerView.Adapter<DiseaseAdapter.MyViewHolder> {
    private List<Disease_type> diseaseList;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView disease, prob,id;

        public MyViewHolder(View view) {
            super(view);
            id=(TextView)view.findViewById(R.id.dis_id);
            disease = (TextView) view.findViewById(R.id.textQues);
            prob = (TextView) view.findViewById(R.id.textCategory);
        }
    }
    public DiseaseAdapter(List<Disease_type> diseaseList)
    {
        this.diseaseList=diseaseList;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.disease_row,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Disease_type disease_type=diseaseList.get(position);
        holder.id.setText(disease_type.getID());
        holder.disease.setText(disease_type.getDISEASE());
        holder.prob.setText("Probability : "+disease_type.getPROB());
    }

    @Override
    public int getItemCount() {
        return diseaseList.size();
    }
}
