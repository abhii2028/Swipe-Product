package com.swipe.product.utils

import android.animation.Animator
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    //Internet connectivity check in Android Q
    val networks = connectivityManager.allNetworks
    var hasInternet = false
    if (networks.isNotEmpty()) {
        for (network in networks) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            if (networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true) hasInternet = true
        }
    }
    return hasInternet
}

//Regular expression
object Regex {
    val alphaNumeric = "^[a-zA-Z0-9]*\$".toRegex()
    val alphabetic = "^[a-zA-Z]*\$".toRegex()
    val numeric = "^[-+]?[0-9]+([0-9]+)*$".toRegex()
    val numericDecimal = "^-?[0-9]\\d*(\\.\\d+)?\$".toRegex()
    val specChar = "[^a-zA-z0-9]+".toRegex()
    val onlyDigit = "\\d+".toRegex()
}

/**
 * Make any data type into [String] data type
 */
val Any?.toString: String
    get() {
        return when (this) {
            (this == null) -> ""
            else -> {
                val text = toString()
                if (text.equals("null", true)) "" else text.trim()
            }
        }
    }

/**
 * Make any data type into [Long] data type
 */
val Any?.toLong: Long
    get() {
        return when (this) {
            is String -> {
                if (this.matches(Regex.numericDecimal)) {
                    if (this.contains("."))
                        this.toDouble.toLong()
                    else this.toLong()
                } else 0
            }

            is Long -> this
            is Float -> this.toLong()
            is Double -> this.toLong()
            is Int -> this.toLong()
            is Boolean -> if (this) 1L else 0L
            else -> 0L
        }
    }

/**
 * Make any data type into [Int] data type
 */
val Any?.toInt: Int
    get() {
        return when (this) {
            is String -> {
                if (this.matches(Regex.numericDecimal)) {
                    if (this.contains("."))
                        this.toDouble.toInt()
                    else this.toInt()
                } else 0
            }

            is Char -> this.toString().toInt()
            is Long -> this.toInt()
            is Float -> this.toInt()
            is Double -> this.toInt()
            is Int -> this
            is Byte -> this.toInt()
            is Boolean -> if (this) 1 else 0
            else -> 0
        }
    }

/**
 * Make any data type into [Float] data type
 */
val Any?.toFloat: Float
    get() {
        return when (this) {
            is String -> {
                if (this.matches(Regex.numericDecimal)) {
                    if (this.contains("."))
                        this.toDouble.toFloat()
                    else this.toFloat()
                } else 0f
            }

            is Long -> this.toFloat()
            is Float -> this
            is Double -> this.toFloat()
            is Int -> this.toFloat()
            is Boolean -> if (this) 1f else 0f
            else -> 0f
        }
    }

/**
 * Make any data type into [Double] data type
 */
val Any?.toDouble: Double
    get() {
        return when (this) {
            is String -> if (this.matches(Regex.numericDecimal)) this.toDouble() else 0.0
            is Long -> this.toDouble()
            is Float -> this.toDouble()
            is Double -> if (this.toString().equals("nan", true)) 0.0 else this
            is Int -> this.toDouble()
            is Boolean -> if (this) 1.0 else 0.0
            else -> 0.0
        }
    }

/**
 * Make any data type into [Boolean] data type
 */
val Any?.toBoolean: Boolean
    get() {
        return when (this) {
            is String -> {
                val values = "true|active|on|online|approve|1".toRegex()
                this.toLowerCase(Locale.US).contains(values)
            }

            is Boolean -> this
            is Long -> this > 0L
            is Float -> this > 0f
            is Double -> this > 0.0
            is Int -> this > 0
            else -> false
        }
    }

/**
 * It will set empty model if its null
 */
val <T> ArrayList<T>?.cleanList: ArrayList<T>
    get() {
        return this ?: arrayListOf()
    }

/**
 * Check the position is valid in the list or not
 */
fun <T> ArrayList<T>?.has(position: Int): Boolean {
    return !isNullOrEmpty() && position >= 0 && position < size
}

/**
 * Check the object is valid in the list or not
 */
fun <T> ArrayList<T>?.has(obj: T?): Boolean {
    return this != null && obj != null && isNotEmpty() && try {
        contains(obj)
    } catch (e: Exception) {
        false
    }
}

/**
 * method to send call back after user stop typing
 */
fun afterTextChangedDelayed(
    delay: Long = 1000L,
    afterTextChanged: (String, Boolean) -> Unit
): TextWatcher {
    //return text watcher
    return object : TextWatcher {
        var timer: CountDownTimer? = null

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(editable: Editable?) {
            timer?.cancel()
            timer = object : CountDownTimer(delay, delay + 500) {
                override fun onTick(millisUntilFinished: Long) {}

                override fun onFinish() {
                    //trigger after hold time is completed
                    afterTextChanged.invoke(editable?.toString().toString, true)
                }
            }.start()
            //trigger on every change
            afterTextChanged.invoke(editable?.toString().toString, false)
        }
    }
}

