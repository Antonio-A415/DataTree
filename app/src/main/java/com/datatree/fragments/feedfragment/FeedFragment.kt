package com.datatree.fragments.feedfragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.datatree.R
import com.datatree.databinding.FragmentFeedBinding
import com.datatree.infraestructure.adapters.AlertAdapter
import com.datatree.infraestructure.adapters.ImageViewPagerAdapter_feed_fragment
import com.datatree.infraestructure.dataclass.AlertPost
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FeedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint //agregar al anotacion para usar el ViewModel inyectado.
//agregar al anotacion para usar el ViewModel inyectado.
class FeedFragment : Fragment() {

    private var _binding : FragmentFeedBinding? = null
    private val binding get() =_binding!!
    private lateinit var imageAdapter : ImageViewPagerAdapter_feed_fragment

    private var isLiked = false
    //private val viewModel : FeedFragmentViewModel by viewModels()

    private val alerts = mutableListOf<AlertPost>()
    private lateinit var alertAdapter: AlertAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        */
        // Configurar transiciones compartidas para una entrada animada
        //supportPostponeEnterTransition()

        // Animar la entrada del CardView con fade y elevación

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding  = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
        //return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val alert = arguments?.getParcelable<AlertPost>("Alert_data")

        //alert?.let { viewModel.setAlert(it) }

        alertAdapter = AlertAdapter(requireContext(), alerts)
        binding.feedRecyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = alertAdapter
        }

        loadMockAlerts()
    }

    // Simula datos, puedes reemplazar esto con datos reales desde Firebase o una API
    private fun loadMockAlerts() {
        val jsonImages = JSONArray()
        jsonImages.put("https://mundoagro.cl/wp-content/uploads/2020/12/Mosquita-blanca-en-tomate-min-1-scaled.jpg")
        jsonImages.put("https://laredmultimedia.com/wp-content/uploads/2023/04/2-Humedad.jpg")

        alerts.add(
            AlertPost(
                id = 1,
                userId = "user1",
                region = "Yucatán",
                title = "Plaga en tomates",
                description = "Se detectó una plaga cerca del cultivo.",
                images = jsonImages.toString(),
                createdAt = Date()
            )
        )

        alerts.add(
            AlertPost(
                id = 2,
                userId = "user2",
                region = "Campeche",
                title = "Exceso de humedad",
                description = "Problema en los sensores de riego.",
                images = jsonImages.toString(),
                createdAt = Date()
            )
        )

        alertAdapter.notifyDataSetChanged()
    }


    private lateinit var imageViewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var btnLike: MaterialButton

    private fun setupUI(view: View, alert: AlertPost) {
        imageViewPager = view.findViewById(R.id.image_viewpager)
        tabLayout = view.findViewById(R.id.image_indicator)
        btnLike = view.findViewById(R.id.btn_like)

        val txtTitle: TextView = view.findViewById(R.id.txt_title)
        val txtDescription: TextView = view.findViewById(R.id.txt_description)
        val txtRegion: TextView = view.findViewById(R.id.txt_region)
        val txtTime: TextView = view.findViewById(R.id.txt_time)
        val chipCategory: Chip = view.findViewById(R.id.btn_category)

        txtTitle.text = alert.title
        txtDescription.text = alert.description
        txtRegion.text = alert.region

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        txtTime.text = sdf.format(Date())

        val images = parseImages(alert.images)
        imageAdapter = ImageViewPagerAdapter_feed_fragment(requireContext(), images)
        imageViewPager.adapter = imageAdapter

        TabLayoutMediator(tabLayout, imageViewPager) { _, _ -> }.attach()

        setupButtonAnimations(view)
        animateIndicators()
        imageViewPager.setPageTransformer(DepthPageTransformer())
    }

    private fun animateIndicators() {
        // aqui debes agregar animaciones
    }


    private fun parseImages(jsonImages: String): List<String> {
        val imageUrls = mutableListOf<String>()
        try {
            val jsonArray = JSONArray(jsonImages)
            for (i in 0 until jsonArray.length()) {
                imageUrls.add(jsonArray.getString(i))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            imageUrls.add("drawable://${R.drawable.tomate}")
        }
        return imageUrls
    }

    private fun setupButtonAnimations(view: View) {
        btnLike.setOnClickListener {
            isLiked = !isLiked
            val scaleDownX = ObjectAnimator.ofFloat(btnLike, "scaleX", 0.9f)
            val scaleDownY = ObjectAnimator.ofFloat(btnLike, "scaleY", 0.9f)
            scaleDownX.duration = 100
            scaleDownY.duration = 100

            val scaleUpX = ObjectAnimator.ofFloat(btnLike, "scaleX", 1.0f)
            val scaleUpY = ObjectAnimator.ofFloat(btnLike, "scaleY", 1.0f)
            scaleUpX.duration = 100
            scaleUpY.duration = 100

            val scaleDown = AnimatorSet().apply {
                play(scaleDownX).with(scaleDownY)
            }
            val scaleUp = AnimatorSet().apply {
                play(scaleUpX).with(scaleUpY)
            }
            scaleDown.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    scaleUp.start()
                }
            })
            scaleDown.start()

            btnLike.text = "Me gusta"
            val color = if (isLiked) R.color.accent_red else R.color.text_secondary
            btnLike.setIconTint(ColorStateList.valueOf(requireContext().getColor(color)))
        }

        val btnComment: MaterialButton = view.findViewById(R.id.btn_comment)
        val btnShare: MaterialButton = view.findViewById(R.id.btn_share)

        btnComment.setOnClickListener {
            Toast.makeText(requireContext(), "Comentar...", Toast.LENGTH_SHORT).show()
        }

        btnShare.setOnClickListener {
            Toast.makeText(requireContext(), "Compartir...", Toast.LENGTH_SHORT).show()
        }
    }


    /*
    private fun observeViewModel() {
        viewModel.alert.observe(viewLifecycleOwner) { alert ->

            binding.txtTitle.text = alert.title
            binding.txtDescription.text = alert.description
            binding.txtRegion.text = alert.region

            val imageUrls = viewModel.parseImages(alert.images)
            imageAdapter = ImageViewPagerAdapter(requireContext(), imageUrls)
            binding.imageViewpager.adapter = imageAdapter

            TabLayoutMediator(binding.imageIndicator, binding.imageViewpager) { _, _ -> }.attach()
        }

        viewModel.isLiked.observe(viewLifecycleOwner) { liked ->
            val color = if (liked) R.color.accent_red else R.color.text_secondary
            binding.btnLike.setIconTint(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), color)))
        }
    }
    private fun setupListeners() {
        binding.btnLike.setOnClickListener {
            viewModel.toggleLike()
            animateLikeButton(binding.btnLike)
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnShare.setOnClickListener {
            shareAlert(viewModel.alert.value)
        }
    }

    */
    private fun shareAlert(alert: AlertPost?) {
        alert?.let {
            val shareText = "${it.title}\n\n${it.description}\n\nRegión: ${it.region}"
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Compartir alerta"))
        }
    }
    private fun animateLikeButton(button: MaterialButton) {
        val scaleDownX = ObjectAnimator.ofFloat(button, "scaleX", 0.9f)
        val scaleDownY = ObjectAnimator.ofFloat(button, "scaleY", 0.9f)
        val scaleUpX = ObjectAnimator.ofFloat(button, "scaleX", 1.0f)
        val scaleUpY = ObjectAnimator.ofFloat(button, "scaleY", 1.0f)

        scaleDownX.duration = 100
        scaleDownY.duration = 100
        scaleUpX.duration = 100
        scaleUpY.duration = 100

        val scaleDown = AnimatorSet().apply { play(scaleDownX).with(scaleDownY) }
        val scaleUp = AnimatorSet().apply { play(scaleUpX).with(scaleUpY) }

        scaleDown.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                scaleUp.start()
            }
        })

        scaleDown.start()
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FeedFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FeedFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

