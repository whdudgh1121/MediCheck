package com.example.medicheck;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MyPageActivity extends AppCompatActivity {

    private LinearLayout dynamicLayout;
    private ImageButton btnMenu, btnLogo, btnProfile;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri; // 선택된 이미지의 URI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage);

        btnMenu = findViewById(R.id.buttonMenu);
        btnLogo = findViewById(R.id.logo);
        btnProfile = findViewById(R.id.btnProfile);

        dynamicLayout = findViewById(R.id.layoutDynamic);
        Button buttonAdd = findViewById(R.id.buttonAdd);

        // 동적 필드 추가 버튼 클릭 리스너
        buttonAdd.setOnClickListener(view -> addDynamicFields());

        // 프로필 버튼 클릭 시 갤러리 열기
        btnProfile.setOnClickListener(v -> openGallery());

        // 메뉴 버튼 클릭 리스너
        btnMenu.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(MyPageActivity.this, btnMenu);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.search_box) {
                    startActivity(new Intent(MyPageActivity.this, SearchBox.class));
                    return true;
                } else if (item.getItemId() == R.id.menu_profile) {
                    startActivity(new Intent(MyPageActivity.this, MyPageActivity.class));
                    return true;
                } else {
                    return false;
                }
            });

            popupMenu.show();
        });

        // 로고 버튼 클릭 리스너
        btnLogo.setOnClickListener(v -> {
            Intent intent = new Intent(MyPageActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void addDynamicFields() {
        LinearLayout newEntry = new LinearLayout(this);
        newEntry.setOrientation(LinearLayout.VERTICAL);
        newEntry.setPadding(16, 16, 16, 16);

        EditText inputName = new EditText(this);
        inputName.setHint("보호자 이름");

        EditText inputPhone = new EditText(this);
        inputPhone.setHint("전화번호");

        Button buttonSave = new Button(this);
        buttonSave.setText("저장");

        Button buttonCancel = new Button(this);
        buttonCancel.setText("취소");

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.setMargins(0, 8, 0, 8);
        buttonSave.setLayoutParams(buttonParams);
        buttonCancel.setLayoutParams(buttonParams);

        buttonSave.setOnClickListener(view -> {
            String name = inputName.getText().toString();
            String phone = inputPhone.getText().toString();

            if (!name.isEmpty() && !phone.isEmpty()) {
                addSavedEntry(newEntry, name, phone);
            } else {
                Toast.makeText(this, "이름과 전화번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        buttonCancel.setOnClickListener(view -> dynamicLayout.removeView(newEntry));

        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.RECTANGLE);
        background.setColor(Color.parseColor("#2196F3"));
        buttonCancel.setBackground(background);
        buttonSave.setBackground(background);

        buttonCancel.setTextColor(Color.WHITE);
        buttonSave.setTextColor(Color.WHITE);

        newEntry.addView(inputName);
        newEntry.addView(inputPhone);
        newEntry.addView(buttonSave);
        newEntry.addView(buttonCancel);
        dynamicLayout.addView(newEntry);
    }

    private void addSavedEntry(LinearLayout oldEntry, String name, String phone) {
        dynamicLayout.removeView(oldEntry);

        LinearLayout savedEntry = new LinearLayout(this);
        savedEntry.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams savedEntryParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        savedEntryParams.setMargins(0, 16, 0, 16);
        savedEntry.setLayoutParams(savedEntryParams);

        TextView textView = new TextView(this);
        textView.setText(String.format("%s (%s)", name, phone));
        textView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        Button buttonDelete = new Button(this);
        buttonDelete.setText("삭제");

        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.RECTANGLE);
        background.setColor(Color.parseColor("#2196F3"));
        background.setCornerRadius(100f);
        buttonDelete.setBackground(background);
        buttonDelete.setTextColor(Color.WHITE);

        buttonDelete.setOnClickListener(view -> dynamicLayout.removeView(savedEntry));

        savedEntry.addView(textView);
        savedEntry.addView(buttonDelete);
        dynamicLayout.addView(savedEntry);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            showImagePreviewPopup();
        }
    }

    private void showImagePreviewPopup() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View popupView = inflater.inflate(R.layout.dialog_profile, null);

        ImageView imageViewPopup = popupView.findViewById(R.id.imageViewPopup);
        Button buttonPopupSave = popupView.findViewById(R.id.buttonPopupSave);
        Button buttonPopupCancel = popupView.findViewById(R.id.buttonPopupCancel);

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            imageViewPopup.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(popupView)
                .setCancelable(false)
                .create();

        buttonPopupSave.setOnClickListener(v -> {
            btnProfile.setImageURI(imageUri);
            dialog.dismiss();
        });

        buttonPopupCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
