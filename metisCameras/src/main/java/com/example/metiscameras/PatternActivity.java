package com.example.metiscameras;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.metiscameras.models.responses.PatternResponse;

public class PatternActivity extends AppCompatActivity {

    private PatternResponse pattern;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getIntent().getExtras();
        if(arguments != null){
            pattern = (PatternResponse) arguments.getSerializable(PatternResponse.class.getSimpleName());
        }

        setContentView(R.layout.activity_pattern);

//        if(pattern.isNew()) {
//            TextView mainLabel = (TextView) findViewById(R.id.main_label);
//            mainLabel.setText("Добавление паттерна");
//        }

        Button open = (Button) findViewById(R.id.button_go_back);
        open.setOnClickListener(view -> {
            String article = ((EditText) findViewById(R.id.article_input)).getText().toString();
            String name = ((EditText) findViewById(R.id.name_input)).getText().toString();
            String matreial = ((EditText) findViewById(R.id.material_input)).getText().toString();

//            if(!article.equals(pattern.getArticle())){
//
//            }

            finish();
        });
    }
}