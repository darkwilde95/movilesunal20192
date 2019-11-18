package com.example.directory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class FilterDialogFragment extends DialogFragment {

    public interface FiltersDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, boolean consultory, boolean development, boolean factory);
        public void onDialogNegaticeClick(DialogFragment dialog);
    }

    private FiltersDialogListener listener;
    private LinearLayout consultory;
    private LinearLayout development;
    private LinearLayout factory;
    private boolean consult;
    private boolean develop;
    private boolean factor;

    public FilterDialogFragment(boolean consult, boolean develop, boolean factor) {
        this.consult = consult;
        this.develop = develop;
        this.factor = factor;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View aux = inflater.inflate(R.layout.search_filters, null);

        CheckBox b = aux.findViewById(R.id.filter_consultory);
        b.setChecked(consult);
        b = aux.findViewById(R.id.filter_development);
        b.setChecked(develop);
        b = aux.findViewById(R.id.filter_factory);
        b.setChecked(factor);

        builder.setView(aux)
        .setTitle(R.string.filters_text)
        .setPositiveButton(R.string.accept_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CheckBox b = aux.findViewById(R.id.filter_consultory);
                consult = b.isChecked();
                b = aux.findViewById(R.id.filter_development);
                develop = b.isChecked();
                b = aux.findViewById(R.id.filter_factory);
                factor = b.isChecked();
                listener.onDialogPositiveClick(FilterDialogFragment.this, consult, develop, factor);
            }
        })
        .setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onDialogNegaticeClick(FilterDialogFragment.this);
            }
        });

        consultory = aux.findViewById(R.id.filter_consultory_ll);
        development = aux.findViewById(R.id.filter_development_ll);
        factory = aux.findViewById(R.id.filter_factory_ll);

        consultory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox b = aux.findViewById(R.id.filter_consultory);
                b.setChecked(!b.isChecked());
            }
        });

        development.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox b = aux.findViewById(R.id.filter_development);
                b.setChecked(!b.isChecked());
            }
        });

        factory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox b = aux.findViewById(R.id.filter_factory);
                b.setChecked(!b.isChecked());
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (FiltersDialogListener) context;
        } catch (Exception e) {}
    }
}
