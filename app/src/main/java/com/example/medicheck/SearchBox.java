package com.example.medicheck;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import androidx.appcompat.app.AppCompatActivity;

public class SearchBox extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_box);

        // buttonMenu 버튼 초기화
        ImageButton buttonMenu = findViewById(R.id.buttonMenu);

        // 메뉴 버튼 클릭 시 팝업 메뉴 표시
        buttonMenu.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(SearchBox.this, buttonMenu);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

            // 메뉴 항목 클릭 시 이벤트 처리
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.search_box) {
                    // 진료 내역 확인하기로 이동
                    startActivity(new Intent(SearchBox.this, SearchBox.class));
                    return true;
                } else if (item.getItemId() == R.id.menu_profile) {
                    // 프로필/비상연락처 등록으로 이동
                    startActivity(new Intent(SearchBox.this, MyPageActivity.class));
                    return true;
                } else {
                    return false;
                }
            });

            popupMenu.show();
        });

        ImageButton logoButton = findViewById(R.id.logo);

        // logo 버튼 클릭 시 MainActivity로 이동
        logoButton.setOnClickListener(v -> {
            // MainActivity로 이동하는 인텐트 생성
            Intent intent = new Intent(SearchBox.this, MainActivity.class);
            startActivity(intent);
        });
    }
}
