package com.swipe.product.presentation.products.fragment

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.swipe.product.R
import com.swipe.product.databinding.FragmentListProductBinding
import com.swipe.product.domain.model.Product
import com.swipe.product.presentation.products.ProductAdapter
import com.swipe.product.presentation.products.ProductViewModel
import com.swipe.product.presentation.products.activity.ProductsActivity
import com.swipe.product.utils.afterTextChangedDelayed
import com.swipe.product.utils.isNetworkAvailable
import com.swipe.product.utils.toFloat
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * This class will be used to define and set product listing
 * @author : prustyA : 16/07/2023
 */
class ProductListFragment : Fragment() {

    //init binding
    private lateinit var fragmentListProductBinding: FragmentListProductBinding

    //init adapter
    private var productAdapter: ProductAdapter = ProductAdapter()

    //find parent activity
    private val parent: ProductsActivity by lazy { activity as ProductsActivity }

    //get view model from activity
    private val productViewModel: ProductViewModel by lazy { parent.productViewModel }

    //on fragment creation
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentListProductBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_list_product,container,false)
        return fragmentListProductBinding.root
    }

    //after fragment is set to execute
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        setListener()
    }

    /**
     * This method will be used to initialize objects
     * @author : prustyA : 16/07/2023
     */
    private fun init() {
        //init recyclerview
        fragmentListProductBinding.rvProducts.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = productAdapter
        }
        //get products
        fetchProducts()
    }

    /**
     * This method will be used to fetch latest products
     * @author : prustyA : 16/07/2023
     */
    private fun fetchProducts() {
        //reset search text
        fragmentListProductBinding.svSearch.setText("")
        //check for network
        if (parent.isNetworkAvailable()) {
            //fetch all products
            productViewModel.getProducts()
        } else {
            //send internet error message
            productViewModel.messageData.value = parent.getString(R.string.no_internet_connection)
            //hide refresh
            fragmentListProductBinding.srlRefresh.isRefreshing = false
        }
    }

    /**
     * This method will be used to start listeners
     * @author : prustyA : 16/07/2023
     */
    private fun setListener() {
        //set swipe listener
        fragmentListProductBinding.srlRefresh.setOnRefreshListener { fetchProducts() }
        //set for add button
        fragmentListProductBinding.tvAdd.setOnClickListener { parent.changeFragment(ProductAddFragment()) }
        //set view model observers
        with(productViewModel) {
            //observe product list
            productsData.observe(parent, Observer {
                fragmentListProductBinding.srlRefresh.isRefreshing = false
                productAdapter.update(it.apply { randomizeHeight() })
            })
            //observe progress
            showProgressbar.observe(parent, Observer { isVisible ->
                fragmentListProductBinding.srlRefresh.isRefreshing = isVisible
            })
        }
        //text watcher
        fragmentListProductBinding.svSearch.addTextChangedListener(afterTextChangedDelayed { text, isDelay ->
            //set progress
            productViewModel.showProgressbar.value = true
            if (isDelay) {
                //get filter list
                val filterList: ArrayList<Product> = if (text.isNotEmpty()) {
                    //search
                    productViewModel.productsData.value?.filter { it.name.contains(text,true) } as ArrayList<Product>
                } else {
                    //all products
                    productViewModel.productsData.value?: ArrayList()
                }
                //update
                productAdapter.update(filterList)
                //set progress
                productViewModel.showProgressbar.value = false
            }
        })
    }

    /**
     * This method will be used to generate random height to achieve staggered view
     * @author : prustyA : 16/07/2023
     */
    private fun ArrayList<Product>.randomizeHeight() {
        forEach { product ->
            product.height = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                Random.nextInt(250, 400).toFloat,
                resources.displayMetrics
            ).roundToInt()

        }
    }
}