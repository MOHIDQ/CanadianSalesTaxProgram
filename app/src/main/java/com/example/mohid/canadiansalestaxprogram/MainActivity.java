package com.example.mohid.canadiansalestaxprogram;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    dataBaseHelper mydb;
    ImageView emailImage;
    EditText costEdit, TaxPEdit, TaxGEdit, TaxHEdit, TotalEdit;
    Button calc, viewAll, calculate;
    TextView cost, hstText, gstText, pstText;
    Spinner prov;
    private double pst, gst, hst;
    private Calendar cal = Calendar.getInstance();
    private int dd = cal.get(Calendar.DAY_OF_MONTH);
    private int mm = cal.get(Calendar.MONTH);
    private int yy = cal.get(Calendar.YEAR);
    private StringBuilder date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mydb = new dataBaseHelper(this);
        emailImage = (ImageView)findViewById(R.id.emailImage);
        emailImage.setImageResource(R.drawable.image1);
        costEdit = (EditText)findViewById(R.id.CostEdit);
        TaxPEdit = (EditText)findViewById(R.id.TaxPEdit);
        TaxGEdit = (EditText)findViewById(R.id.TaxGEdit);
        TaxHEdit = (EditText)findViewById(R.id.TaxHEdit);
        TotalEdit = (EditText)findViewById(R.id.TotalEdit);
        calc = (Button)findViewById(R.id.calc);
        calculate = (Button)findViewById(R.id.calculate);
        viewAll = (Button)findViewById(R.id.viewAll);
        hstText = (TextView)findViewById(R.id.hstText);
        gstText = (TextView)findViewById(R.id.gstText);
        pstText = (TextView)findViewById(R.id.pstText);
        costEdit.setShowSoftInputOnFocus(false);

        prov = (Spinner)findViewById(R.id.province);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.Provinces,R.layout.spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        prov.setAdapter(adapter);
        prov.setOnItemSelectedListener(this);

        date = (new StringBuilder().append(yy).append(" ").append("/").append(mm + 1).append("/").append(dd));
        TaxPEdit.setKeyListener(null); TaxGEdit.setKeyListener(null); TaxHEdit.setKeyListener(null); TotalEdit.setKeyListener(null);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        addData();
        viewData();
        emailAct();
    }
    //method valiadiation to determine if valid/legal value has been entered
    public boolean validation() {
        int countDot = 0;
        for (int i =0; i < costEdit.getText().toString().length(); i++) {
            if (costEdit.getText().toString().equals(".")) {
                return true;
            }
            if (costEdit.getText().toString().charAt(i) == '.') {
                countDot ++;
            }
        }
        if (countDot >= 2) {
            return true;
        }
        return false;
    }
    //method addData saves data of calculation
    public void addData() {
        calc.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isInserted = mydb.insertData(costEdit.getText().toString(), TaxPEdit.getText().toString(),
                                TaxGEdit.getText().toString(), TaxHEdit.getText().toString(),
                                TotalEdit.getText().toString(), date.toString());
                        if (isInserted){
                            Toast.makeText(MainActivity.this, "Transaction Saved", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Data Not Inserted", Toast.LENGTH_LONG).show();

                        }
                    }
                }

        );
    }

    public void viewData() {
        viewAll.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cursor result = mydb.getData();
                        if (result.getCount() == 0) {
                            //show message
                            showMessage("Transaction History", "Nothing Found");
                            return;
                        }
                        StringBuffer buffer = new StringBuffer();
                        while (result.moveToNext()) {
                            buffer.append("Transaction #: " + result.getString(0) + "\n");
                            buffer.append("Price: $" + result.getString(1) + "\n");
                            buffer.append("PST: $" + result.getString(2) + "\n");
                            buffer.append("GST: $" + result.getString(3) + "\n");
                            buffer.append("HST: $" + result.getString(4) + "\n");
                            buffer.append("TOTAL: " + result.getString(5) + "\n");
                            buffer.append("DATE: " + result.getString(6) + "\n\n");
                        }
                        showMessage("Transaction History", buffer.toString());
                    }
                }
        );
    }
    public void showMessage(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }
    public void emailAct() {
        emailImage.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent emailIntent = new Intent(MainActivity.this, EmailActivity.class);
                        if (costEdit.getText().toString().equals("")) {
                            emailIntent.putExtra("cost", "0.00");
                        }
                        else {
                            emailIntent.putExtra("cost", costEdit.getText().toString());
                        }
                        emailIntent.putExtra("PST", TaxPEdit.getText().toString());
                        emailIntent.putExtra("GST", TaxGEdit.getText().toString());
                        emailIntent.putExtra("HST", TaxHEdit.getText().toString());
                        emailIntent.putExtra("total", TotalEdit.getText().toString());
                        startActivity(emailIntent);
                    }
                }
        );
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String Text = prov.getSelectedItem().toString();
        Toast.makeText(this, "You Selected: " + Text, Toast.LENGTH_SHORT).show();
        TaxPEdit.setText("0.00"); TaxGEdit.setText("0.00");
        TaxHEdit.setText("0.00"); TotalEdit.setText("0.00");
        costEdit.setText("");
        switch(position) {
            case 0:
                hstText.setText("HST(13%)");
                pstText.setText("PST(0%)");
                gstText.setText("GST(0%)");
                calculate.setOnClickListener(
                        new Button.OnClickListener() {
                            public void onClick(View v) {
                                hst = 0.13;
                                if (!costEdit.getText().toString().equals("")) {
                                TaxGEdit.setText("0.00"); TaxPEdit.setText("0.00");
                                String x = costEdit.getText().toString();
                                if (!validation()) {
                                    double y = Double.parseDouble(x);
                                    double g = Math.round((y * hst) * 100.0) / 100.0;
                                    double t = Math.round((y + g) * 100.0) / 100.0;
                                    String h = Double.toString(g);
                                    String tt = Double.toString(t);

                                    TotalEdit.setText("$" + tt);
                                    TaxHEdit.setText(h);
                                }
                                    else if (validation()) {
                                        Toast.makeText(MainActivity.this, "Enter A Valid Value", Toast.LENGTH_SHORT).show();
                                }
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "Enter A Valid Value", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
            break;
            case 1:
                hstText.setText("HST(0%)");
                pstText.setText("PST(9.975%)");
                gstText.setText("GST(5%)");
                TaxPEdit.setText("0.00"); TaxGEdit.setText("0.00");
                TaxHEdit.setText("0.00"); TotalEdit.setText("0.00");
                calculate.setOnClickListener(
                        new Button.OnClickListener() {
                            public void onClick(View v) {
                                gst = 0.05; pst = 0.09975;
                                if (!costEdit.getText().toString().equals("")) {
                                    TaxHEdit.setText("0.00");
                                    String x = costEdit.getText().toString();
                                    if (!validation()) {
                                        double y = Double.parseDouble(x);
                                        double g = Math.round((y * gst) * 100.0) / 100.0;
                                        double p = Math.round((y * pst) * 100.0) / 100.0;
                                        String pp = Double.toString(p);
                                        double t = Math.round((p + g + y) * 100.0) / 100.0;
                                        String h = Double.toString(g);
                                        String tt = Double.toString(t);
                                        TotalEdit.setText("$" + tt);
                                        TaxGEdit.setText(h);
                                        TaxPEdit.setText(pp);
                                    }
                                    else if (validation()) {
                                        Toast.makeText(MainActivity.this, "Enter A Valid Value", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "Enter A Valid Value", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
            break;
            case 2:
                hstText.setText("HST(0%)");
                pstText.setText("PST(0%)");
                gstText.setText("GST(5%)");
                TaxPEdit.setText("0.00"); TaxGEdit.setText("0.00");
                TaxHEdit.setText("0.00"); TotalEdit.setText("0.00");
                calculate.setOnClickListener(
                        new Button.OnClickListener() {
                            public void onClick(View v) {
                                gst = 0.05;
                                if (!costEdit.getText().toString().equals("")) {
                                    TaxHEdit.setText("0.00");
                                    TaxPEdit.setText("0.00");
                                    String x = costEdit.getText().toString();
                                    if (!validation()) {
                                        double y = Double.parseDouble(x);
                                        double g = Math.round((y * gst) * 100.0) / 100.0;
                                        double t = Math.round((g + y) * 100.0) / 100.0;
                                        String h = Double.toString(g);
                                        String tt = Double.toString(t);
                                        TotalEdit.setText("$" + tt);
                                        TaxGEdit.setText(h);
                                    }
                                    else if (validation()) {
                                        Toast.makeText(MainActivity.this, "Enter A Valid Value", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "Enter A Valid Value", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
            break;
            case 3:
                hstText.setText("HST(0%)");
                pstText.setText("PST(8%)");
                gstText.setText("GST(5%)");
                TaxPEdit.setText("0.00"); TaxGEdit.setText("0.00");
                TaxHEdit.setText("0.00"); TotalEdit.setText("0.00");
                calculate.setOnClickListener(
                        new Button.OnClickListener() {
                            public void onClick(View v) {
                                gst = 0.05; pst = 0.08;
                                if (!costEdit.getText().toString().equals("")) {
                                    TaxHEdit.setText("0.00");
                                    String x = costEdit.getText().toString();
                                    if (!validation()) {
                                        double y = Double.parseDouble(x);
                                        double g = Math.round((y * gst) * 100.0) / 100.0;
                                        double p = Math.round((y * pst) * 100.0) / 100.0;
                                        String pp = Double.toString(p);
                                        double t = Math.round((p + g + y) * 100.0) / 100.0;
                                        String h = Double.toString(g);
                                        String tt = Double.toString(t);
                                        TotalEdit.setText("$" + tt);
                                        TaxGEdit.setText(h);
                                        TaxPEdit.setText(pp);
                                    }
                                    else if (validation()) {
                                        Toast.makeText(MainActivity.this, "Enter A Valid Value", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "Enter A Valid Value", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
            break;
            case 4:
                hstText.setText("HST(0%)");
                pstText.setText("PST(7%)");
                gstText.setText("GST(5%)");
                TaxPEdit.setText("0.00"); TaxGEdit.setText("0.00");
                TaxHEdit.setText("0.00"); TotalEdit.setText("0.00");
                calculate.setOnClickListener(
                        new Button.OnClickListener() {
                            public void onClick(View v) {
                                gst = 0.05; pst = 0.07;
                                if (!costEdit.getText().toString().equals("")) {
                                    TaxHEdit.setText("0.00");
                                    String x = costEdit.getText().toString();
                                    if (!validation()) {
                                        double y = Double.parseDouble(x);
                                        double g = Math.round((y * gst) * 100.0) / 100.0;
                                        double p = Math.round((y * pst) * 100.0) / 100.0;
                                        String pp = Double.toString(p);
                                        double t = Math.round((p + g + y) * 100.0) / 100.0;
                                        String h = Double.toString(g);
                                        String tt = Double.toString(t);
                                        TotalEdit.setText("$" + tt);
                                        TaxGEdit.setText(h);
                                        TaxPEdit.setText(pp);
                                    }
                                    else if (validation()) {
                                        Toast.makeText(MainActivity.this, "Enter A Valid Value", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "Enter A Valid Value", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
            break;
            case 5:
                hstText.setText("HST(0%)");
                pstText.setText("PST(5%)");
                gstText.setText("GST(5%)");
                TaxPEdit.setText("0.00"); TaxGEdit.setText("0.00");
                TaxHEdit.setText("0.00"); TotalEdit.setText("0.00");
                calculate.setOnClickListener(
                        new Button.OnClickListener() {
                            public void onClick(View v) {
                                gst = 0.05; pst = 0.05;
                                if (!costEdit.getText().toString().equals("")) {
                                    TaxHEdit.setText("0.00");
                                    String x = costEdit.getText().toString();
                                    if (!validation()) {
                                        double y = Double.parseDouble(x);
                                        double g = Math.round((y * gst) * 100.0) / 100.0;
                                        double p = Math.round((y * pst) * 100.0) / 100.0;
                                        String pp = Double.toString(p);
                                        double t = Math.round((p + g + y) * 100.0) / 100.0;
                                        String h = Double.toString(g);
                                        String tt = Double.toString(t);
                                        TotalEdit.setText("$" + tt);
                                        TaxGEdit.setText(h);
                                        TaxPEdit.setText(pp);
                                    }
                                    else if (validation()) {
                                        Toast.makeText(MainActivity.this, "Enter A Valid Value", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "Enter A Valid Value", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
            break;
            case 6:
                hstText.setText("HST(15%)");
                pstText.setText("PST(0%)");
                gstText.setText("GST(0%)");
                TaxPEdit.setText("0.00"); TaxGEdit.setText("0.00");
                TaxHEdit.setText("0.00"); TotalEdit.setText("0.00");
                calculate.setOnClickListener(
                        new Button.OnClickListener() {
                            public void onClick(View v) {
                                hst = 0.15;
                                if (!costEdit.getText().toString().equals("")) {
                                    TaxHEdit.setText("0.00");
                                    String x = costEdit.getText().toString();
                                    if (!validation()) {
                                        double y = Double.parseDouble(x);
                                        double g = Math.round((y * hst) * 100.0) / 100.0;
                                        double t = Math.round((g + y) * 100.0) / 100.0;
                                        String h = Double.toString(g);
                                        String tt = Double.toString(t);
                                        TotalEdit.setText("$" + tt);
                                        TaxHEdit.setText(h);
                                    }
                                    else if (validation()) {
                                        Toast.makeText(MainActivity.this, "Enter A Valid Value", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "Enter A Valid Value", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
            break;
            case 7:
                hstText.setText("HST(15%)");
                pstText.setText("PST(0%)");
                gstText.setText("GST(0%)");
                TaxPEdit.setText("0.00"); TaxGEdit.setText("0.00");
                TaxHEdit.setText("0.00"); TotalEdit.setText("0.00");
                calculate.setOnClickListener(
                        new Button.OnClickListener() {
                            public void onClick(View v) {
                                hst = 0.15;
                                if (!costEdit.getText().toString().equals("")) {
                                    TaxHEdit.setText("0.00");
                                    String x = costEdit.getText().toString();
                                    if (!validation()) {
                                        double y = Double.parseDouble(x);
                                        double g = Math.round((y * hst) * 100.0) / 100.0;
                                        double t = Math.round((g + y) * 100.0) / 100.0;
                                        String h = Double.toString(g);
                                        String tt = Double.toString(t);
                                        TotalEdit.setText("$" + tt);
                                        TaxHEdit.setText(h);
                                    }
                                    else if (validation()) {
                                        Toast.makeText(MainActivity.this, "Enter A Valid Value", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "Enter A Valid Value", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
            break;
            case 8:
                hstText.setText("HST(15%)");
                pstText.setText("PST(0%)");
                gstText.setText("GST(0%)");
                TaxPEdit.setText("0.00"); TaxGEdit.setText("0.00");
                TaxHEdit.setText("0.00"); TotalEdit.setText("0.00");
                calculate.setOnClickListener(
                        new Button.OnClickListener() {
                            public void onClick(View v) {
                                hst = 0.15;
                                if (!costEdit.getText().toString().equals("")) {
                                    TaxHEdit.setText("0.00");
                                    String x = costEdit.getText().toString();
                                    if (!validation()) {
                                        double y = Double.parseDouble(x);
                                        double g = Math.round((y * hst) * 100.0) / 100.0;
                                        double t = Math.round((g + y) * 100.0) / 100.0;
                                        String h = Double.toString(g);
                                        String tt = Double.toString(t);
                                        TotalEdit.setText("$" + tt);
                                        TaxHEdit.setText(h);
                                    }
                                    else if (validation()) {
                                        Toast.makeText(MainActivity.this, "Enter A Valid Value", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "Enter A Valid Value", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
            break;
            case 9:
                hstText.setText("HST(14%)");
                pstText.setText("PST(0%)");
                gstText.setText("GST(0%)");
                TaxPEdit.setText("0.00"); TaxGEdit.setText("0.00");
                TaxHEdit.setText("0.00"); TotalEdit.setText("0.00");
                calculate.setOnClickListener(
                        new Button.OnClickListener() {
                            public void onClick(View v) {
                                hst = 0.14;
                                if (!costEdit.getText().toString().equals("")) {
                                    TaxHEdit.setText("0.00");
                                    String x = costEdit.getText().toString();
                                    if (!validation()) {
                                        double y = Double.parseDouble(x);
                                        double g = Math.round((y * hst) * 100.0) / 100.0;
                                        double t = Math.round((g + y) * 100.0) / 100.0;
                                        String h = Double.toString(g);
                                        String tt = Double.toString(t);
                                        TotalEdit.setText("$" + tt);
                                        TaxHEdit.setText(h);
                                    }
                                    else if (validation()) {
                                        Toast.makeText(MainActivity.this, "Enter A Valid Value", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "Enter A Valid Value", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
            break;
            case 10:
                hstText.setText("HST(0%)");
                pstText.setText("PST(0%)");
                gstText.setText("GST(5%)");
                TaxPEdit.setText("0.00"); TaxGEdit.setText("0.00");
                TaxHEdit.setText("0.00"); TotalEdit.setText("0.00");
                calculate.setOnClickListener(
                        new Button.OnClickListener() {
                            public void onClick(View v) {
                                gst = 0.05;
                                if (!costEdit.getText().toString().equals("")) {
                                    TaxHEdit.setText("0.00");
                                    String x = costEdit.getText().toString();
                                    if (!validation()) {
                                        double y = Double.parseDouble(x);
                                        double g = Math.round((y * gst) * 100.0) / 100.0;
                                        double t = Math.round((g + y) * 100.0) / 100.0;
                                        String h = Double.toString(g);
                                        String tt = Double.toString(t);
                                        TotalEdit.setText("$" + tt);
                                        TaxGEdit.setText(h);
                                    }
                                    else if (validation()) {
                                        Toast.makeText(MainActivity.this, "Enter A Valid Value", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "Enter A Valid Value", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
            break;
            case 11:
                hstText.setText("HST(0%)");
                pstText.setText("PST(0%)");
                gstText.setText("GST(5%)");
                TaxPEdit.setText("0.00"); TaxGEdit.setText("0.00");
                TaxHEdit.setText("0.00"); TotalEdit.setText("0.00");
                calculate.setOnClickListener(
                        new Button.OnClickListener() {
                            public void onClick(View v) {
                                gst = 0.05;
                                if (!costEdit.getText().toString().equals("")) {
                                    TaxHEdit.setText("0.00");
                                    String x = costEdit.getText().toString();
                                    if (!validation()) {
                                        double y = Double.parseDouble(x);
                                        double g = Math.round((y * gst) * 100.0) / 100.0;
                                        double t = Math.round((g + y) * 100.0) / 100.0;
                                        String h = Double.toString(g);
                                        String tt = Double.toString(t);
                                        TotalEdit.setText("$" + tt);
                                        TaxGEdit.setText(h);
                                    }
                                    else if (validation()) {
                                        Toast.makeText(MainActivity.this, "Enter A Valid Value", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "Enter A Valid Value", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
            break;
            case 12:
                hstText.setText("HST(0%)");
                pstText.setText("PST(0%)");
                gstText.setText("GST(5%)");
                TaxPEdit.setText("0.00"); TaxGEdit.setText("0.00");
                TaxHEdit.setText("0.00"); TotalEdit.setText("0.00");

                    calculate.setOnClickListener(
                            new Button.OnClickListener() {
                                public void onClick(View v) {
                                    gst = 0.05;
                                    if (!costEdit.getText().toString().equals("")) {
                                        TaxHEdit.setText("0.00");
                                        String x = costEdit.getText().toString();
                                        if (!validation()) {
                                            double y = Double.parseDouble(x);
                                            double g = Math.round((y * gst) * 100.0) / 100.0;
                                            double t = Math.round((g + y) * 100.0) / 100.0;
                                            String h = Double.toString(g);
                                            String tt = Double.toString(t);
                                            TotalEdit.setText("$" + tt);
                                            TaxGEdit.setText(h);
                                        }
                                        else if (validation()) {
                                            Toast.makeText(MainActivity.this, "Enter A Valid Value", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                        else {
                                        Toast.makeText(MainActivity.this, "Enter A Valid Value", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                    );
            break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void onClickOne(View v) {
        String oldText = costEdit.getText().toString();
        costEdit.setText(oldText + "1");
    }
    public void onClickTwo(View v) {
        String oldText = costEdit.getText().toString();
        costEdit.setText(oldText + "2");
    }
    public void onClickThree(View v) {
        String oldText = costEdit.getText().toString();
        costEdit.setText(oldText + "3");
    }
    public void onClickFour(View v) {
        String oldText = costEdit.getText().toString();
        costEdit.setText(oldText + "4");
    }
    public void onClickFive(View v) {
        String oldText = costEdit.getText().toString();
        costEdit.setText(oldText + "5");
    }
    public void onClickSix(View v) {
        String oldText = costEdit.getText().toString();
        costEdit.setText(oldText + "6");
    }
    public void onClickSeven(View v) {
        String oldText = costEdit.getText().toString();
        costEdit.setText(oldText + "7");
    }
    public void onClickEight(View v) {
        String oldText = costEdit.getText().toString();
        costEdit.setText(oldText + "8");
    }
    public void onClickNine(View v) {
        String oldText = costEdit.getText().toString();
        costEdit.setText(oldText + "9");
    }
    public void onClickZero(View v) {
        String oldText = costEdit.getText().toString();
        costEdit.setText(oldText + "0");
    }
    public void onClickDeci(View v) {
        String oldText = costEdit.getText().toString();
        costEdit.setText(oldText + ".");
    }
    public void onClickClear(View v) {
        costEdit.setText("");
    }
}
