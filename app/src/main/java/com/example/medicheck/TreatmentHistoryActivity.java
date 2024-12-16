package com.example.medicheck;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TreatmentHistoryActivity extends AppCompatActivity {

    private ImageButton btnMenu;
    private LinearLayout treatment1Details;
    private ImageView treatment1Arrow;
    private boolean isTreatment1Visible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment_history);

        String userId = getIntent().getStringExtra("userId");
        String searchQuery = getIntent().getStringExtra("searchQuery");

        fetchMedicalHistory(userId, searchQuery);

        // 진료 정보 뷰 초기화
        treatment1Details = findViewById(R.id.treatment1_details);

        // 메뉴 버튼 초기화
        btnMenu = findViewById(R.id.buttonMenu);

        // 메뉴 버튼 클릭 리스너 설정
        btnMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(TreatmentHistoryActivity.this, btnMenu);

            // popup_menu.xml을 팝업 메뉴에 적용
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

            // 메뉴 항목 클릭 리스너 설정
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.search_box) {
                    // 검색 페이지로 이동
                    Intent intent = new Intent(TreatmentHistoryActivity.this, SearchBox.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.menu_profile) {
                    // 마이페이지로 이동
                    Intent intent = new Intent(TreatmentHistoryActivity.this, MyPageActivity.class);
                    startActivity(intent);
                    return true;
                } else {
                    return false;
                }
            });

            // 메뉴 표시
            popupMenu.show();
        });
    }

    private void fetchMedicalHistory(String userId, String searchQuery) {
        String url = "http://192.168.25.32/get_treatment_history.php"; // PHP 파일 URL

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        LinearLayout treatmentLayout = findViewById(R.id.treatment1_details);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject record = jsonArray.getJSONObject(i);

                            String disease = record.getString("disease");
                            String content = record.getString("content");
                            String medicalDay = record.getString("medical_day");

                            // 동적으로 텍스트뷰 추가
                            TextView treatmentView = new TextView(this);
                            treatmentView.setText("질병: " + disease + "\n내용: " + content + "\n진료일: " + medicalDay);
                            treatmentView.setTextSize(16);
                            treatmentView.setPadding(10, 10, 10, 10);

                            treatmentLayout.addView(treatmentView);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "데이터 처리 중 오류 발생", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "서버 오류: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userId", userId);
                params.put("searchQuery", searchQuery);
                return params;
            }
        };

        queue.add(stringRequest);
    }
}
