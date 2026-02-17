package com.datatree.fragments.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.datatree.databinding.BottomsheetImagesBinding
import com.datatree.infraestructure.adapters.ImagePagerAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.math.hypot

class ImageBottomSheet(
    private val imagenes: List<String>,
    private val startPosition: Int
) : BottomSheetDialogFragment() {

    private var _binding: BottomsheetImagesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetImagesBinding.inflate(inflater, container, false)

        // Setup ViewPager2 con Glide adapter
        val adapter = ImagePagerAdapter(imagenes)
        binding.viewPagerImages.adapter = adapter
        binding.viewPagerImages.setCurrentItem(startPosition, false)

        // Setup TabLayout
        TabLayoutMediator(binding.tabLayout, binding.viewPagerImages) { _, _ -> }.attach()

        // Animación de entrada
        binding.circularLayout.post {
            val centerX = binding.circularLayout.width / 2
            val centerY = binding.circularLayout.height / 2
            val finalRadius = hypot(centerX.toDouble(), centerY.toDouble()).toFloat()

            binding.circularLayout.visibility = View.VISIBLE

            val anim = ViewAnimationUtils.createCircularReveal(
                binding.circularLayout,
                centerX,
                centerY,
                0f,
                finalRadius
            )
            anim.duration = 400
            anim.start()
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.let { d ->
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        }
    }

    // Animación de salida
    override fun dismiss() {
        val centerX = binding.circularLayout.width / 2
        val centerY = binding.circularLayout.height / 2
        val initialRadius = hypot(centerX.toDouble(), centerY.toDouble()).toFloat()

        val anim = ViewAnimationUtils.createCircularReveal(
            binding.circularLayout,
            centerX,
            centerY,
            initialRadius,
            0f
        )
        anim.duration = 400
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super@ImageBottomSheet.dismissAllowingStateLoss()
            }
        })
        anim.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
