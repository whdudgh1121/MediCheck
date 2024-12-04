package com.example.medicheck;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private CalendarView calendarV;
    private Dialog diaryDlg;
    private TextView diaryTv;
    private EditText contextEdt;
    private Button saveBtn, changeBtn, deleteBtn;

    // 날짜별 내용을 저장하기 위한 HashMap
    private HashMap<String, String> diaryMap;

    private String selectedDate; // 선택된 날짜를 저장

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 캘린더 관련 초기화
        calendarV = findViewById(R.id.cal);

        // 다이얼로그 초기화
        diaryDlg = new Dialog(this);
        diaryDlg.setContentView(R.layout.dialog_cal);

        diaryTv = diaryDlg.findViewById(R.id.diaryTextView);
        contextEdt = diaryDlg.findViewById(R.id.contextEditText);
        saveBtn = diaryDlg.findViewById(R.id.save_Btn);
        changeBtn = diaryDlg.findViewById(R.id.cha_Btn);
        deleteBtn = diaryDlg.findViewById(R.id.del_Btn);

        // HashMap 초기화
        diaryMap = new HashMap<>();

        // 캘린더 날짜 선택 리스너
        calendarV.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // 선택된 날짜 포맷
            selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;

            // 기존 내용 불러오기
            String savedContent = diaryMap.getOrDefault(selectedDate, "");

            // 팝업창 설정
            diaryTv.setText(selectedDate);
            contextEdt.setText(savedContent); // 저장된 내용이 있으면 표시, 없으면 빈칸
            contextEdt.setVisibility(View.VISIBLE);
            saveBtn.setVisibility(View.VISIBLE);

            // 수정/삭제 버튼 초기 상태
            changeBtn.setVisibility(savedContent.isEmpty() ? View.INVISIBLE : View.VISIBLE);
            deleteBtn.setVisibility(savedContent.isEmpty() ? View.INVISIBLE : View.VISIBLE);

            // 팝업창 표시
            diaryDlg.show();
        });

        // 저장 버튼 클릭 리스너
        saveBtn.setOnClickListener(v -> {
            String content = contextEdt.getText().toString();
            if (!content.isEmpty()) {
                // 내용 저장
                diaryMap.put(selectedDate, content);

                // 다이얼로그 닫기
                diaryDlg.dismiss();
            }
        });

        // 수정 버튼 클릭 리스너
        changeBtn.setOnClickListener(v -> {
            String updatedContent = contextEdt.getText().toString();
            if (!updatedContent.isEmpty()) {
                // 내용 업데이트
                diaryMap.put(selectedDate, updatedContent);

                // 다이얼로그 닫기
                diaryDlg.dismiss();
            }
        });

        // 삭제 버튼 클릭 리스너
        deleteBtn.setOnClickListener(v -> {
            // 선택된 날짜의 내용 삭제
            diaryMap.remove(selectedDate);
            contextEdt.setText("");

            // 다이얼로그 닫기
            diaryDlg.dismiss();
        });

        // 메뉴 관련 초기화
        ImageButton buttonMenu = findViewById(R.id.buttonMenu);

        // 메뉴 버튼 클릭 시 팝업 메뉴 표시
        buttonMenu.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, buttonMenu);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

            // 메뉴 항목 클릭 시 이벤트 처리
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.search_box) {
                    // 진료 내역 확인하기로 이동
                    startActivity(new Intent(MainActivity.this, SearchBox.class));
                    return true;
                } else if (item.getItemId() == R.id.menu_profile) {
                    // 프로필/비상연락처 등록으로 이동
                    startActivity(new Intent(MainActivity.this, MyPageActivity.class));
                    return true;
                } else {
                    return false;
                }
            });

            popupMenu.show();
        });
    }
}
