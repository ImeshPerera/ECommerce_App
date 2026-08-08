package com.imeshperera.ecomapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.imeshperera.ecomapp.R;

public class WishlistFragment extends Fragment {

    public WishlistFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Full wishlist feature implemented in Phase 2
        View root = inflater.inflate(R.layout.fragment_wishlist, container, false);
        return root;
    }
}
