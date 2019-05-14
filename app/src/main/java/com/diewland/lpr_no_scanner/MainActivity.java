package com.diewland.lpr_no_scanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText text_idcard;
    private Button btn_idcard;

    private static final int PICK_CAMERA = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_idcard = findViewById(R.id.btn_idcard);
        text_idcard = findViewById(R.id.text_idcard);

        btn_idcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, LivePreviewActivity.class);
                startActivityForResult(myIntent, PICK_CAMERA);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if((resultCode == RESULT_OK) && (requestCode == PICK_CAMERA)){
            if(data != null){
                String aid = data.getStringExtra("ACC_ID");
                text_idcard.setText(aid);
            }
        }
    }

}
