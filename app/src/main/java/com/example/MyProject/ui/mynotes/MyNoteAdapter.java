package com.example.MyProject.ui.mynotes;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MyProject.R;
import com.example.MyProject.data.MyNote;
import com.example.MyProject.data.Product;
import com.example.MyProject.databinding.ListItemBinding;
import com.example.MyProject.ui.ProductAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyNoteAdapter extends RecyclerView.Adapter<MyNoteAdapter.ProductViewHolder> {
    private List<MyNote> productList = new ArrayList<>();

    public void setProductList(List<MyNote> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    public List<MyNote> getProductList() {
        return productList;
    }

    public interface OnItemProductClickListener {
        void onProductClick(int position);
    }

    private ProductAdapter.OnItemProductClickListener onItemProductClickListener;

    public void setOnItemProductClickListener(ProductAdapter.OnItemProductClickListener onItemProductClickListener) {
        this.onItemProductClickListener = onItemProductClickListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,
                parent, false);
        return new ProductViewHolder(ListItemBinding.bind(view));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {


        Log.e("abcd", "onChanged: set " + holder);
        MyNote product = productList.get(position);
        holder.binding.tvTitle.setText(product.getTitle());

        if (product.getProven() == 1){
            holder.binding.proven.setImageResource(R.drawable.check);
        }
        else if (product.getProven() == 0){
            holder.binding.proven.setImageResource(R.drawable.cross);
        }

        holder.binding.getRoot().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemProductClickListener.onProductClick(position);
                    }
                }
        );



    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {

        ListItemBinding binding;

        public ProductViewHolder(ListItemBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }
}
