package com.example.testapp.binding_adapters

import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

@BindingAdapter("setAdapter")
fun RecyclerView.setAdapter(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>?) {
    adapter?.let {
        this.adapter = it
    }
}