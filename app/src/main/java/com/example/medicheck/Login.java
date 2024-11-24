package com.example.medicheck;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // login.xml 변수
        Button btnlogin, btnJoin,btnTest;

        // 로그인 변수 연결
        btnlogin = findViewById(R.id.Btnlogin);
        btnJoin = findViewById(R.id.BtnJoin);
        btnTest = findViewById(R.id.btnTest);


        //메인으로 이동, 추후 삭제 예정
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        // Dialog_Join 팝업창 생성
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // dialog_join.xml 파일 인플레이트
                View dialogView = View.inflate(Login.this, R.layout.dialog_join, null);

                // 팝업창 변수 연결
                EditText dlgEdtNa = dialogView.findViewById(R.id.edtNa);
                EditText dlgEdtPho = dialogView.findViewById(R.id.edtPho);
                EditText dlgEdtBirth = dialogView.findViewById(R.id.edtBirth);
                EditText dlgEdtGuNa = dialogView.findViewById(R.id.edtGuName);
                EditText dlgEdtGuPho = dialogView.findViewById(R.id.edtGuPho);
                EditText dlgEdtId = dialogView.findViewById(R.id.edtId);
                EditText dlgEdtPw = dialogView.findViewById(R.id.edtPw);
                CheckBox dlgCbGu = dialogView.findViewById(R.id.cbGuard);


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
                AlertDialog.Builder dlg = new AlertDialog.Builder(Login.this);
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
    }}
