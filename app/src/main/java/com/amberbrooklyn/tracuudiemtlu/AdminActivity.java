package com.amberbrooklyn.tracuudiemtlu;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminActivity extends AppCompatActivity {

    private EditText edtAdminMaSV, edtAdminTenSV, edtAdminMonHoc, edtAdminDiem;
    private Spinner spnAdminNamHoc, spnAdminHocKi;
    private Button btnLuuDiem, btnXoaDiem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        edtAdminMaSV = findViewById(R.id.edtAdminMaSV);
        edtAdminTenSV = findViewById(R.id.edtAdminTenSV);
        edtAdminMonHoc = findViewById(R.id.edtAdminMonHoc);
        edtAdminDiem = findViewById(R.id.edtAdminDiem);
        spnAdminNamHoc = findViewById(R.id.spnAdminNamHoc);
        spnAdminHocKi = findViewById(R.id.spnAdminHocKi);
        btnLuuDiem = findViewById(R.id.btnLuuDiem);
        btnXoaDiem = findViewById(R.id.btnXoaDiem);

        setupSpinners();

        btnLuuDiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String maSV = edtAdminMaSV.getText().toString().trim();
                String tenSV = edtAdminTenSV.getText().toString().trim();
                String monHoc = edtAdminMonHoc.getText().toString().trim();
                String diem = edtAdminDiem.getText().toString().trim();
                String namHoc = spnAdminNamHoc.getSelectedItem().toString();
                String hocKi = spnAdminHocKi.getSelectedItem().toString();

                if (maSV.isEmpty() || tenSV.isEmpty() || monHoc.isEmpty() || diem.isEmpty()) {
                    Toast.makeText(AdminActivity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("SinhVien");

                myRef.child(maSV).child("thongTin").child("ten").setValue(tenSV);

                myRef.child(maSV).child(namHoc).child(hocKi).child(monHoc).setValue(diem);

                Toast.makeText(AdminActivity.this, "Lưu điểm thành công.", Toast.LENGTH_SHORT).show();

                edtAdminMonHoc.setText("");
                edtAdminDiem.setText("");
            }
        });

        btnXoaDiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String maSV = edtAdminMaSV.getText().toString().trim();
                String monHoc = edtAdminMonHoc.getText().toString().trim();
                String namHoc = spnAdminNamHoc.getSelectedItem().toString();
                String hocKi = spnAdminHocKi.getSelectedItem().toString();

                if (maSV.isEmpty() || monHoc.isEmpty()) {
                    Toast.makeText(
                            AdminActivity.this,
                            "Nhập MSSV và môn học cần xóa!",
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                DatabaseReference myRef =
                        FirebaseDatabase.getInstance()
                                .getReference("SinhVien");

                myRef.child(maSV)
                        .child(namHoc)
                        .child(hocKi)
                        .child(monHoc)
                        .removeValue();

                Toast.makeText(
                        AdminActivity.this,
                        "Xóa điểm thành công!",
                        Toast.LENGTH_SHORT
                ).show();

                edtAdminMonHoc.setText("");
                edtAdminDiem.setText("");
            }
        });

    }

    private void setupSpinners() {
        String[] arrNamHoc = {"2024-2025", "2025-2026"};
        ArrayAdapter<String> adapterNH = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrNamHoc);
        spnAdminNamHoc.setAdapter(adapterNH);

        String[] arrHocKi = {"1", "2", "3"};
        ArrayAdapter<String> adapterHK = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrHocKi);
        spnAdminHocKi.setAdapter(adapterHK);
    }
}