/**
 *Method to load image
 **/
fun ImageView?.loadImage(
    source: String?, @DrawableRes defaultLogo: Int = 0, callback: () -> Unit = {}
) {
    this?.apply {
        //call back
        val listener = object : RequestListener<Uri?, GlideDrawable?> {
            override fun onException(
                e: Exception?,
                model: Uri?,
                target: Target<GlideDrawable?>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: GlideDrawable?,
                model: Uri?,
                target: Target<GlideDrawable?>?,
                isFromMemoryCache: Boolean,
                isFirstResource: Boolean
            ): Boolean {
                callback.invoke()
                return false
            }
        }
        //get url
        val url = source.toString
        //check
        if (url.isNotEmpty()) {
            //image according to the url
            Glide.with(context)
                .from(Uri::class.java)
                .placeholder(defaultLogo)
                .error(defaultLogo)
                .listener(listener)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .load(Uri.parse(url))
                .into(this)

        } else {
            //default image
            loadImage(defaultLogo)
        }
    }
}

/**
 *Method to load image
 **/
fun ImageView?.loadImage(
    @DrawableRes source: Int,
    @DrawableRes defaultLogo: Int = 0, callback: () -> Unit = {}
) {
    this?.apply {
        //call back
        val listener = object : RequestListener<Int?, GlideDrawable?> {
            override fun onException(
                e: Exception?,
                model: Int?,
                target: Target<GlideDrawable?>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: GlideDrawable?,
                model: Int?,
                target: Target<GlideDrawable?>?,
                isFromMemoryCache: Boolean,
                isFirstResource: Boolean
            ): Boolean {
                callback.invoke()
                return false
            }
        }
        //check
        //default image
        Glide.with(context)
            .load(source)
            .placeholder(defaultLogo)
            .error(defaultLogo)
            .listener(listener)
            .into(this)
    }
}

/**
 * This method will be used to set tint for the given image view
 * @author : prustyA : 22/06/2022
 */
fun ImageView?.setTint(@ColorRes color: Int) {
    this?.apply {
        //paint once view is free
        post {
            drawable?.let {
                val wrapDrawable = DrawableCompat.wrap(drawable)
                wrapDrawable?.mutate()
                DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(context, color))
                wrapDrawable?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
            }
        }
    }
}

/**
 * Method to show view
 * @param duration : [Long]
 * @param complete : Return when task is complete
 */
fun View?.show(duration: Long = 200L, complete: (View) -> Unit = {}) {
    this?.apply {
        alpha = 0f
        visibility = View.VISIBLE
        bringToFront()

        animate()
            .setDuration(duration)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator) {
                    complete.invoke(this@apply)
                }

                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
                override fun onAnimationCancel(animation: Animator) {}

            })
            .alpha(1f)
            .start()
    }
}

/**
 * Method to hide view
 * @param duration : [Long]
 * @param complete : Return when task is complete
 */
fun View?.hide(duration: Long = 200L, complete: (View) -> Unit = {}) {
    this?.apply {
        alpha = 1f
        visibility = View.VISIBLE

        animate()
            .setDuration(duration)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator) {
                    complete.invoke(this@apply)
                }

                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
                override fun onAnimationCancel(animation: Animator) {}

            })
            .alpha(0f)
            .start()
    }
}

/**
 * This method will be used to hide keyboard
 * @author : prustyA : 16/07/2023
 */
fun Activity?.hideKeyboard() {
    this?.currentFocus?.let { view ->
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

/**
 * Create a new file from given uri and return it
 */
fun Context.getFile(uri: Uri?): File? {

    fun getMimeType(uri: Uri): String? {
        val cR: ContentResolver = contentResolver
        return cR.getType(uri)
    }

    fun getExtension(uri: Uri?): String? {
        uri ?: return null
        val mime = MimeTypeMap.getSingleton()
        val extension = mime.getExtensionFromMimeType(getMimeType(uri))
        return extension ?: ""
    }

    /**
     * Get Original file name from Uri
     *  https://developer.android.com/training/secure-file-sharing/retrieve-info
     */
    fun getFileName(uri: Uri?): String? {
        uri ?: return null
        return contentResolver?.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(nameIndex)
        }
    }
    fun OutputStream.copyInputStreamToFile(inputStream: InputStream?) {
        this.use { fileOut ->
            inputStream?.copyTo(fileOut)
        }
    }


    uri ?: return null

    val tmpFile = File(cacheDir, getFileName(uri) ?: "temp_file_name.${getExtension(uri)}")
    try {
        FileOutputStream(tmpFile).copyInputStreamToFile(contentResolver.openInputStream(uri))
        return tmpFile
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return null
}