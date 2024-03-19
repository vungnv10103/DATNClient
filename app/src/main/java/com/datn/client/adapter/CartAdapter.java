package com.datn.client.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daimajia.swipe.SwipeLayout;
import com.datn.client.R;
import com.datn.client.action.IAction;
import com.datn.client.models.ProductCart;
import com.datn.client.ui.cart.ICartView;
import com.datn.client.ui.product.DetailProductActivity;
import com.datn.client.ui.product.ProductPresenter.STATUS_CART;
import com.datn.client.utils.Constants;
import com.datn.client.utils.Currency;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Objects;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private final List<ProductCart> productCarts;
    private final Context context;
    private final IAction iActionCart;
    private final ICartView iCartView;

    public CartAdapter(Context context, List<ProductCart> dataProductCart, IAction iActionCart, ICartView iCartView) {
        this.productCarts = dataProductCart;
        this.context = context;
        this.iActionCart = iActionCart;
        this.iCartView = iCartView;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductCart productCart = productCarts.get(position);
        holder.tvName.setText(productCart.getName());
        Glide.with(context).load(productCart.getImage()).into(holder.imgProduct);
        int quantityCart = Integer.parseInt(productCart.getQuantity_cart());
        int quantityProduct = Integer.parseInt(productCart.getQuantity_product());
        int priceOne = Integer.parseInt(productCart.getPrice());
        holder.tvPrice.setText(Currency.formatCurrency(String.valueOf(priceOne * quantityCart)));
        holder.tvQuantity.setText(String.valueOf(quantityCart));
        holder.tvOptions.setText(productCart.getCreated_at());
        holder.cbSelected.setChecked(productCart.getStatus_cart() == STATUS_CART.SELECTED.getValue());
        boolean isNightMode = Constants.isNightMode;
        int colorId;
        if (isNightMode) {
            colorId = ContextCompat.getColor(context, R.color.black_200);
        } else {
            colorId = ContextCompat.getColor(context, R.color.tutu);
        }
        holder.layoutCart.setBackgroundColor(colorId);

        if (quantityCart <= 1) {
            holder.btnMinus.setIconTintResource(R.color.gray_400);
            holder.btnPlus.setIconTintResource(R.color.big_stone);
        } else if (quantityCart >= Math.min(quantityProduct, 20)) {
            holder.btnMinus.setIconTintResource(R.color.big_stone);
            holder.btnPlus.setIconTintResource(R.color.gray_400);
        }
        holder.btnPlus.setOnClickListener(v -> iCartView.onUpdateQuantity(productCart.get_id(), position, "plus", 1));
        holder.btnMinus.setOnClickListener(v -> iCartView.onUpdateQuantity(productCart.get_id(), position, "minus", 1));

        holder.setAnimationText();
        holder.setSwipeLayout();
        holder.setLayoutParamsDrag(context);

        holder.cbSelected.setOnClickListener(v -> {
            boolean isChecked = holder.cbSelected.isChecked();
            iCartView.onUpdateStatus(productCart.get_id(), position, isChecked ? STATUS_CART.SELECTED.getValue() : STATUS_CART.DEFAULT.getValue());
        });
        holder.layoutCart.setOnClickListener(v -> iActionCart.onClick(productCart));


        // Drag layout

        holder.layoutGoShop.setOnClickListener(v -> doGoShop(context, productCart.getProduct_id()));
        holder.btnGoShop.setOnClickListener(v -> doGoShop(context, productCart.getProduct_id()));
        holder.layoutDelete.setOnClickListener(v -> iCartView.onUpdateStatus(productCart.get_id(), position, STATUS_CART.DELETED.getValue()));
        holder.btnDelete.setOnClickListener(v -> iCartView.onUpdateStatus(productCart.get_id(), position, STATUS_CART.DELETED.getValue()));
        holder.layoutBuyNow.setOnClickListener(v -> iCartView.onBuyNow(productCart.get_id()));
        holder.btnBuyNow.setOnClickListener(v -> iCartView.onBuyNow(productCart.get_id()));

    }


    @Override
    public int getItemCount() {
        return productCarts == null ? 0 : productCarts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final SwipeLayout swipeLayout;
        private final LinearLayout dragLayout, layoutDelete, layoutGoShop, layoutBuyNow;
        private final RelativeLayout layoutCart;
        private final ImageView imgProduct;
        private final TextView tvName, tvQuantity, tvPrice, tvOptions, tv_go_shop, tv_delete, tv_buy_now;
        private final MaterialButton btnMinus, btnPlus, btnDelete, btnGoShop, btnBuyNow;
        private final CheckBox cbSelected;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            swipeLayout = itemView.findViewById(R.id.swipe_cart);
            dragLayout = itemView.findViewById(R.id.bottom_wrapper);
            layoutCart = itemView.findViewById(R.id.layout_cart);
            layoutDelete = itemView.findViewById(R.id.layout_delete);
            layoutGoShop = itemView.findViewById(R.id.layout_go_shop);
            layoutBuyNow = itemView.findViewById(R.id.layout_buy_now);
            imgProduct = itemView.findViewById(R.id.img_product);
            tvName = itemView.findViewById(R.id.tv_name);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvOptions = itemView.findViewById(R.id.tv_options);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnGoShop = itemView.findViewById(R.id.btn_go_shop);
            btnBuyNow = itemView.findViewById(R.id.btn_buy_now);
            tv_go_shop = itemView.findViewById(R.id.tv_go_shop);
            tv_delete = itemView.findViewById(R.id.tv_delete);
            tv_buy_now = itemView.findViewById(R.id.tv_buy_now);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            cbSelected = itemView.findViewById(R.id.cb_selected);
        }

        private void setAnimationText() {
            tv_delete.setSelected(true);
            tv_delete.setSingleLine(true);
            tv_go_shop.setSelected(true);
            tv_go_shop.setSingleLine(true);
            tv_buy_now.setSelected(true);
            tv_buy_now.setSingleLine(true);
        }

        private void setLayoutParamsDrag(Context context) {
            // setLayoutParams dragLayout
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            int initHeight = getViewHeight(context, R.layout.item_cart);
            dragLayout.setLayoutParams(new FrameLayout.LayoutParams(width / 3 * 2, initHeight));
        }

        private void setSwipeLayout() {
            //set show mode.
            swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            //add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)
            swipeLayout.addDrag(SwipeLayout.DragEdge.Left, dragLayout);
            swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onClose(SwipeLayout layout) {
                    //when the SurfaceView totally cover the BottomView.
                    System.out.println("onClose");
                }

                @Override
                public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                    //you are swiping.
                    System.out.println("onUpdate");

                }

                @Override
                public void onStartOpen(SwipeLayout layout) {
                    System.out.println("onStartOpen");
                }

                @Override
                public void onOpen(SwipeLayout layout) {
                    //when the BottomView totally show.
                    System.out.println("onOpen");
                }

                @Override
                public void onStartClose(SwipeLayout layout) {
                    System.out.println("onStartClose");
                }

                @Override
                public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                    //when user's hand released.
                    System.out.println("onHandRelease");
                }
            });
        }
    }

    private static int getViewHeight(Context context, int viewResourceId) {
        View view = View.inflate(context, viewResourceId, null);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return view.getMeasuredHeight();
    }


    private static void doGoShop(Context context, String productID) {
        Intent intent = new Intent(context, DetailProductActivity.class);
        intent.putExtra("productID", productID);
        context.startActivity(intent);
    }

    private static void doBuyNow(Context context, String cartID) {

    }

    public void updateList(List<ProductCart> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new CartDiffCallback(newList, productCarts));
        int oldSize = productCarts.size();
        productCarts.clear();
        productCarts.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
        int newSize = newList.size();
        System.out.println("oldSize: " + oldSize + " - newSize:" + newSize);
    }

    private static class CartDiffCallback extends DiffUtil.Callback {
        private final List<ProductCart> oldCartList;
        private final List<ProductCart> newCartList;

        public CartDiffCallback(List<ProductCart> newCartList, List<ProductCart> oldCartList) {
            this.newCartList = newCartList;
            this.oldCartList = oldCartList;
        }

        @Override
        public int getOldListSize() {
            return oldCartList.size();
        }

        @Override
        public int getNewListSize() {
            return newCartList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return Objects.equals(oldCartList.get(oldItemPosition).get_id(), newCartList.get(newItemPosition).get_id());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            ProductCart oldCart = oldCartList.get(oldItemPosition);
            ProductCart newCart = newCartList.get(newItemPosition);
            return oldCart.getName().equals(newCart.getName())
                    && Objects.equals(oldCart.getStatus_cart(), newCart.getStatus_cart())
                    && Objects.equals(oldCart.getImage(), newCart.getImage())
                    && Objects.equals(oldCart.getPrice(), newCart.getPrice())
                    && Objects.equals(oldCart.getQuantity_cart(), newCart.getQuantity_cart());
        }
    }
}
