package com.swipe.product.presentation.products.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.swipe.product.R
import com.swipe.product.databinding.ActivityProductBinding
import com.swipe.product.presentation.products.ProductViewModel
import com.swipe.product.presentation.products.fragment.ProductListFragment
import com.swipe.product.utils.hideKeyboard
import com.swipe.product.utils.toString
import org.koin.android.viewmodel.ext.android.viewModel


/**
 * This class will be used as entry point for UI
 * @author : prustyA : 16/07/2023
 */
class ProductsActivity : AppCompatActivity() {

    //init binding
    private lateinit var activityProductBinding: ActivityProductBinding

    //init view model
    val productViewModel: ProductViewModel by viewModel()

    //Set fragments
    private val currentFragment: Fragment? get() = fm.primaryNavigationFragment

    //create fragment transaction
    private val fm = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //set binding
        activityProductBinding = DataBindingUtil.setContentView(this, R.layout.activity_product)
        //open list fragment
        changeFragment(ProductListFragment())
        //set view model observers
        with(productViewModel) {
            //observe any error message
            messageData.observe(this@ProductsActivity, Observer {
                //set text
                if (it.toString.isNotEmpty()) {
                    activityProductBinding.tvAlert.text = it.toString
                    //show
                    activityProductBinding.tvAlert.visibility = View.VISIBLE
                    //hide after some time
                    Handler(Looper.getMainLooper()).postDelayed(Runnable {
                        //hide
                        activityProductBinding.tvAlert.visibility = View.GONE
                    }, 1000L)
                }
            })
        }
    }

    /**
     * This method will be used to change fragment
     * @author : prustyA : 16/07/2023
     */
    fun changeFragment(fragment: Fragment) {
        //hide keyboard
        hideKeyboard()
        val ft = fm.beginTransaction()
        val tagFragmentName = fragment::class.java.simpleName
        if (currentFragment != null) {
            ft.hide(currentFragment!!)
        }
        var fragmentTemp: Fragment? = fm.findFragmentByTag(tagFragmentName)
        if (fragmentTemp == null) {
            fragmentTemp = fragment
            ft.add(R.id.container, fragmentTemp, tagFragmentName)
        } else {
            ft.show(fragmentTemp)
        }
        ft.setPrimaryNavigationFragment(fragmentTemp)
        ft.setReorderingAllowed(true)
        ft.commitNowAllowingStateLoss()
    }

    //perform back trace
    override fun onBackPressed() {
        //start transaction
        if (currentFragment != null &&
            currentFragment != fm.findFragmentByTag(ProductListFragment::class.java.simpleName)
        ) {
            changeFragment(ProductListFragment())
        } else {
            super.onBackPressed()
        }
    }
}
