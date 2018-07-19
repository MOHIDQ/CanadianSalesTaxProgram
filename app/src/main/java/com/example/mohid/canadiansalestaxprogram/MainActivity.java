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
            case 0://option for alberta
                taxCalc(0.05,0,0);
                break;
            case 1: //option for british columbia
                taxCalc(0.05, 0.07, 0);
                break;
            case 2: //option for manitoba
                taxCalc(0.08, 0.05, 0);
                break;
            case 3: //option for new brunswick
                taxCalc(0, 0, 0.15);
                break;
            case 4: //option for newfoundland and labrador
                taxCalc(0,0,0.15);
                break;
            case 5: //option for northwest territories
                taxCalc(0.05, 0, 0);
                break;
            case 6://option for nova scotia
                taxCalc(0,0,0.15);
                break;
            case 7: //option for nunavut
                taxCalc(0.05, 0, 0);
                break;
            case 8: //option for ontario
                taxCalc(0,0,0.13);
                break;
            case 9: //option for PEI
                taxCalc(0,0,0.15);
                break;
            case 10: //option for quebec
                taxCalc(0.05, 0.09975, 0);
                break;
            case 11: //option ofr saskatchewan
                taxCalc(0.05, 0.06, 0);
                break;
            case 12: //option for yukon
                taxCalc(0.05, 0, 0);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //method to calculate the taxes and update screen
    public void taxCalc(double gt, double pt, double ht) {
        hstText.setText("HST(" + (ht * 100.0) + "%)");
        pstText.setText("PST(" + (pt * 1000.0 / 10.0) + "%)");
        gstText.setText("GST(" + (gt * 100.0) + "%)");
        gst = gt;
        pst = pt;
        hst = ht;
        calculate.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        if (!costEdit.getText().toString().equals("")) {
                            //TaxHEdit.setText("0.00"); TaxPEdit.setText("0.00");
                            String x = costEdit.getText().toString();
                            if (!validation()) {
                                double y = Double.parseDouble(x);
                                double g = Math.round((y * gst) * 100.0) / 100.0;
                                double t = Math.round((y + g) * 100.0) / 100.0;
                                String h = Double.toString(g);

                                double y2 = Double.parseDouble(x);
                                double g2 = Math.round((y2 * pst) * 100.0) / 100.0;
                                double t2 = Math.round((y2 + g2) * 100.0) / 100.0;
                                String h2 = Double.toString(g2);

                                double y3 = Double.parseDouble(x);
                                double g3 = Math.round((y3 * hst) * 100.0) / 100.0;
                                double t3 = Math.round((y3 + g3) * 100.0) / 100.0;
                                String h3 = Double.toString(g3);

                                double total = (g + g2 + g3 + y) * 100.0 / 100.0;


                                TotalEdit.setText("$" + total);
                                TaxGEdit.setText(h);
                                TaxPEdit.setText(h2);
                                TaxHEdit.setText(h3);

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
