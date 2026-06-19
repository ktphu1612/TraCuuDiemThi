package com.amberbrooklyn.tracuudiemtlu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class GradeAdapter extends RecyclerView.Adapter<GradeAdapter.GradeViewHolder> {

    private List<Grade> gradeList;

    public GradeAdapter(List<Grade> gradeList) {
        this.gradeList = gradeList;
    }

    @NonNull
    @Override
    public GradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grade, parent, false);
        return new GradeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GradeViewHolder holder, int position) {
        Grade grade = gradeList.get(position);
        holder.txtMonHoc.setText(grade.getSubject());
        holder.txtDiem.setText(grade.getScore());
    }

    @Override
    public int getItemCount() {
        return gradeList.size();
    }

    public static class GradeViewHolder extends RecyclerView.ViewHolder {
        TextView txtMonHoc, txtDiem;
        public GradeViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMonHoc = itemView.findViewById(R.id.txtMonHoc);
            txtDiem = itemView.findViewById(R.id.txtDiem);
        }
    }
}
