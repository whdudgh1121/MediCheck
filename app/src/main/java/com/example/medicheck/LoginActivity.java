package com.example.medicheck;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    // 로그인 관련 변수
    private EditText editTextId, editTextPassword;
    private Button btnLogin, btnJoin, btnTest;

    // 회원가입 팝업 관련 변수
    private EditText dlgEdtNa, dlgEdtPho, dlgEdtBirth, dlgEdtGuNa, dlgEdtGuPho, dlgEdtId, dlgEdtPw ;
    private RadioGroup dlgGender;

    private CheckBox dlgCbGu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Log.d("LoginActivity", "onCreate called");

        // 로그인 관련 변수 연결
        editTextId = findViewById(R.id.editTextId);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.Btnlogin);
        btnJoin = findViewById(R.id.BtnJoin);
        btnTest = findViewById(R.id.btnTest);

        // Test 버튼 클릭
        btnTest.setOnClickListener(v -> {
            Log.d("ButtonTest", "Test button clicked");
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // 로그인 버튼 클릭
        btnLogin.setOnClickListener(v -> {
            String id = editTextId.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            new LoginTask().execute(id, password);
        });

        // 회원가입 버튼 클릭
        btnJoin.setOnClickListener(v -> {
            Log.d("ButtonTest", "Join button clicked");

            // dialog_join.xml 파일 인플레이트
            View dialogView = View.inflate(LoginActivity.this, R.layout.dialog_join, null);

            // 팝업창 변수 연결
            dlgEdtNa = dialogView.findViewById(R.id.edtNa);
            dlgEdtPho = dialogView.findViewById(R.id.edtPho);
            dlgEdtBirth = dialogView.findViewById(R.id.edtBirth);
            dlgEdtGuNa = dialogView.findViewById(R.id.edtGuName);
            dlgEdtGuPho = dialogView.findViewById(R.id.edtGuPho);
            dlgEdtId = dialogView.findViewById(R.id.edtId);
            dlgEdtPw = dialogView.findViewById(R.id.edtPw);
            dlgCbGu = dialogView.findViewById(R.id.cbGuard);
            dlgGender = dialogView.findViewById(R.id.rgGe);
            dlgCbGu.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    dlgEdtGuNa.setVisibility(View.VISIBLE);
                    dlgEdtGuPho.setVisibility(View.VISIBLE);
                } else {
                    dlgEdtGuNa.setVisibility(View.GONE);
                    dlgEdtGuPho.setVisibility(View.GONE);
                }
            });

            // AlertDialog.Builder 생성
            AlertDialog.Builder dlg = new AlertDialog.Builder(LoginActivity.this);
            dlg.setTitle("회원가입 정보 입력");
            dlg.setView(dialogView);

            dlg.setPositiveButton("확인", (dialog, which) -> {
                String id = dlgEdtId.getText().toString();
                String name = dlgEdtNa.getText().toString();
                String birth = dlgEdtBirth.getText().toString();
                String phone = dlgEdtPho.getText().toString();
                String pw = dlgEdtPw.getText().toString();
                int selectedGender = dlgGender.getCheckedRadioButtonId();
                String gender = (selectedGender == R.id.rbMan) ? "M" : "F";
                boolean hasGuardian = dlgCbGu.isChecked();
                String guardianName = dlgEdtGuNa.getText().toString();
                String guardianPhone = dlgEdtGuPho.getText().toString();

                if (name.isEmpty() || phone.isEmpty() || birth.isEmpty() || id.isEmpty() || pw.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "필수 정보를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (hasGuardian && (guardianName.isEmpty() || guardianPhone.isEmpty())) {
                    Toast.makeText(getApplicationContext(), "보호자 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                submitUserData(id, name, birth, phone, pw,gender, hasGuardian, guardianName, guardianPhone);
            });

            dlg.setNegativeButton("취소", (dialog, which) ->
                    Toast.makeText(getApplicationContext(), "가입을 취소하였습니다.", Toast.LENGTH_SHORT).show()
            );

            dlg.show();
        });
    }

    private void submitUserData(String id, String name, String birth, String phone, String password,String gender, boolean hasGuardian, String guardianName, String guardianPhone) {
        String url = "http://192.168.25.32/join.php"; // 서버 URL
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> Toast.makeText(LoginActivity.this, "회원가입 성공!", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(LoginActivity.this, "회원가입 실패: " + error.getMessage(), Toast.LENGTH_LONG).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("name", name);
                params.put("birth", birth);
                params.put("phone", phone);
                params.put("password", password);
                params.put("sex", gender);
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                int age = currentYear - Integer.parseInt(birth.substring(0, 4));
                params.put("age", String.valueOf(age));

                if (hasGuardian) {
                    params.put("guardian_name", guardianName);
                    params.put("guardian_phone", guardianPhone);
                }

                return params;
            }
        };

        queue.add(stringRequest);
    }

    private class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String id = params[0];
            String password = params[1];
            String result;

            try {
                URL url = new URL("http://192.168.25.32/login.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String postData = "id=" + id + "&password=" + password;
                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                result = reader.readLine();
                reader.close();

                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return "error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(LoginActivity.this, "Connection error.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONObject jsonResponse = new JSONObject(result);
                String status = jsonResponse.getString("status");

                if ("success".equals(status)) {
                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid ID or password.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(LoginActivity.this, "Connection error.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
