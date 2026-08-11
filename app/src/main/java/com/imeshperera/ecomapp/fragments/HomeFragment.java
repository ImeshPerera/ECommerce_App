package com.imeshperera.ecomapp.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment implements FilterBottomSheetFragment.FilterListener {

    LinearLayout linearLayout;
    ProgressDialog progressDialog;
    RecyclerView catRecyclerview, newProductRecyclerview, allProductRecyclerview;
    ImageButton filterBtn;
    //Categories
    CategoryAdapter categoryAdapter;
    NewProductAdapter newProductAdapter;
    AllProductAdapter allProductAdapter;
    List<CategoryModel> categoryModelList;
    List<NewProductModel> newProductModelList, allProductModelList;
    List<NewProductModel> originalAllProductList;

    // Filter state
    String currentSortOption = "none";
    Set<String> currentSelectedBrands = new HashSet<>();
    String currentSearchText = "";

    //Firestorm
    FirebaseFirestore db;

    public HomeFragment() {}

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        progressDialog = new ProgressDialog(getActivity());

        catRecyclerview = root.findViewById(R.id.rec_category);
        newProductRecyclerview = root.findViewById(R.id.new_product_rec);
        allProductRecyclerview = root.findViewById(R.id.all_rec);
        filterBtn = root.findViewById(R.id.filter_btn);

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
                currentSearchText = s.toString();
                applyFilterAndSort(currentSortOption, currentSelectedBrands);
            }
        });

        // Filter button
        filterBtn.setOnClickListener(v -> {
            // Collect unique brands
            List<String> brands = new ArrayList<>();
            Set<String> brandSet = new HashSet<>();
            for (NewProductModel model : originalAllProductList) {
                if (model.getBrand() != null && !brandSet.contains(model.getBrand())) {
                    brandSet.add(model.getBrand());
                    brands.add(model.getBrand());
                }
            }

            FilterBottomSheetFragment filterSheet = FilterBottomSheetFragment.newInstance(
                    brands, currentSortOption, currentSelectedBrands);
            filterSheet.setFilterListener(HomeFragment.this);
            filterSheet.show(getChildFragmentManager(), "FilterSheet");
        });

        progressDialog.setTitle("Welcome to Phone House");
        progressDialog.setMessage("Please wait....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        com.facebook.shimmer.ShimmerFrameLayout shimmerNewProducts = root.findViewById(R.id.shimmer_new_products);
        com.facebook.shimmer.ShimmerFrameLayout shimmerPopularProducts = root.findViewById(R.id.shimmer_popular_products);

        if (shimmerNewProducts != null) shimmerNewProducts.startShimmer();
        if (shimmerPopularProducts != null) shimmerPopularProducts.startShimmer();

        db = FirebaseFirestore.getInstance();
        linearLayout = root.findViewById(R.id.home_layout);

        catRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        newProductRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        allProductRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        categoryModelList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(), categoryModelList);
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
                        }
                    }
                });

        allProductModelList = new ArrayList<>();
        allProductAdapter = new AllProductAdapter(getContext(), allProductModelList);
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
                            }
                            allProductAdapter.notifyDataSetChanged();

                            if (shimmerPopularProducts != null) {
                                shimmerPopularProducts.stopShimmer();
                                shimmerPopularProducts.setVisibility(View.GONE);
                            }
                            allProductRecyclerview.setVisibility(View.VISIBLE);
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }

                            // Load wishlist state for current user
                            loadWishlistState();
                        }
                    }
                });

        newProductModelList = new ArrayList<>();
        newProductAdapter = new NewProductAdapter(getContext(), newProductModelList);
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
                            }
                            newProductAdapter.notifyDataSetChanged();

                            if (shimmerNewProducts != null) {
                                shimmerNewProducts.stopShimmer();
                                shimmerNewProducts.setVisibility(View.GONE);
                            }
                            newProductRecyclerview.setVisibility(View.VISIBLE);
                        }
                    }
                });

        return root;
    }

    private void loadWishlistState() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) return;
        String uid = auth.getCurrentUser().getUid();

        db.collection("wishlist").document(uid).collection("items")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Set<String> wishlisted = new HashSet<>();
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            String name = doc.getString("name");
                            if (name != null) wishlisted.add(name);
                        }
                        if (allProductAdapter != null) allProductAdapter.setWishlistedNames(wishlisted);
                        if (newProductAdapter != null) newProductAdapter.setWishlistedNames(wishlisted);
                    }
                });
    }

    @Override
    public void onFilterApplied(String sortOption, Set<String> selectedBrands) {
        currentSortOption = sortOption;
        currentSelectedBrands = selectedBrands;
        applyFilterAndSort(sortOption, selectedBrands);
    }

    private void applyFilterAndSort(String sortOption, Set<String> selectedBrands) {
        List<NewProductModel> filtered = new ArrayList<>();

        for (NewProductModel item : originalAllProductList) {
            // Text search filter
            boolean matchesSearch = currentSearchText.isEmpty() ||
                    (item.getName() != null && item.getName().toLowerCase().contains(currentSearchText.toLowerCase())) ||
                    (item.getBrand() != null && item.getBrand().toLowerCase().contains(currentSearchText.toLowerCase()));

            // Brand filter
            boolean matchesBrand = selectedBrands.isEmpty() ||
                    (item.getBrand() != null && selectedBrands.contains(item.getBrand()));

            if (matchesSearch && matchesBrand) {
                filtered.add(item);
            }
        }

        // Sort
        switch (sortOption) {
            case "price_low":
                Collections.sort(filtered, (a, b) -> {
                    double pa = parsePrice(a.getPrice());
                    double pb = parsePrice(b.getPrice());
                    return Double.compare(pa, pb);
                });
                break;
            case "price_high":
                Collections.sort(filtered, (a, b) -> {
                    double pa = parsePrice(a.getPrice());
                    double pb = parsePrice(b.getPrice());
                    return Double.compare(pb, pa);
                });
                break;
            case "rating":
                Collections.sort(filtered, (a, b) -> {
                    double ra = parseRating(a.getRate());
                    double rb = parseRating(b.getRate());
                    return Double.compare(rb, ra);
                });
                break;
            case "az":
                Collections.sort(filtered, (a, b) -> {
                    String na = a.getName() != null ? a.getName() : "";
                    String nb = b.getName() != null ? b.getName() : "";
                    return na.compareToIgnoreCase(nb);
                });
                break;
            default:
                // no sort
                break;
        }

        if (allProductAdapter != null) {
            allProductAdapter.setFilteredList(filtered);
        }
    }

    private double parsePrice(String price) {
        if (price == null) return 0.0;
        try {
            return Double.parseDouble(price.replaceAll("[^\\d.]", ""));
        } catch (Exception e) {
            return 0.0;
        }
    }

    private double parseRating(String rate) {
        if (rate == null) return 0.0;
        try {
            return Double.parseDouble(rate.replaceAll("[^\\d.]", ""));
        } catch (Exception e) {
            return 0.0;
        }
    }
}
