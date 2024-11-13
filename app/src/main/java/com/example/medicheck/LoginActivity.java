package com.example.medicheck;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextId, editTextPassword;
    private Button btnLogin, btnJoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // 아이디와 비밀번호 입력 필드 및 로그인/가입 버튼 초기화
        editTextId = findViewById(R.id.editTextId);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.Btnlogin);
        btnJoin = findViewById(R.id.BtnJoin);

        // 로그인 버튼 클릭 시 LoginTask 실행
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editTextId.getText().toString().trim(); // 아이디 입력값 가져오기
                String password = editTextPassword.getText().toString().trim(); // 비밀번호 입력값 가져오기
                new LoginTask().execute(id, password); // LoginTask를 실행하여 서버에 로그인 요청
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
                URL url = new URL("http://10.0.2.2/login.php");
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
            // 서버 응답에 따라 결과 처리
            if (result.equals("success")) {
                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                // 로그인 성공 시 다른 화면으로 이동하는 등의 추가 작업 수행
            } else if (result.equals("fail")) {
                Toast.makeText(LoginActivity.this, "Invalid ID or password.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, "Connection error.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
