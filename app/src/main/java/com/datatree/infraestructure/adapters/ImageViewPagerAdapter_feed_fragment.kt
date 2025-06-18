package com.datatree.infraestructure.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.datatree.R
import com.github.chrisbanes.photoview.PhotoView
import com.github.chrisbanes.photoview.OnScaleChangedListener
import com.github.chrisbanes.photoview.OnPhotoTapListener

class ImageViewPagerAdapter_feed_fragment(
    private val contexto: Context,
    private val imageUrls: List<String>
) :  RecyclerView.Adapter<ImageViewPagerAdapter_feed_fragment.ImageViewHolder>(){




    /**
     * Called when RecyclerView needs a new [ViewHolder] of the given type to represent
     * an item.
     *
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     *
     *
     * The new ViewHolder will be used to display items of the adapter using
     * [.onBindViewHolder]. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary [View.findViewById] calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see .getItemViewType
     * @see .onBindViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(contexto).inflate(R.layout.item_image_slide_viewpager, parent, false)
        return ImageViewHolder(view)
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int {
        return imageUrls.size
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the [ViewHolder.itemView] to reflect the item at the given
     * position.
     *
     *
     * Note that unlike [android.widget.ListView], RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the `position` parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use [ViewHolder.getBindingAdapterPosition] which
     * will have the updated adapter position.
     *
     * Override [.onBindViewHolder] instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageUrls[position]

        Glide.with(contexto)
            .load(imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(R.drawable.access_time_24)
            .into(holder.photoView)

        //configurar photoview para zoom y gestos
        holder.photoView.apply{
            // Habilitar rotación de imágenes (opcional)
            setRotationBy(0f)
            // Establecer zoom mínimo y máximo
            setMinimumScale(0.5f)
            setMaximumScale(3.0f)
            // Habilitar zoom al doble tap
            setMediumScale(1.75f)
        }

        // Listener para eventos de PhotoView
        OnScaleChangedListener { scaleFactor, focusX, focusY ->
            // Puedes personalizar comportamiento adicional aquí
            // Por ejemplo, mostrar u ocultar controles basados en el nivel de zoom
            if (scaleFactor > 1.5f) {
                // La imagen está ampliada, podrías ocultar elementos UI
                holder.gradientOverlay.visibility = View.GONE
            } else {
                // La imagen está en escala normal, restaurar elementos UI
                holder.gradientOverlay.visibility = View.VISIBLE
            }
        }

        // Listener personalizado para tap simple
        OnPhotoTapListener { view, x, y ->
            // Activar alguna acción al tocar la imagen
            // Por ejemplo, mostrar/ocultar controles adicionales
        }
    }

    //auxiliar class
    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val photoView : PhotoView = itemView.findViewById(R.id.image_slide)
        val gradientOverlay : View = itemView.findViewById(R.id.gradient_overlay)
    }
}