package com.swipe.product.presentation.products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.swipe.product.R
import com.swipe.product.databinding.ItemProductBinding
import com.swipe.product.domain.model.Product
import com.swipe.product.utils.loadImage
import com.swipe.product.utils.toDouble
import com.swipe.product.utils.toString
import java.math.RoundingMode
import java.text.DecimalFormat

class ProductAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //default products
    private var products: ArrayList<Product> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holderPostBinding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context), R.layout.item_product, parent, false
        )
        return ProductVH(holderPostBinding)
    }

    override fun getItemCount(): Int = if (products.isEmpty()) 0 else products.size

    private fun getItem(position: Int): Product = products[position]

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ProductAdapter.ProductVH).onBind(getItem(position))
    }

    private inner class ProductVH(private val viewDataBinding: ViewDataBinding) :
        RecyclerView.ViewHolder(viewDataBinding.root) {

        fun onBind(product: Product) {
            (viewDataBinding as ItemProductBinding).apply {
                cvProduct.layoutParams =
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, product.height)
                this.product = product.apply {
                    //update image
                    ivImage.loadImage(product.image, R.drawable.placeholder)
                    //format price and tax
                    val formatter = DecimalFormat("###.##").apply {
                        roundingMode = RoundingMode.DOWN
                    }
                    price = formatter.format(price.toDouble)
                    tax = formatter.format(tax.toDouble)
                }
            }
        }
    }

    fun update(products: ArrayList<Product>?) {
        products?.apply {
            this@ProductAdapter.products = this
            notifyDataSetChanged()
        }
    }

    fun add(product: Product, position: Int = this.products.size - 1) {
        if (position <= 0) {
            this.products.add(product)
            notifyItemInserted(0)
        } else {
            this.products.add(position, product)
            notifyItemInserted(position)
        }
    }
}