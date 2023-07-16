package com.swipe.product.domain.model

import com.google.gson.annotations.SerializedName
import java.io.File
import java.io.Serializable

class Product : Serializable {
    @SerializedName("product_id")
    var id: Int = 0

    @SerializedName("product_name")
    var name: String = ""

    @SerializedName("image")
    var image: String = ""

    @SerializedName("product_type")
    var type: String = ""

    @SerializedName("price")
    var price: String = ""

    @SerializedName("tax")
    var tax: String = ""

    var height: Int = 200
    var file: File? = null

    fun clear() {
        this.id = 0
        this.name = ""
        this.type = ""
        this.price = ""
        this.tax = ""
        this.height = 200
        this.file = null
    }
}