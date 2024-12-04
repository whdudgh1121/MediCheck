package com.example.medicheck;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject; // 이 부분을 추가합니다.
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    // 로그인 관련 변수
    private EditText editTextId, editTextPassword;
    private Button btnLogin, btnJoin, btnTest;

    // 회원가입 팝업 관련 변수
    private EditText dlgEdtNa, dlgEdtPho, dlgEdtBirth, dlgEdtGuNa, dlgEdtGuPho, dlgEdtId, dlgEdtPw;
    private CheckBox dlgCbGu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Log.d("LoginActivity", "onCreate called"); // 이 로그가 출력되는지 확인

        // 로그인 관련 변수 연결
        editTextId = findViewById(R.id.editTextId);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.Btnlogin);
        btnJoin = findViewById(R.id.BtnJoin);
        btnTest = findViewById(R.id.btnTest);

        // Test 버튼 클릭 시 MainActivity로 이동 (추후 삭제 예정)
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ButtonTest", "Test button clicked"); // 로그 출력
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 로그인 버튼 클릭 시 LoginTask 실행
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editTextId.getText().toString().trim(); // 아이디 입력값 가져오기
                String password = editTextPassword.getText().toString().trim(); // 비밀번호 입력값 가져오기
                new LoginTask().execute(id, password); // LoginTask를 실행하여 서버에 로그인 요청
            }
        });

        // 회원가입 버튼 클릭 시 팝업창 띄우기
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ButtonTest", "Join button clicked"); // 로그 출력

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

                // 보호자 정보 입력 체크박스 상태 여부
                dlgCbGu.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        dlgEdtGuNa.setVisibility(View.VISIBLE);
                        dlgEdtGuPho.setVisibility(View.VISIBLE);
                    } else {
                        dlgEdtGuNa.setVisibility(View.GONE);
                        dlgEdtGuPho.setVisibility(View.GONE);
                    }
                });

                // AlertDialog.Builder 생성 -> 대화상자 생성
                AlertDialog.Builder dlg = new AlertDialog.Builder(LoginActivity.this);
                dlg.setTitle("회원가입 정보 입력");
                dlg.setView(dialogView); // 대화상자

                // setPositiveButton
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = dlgEdtNa.getText().toString();
                        String birth = dlgEdtBirth.getText().toString();
                        String phone = dlgEdtPho.getText().toString();
                        String id = dlgEdtId.getText().toString();
                        String pw = dlgEdtPw.getText().toString();
                        String guardianName = dlgEdtGuNa.getText().toString();
                        String guardianPhone = dlgEdtGuPho.getText().toString();

                        if (name.isEmpty() || phone.isEmpty() || birth.isEmpty() || id.isEmpty() || pw.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "필수 정보를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                        } else {
                            if (dlgCbGu.isChecked() && (guardianName.isEmpty() || guardianPhone.isEmpty())) {
                                Toast.makeText(getApplicationContext(), "보호자 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "가입을 완료하였습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "가입을 취소하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

                dlg.show(); // 팝업창 보이기
            }
        });
    }

    // 비동기 작업을 수행하여 서버와 로그인 요청을 처리하는 클래스
    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String id = params[0];
            String password = params[1];
            String result;

            try {
                // 서버 URL 설정 (실제 서버 URL로 변경 필요)
                URL url = new URL("http://192.168.25.41/login.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // 서버로 전송할 데이터 설정
                String postData = "id=" + id + "&password=" + password;
                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                // 서버에서 응답 읽기
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                result = reader.readLine();
                reader.close();

                return result; // 서버 응답 반환
            } catch (Exception e) {
                e.printStackTrace();
                return "error"; // 오류 발생 시 "error" 반환
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
