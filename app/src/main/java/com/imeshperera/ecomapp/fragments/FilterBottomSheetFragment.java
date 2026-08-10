package com.imeshperera.ecomapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.imeshperera.ecomapp.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FilterBottomSheetFragment extends BottomSheetDialogFragment {

    public interface FilterListener {
        void onFilterApplied(String sortOption, Set<String> selectedBrands);
    }

    private FilterListener listener;
    private List<String> brandList;
    private String currentSortOption;
    private Set<String> currentSelectedBrands;

    public static FilterBottomSheetFragment newInstance(List<String> brands, String currentSort, Set<String> selectedBrands) {
        FilterBottomSheetFragment fragment = new FilterBottomSheetFragment();
        fragment.brandList = brands != null ? brands : new ArrayList<>();
        fragment.currentSortOption = currentSort != null ? currentSort : "none";
        fragment.currentSelectedBrands = selectedBrands != null ? new HashSet<>(selectedBrands) : new HashSet<>();
        return fragment;
    }

    public void setFilterListener(FilterListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.bottom_sheet_filter, container, false);

        RadioGroup sortGroup = root.findViewById(R.id.sort_radio_group);
        ChipGroup brandChipGroup = root.findViewById(R.id.brand_chip_group);
        Button applyBtn = root.findViewById(R.id.filter_apply_btn);
        Button resetBtn = root.findViewById(R.id.filter_reset_btn);

        // Set current sort selection
        switch (currentSortOption) {
            case "price_low": sortGroup.check(R.id.sort_price_low); break;
            case "price_high": sortGroup.check(R.id.sort_price_high); break;
            case "rating": sortGroup.check(R.id.sort_rating); break;
            case "az": sortGroup.check(R.id.sort_az); break;
            default: sortGroup.check(R.id.sort_none); break;
        }

        // Populate brand chips
        for (String brand : brandList) {
            Chip chip = new Chip(requireContext());
            chip.setText(brand);
            chip.setCheckable(true);
            chip.setChecked(currentSelectedBrands.contains(brand));
            brandChipGroup.addView(chip);
        }

        applyBtn.setOnClickListener(v -> {
            // Get sort
            String sortOption = "none";
            int checkedId = sortGroup.getCheckedRadioButtonId();
            if (checkedId == R.id.sort_price_low) sortOption = "price_low";
            else if (checkedId == R.id.sort_price_high) sortOption = "price_high";
            else if (checkedId == R.id.sort_rating) sortOption = "rating";
            else if (checkedId == R.id.sort_az) sortOption = "az";

            // Get selected brands
            Set<String> selectedBrands = new HashSet<>();
            for (int i = 0; i < brandChipGroup.getChildCount(); i++) {
                View child = brandChipGroup.getChildAt(i);
                if (child instanceof Chip) {
                    Chip c = (Chip) child;
                    if (c.isChecked()) {
                        selectedBrands.add(c.getText().toString());
                    }
                }
            }

            if (listener != null) {
                listener.onFilterApplied(sortOption, selectedBrands);
            }
            dismiss();
        });

        resetBtn.setOnClickListener(v -> {
            sortGroup.check(R.id.sort_none);
            for (int i = 0; i < brandChipGroup.getChildCount(); i++) {
                View child = brandChipGroup.getChildAt(i);
                if (child instanceof Chip) {
                    ((Chip) child).setChecked(false);
                }
            }
        });

        return root;
    }
}
