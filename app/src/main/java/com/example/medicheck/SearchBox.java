package com.example.medicheck;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;


public class SearchBox extends AppCompatActivity {

    private ImageButton btnSearch, btnMyPage,btnMenu, btnLogo,btn;
    private Dialog menuDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_box);
        androidx.appcompat.widget.SearchView searchView = findViewById(R.id.search_view);
        Button btnCheck = findViewById(R.id.btn_check);
        String userId = getIntent().getStringExtra("userId"); // MainActivity에서 받은 userId
        btnCheck.setOnClickListener(v -> {
            String query = searchView.getQuery().toString();
            if (!query.isEmpty()) {
                Intent intent = new Intent(SearchBox.this, TreatmentHistoryActivity.class);
                intent.putExtra("searchQuery", query); // 검색어 전달
                intent.putExtra("userId", userId); // userId 전달
                startActivity(intent);
            } else {
                Toast.makeText(this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });
        // 진료과 Spinner 설정
        /*Spinner spinnerDepartments = findViewById(R.id.spinner_departments);
        ArrayAdapter<CharSequence> adapterDepartments = ArrayAdapter.createFromResource(
                this,
                R.array.de_array, // 연결된 배열 참조
                android.R.layout.simple_spinner_item
        );
        adapterDepartments.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartments.setAdapter(adapterDepartments);

        // 스피너 초기값을 힌트로 설정
        spinnerDepartments.setSelection(adapterDepartments.getCount() - 1);

        // 질병 Spinner 설정
        Spinner spinnerDiseases = findViewById(R.id.spinner_diseases);
        ArrayAdapter<CharSequence> adapterDiseases = ArrayAdapter.createFromResource(
                this,
                R.array.di_array, // 연결된 배열 참조
                android.R.layout.simple_spinner_item
        );
        adapterDiseases.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDiseases.setAdapter(adapterDiseases);

        // 스피너 초기값을 힌트로 설정
        spinnerDiseases.setSelection(adapterDiseases.getCount() - 1);

        // Spinner 선택 이벤트 처리
        spinnerDepartments.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDepartment = (String) parent.getItemAtPosition(position);
                if (position == adapterDepartments.getCount() - 1) {
                    // 힌트 항목 선택 시 처리
                    Toast.makeText(SearchBox.this, "진료과를 선택하세요", Toast.LENGTH_SHORT).show();
                } else {
                    // 실제 선택한 항목 처리
                    Toast.makeText(SearchBox.this, "선택한 진료과: " + selectedDepartment, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 선택되지 않은 경우 처리
            }
        });

        spinnerDiseases.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDisease = (String) parent.getItemAtPosition(position);
                if (position == adapterDiseases.getCount() - 1) {
                    // 힌트 항목 선택 시 처리
                    Toast.makeText(SearchBox.this, "질병을 선택하세요", Toast.LENGTH_SHORT).show();
                } else {
                    // 실제 선택한 항목 처리
                    Toast.makeText(SearchBox.this, "선택한 질병: " + selectedDisease, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 선택되지 않은 경우 처리
            }
        });*/



        btnMenu = findViewById(R.id.buttonMenu);
        btnLogo =findViewById(R.id.logo);


        // 메뉴 다이얼로그 초기화
        menuDlg = new Dialog(this);
        menuDlg.setContentView(R.layout.dialog_menu);

        // 메뉴 버튼 클릭 리스너
        btnMenu.setOnClickListener(v -> {
            menuDlg.show();

            // 다이얼로그 내 이미지 버튼 초기화 및 클릭 리스너 추가
            ImageView btnSearch = menuDlg.findViewById(R.id.meSearchImage);
            ImageView btnMyPage = menuDlg.findViewById(R.id.meUserImage);

            // 검색 페이지 이동
            btnSearch.setOnClickListener(view -> {
                Intent intent = new Intent(SearchBox.this, SearchBox.class);
                startActivity(intent);
                menuDlg.dismiss();
            });

            // 마이페이지 이동
            btnMyPage.setOnClickListener(view -> {
                Intent intent = new Intent(SearchBox.this, MyPageActivity.class);
                startActivity(intent);
                menuDlg.dismiss();
            });
        });

        btnLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchBox.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}