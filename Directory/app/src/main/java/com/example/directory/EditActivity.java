package com.example.directory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.directory.data.DirectoryContract.*;
import com.example.directory.data.DirectoryDbHelper;
import com.google.android.material.textfield.TextInputEditText;


public class EditActivity extends AppCompatActivity {

    private TextInputEditText name;
    private TextInputEditText website;
    private TextInputEditText phone;
    private TextInputEditText email;
    private TextInputEditText products;
    private int mId;

    private CheckBox consultoryCheckbox;
    private CheckBox developmentCheckbox;
    private CheckBox factoryCheckbox;
    private LinearLayout consultory;
    private LinearLayout development;
    private LinearLayout factory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        name = findViewById(R.id.et_name);
        website = findViewById(R.id.et_website);
        phone = findViewById(R.id.et_phone);
        email = findViewById(R.id.et_email);
        products = findViewById(R.id.et_products);

        consultory = findViewById(R.id.consultory_container);
        development = findViewById(R.id.development_container);
        factory = findViewById(R.id.factory_container);
        consultoryCheckbox = findViewById(R.id.consultory_checkbox);
        developmentCheckbox = findViewById(R.id.development_checkbox);
        factoryCheckbox = findViewById(R.id.factory_checkbox);

        setData();
        consultory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consultoryCheckbox.setChecked(!consultoryCheckbox.isChecked());
            }
        });

        development.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                developmentCheckbox.setChecked(!developmentCheckbox.isChecked());
            }
        });

        factory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                factoryCheckbox.setChecked(!factoryCheckbox.isChecked());
            }
        });
    }

    private void setData() {
        Intent intent = getIntent();
        if (intent.getBooleanExtra("EA_update", false)) {
            name.setText(intent.getStringExtra("EA_update_name"));
            website.setText(intent.getStringExtra("EA_update_website"));
            email.setText(intent.getStringExtra("EA_update_email"));
            phone.setText(intent.getStringExtra("EA_update_phone"));
            products.setText(intent.getStringExtra("EA_update_products"));
            mId = intent.getIntExtra("EA_update_id", -1);
            consultoryCheckbox.setChecked(intent.getBooleanExtra("EA_update_consultory", false));
            developmentCheckbox.setChecked(intent.getBooleanExtra("EA_update_development", false));
            factoryCheckbox.setChecked(intent.getBooleanExtra("EA_update_factory", false));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.confirm_add:
                DirectoryDbHelper mDatabase = new DirectoryDbHelper(getApplicationContext());
                SQLiteDatabase db = mDatabase.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(EnterpriseEntry.COLUMN_ENTERPRISE_NAME, name.getText().toString());
                values.put(EnterpriseEntry.COLUMN_WEB_SITE, website.getText().toString());
                values.put(EnterpriseEntry.COLUMN_CONTACT_PHONE, phone.getText().toString());
                values.put(EnterpriseEntry.COLUMN_CONTACT_EMAIL, email.getText().toString());
                values.put(EnterpriseEntry.COLUMN_PRODUCTS_SERVICES, products.getText().toString().trim());

                long newId = -1;
                String[] id = new String[]{Integer.toString(mId)};
                Intent prev = getIntent();
                boolean update = prev.getBooleanExtra("EA_update", false);
                if (update) {
                    db.update(EnterpriseEntry.TABLE_NAME, values, EnterpriseEntry._ID + " = ?", id);
                    db.delete(ClassificationsEnterprise.TABLE_NAME, ClassificationsEnterprise.COLUMN_ENTERPRISE_ID + " = ?", id);
                } else {
                    newId = db.insert(EnterpriseEntry.TABLE_NAME, null, values);
                }

                ContentValues classifications;
                if (consultoryCheckbox.isChecked()) {
                    classifications = new ContentValues();
                    classifications.put(ClassificationsEnterprise.COLUMN_ENTERPRISE_ID, update ? mId : newId);
                    classifications.put(ClassificationsEnterprise.COLUMN_CLASSIFICATION_ID, ClassificationsEnterprise.CONSULTORY_ID);
                    db.insert(ClassificationsEnterprise.TABLE_NAME, null, classifications);
                }

                if (developmentCheckbox.isChecked()) {
                    classifications = new ContentValues();
                    classifications.put(ClassificationsEnterprise.COLUMN_ENTERPRISE_ID, update ? mId : newId);
                    classifications.put(ClassificationsEnterprise.COLUMN_CLASSIFICATION_ID, ClassificationsEnterprise.DEVELOPMENT_ID);
                    db.insert(ClassificationsEnterprise.TABLE_NAME, null, classifications);
                }

                if (factoryCheckbox.isChecked()) {
                    classifications = new ContentValues();
                    classifications.put(ClassificationsEnterprise.COLUMN_ENTERPRISE_ID, update ? mId : newId);
                    classifications.put(ClassificationsEnterprise.COLUMN_CLASSIFICATION_ID, ClassificationsEnterprise.FACTORY_ID);
                    db.insert(ClassificationsEnterprise.TABLE_NAME, null, classifications);
                }

                Toast.makeText(getApplicationContext(), "Contacto guardado", Toast.LENGTH_SHORT).show();
                finish();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
