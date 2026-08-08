package com.imeshperera.ecomapp.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.imeshperera.ecomapp.R;
import com.imeshperera.ecomapp.adapters.AllProductAdapter;
import com.imeshperera.ecomapp.adapters.CategoryAdapter;
import com.imeshperera.ecomapp.adapters.NewProductAdapter;
import com.imeshperera.ecomapp.models.CategoryModel;
import com.imeshperera.ecomapp.models.NewProductModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    LinearLayout linearLayout;
    ProgressDialog progressDialog;
    RecyclerView catRecyclerview, newProductRecyclerview, allProductRecyclerview;
    //Categories
    CategoryAdapter categoryAdapter;
    NewProductAdapter newProductAdapter;
    AllProductAdapter allProductAdapter;
    List<CategoryModel> categoryModelList;
    List<NewProductModel> newProductModelList, allProductModelList;
    List<NewProductModel> originalAllProductList;

    //Firestorm
    FirebaseFirestore db;

    public HomeFragment() {

    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        progressDialog = new ProgressDialog(getActivity());

        catRecyclerview = root.findViewById(R.id.rec_category);
        newProductRecyclerview = root.findViewById(R.id.new_product_rec);
        allProductRecyclerview = root.findViewById(R.id.all_rec);

        android.widget.TextView catSeeAll = root.findViewById(R.id.category_see_all);
        android.widget.TextView newSeeAll = root.findViewById(R.id.newProducts_see_all);
        android.widget.TextView popSeeAll = root.findViewById(R.id.popular_see_all);

        View.OnClickListener seeAllClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.content.Intent intent = new android.content.Intent(getActivity(), com.imeshperera.ecomapp.activities.ShowAllActivity.class);
                startActivity(intent);
            }
        };

        catSeeAll.setOnClickListener(seeAllClickListener);
        newSeeAll.setOnClickListener(seeAllClickListener);
        popSeeAll.setOnClickListener(seeAllClickListener);

        originalAllProductList = new ArrayList<>();
        android.widget.EditText searchBar = root.findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                filter(s.toString());
            }
        });

        progressDialog.setTitle("Welcome to Phone House");
        progressDialog.setMessage("Please wait....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        db = FirebaseFirestore.getInstance();
        linearLayout = root.findViewById(R.id.home_layout);
        linearLayout.setVisibility(View.GONE);

        catRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
        newProductRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
        allProductRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(),2));

        categoryModelList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(),categoryModelList);
        catRecyclerview.setAdapter(categoryAdapter);
        db.collection("Category")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CategoryModel categoryModel = document.toObject(CategoryModel.class);
                                categoryModelList.add(categoryModel);
                                categoryAdapter.notifyDataSetChanged();
                            }
                        } else {

                        }
                    }
                });

        allProductModelList = new ArrayList<>();
        allProductAdapter = new AllProductAdapter(getContext(),allProductModelList);
        allProductRecyclerview.setAdapter(allProductAdapter);
        db.collection("New Products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                NewProductModel allproductModel = document.toObject(NewProductModel.class);
                                allProductModelList.add(allproductModel);
                                originalAllProductList.add(allproductModel);
                                allProductAdapter.notifyDataSetChanged();
                                linearLayout.setVisibility(View.VISIBLE);
                                progressDialog.dismiss();
                            }
                        } else {

                        }
                    }
                });

        newProductModelList = new ArrayList<>();
        newProductAdapter = new NewProductAdapter(getContext(),newProductModelList);
        newProductRecyclerview.setAdapter(newProductAdapter);
        db.collection("New Products")
                .whereEqualTo("cat", "new")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                NewProductModel productModel = document.toObject(NewProductModel.class);
                                newProductModelList.add(productModel);
                                newProductAdapter.notifyDataSetChanged();
                            }
                        } else {

                        }
                    }
                });


        return root;
    }

    private void filter(String text) {
        List<NewProductModel> filteredList = new ArrayList<>();
        for (NewProductModel item : originalAllProductList) {
            if ((item.getName() != null && item.getName().toLowerCase().contains(text.toLowerCase())) ||
                    (item.getBrand() != null && item.getBrand().toLowerCase().contains(text.toLowerCase()))) {
                filteredList.add(item);
            }
        }
        allProductAdapter.setFilteredList(filteredList);
    }
}