class DepthPageTransformer : ViewPager2.PageTransformer {
    /**
     * Apply a property transformation to the given page.
     *
     * @param page Apply the transformation to this page
     * @param position Position of page relative to the current front-and-center
     * position of the pager. 0 is front and center. 1 is one full
     * page position to the right, and -2 is two pages to the left.
     * Minimum / maximum observed values depend on how many pages we keep
     * attached, which depends on offscreenPageLimit.
     *
     * @see .setOffscreenPageLimit
     */
    private val MIN_SCALE = 0.85f
    private val MIN_ALPHA = 0.5f

    override fun transformPage(view: View, position: Float) {
        val pageWidth = view.width

        when {
            position < -1 -> { // [-Infinity,-1)
                // Esta página está fuera de pantalla a la izquierda
                view.alpha = 0f
            }
            position <= 0 -> { // [-1,0]
                // Página en movimiento en pantalla
                view.alpha = 1f
                view.translationX = 0f
                view.scaleX = 1f
                view.scaleY = 1f
            }
            position <= 1 -> { // (0,1]
                // Página hacia afuera
                val scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position))
                val alphaFactor = MIN_ALPHA + (1 - MIN_ALPHA) * (1 - Math.abs(position))

                view.alpha = alphaFactor
                view.translationX = pageWidth * -position
                view.scaleX = scaleFactor
                view.scaleY = scaleFactor
            }
            else -> { // (1,+Infinity]
                // Esta página está fuera de pantalla a la derecha
                view.alpha = 0f
            }
        }
    }
}


