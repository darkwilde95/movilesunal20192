package com.example.directory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class ContactAdapter extends ArrayAdapter<EnterpriseContact> implements Filterable {

    private Context context;

    public ContactAdapter(@NonNull Context context) {
        super(context, 0);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if (listItem == null) {
            listItem = LayoutInflater.from(getContext()).inflate(R.layout.contact_list_tile, parent, false);
        }

        EnterpriseContact currentContact = getItem(position);
        TextView name = (TextView) listItem.findViewById(R.id.enterprise_name);
        TextView email = (TextView) listItem.findViewById(R.id.enterprise_email);
        TextView phone = (TextView) listItem.findViewById(R.id.enterprise_phone);

        name.setText(currentContact.getName());
        email.setText(currentContact.getContactEmail());
        phone.setText(currentContact.getContactPhone());

        return listItem;
    }

    @NonNull
    @Override
    public Filter getFilter() {

        return super.getFilter();
    }
}
