package com.example.uts_a22202302996.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.adapter.ViewPagerAdapter;
import com.example.uts_a22202302996.databinding.FragmentHomeBinding;
import com.example.uts_a22202302996.product.AllProductsFragment;
import com.example.uts_a22202302996.product.HeadsetFragment;
import com.example.uts_a22202302996.product.KeyboardFragment;
import com.example.uts_a22202302996.product.MouseFragment;
import com.google.android.material.tabs.TabLayout;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ViewPagerAdapter viewPagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupViewPager();

        HomeViewModel homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        Toolbar toolbar = binding.header.findViewById(R.id.toolbar);
        SearchView searchView = toolbar.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                homeViewModel.setSearchQuery(query); // Update search query in ViewModel
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                homeViewModel.setSearchQuery(newText); // Enable live search
                return true;
            }
        });

        return root;
    }

    private void setupViewPager() {
        TabLayout tabLayout = binding.tabLayout;
        ViewPager viewPager = binding.viewPager;

        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(new AllProductsFragment(), "All");
        viewPagerAdapter.addFragment(new KeyboardFragment(), "Keyboard");
        viewPagerAdapter.addFragment(new MouseFragment(), "Mouse");
        viewPagerAdapter.addFragment(new HeadsetFragment(), "Headset");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}