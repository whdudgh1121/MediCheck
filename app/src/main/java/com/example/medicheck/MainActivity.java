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
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private CalendarView calendarV;
    private Dialog diaryDlg, menuDlg;
    private TextView diaryTv;
    private EditText contextEdt;
    private Button saveBtn, changeBtn, deleteBtn;
    private ImageButton btnMenu, btnLogo;

    private HashMap<String, String> diaryMap;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 초기화
        calendarV = findViewById(R.id.cal);
        btnMenu = findViewById(R.id.buttonMenu);
        btnLogo = findViewById(R.id.logo);

        diaryDlg = new Dialog(this);
        diaryDlg.setContentView(R.layout.dialog_cal);

        // 다이얼로그에서 사용될 뷰 초기화
        diaryTv = diaryDlg.findViewById(R.id.diaryTextView);
        contextEdt = diaryDlg.findViewById(R.id.contextEditText);
        saveBtn = diaryDlg.findViewById(R.id.save_Btn);
        changeBtn = diaryDlg.findViewById(R.id.cha_Btn);
        deleteBtn = diaryDlg.findViewById(R.id.del_Btn);

        // 메뉴 다이얼로그 초기화
        menuDlg = new Dialog(this);
        menuDlg.setContentView(R.layout.dialog_menu);

        // HashMap 초기화
        diaryMap = new HashMap<>();

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

            // 다이얼로그 내 이미지 버튼 초기화 및 클릭 리스너 추가
            ImageView btnSearch = menuDlg.findViewById(R.id.meSearchImage);
            ImageView btnMyPage = menuDlg.findViewById(R.id.meUserImage);

            // 검색 페이지 이동
            btnSearch.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, SearchBox.class);
                startActivity(intent);
                menuDlg.dismiss();
            });

            // 마이페이지 이동
            btnMyPage.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, MyPageActivity.class);
                startActivity(intent);
                menuDlg.dismiss();
            });
        });

        // logo 버튼 클릭 리스너
        btnLogo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
