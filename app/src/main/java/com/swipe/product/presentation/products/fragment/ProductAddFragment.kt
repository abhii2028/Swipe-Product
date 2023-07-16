package com.swipe.product.presentation.products.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.swipe.product.R
import com.swipe.product.databinding.FragmentAddProductBinding
import com.swipe.product.domain.model.Product
import com.swipe.product.presentation.products.ProductViewModel
import com.swipe.product.presentation.products.activity.ProductsActivity
import com.swipe.product.utils.getFile
import com.swipe.product.utils.has
import com.swipe.product.utils.isNetworkAvailable
import com.swipe.product.utils.loadImage
import com.swipe.product.utils.toString
import java.io.BufferedReader
import java.io.InputStreamReader


/**
 * This class will be used to define and add new product
 * @author : prustyA : 16/07/2023
 */
class ProductAddFragment : Fragment() {

    companion object {
        const val REQUEST_FILE = 100
    }

    //init binding
    private lateinit var fragmentAddProductBinding: FragmentAddProductBinding

    //find parent activity
    private val parent: ProductsActivity by lazy { activity as ProductsActivity }

    //get view model from parent activity
    private val productViewModel: ProductViewModel by lazy { parent.productViewModel }

    //default product
    private val product = Product()

    //type adapter
    private val types: ArrayList<String> by lazy { fetchTypes(15) }
    private var typeAdapter: ArrayAdapter<String>? = null

    //on fragment creation
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentAddProductBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_product, container, false)
        return fragmentAddProductBinding.root
    }

    //after fragment is set to execute
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        setListener()
    }

    //on hide / show
    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden) reset()
    }

    /**
     * This method will be used to initialize objects
     * @author : prustyA : 16/07/2023
     */
    private fun init() {
        //create adapter
        typeAdapter = ArrayAdapter(parent, android.R.layout.simple_spinner_item, types)
        typeAdapter?.setDropDownViewResource(
            android.R.layout
                .simple_spinner_dropdown_item
        )
        fragmentAddProductBinding.edtType.adapter = typeAdapter

    }

    /**
     * This method will be used to start listeners
     * @author : prustyA : 16/07/2023
     */
    private fun setListener() {
        //set for image
        fragmentAddProductBinding.ivImage.setOnClickListener { openPicker() }
        //set for back button
        fragmentAddProductBinding.tvBack.setOnClickListener { parent.onBackPressed() }
        //set for add button
        fragmentAddProductBinding.tvAdd.setOnClickListener { addProduct() }
        //set on type selection
        fragmentAddProductBinding.edtType.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                product.type = if (types.has(position)) types[position] else ""
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //nothing happened
            }
        }
    }

    //fetch types
    private fun fetchTypes(of: Int): ArrayList<String> {
        val words = ArrayList<String>()
        try {
            val inputStream = resources.openRawResource(R.raw.words)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            var eachline = bufferedReader.readLine()
            while (eachline != null) {
                // `the words in the file are separated by space`, so to get each words
                val word = eachline.split(" ".toRegex()).dropLastWhile { it.isEmpty() }[0]
                eachline = bufferedReader.readLine()
                words.add(word)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return words
    }

    //reset each data
    private fun reset() {
        //set
        fragmentAddProductBinding.edtName.setText("")
        fragmentAddProductBinding.edtType.setSelection(0)
        fragmentAddProductBinding.edtPrice.setText("")
        fragmentAddProductBinding.edtTax.setText("")
        fragmentAddProductBinding.ivImage.loadImage(R.drawable.ic_image)
        //clear product
        product.clear()
    }

    //add product
    private fun addProduct() {
        //save all in products
        product.apply {
            product.name = fragmentAddProductBinding.edtName.text.toString
            product.price = fragmentAddProductBinding.edtPrice.text.toString
            product.tax = fragmentAddProductBinding.edtTax.text.toString
        }

        //check for valid data
        val isValid = product.name.isNotEmpty() &&
                product.type.isNotEmpty() &&
                product.price.isNotEmpty() &&
                product.tax.isNotEmpty()
        //check for validation
        if (isValid) {
            //check for network
            if (parent.isNetworkAvailable()) {
                //add to products
                productViewModel.addProduct(product)
                //sent back to list screen
                fragmentAddProductBinding.tvBack.performClick()
            } else {
                //send internet error message
                productViewModel.messageData.value =
                    parent.getString(R.string.no_internet_connection)
            }
        } else {
            productViewModel.messageData.value = parent.getString(R.string.invalid_data)
        }
    }

    //open gallery
    private fun openPicker() {
        val fileIntent = Intent(Intent.ACTION_GET_CONTENT)
        fileIntent.addCategory(Intent.CATEGORY_OPENABLE)
        fileIntent.type = "image/*"
        startActivityForResult(fileIntent, REQUEST_FILE)
    }

    //on activity result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //update paths
        if (requestCode == REQUEST_FILE && resultCode == AppCompatActivity.RESULT_OK) {
            var uri: Uri? = null
            if (data != null && data.data != null)
                uri = data.data!!
            //save file
            product.file = parent.getFile(uri)
            product.image = uri.toString
            //load image
            fragmentAddProductBinding.ivImage.loadImage(uri.toString, R.drawable.ic_image)
        }
    }
}