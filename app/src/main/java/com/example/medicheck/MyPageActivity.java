package com.example.medicheck;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
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

        Button edtNameButton = findViewById(R.id.edtName);
        edtNameButton.setOnClickListener(v -> showNameChangeDialog());

        Button edtPhoneButton = findViewById(R.id.edtPhone);
        edtPhoneButton.setOnClickListener(v -> showPhoneChangeDialog());

        Button edtAddressButton = findViewById(R.id.edtAdr);
        edtAddressButton.setOnClickListener(v -> showAddressChangeDialog());

        Button edtPasswordButton = findViewById(R.id.edtPassword);
        edtPasswordButton.setOnClickListener(v -> showPasswordChangeDialog());


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
    private void showNameChangeDialog() {
        // 다이얼로그 뷰를 동적으로 가져오기
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_name, null);

        // 다이얼로그 내부의 뷰 참조
        EditText inputName = dialogView.findViewById(R.id.editTextName);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        // 기존 이름 표시 (예: "김민지")
        TextView nameTextView = findViewById(R.id.userName); // 실제 이름 TextView ID로 교체 필요
        String currentName = nameTextView.getText().toString();
        inputName.setText(currentName);

        // 다이얼로그 생성
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        // 저장 버튼 클릭 리스너
        btnSave.setOnClickListener(v -> {
            String newName = inputName.getText().toString();

            if (!TextUtils.isEmpty(newName)) {
                // 이름 변경
                nameTextView.setText(newName);
                dialog.dismiss();
                Toast.makeText(this, "이름이 변경되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        // 취소 버튼 클릭 리스너
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
    // 연락처 변경 다이얼로그
    private void showPhoneChangeDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_phone, null);

        EditText inputPhone = dialogView.findViewById(R.id.editTextPhone); // 수정된 ID
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        TextView phoneTextView = findViewById(R.id.userPhone); // 기존 연락처 TextView
        String currentPhone = phoneTextView.getText().toString();
        inputPhone.setText(currentPhone);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        btnSave.setOnClickListener(v -> {
            String newPhone = inputPhone.getText().toString();

            if (!TextUtils.isEmpty(newPhone)) {
                phoneTextView.setText(newPhone);
                dialog.dismiss();
                Toast.makeText(this, "연락처가 변경되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "연락처를 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }


    // 주소 변경 다이얼로그
    private void showAddressChangeDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_address, null);

        EditText inputAddress = dialogView.findViewById(R.id.editTextAdr);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        TextView addressTextView = findViewById(R.id.userAdr); // 실제 주소 TextView ID로 교체 필요
        String currentAddress = addressTextView.getText().toString();
        inputAddress.setText(currentAddress);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        btnSave.setOnClickListener(v -> {
            String newAddress = inputAddress.getText().toString();

            if (!TextUtils.isEmpty(newAddress)) {
                addressTextView.setText(newAddress);
                dialog.dismiss();
                Toast.makeText(this, "주소가 변경되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // 비밀번호 변경 다이얼로그
    private void showPasswordChangeDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_password, null);

        EditText inputPassword = dialogView.findViewById(R.id.editTextPassword);
        EditText inputConfirmPassword = dialogView.findViewById(R.id.editTextConfirmPassword);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        btnSave.setOnClickListener(v -> {
            String newPassword = inputPassword.getText().toString();
            String confirmPassword = inputConfirmPassword.getText().toString();

            if (!TextUtils.isEmpty(newPassword) && newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else if (TextUtils.isEmpty(newPassword)) {
                Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }




}
