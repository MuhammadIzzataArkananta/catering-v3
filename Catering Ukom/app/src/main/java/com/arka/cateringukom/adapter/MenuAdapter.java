package com.arka.cateringukom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arka.cateringukom.R;
import com.arka.cateringukom.api.model.Menu;

import java.util.List;


public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private Context context;
    private List<Menu> menuList;
    private OnMenuItemChangeListener listener;

    public MenuAdapter(Context context, List<Menu> menuList, OnMenuItemChangeListener listener) {
        this.context = context;
        this.menuList = menuList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout adapter (misalnya file: item_menu.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.list_card_menus, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {


        Menu menu = menuList.get(position);

        holder.tvName.setText(menu.getName());
        holder.tvPrice.setText("Rp " + menu.getPrice());
        holder.tvQuantity.setText(String.valueOf(menu.getQuantity()));


        // Listener tombol plus
        holder.imageAdd.setOnClickListener(v -> {
            int quantity = menu.getQuantity();
            quantity++;
            menu.setQuantity(quantity);
            holder.tvQuantity.setText(String.valueOf(quantity));

            // Panggil callback ke Activity
            if (listener != null) {
                listener.onQuantityChanged();
            }
        });

        // Listener tombol minus
        holder.imageMinus.setOnClickListener(v -> {
            int quantity = menu.getQuantity();
            if (quantity > 0) {
                quantity--;
                menu.setQuantity(quantity);
                holder.tvQuantity.setText(String.valueOf(quantity));

                // Panggil callback ke Activity
                if (listener != null) {
                    listener.onQuantityChanged();
                }
            }
        });

//        Glide.with(context).load(menu.getImageUrl()).into(holder.imageMenu);
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView imageMenu;
        TextView tvName, tvPrice, tvQuantity;
        ImageView imageMinus, imageAdd;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvPaket);
            imageMinus = itemView.findViewById(R.id.imageMinus);
            imageAdd = itemView.findViewById(R.id.imageAdd);
//            imageMenu = itemView.findViewById(R.id.imageMenu);
        }
    }

    public interface OnMenuItemChangeListener {
        void onQuantityChanged();
    }
}

