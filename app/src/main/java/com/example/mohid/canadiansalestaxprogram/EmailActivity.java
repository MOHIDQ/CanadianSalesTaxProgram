package com.example.mohid.canadiansalestaxprogram;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EmailActivity extends AppCompatActivity {
    EditText emailText, subjectText, bodyText;
    Button sendButton, backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        emailText = (EditText)findViewById(R.id.emailText);
        subjectText = (EditText)findViewById(R.id.subjectText);
        bodyText = (EditText)findViewById(R.id.bodyText);
        sendButton = (Button)findViewById(R.id.sendButton);
        backButton = (Button)findViewById(R.id.backButton);
        bodyText.setText("Cost: $" + getIntent().getStringExtra("cost") + "\n" +
                         "PST: $" + getIntent().getStringExtra("PST") + "\n" +
                         "GST: $" + getIntent().getStringExtra("GST") + "\n" +
                         "HST: $ " + getIntent().getStringExtra("HST") + "\n"+
                         "Total Cost: " + getIntent().getStringExtra("total"));
        subjectText.setText("Sent by Canadians Sales Tax Program");
        sendMail();
        goBack();
    }

    public void sendMail() {
        sendButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String to = emailText.getText().toString();
                        String subject = subjectText.getText().toString();
                        String body = bodyText.getText().toString();
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
                        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                        intent.putExtra(Intent.EXTRA_TEXT, body);
                        intent.setType("message/rfc822");
                        startActivity(Intent.createChooser(intent, "Select an email application"));
                    }
                }
        );
    }
    public void goBack() {
        backButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(EmailActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }
}
