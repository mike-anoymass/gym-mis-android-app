package com.example.myfirstapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;


public class AddMember extends Activity implements RadioGroup.OnCheckedChangeListener {
    EditText name, mobile, email, dob, address, code;
    RadioGroup radioGroup;
    RadioButton male;
    String selectedGender, doj;
    CircularImageView imageView;
    Bitmap btm;
    Uri selectedImageUri;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_member);

        initializeVariables();

        ActionBar bar = getActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        radioGroup.setOnCheckedChangeListener(this);
    }

    public void initializeVariables() {
        name = findViewById(R.id.name);
        mobile = findViewById(R.id.mobile);
        email = findViewById(R.id.email);
        address = findViewById(R.id.address);
        code = findViewById(R.id.code);
        dob = findViewById(R.id.dob);
        radioGroup = findViewById(R.id.radioGroup);
        male = findViewById(R.id.male);
        selectedGender = "Male";

        imageView = findViewById(R.id.image);
    }


    public void tbActivities() {
        /*if(tb.isChecked()){
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }else{
            password.setInputType(InputType.TYPE_CLASS_TEXT);
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater blowUp = getMenuInflater();
        blowUp.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aboutUs:
                Intent i = new Intent("com.example.myfirstapp.ABOUT");
                startActivity(i);
                break;
            case R.id.preference:
                Intent p = new Intent("com.example.myfirstapp.PREFERENCE");
                startActivity(p);
                break;

            case R.id.exit:
                finish();
                break;

            case android.R.id.home:
                finish();
                break;
        }
        return false;
    }

    public void loadDatePicker(View view) {
        Intent i = new Intent(this, DatePickerSettings.class);
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    dob.setText(data.getData().toString());
                }
                break;

            case 0:
                if (resultCode == RESULT_OK) {
                    selectedImageUri = data.getData();
                    imageView.setImageURI(selectedImageUri);
                }
                break;

        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (male.isChecked()) {
            //Toast.makeText(this, "male selected", Toast.LENGTH_SHORT).show();aww
            selectedGender = "Male";
        } else {
            //Toast.makeText(this, "female selected", Toast.LENGTH_SHORT).show();
            selectedGender = "Female";
        }
    }

    public void backBtnClicked(View view) {
        finish();
    }

    public void saveBtnClicked(View view) throws IOException {

        boolean validated = validateInputData();

        if (validated) {
            DBOpenHelper dbOpenHelper = new DBOpenHelper(this);
            SQLiteDatabase database;

            dbOpenHelper.openDatabase();
            database = dbOpenHelper.getReadableDatabase();

            ContentValues initialValues = new ContentValues();
            initialValues.put("full_name", name.getText().toString().trim().toLowerCase());
            initialValues.put("mobile", code.getText().toString() + mobile.getText().toString());
            initialValues.put("email", email.getText().toString());
            initialValues.put("address", address.getText().toString());
            initialValues.put("gender", selectedGender);
            initialValues.put("dob", dob.getText().toString());
            initialValues.put("join_date", getDate());

            if (selectedImageUri != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                    byte[] inputData = Utils.getBytes(inputStream);
                    initialValues.put("image", inputData);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }


            Long result = database.insert("member", null, initialValues);

            if (result == -1) {
                Toast.makeText(this, "Failed to add member", Toast.LENGTH_SHORT).show();
            } else {
                clearFields();
                Toast.makeText(this, "Member added", Toast.LENGTH_SHORT).show();
            }

            database.close();
            dbOpenHelper.close();
        }
    }

    private String getDate() {
        Date time = Calendar.getInstance().getTime();
        SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        String longTime = time.toString();
        String garbage = longTime.substring(longTime.indexOf("GMT")).trim();

        String shortTime = longTime.replace(garbage, "");

        return date.format(time) + "\n" + shortTime;
    }

    private boolean validateInputData() {
        String name, mobile, address;
        name = this.name.getText().toString();
        mobile = this.mobile.getText().toString();
        address = this.address.getText().toString();

        if (name.isEmpty() || mobile.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please Complete the form", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void clearFields() {
        name.setText("");
        mobile.setText("");
        email.setText("");
        address.setText("");
        male.setChecked(true);
        dob.setText("");
        selectedGender = "Male";
        selectedImageUri = null;
        imageView.setImageResource(R.drawable.image_32);
    }

    public void setImage(View view) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, 0);
    }
}
