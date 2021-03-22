package com.example.simplerentalapplication.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.simplerentalapplication.R;

import org.jetbrains.annotations.NotNull;

public class PropertyFragment extends Fragment {

    RecyclerView recyclerView;

    public PropertyFragment(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return recyclerView;
    }
}