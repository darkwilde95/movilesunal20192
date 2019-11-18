package com.example.directory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.DialogFragment;

import com.example.directory.data.DirectoryContract.*;
import com.example.directory.data.DirectoryDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements FilterDialogFragment.FiltersDialogListener {

    private ListView enterprises;
    private FloatingActionButton fab;
    private ContactAdapter adapter;
    private String[] columns;
    private SearchView search;
    private ImageView filterOptions;
    private SQLiteDatabase mDatabase;
    private boolean consultoryFilter;
    private boolean developmentFilter;
    private boolean factoryFilter;
    private boolean searching;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        columns = new String[]{
            EnterpriseEntry._ID,
            EnterpriseEntry.COLUMN_ENTERPRISE_NAME,
            EnterpriseEntry.COLUMN_CONTACT_EMAIL,
            EnterpriseEntry.COLUMN_CONTACT_PHONE
        };
        mDatabase = new DirectoryDbHelper(getApplicationContext()).getReadableDatabase();
        enterprises = findViewById(R.id.enterprises_list);
        fab = findViewById(R.id.add_enterprise);
        adapter = new ContactAdapter(getApplicationContext());
        enterprises.setAdapter(adapter);
        consultoryFilter = false;
        developmentFilter = false;
        factoryFilter = false;
        enterprises.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EnterpriseContact current = adapter.getItem(position);
                Intent intent = new Intent(getApplicationContext(), ContactDetailActivity.class);
                intent.putExtra("CD_contact_id", current.getId());
                startActivity(intent);

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                intent.putExtra("EA_update", false);
                startActivity(intent);
            }
        });

    }

    private void updateList() {
        adapter.clear();
        Cursor c = mDatabase.query(
            EnterpriseEntry.TABLE_NAME,
            columns, null, null, null, null, null);

        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            adapter.add(new EnterpriseContact(
                c.getInt(0),
                c.getString(1),
                c.getString(2),
                c.getString(3))
            );
            c.moveToNext();
        }
    }

    private void updateSearch(String newText) {
        adapter.clear();
        Cursor c = mDatabase.rawQuery(query(), new String[] { "%" + newText + "%"});
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            adapter.add(new EnterpriseContact(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3)));
            c.moveToNext();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (searching) {
            adapter.clear();
            updateSearch(search.getQuery().toString());
        } else {
            updateList();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.search);
        if (search == null) {
            search = item.getActionView().findViewById(R.id.search_bar);
            search.setFocusableInTouchMode(true);
            filterOptions = item.getActionView().findViewById(R.id.filter_options);
            filterOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment fragment = new FilterDialogFragment(consultoryFilter, developmentFilter, factoryFilter);
                    fragment.show(getSupportFragmentManager(), "filters");
                }
            });
        }

        MenuItemCompat.OnActionExpandListener expandListener = new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                adapter.clear();
                search.onActionViewExpanded();
                searching = true;
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                search.onActionViewCollapsed();
                searching = false;
                updateList();
                return true;
            }
        };

        MenuItemCompat.setOnActionExpandListener(item, expandListener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.search) {
            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (!newText.equalsIgnoreCase("")) {
                        updateSearch(newText);
                    }
                    return false;
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    private String query() {
        String filters = "";
        if (consultoryFilter || developmentFilter || factoryFilter) {
            filters = " AND (";
            if (consultoryFilter) {
                filters += ClassificationsEnterprise.COLUMN_CLASSIFICATION_ID + " = " +
                        ClassificationsEnterprise.CONSULTORY_ID;
            }
            if (factoryFilter) {
                if (consultoryFilter) {
                    filters += " OR " + ClassificationsEnterprise.COLUMN_CLASSIFICATION_ID +
                            " = " + ClassificationsEnterprise.FACTORY_ID;
                } else {
                    filters += ClassificationsEnterprise.COLUMN_CLASSIFICATION_ID +
                            " = " + ClassificationsEnterprise.FACTORY_ID;
                }
            }
            if (developmentFilter) {
                if (consultoryFilter || factoryFilter) {
                    filters += " OR " + ClassificationsEnterprise.COLUMN_CLASSIFICATION_ID +
                            " = " + ClassificationsEnterprise.DEVELOPMENT_ID;
                } else {
                    filters += ClassificationsEnterprise.COLUMN_CLASSIFICATION_ID +
                            " = " + ClassificationsEnterprise.DEVELOPMENT_ID;
                }
            }
            filters += ")";
        }

        return "SELECT DISTINCT " + EnterpriseEntry._ID + "," + EnterpriseEntry.COLUMN_ENTERPRISE_NAME + "," +
                EnterpriseEntry.COLUMN_CONTACT_EMAIL + "," + EnterpriseEntry.COLUMN_CONTACT_PHONE +
                " FROM " + EnterpriseEntry.TABLE_NAME + " JOIN " + ClassificationsEnterprise.TABLE_NAME +
                " ON " + EnterpriseEntry._ID + " = " + ClassificationsEnterprise.COLUMN_ENTERPRISE_ID +
                " WHERE " + EnterpriseEntry.COLUMN_ENTERPRISE_NAME + " LIKE ?" + filters;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, boolean consult, boolean develop, boolean factor) {
        consultoryFilter = consult;
        developmentFilter = develop;
        factoryFilter = factor;
        updateSearch(search.getQuery().toString());
    }

    @Override
    public void onDialogNegaticeClick(DialogFragment dialog) {
        dialog.dismiss();
    }
}
