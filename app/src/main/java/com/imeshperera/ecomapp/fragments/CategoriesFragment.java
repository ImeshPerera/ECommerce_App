package com.imeshperera.ecomapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.imeshperera.ecomapp.R;
import com.imeshperera.ecomapp.adapters.CategoryAdapter;
import com.imeshperera.ecomapp.models.CategoryModel;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment {

    RecyclerView recyclerView;
    CategoryAdapter adapter;
    List<CategoryModel> categoryList;
    FirebaseFirestore db;

    public CategoriesFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_categories, container, false);

        db = FirebaseFirestore.getInstance();
        recyclerView = root.findViewById(R.id.categories_grid_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        categoryList = new ArrayList<>();
        adapter = new CategoryAdapter(getContext(), categoryList);
        recyclerView.setAdapter(adapter);

        loadCategories();
        return root;
    }

    private void loadCategories() {
        db.collection("Category")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            categoryList.clear();
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                CategoryModel model = doc.toObject(CategoryModel.class);
                                categoryList.add(model);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
