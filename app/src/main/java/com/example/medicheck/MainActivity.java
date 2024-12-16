package com.example.medicheck;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private CalendarView calendarV;
    private Dialog diaryDlg, menuDlg;
    private TextView diaryTv, btnNotication;
    private EditText contextEdt;
    private Button saveBtn, changeBtn, deleteBtn;
    private ImageButton btnMenu, btnLogo;

    private HashMap<String, String> diaryMap;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // LoginActivity에서 전달된 userId를 받음
        String userId = getIntent().getStringExtra("userId");
        // 뷰 초기화
        calendarV = findViewById(R.id.cal);
        btnMenu = findViewById(R.id.buttonMenu);
        btnLogo = findViewById(R.id.logo);
        btnNotication = findViewById(R.id.btnNotication);

        // 다이얼로그 초기화
        diaryDlg = new Dialog(this);
        diaryDlg.setContentView(R.layout.dialog_cal);

        menuDlg = new Dialog(this);
        menuDlg.setContentView(R.layout.dialog_menu);

        // 다이얼로그 내 뷰 초기화
        diaryTv = diaryDlg.findViewById(R.id.diaryTextView);
        contextEdt = diaryDlg.findViewById(R.id.contextEditText);
        saveBtn = diaryDlg.findViewById(R.id.save_Btn);
        changeBtn = diaryDlg.findViewById(R.id.cha_Btn);
        deleteBtn = diaryDlg.findViewById(R.id.del_Btn);

        // HashMap 초기화
        diaryMap = new HashMap<>();

        // 알림 데이터 가져오기
        fetchUserNotifications();

        // 캘린더 날짜 선택 리스너
        calendarV.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
            String savedContent = diaryMap.getOrDefault(selectedDate, "");

            diaryTv.setText(selectedDate);
            contextEdt.setText(savedContent);
            contextEdt.setVisibility(View.VISIBLE);
            saveBtn.setVisibility(View.VISIBLE);

            changeBtn.setVisibility(savedContent.isEmpty() ? View.INVISIBLE : View.VISIBLE);
            deleteBtn.setVisibility(savedContent.isEmpty() ? View.INVISIBLE : View.VISIBLE);

            diaryDlg.show();
        });

        // 저장 버튼 클릭 리스너
        saveBtn.setOnClickListener(v -> {
            String content = contextEdt.getText().toString();
            if (!content.isEmpty()) {
                diaryMap.put(selectedDate, content);
                diaryDlg.dismiss();
            }
        });

        // 수정 버튼 클릭 리스너
        changeBtn.setOnClickListener(v -> {
            String updatedContent = contextEdt.getText().toString();
            if (!updatedContent.isEmpty()) {
                diaryMap.put(selectedDate, updatedContent);
                diaryDlg.dismiss();
            }
        });

        // 삭제 버튼 클릭 리스너
        deleteBtn.setOnClickListener(v -> {
            diaryMap.remove(selectedDate);
            contextEdt.setText("");
            diaryDlg.dismiss();
        });

        // 메뉴 버튼 클릭 리스너
        btnMenu.setOnClickListener(v -> {
            menuDlg.show();

            ImageView btnSearch = menuDlg.findViewById(R.id.meSearchImage);
            ImageView btnMyPage = menuDlg.findViewById(R.id.meUserImage);

            btnSearch.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, SearchBox.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                menuDlg.dismiss();
            });

            btnMyPage.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, MyPageActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);

                menuDlg.dismiss();
            });
        });

        // 로고 버튼 클릭 리스너
        btnLogo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // 공지사항 데이터를 서버에서 가져오는 함수
    private void fetchUserNotifications() {
        String url = "http://192.168.25.32/notice.php"; // 공지사항 가져오는 PHP URL

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        Log.d("ServerResponse", response);

                        // JSON 응답 처리
                        JSONArray jsonArray = new JSONArray(response);
                        StringBuilder notices = new StringBuilder();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject noticeObject = jsonArray.getJSONObject(i);
                            String notice = noticeObject.getString("notice");
                            notices.append(notice).append("\n");
                        }

                        btnNotication.setText(notices.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing notifications", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Server error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        queue.add(stringRequest);
    }
}
