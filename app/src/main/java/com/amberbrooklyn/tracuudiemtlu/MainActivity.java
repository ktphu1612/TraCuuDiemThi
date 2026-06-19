package com.amberbrooklyn.tracuudiemtlu;

import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.widget.LinearLayout;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText edtMaSV;
    private Button btnTraCuu;
    private TextView txtTenSV;
    private RecyclerView rvDiemThi;
    private Spinner spnHocKi, spnNamHoc;

    private GradeAdapter adapter;
    private List<Grade> gradeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtMaSV = findViewById(R.id.edtMaSV);
        btnTraCuu = findViewById(R.id.btnTraCuu);
        txtTenSV = findViewById(R.id.txtTenSV);
        //nut bi mat
        TextView txtTitleSecret = findViewById(R.id.txtTitleSecret);

        //bam 3 lan login admin
        txtTitleSecret.setOnClickListener(new View.OnClickListener() {
            long lastClickTime = 0;
            int clickCount = 0;

            @Override
            public void onClick(View v) {
                long clickTime = System.currentTimeMillis();
                if (clickTime - lastClickTime < 500) {
                    clickCount++;
                    if (clickCount == 3) {
                        showLoginDialog();
                        clickCount = 0;
                    }
                } else {
                    clickCount = 1;
                }
                lastClickTime = clickTime;
            }
        });

        rvDiemThi = findViewById(R.id.rvDiemThi);
        spnHocKi = findViewById(R.id.spnHocKi);
        spnNamHoc = findViewById(R.id.spnNamHoc);

        setupSpinners();

        gradeList = new ArrayList<>();
        adapter = new GradeAdapter(gradeList);
        rvDiemThi.setLayoutManager(new LinearLayoutManager(this));
        rvDiemThi.setAdapter(adapter);

        btnTraCuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String maSV = edtMaSV.getText().toString().trim();

                if (maSV.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập Mã sinh viên", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!maSV.matches("^A\\d{5}$")) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập đúng Mã Sinh Viên", Toast.LENGTH_SHORT).show();
                    return;
                }
                String namHoc = spnNamHoc.getSelectedItem().toString();
                String hocKi = spnHocKi.getSelectedItem().toString();

                fetchDiemThiTuFirebase(maSV, namHoc, hocKi);
            }
        });
    }

    private void setupSpinners() {
        String[] arrNamHoc = {"2024-2025", "2025-2026"};
        ArrayAdapter<String> adapterNH = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrNamHoc);
        spnNamHoc.setAdapter(adapterNH);

        String[] arrHocKi = {"1", "2", "3"};
        ArrayAdapter<String> adapterHK = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrHocKi);
        spnHocKi.setAdapter(adapterHK);
    }
    private void fetchDiemThiTuFirebase(String maSV, String namHoc, String hocKi) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("SinhVien").child(maSV);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gradeList.clear();

                if (snapshot.exists()) {
                    String tenSV = snapshot.child("thongTin").child("ten").getValue(String.class);
                    txtTenSV.setText("Sinh viên: " + tenSV);
                    txtTenSV.setVisibility(View.VISIBLE);

                    DataSnapshot diemThiSnapshot = snapshot.child(namHoc).child(hocKi);

                    if (diemThiSnapshot.exists()) {
                        for (DataSnapshot monSnapshot : diemThiSnapshot.getChildren()) {
                            String tenMon = monSnapshot.getKey();
                            String diem = monSnapshot.getValue(String.class);
                            gradeList.add(new Grade(tenMon, diem));
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Bạn chưa thi môn nào trong kỳ này.", Toast.LENGTH_LONG).show();
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Không tìm thấy sinh viên này!", Toast.LENGTH_SHORT).show();
                    txtTenSV.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error404!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🔑 Đăng nhập quyền Giáo viên");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText inputUser = new EditText(this);
        inputUser.setHint("Tài khoản");
        layout.addView(inputUser);

        final EditText inputPass = new EditText(this);
        inputPass.setHint("Mật khẩu");
        inputPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(inputPass);

        builder.setView(layout);

        builder.setPositiveButton("Đăng nhập", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String user = inputUser.getText().toString().trim();
                String pass = inputPass.getText().toString().trim();

                if (user.equals("admin") && pass.equals("admin")) {
                    Toast.makeText(MainActivity.this, "Đăng nhập thành công.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}