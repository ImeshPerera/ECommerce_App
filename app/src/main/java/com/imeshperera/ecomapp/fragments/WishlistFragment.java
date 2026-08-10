package com.imeshperera.ecomapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imeshperera.ecomapp.R;
import com.imeshperera.ecomapp.adapters.WishlistAdapter;
import com.imeshperera.ecomapp.models.NewProductModel;

import java.util.ArrayList;
import java.util.List;

public class WishlistFragment extends Fragment implements WishlistAdapter.OnWishlistActionListener {

    RecyclerView recyclerView;
    WishlistAdapter wishlistAdapter;
    List<NewProductModel> wishlistList;
    LinearLayout emptyStateLayout;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    public WishlistFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_wishlist, container, false);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        recyclerView = root.findViewById(R.id.wishlist_recycler);
        emptyStateLayout = root.findViewById(R.id.wishlist_empty_state);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        wishlistList = new ArrayList<>();
        wishlistAdapter = new WishlistAdapter(getContext(), wishlistList, this);
        recyclerView.setAdapter(wishlistAdapter);

        loadWishlist();

        return root;
    }

    private void loadWishlist() {
        if (auth.getCurrentUser() == null) return;
        String uid = auth.getCurrentUser().getUid();

        firestore.collection("wishlist")
                .document(uid)
                .collection("items")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        wishlistList.clear();
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            NewProductModel model = doc.toObject(NewProductModel.class);
                            if (model != null) {
                                wishlistList.add(model);
                            }
                        }
                        wishlistAdapter.notifyDataSetChanged();
                        updateEmptyState();
                    } else {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Failed to load wishlist", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateEmptyState() {
        if (wishlistList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRemoveFromWishlist(NewProductModel product, int position) {
        if (auth.getCurrentUser() == null) return;
        String uid = auth.getCurrentUser().getUid();
        String docId = product.getName() != null ? product.getName().replaceAll("\\s+", "_") : String.valueOf(position);

        firestore.collection("wishlist")
                .document(uid)
                .collection("items")
                .document(docId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    int pos = wishlistList.indexOf(product);
                    if (pos >= 0) {
                        wishlistList.remove(pos);
                        wishlistAdapter.notifyItemRemoved(pos);
                        wishlistAdapter.notifyItemRangeChanged(pos, wishlistList.size());
                    }
                    updateEmptyState();
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Removed from wishlist", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Failed to remove from wishlist", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWishlist(); // Refresh when returning to tab
    }
}
