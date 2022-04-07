package es.ucm.fdi.workitout.utils

import android.widget.ImageView
import androidx.fragment.app.FragmentManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//Se carga la imagen en el ImageView
fun ImageView.loadResource(resource: String?, diskCacheStrategy: DiskCacheStrategy = DiskCacheStrategy.ALL) {
    Glide.with(context).load(resource).diskCacheStrategy(diskCacheStrategy).into(this)
}

//Se obtiene el fragment actual
//val FragmentManager.currentFragment: Fragment?
//    get() = primaryNavigationFragment?.childFragmentManager?.fragments?.first()

//Se obtiene el navController para realizar la navegación
fun FragmentManager.getNavController(container: Int): NavController? {
    val navHostFragment = this.findFragmentById(container) as NavHostFragment?
    return navHostFragment?.navController
}

//Comprueba que se cumpla la condición de error y de ser así muestra el error en el TextInputLayout
fun TextInputLayout.tilError(conditionError: Boolean, resError: Int): Boolean {
    return if (conditionError) {
        this.error = context.getString(resError)
        true
    } else {
        this.error = ""
        false
    }
}

fun TextView.tvError(conditionError: Boolean): Boolean {
    return if (conditionError) {
        this.visibility = View.VISIBLE
        true
    } else {
        this.visibility = View.GONE
        false
    }
}

//Se devuelve el color del atributo
/*@ColorInt
fun Context.getColorFromAttr(@AttrRes attrColor: Int): Int {
    val typedArray = theme.obtainStyledAttributes(intArrayOf(attrColor))
    val textColor = typedArray.getColor(0, 0)
    typedArray.recycle()
    return textColor
}*/

fun ImageView.getByteArray(quality: Int): ByteArray {
    val bitmap = (this.drawable as BitmapDrawable).bitmap
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)
    return baos.toByteArray()
}

fun FirebaseStorage.getImageRef(collection: String, name: String): StorageReference {
    val dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
    val fileName = name.filter { !it.isWhitespace() } + dateTime

    return getReference("images/$collection/$fileName")
}


//Collector para SharedFlow
inline fun <T> Flow<T>.collectFlow(owner: LifecycleOwner, crossinline onCollect: suspend (T) -> Unit) =
    owner.lifecycleScope.launch { owner.repeatOnLifecycle(Lifecycle.State.STARTED) { collect { onCollect(it) } } }

//Collector para StateFlow
inline fun <T> Flow<T>.collectLatestFlow(owner: LifecycleOwner, crossinline onCollect: suspend (T) -> Unit) =
    owner.lifecycleScope.launch { owner.repeatOnLifecycle(Lifecycle.State.STARTED) { collectLatest { onCollect(it) } } }