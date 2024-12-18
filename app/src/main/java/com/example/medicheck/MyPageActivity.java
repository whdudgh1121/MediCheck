package com.example.medicheck;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MyPageActivity extends AppCompatActivity {

    private TextView txtName, txtPhone, txtPassword, txtGuardianName, txtGuardianPhone;
    private ImageButton btnMenu, btnLogo, btnProfile;
    private Button btnEditPhone, btnEditGuardianName, btnEditGuardianPhone, btnedtPassword;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri; // 선택된 이미지의 URI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage);

        // 텍스트뷰 초기화
        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        txtPassword = findViewById(R.id.edtPassword);
        txtGuardianName = findViewById(R.id.txtGuardianName);
        txtGuardianPhone = findViewById(R.id.txtGuardianPhone);

        // 버튼 초기화
        btnMenu = findViewById(R.id.buttonMenu);
        btnLogo = findViewById(R.id.logo);
        btnProfile = findViewById(R.id.btnProfile);
        btnEditPhone = findViewById(R.id.btnEditPhone);
        btnEditGuardianName = findViewById(R.id.btnEditGuardianName);
        btnEditGuardianPhone = findViewById(R.id.btnEditGuardianPhone);
        btnedtPassword = findViewById(R.id.btnedtPassword);

        // Intent로 전달받은 로그인한 사용자 ID
        String userId = getIntent().getStringExtra("userId");
        if (userId != null) {
            fetchUserInfo(userId);
        } else {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
        }

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

        // 프로필 버튼 클릭 리스너
        btnProfile.setOnClickListener(v -> openGallery());

        // 정보 수정 버튼 클릭 리스너 추가
        btnEditPhone.setOnClickListener(v -> showEditPopup("연락처", txtPhone));
        btnEditGuardianName.setOnClickListener(v -> showEditPopup("보호자 이름", txtGuardianName));
        btnEditGuardianPhone.setOnClickListener(v -> showEditPopup("보호자 전화번호", txtGuardianPhone));
        btnedtPassword.setOnClickListener(v -> showEditPopup("비밀번호", txtPassword));
    }

    // 서버에서 사용자 정보 가져오기
    private void fetchUserInfo(String userId) {
        String url = "http://192.168.25.32/get_user_info.php"; // 서버 URL

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");

                        if ("success".equals(status)) {
                            JSONObject userData = jsonResponse.getJSONObject("data");

                            // 데이터를 텍스트뷰에 설정
                            txtName.setText("성명: " + userData.getString("name"));
                            txtPhone.setText("연락처: " + userData.getString("phone"));
                            txtPassword.setText("패스워드: " + userData.getString("password"));
                            txtGuardianName.setText("보호자 이름: " + userData.getString("guardian_name"));
                            txtGuardianPhone.setText("보호자 전화번호: " + userData.getString("guardian_phone"));
                        } else {
                            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing user data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Server error: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", userId); // 서버에 전달할 사용자 ID
                return params;
            }
        };

        queue.add(stringRequest);
    }

    // 팝업창을 띄워 데이터를 변경하는 메서드
    private void showEditPopup(String field, TextView targetTextView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.dialog_edit, null);
        builder.setView(popupView);

        TextView txtPopupTitle = popupView.findViewById(R.id.txtPopupTitle);
        EditText edtPopupInput = popupView.findViewById(R.id.edtPopupInput);
        Button btnPopupSave = popupView.findViewById(R.id.btnPopupSave);
        Button btnPopupCancel = popupView.findViewById(R.id.btnPopupCancel);

        txtPopupTitle.setText(field + " 변경");

        AlertDialog dialog = builder.create();

        btnPopupSave.setOnClickListener(v -> {
            String newValue = edtPopupInput.getText().toString().trim();
            if (!newValue.isEmpty()) {
                targetTextView.setText(field + ": " + newValue);
                updateUserInfo(field, newValue); // 서버에 업데이트 요청
                dialog.dismiss();
            } else {
                Toast.makeText(this, "값을 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        btnPopupCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // 사용자 정보를 업데이트하는 메서드
    private void updateUserInfo(String field, String newValue) {
        String url = "http://192.168.25.32/update_user_info.php";

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        if ("success".equals(status)) {
                            Toast.makeText(this, field + "이(가) 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "변경 실패: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "서버 응답 처리 중 오류 발생", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "서버 오류: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", getIntent().getStringExtra("userId")); // 사용자 ID
                params.put("field", field); // 필드명
                params.put("value", newValue); // 새 값
                return params;
            }
        };

        queue.add(stringRequest);
    }
    // 갤러리 열기
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

    // 이미지 미리보기 팝업
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
