package com.example.uts_a22202302996.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
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
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.badge.ExperimentalBadgeUtils;
import com.google.android.material.tabs.TabLayout;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding; // Binding untuk mengakses komponen UI
    private ViewPagerAdapter viewPagerAdapter; // Adapter untuk mengelola fragment di ViewPager

    @OptIn(markerClass = ExperimentalBadgeUtils.class)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inisialisasi ViewModel
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Inflate layout menggunakan ViewBinding
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Mengatur ViewPager dan TabLayout
        setupViewPager();

        return root;
    }

    /**
     * Mengatur ViewPager dengan fragment dan TabLayout.
     */
    private void setupViewPager() {
        TabLayout tabLayout = binding.tabLayout;
        ViewPager viewPager = binding.viewPager;

        // Inisialisasi ViewPagerAdapter dan menambahkan fragment
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(new AllProductsFragment(), "All");
        viewPagerAdapter.addFragment(new KeyboardFragment(), "Keyboard");
        viewPagerAdapter.addFragment(new MouseFragment(), "Mouse");
        viewPagerAdapter.addFragment(new HeadsetFragment(), "Headset");

        // Mengatur adapter ke ViewPager dan menghubungkannya dengan TabLayout
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Hindari memory leak dengan menghapus binding
    }
}