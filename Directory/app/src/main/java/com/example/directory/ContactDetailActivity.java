package com.example.directory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.directory.data.DirectoryContract.*;
import com.example.directory.data.DirectoryDbHelper;

public class ContactDetailActivity extends AppCompatActivity {

    private String[] enterpriseColumns;
    private String[] classificationsColumn;

    private int mId;
    private String mName;
    private String mWebsite;
    private String mEmail;
    private String mPhone;
    private String mProducts;
    private boolean mConsultory;
    private boolean mDevelopment;
    private boolean mFactory;

    private TextView tName;
    private TextView tWebsite;
    private TextView tEmail;
    private TextView tPhone;
    private TextView tProducts;
    private TextView tClasifications;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        enterpriseColumns = new String[]{
            EnterpriseEntry._ID,
            EnterpriseEntry.COLUMN_ENTERPRISE_NAME,
            EnterpriseEntry.COLUMN_WEB_SITE,
            EnterpriseEntry.COLUMN_CONTACT_PHONE,
            EnterpriseEntry.COLUMN_CONTACT_EMAIL,
            EnterpriseEntry.COLUMN_PRODUCTS_SERVICES
        };

        classificationsColumn = new String[] { ClassificationsEnterprise.COLUMN_CLASSIFICATION_ID };

        tName = findViewById(R.id.cd_name_value);
        tWebsite = findViewById(R.id.cd_website_value);
        tPhone = findViewById(R.id.cd_phone_value);
        tEmail = findViewById(R.id.cd_email_value);
        tProducts = findViewById(R.id.cd_products);
        tClasifications = findViewById(R.id.cd_classifications);
    }

    private void setData() {
        Intent intentReceived = getIntent();
        String[] id = new String[] {Integer.toString(intentReceived.getIntExtra("CD_contact_id", -1))};
        DirectoryDbHelper mDatabase = new DirectoryDbHelper(getApplicationContext());
        SQLiteDatabase db = mDatabase.getReadableDatabase();
        Cursor c = db.query(EnterpriseEntry.TABLE_NAME, enterpriseColumns, EnterpriseEntry._ID + " = ?", id, null, null, null);

        c.moveToFirst();

        mId = c.getInt(0);
        mName = c.getString(1);
        mWebsite = c.getString(2);
        mPhone = c.getString(3);
        mEmail = c.getString(4);
        mProducts = c.getString(5);
        mConsultory = false;
        mDevelopment = false;
        mFactory = false;

        c = db.query(ClassificationsEnterprise.TABLE_NAME, classificationsColumn, ClassificationsEnterprise.COLUMN_ENTERPRISE_ID + " = ?", id, null, null, null);

        c.moveToFirst();

        for (int i = 0; i < c.getCount(); i++) {
            if (c.getInt(0) == ClassificationsEnterprise.CONSULTORY_ID) {
                mConsultory = true;
            }

            if (c.getInt(0) == ClassificationsEnterprise.DEVELOPMENT_ID) {
                mDevelopment = true;
            }

            if (c.getInt(0) == ClassificationsEnterprise.FACTORY_ID) {
                mFactory = true;
            }

            c.moveToNext();
        }

        tName.setText(mName);
        tWebsite.setText(mWebsite);
        tPhone.setText(mPhone);
        tEmail.setText(mEmail);
        tProducts.setText(mProducts);
        tClasifications.setText(
            (mConsultory ? "Consultoría\n" : "") +
            (mDevelopment ? "Desarrollo a la medida\n": "") +
            (mFactory ? "Fábrica de software\n" : "")
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        setData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.edit_contact:

                Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                intent.putExtra("EA_update", true);

                intent.putExtra("EA_update_id", mId);
                intent.putExtra("EA_update_name", mName);
                intent.putExtra("EA_update_website", mWebsite);
                intent.putExtra("EA_update_phone", mPhone);
                intent.putExtra("EA_update_email", mEmail);
                intent.putExtra("EA_update_products", mProducts);

                intent.putExtra("EA_update_consultory", mConsultory);
                intent.putExtra("EA_update_development", mDevelopment);
                intent.putExtra("EA_update_factory", mFactory);

                startActivity(intent);
                return true;

            case R.id.delete_contact:
                showDialog(0);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_warning_text)
        .setCancelable(false)
        .setPositiveButton(R.string.yes_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                DirectoryDbHelper mDatabase = new DirectoryDbHelper(getApplicationContext());
                SQLiteDatabase db = mDatabase.getReadableDatabase();
                String[] aux = new String[] {Integer.toString(mId)};
                db.delete(ClassificationsEnterprise.TABLE_NAME, ClassificationsEnterprise.COLUMN_ENTERPRISE_ID + " = ?", aux);
                db.delete(EnterpriseEntry.TABLE_NAME, EnterpriseEntry._ID + " = ?", aux);
                Toast.makeText(getApplicationContext(), "Contacto eliminado", Toast.LENGTH_SHORT).show();
                finish();
            }
        })
        .setNegativeButton(R.string.no_text, null);
        return builder.create();
    }
}
