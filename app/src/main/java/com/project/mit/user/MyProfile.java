package com.project.mit.user;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.project.mit.R;
import com.project.mit.session.SessionManager;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

public class MyProfile extends AppCompatActivity {

    private static String API_UPDATE_USER = "http://hawkingnight.com/projectmit/API/UpdateUser.php";

    ImageView UserImage;
    EditText FirstNameField, LastNameField, BirthdayField,
            EmailField, PhoneNoField, Address01Field,
            Address02Field, CityField, StateField, PostCodeField;
    Button ButtonSaved;

    String getUID, getFirstName, getLastName, getBirthday, getEmail, getPhoneNo, getAddress01, getAddress02, getCity, getState, getPostCode;
    SessionManager sessionManager;
    DatePickerDialog datePickerDialog;

    private void Declare(){
        UserImage = findViewById(R.id.UserImage);
        FirstNameField = findViewById(R.id.FirstNameField);
        LastNameField = findViewById(R.id.LastNameField);
        BirthdayField = findViewById(R.id.BirthdayField);
        EmailField = findViewById(R.id.EmailField);
        PhoneNoField = findViewById(R.id.PhoneField);
        Address01Field = findViewById(R.id.Address01Field);
        Address02Field = findViewById(R.id.Address02Field);
        CityField = findViewById(R.id.CityField);
        StateField = findViewById(R.id.StateField);
        PostCodeField = findViewById(R.id.PostCodeField);
        ButtonSaved = findViewById(R.id.ButtonSave);
    }
    private void getSession(){
        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.checkLogin();

        HashMap<String, String> UserDetails = sessionManager.getUserDetail();
        getUID = UserDetails.get(SessionManager.UID);
        getFirstName = UserDetails.get(SessionManager.FIRSTNAME);
        getLastName = UserDetails.get(SessionManager.LASTNAME);
        getBirthday = UserDetails.get(SessionManager.BIRTHDAY);
        getEmail = UserDetails.get(SessionManager.EMAIL);
        getPhoneNo = UserDetails.get(SessionManager.PHONE_NO);
        getAddress01 = UserDetails.get(SessionManager.ADDRESS01);
        getAddress02 = UserDetails.get(SessionManager.ADDRESS02);
        getCity = UserDetails.get(SessionManager.CITY);
        getState = UserDetails.get(SessionManager.STATE);
        getPostCode = UserDetails.get(SessionManager.POSTCODE);
    }
    private void MethodSettings(){
        FirstNameField.setText(getFirstName);
        LastNameField.setText(getLastName);
        BirthdayField.setText(getBirthday);
        EmailField.setText(getEmail);
        PhoneNoField.setText(getPhoneNo);
        Address01Field.setText(getAddress01);
        Address02Field.setText(getAddress02);
        CityField.setText(getCity);
        StateField.setText(getState);
        PostCodeField.setText(getPostCode);

        ButtonSaved.setOnClickListener(v -> SaveData());
        BirthdayField.setOnClickListener(v -> BirthdaySettings());
    }

    //START of method to allow image selection using camera/gallery
    private void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, (dialog, item) -> {

            if (options[item].equals("Take Photo")) {
                Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);

            } else if (options[item].equals("Choose from Gallery")) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);

            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        UserImage.setImageBitmap(selectedImage);
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                UserImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                            }
                        }

                    }
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_capture, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //END of method

    private void BirthdaySettings(){
        Calendar calendar = Calendar.getInstance();
        int Day = calendar.get(Calendar.DAY_OF_MONTH);
        int Month = calendar.get(Calendar.MONTH);
        int Year = calendar.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(getApplicationContext(), R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String Date = dayOfMonth + "/" + (month + 1) + "/" + year;
                BirthdayField.setText(Date);
            }
        }, Year, Month, Day);
        datePickerDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        Declare();
        getSession();
        MethodSettings();

        //camera methods
        UserImage = (ImageView) findViewById(R.id.UserImage);
        UserImage.setOnClickListener(view -> selectImage(MyProfile.this));
        //end of camera methods
    }

    private void SaveData(){
        String FirstName = FirstNameField.getText().toString();
        String LastName = LastNameField.getText().toString();
        String Birthday = BirthdayField.getText().toString();
        String EmailAddress = EmailField.getText().toString();
        String PhoneNo = PhoneNoField.getText().toString();
        String Address01 = Address01Field.getText().toString();
        String Address02 = Address02Field.getText().toString();
        String City = CityField.getText().toString();
        String State = StateField.getText().toString();
        String Postcode = PostCodeField.getText().toString();

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("UID", getUID);
        parameters.put("FirstName", FirstName);
        parameters.put("LastName", LastName);
        parameters.put("Birthday", Birthday);
        parameters.put("EmailAddress", EmailAddress);
        parameters.put("PhoneNo", PhoneNo);
        parameters.put("Address01", Address01);
        parameters.put("Address02", Address02);
        parameters.put("City", City);
        parameters.put("State",State);
        parameters.put("Postcode", Postcode);

        JsonObjectRequest request_json = new JsonObjectRequest(API_UPDATE_USER, new JSONObject(parameters),
                response -> Log.i("RESPONSE", "SUCCESS!"),
                error -> Log.i("ERORR", error.toString()));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request_json);
    }
}
