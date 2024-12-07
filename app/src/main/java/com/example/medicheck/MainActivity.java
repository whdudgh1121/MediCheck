package com.example.medicheck;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

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
        fetchNotices();

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
                startActivity(intent);
                menuDlg.dismiss();
            });

            btnMyPage.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, MyPageActivity.class);
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

    // 네트워크 요청 함수
    private void fetchNotices() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL("http://192.168.25.32/notice.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    reader.close();
                    return result.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    parseJSON(result);
                }
            }
        }.execute();
    }

    // JSON 파싱 함수
    private void parseJSON(String json) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            StringBuilder notices = new StringBuilder();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject noticeObject = jsonArray.getJSONObject(i);
                String notice = noticeObject.getString("notice");
                notices.append(notice).append("\n");
            }

            btnNotication.setText(notices.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
