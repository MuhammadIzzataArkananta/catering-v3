package com.arka.cateringukom.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.arka.cateringukom.R;
import com.arka.cateringukom.api.response.OrderResponse;
import com.arka.cateringukom.utils.FunctionHelper;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private Context context;
    private List<OrderResponse> orderList;

    public HistoryAdapter(Context context, List<OrderResponse> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    // Untuk mengupdate data adapter
    public void setDataAdapter(List<OrderResponse> items) {
        orderList.clear();
        orderList.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_riwayat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryAdapter.ViewHolder holder, int position) {
        OrderResponse order = orderList.get(position);
        // Misal, tampilkan "Order #12 (pending)" sebagai nama
        holder.tvNama.setText("Order #" + order.getId() + " (" + order.getStatus() + ")");
        // Tampilkan created_at; Anda bisa memformat tanggal jika perlu
        holder.tvDate.setText(order.getCreatedAt());
        // Tampilkan jumlah order_items, jika ada
        int itemCount = (order.getOrderItems() != null) ? order.getOrderItems().size() : 0;
        holder.tvJml.setText(itemCount + " item(s)");
        // Tampilkan total harga menggunakan helper untuk format rupiah
        holder.tvPrice.setText(FunctionHelper.rupiahFormat(order.getTotalPrice()));

        holder.tvStatus.setText("Makanan " + order.getStatus());

        // Tombol detail untuk membuka activity detail order
//        holder.btnDetail.setOnClickListener(v -> {
////            Intent intent = new Intent(context, MainActivity.class);
////            intent.putExtra("order_id", order.getId());
////            context.startActivity(intent);
//        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvNama, tvDate, tvJml, tvPrice, tvStatus;
        public MaterialButton btnDetail;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNama   = itemView.findViewById(R.id.tvNamaMakananRiwayat);
            tvDate   = itemView.findViewById(R.id.tvTanggalPesananRiwayat);
            tvJml    = itemView.findViewById(R.id.tvJumlahMakananRiwayat);
            tvPrice  = itemView.findViewById(R.id.tvTotalHargaRiwayat);
            tvStatus  = itemView.findViewById(R.id.tvStatusPesananRiwayat);
//            btnDetail = itemView.findViewById(R.id.btnDetailRiwayat);
        }
    }
}