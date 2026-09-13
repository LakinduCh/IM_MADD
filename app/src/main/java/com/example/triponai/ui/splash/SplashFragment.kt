package com.example.triponai.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.triponai.R
import com.example.triponai.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startSplashAnimation()

        Handler(Looper.getMainLooper()).postDelayed({
            navigateToLogin()
        }, 3000)
    }

    private fun startSplashAnimation() {
        // Animation for the logo and title
        val logoAnimation = AnimationSet(true).apply {
            addAnimation(ScaleAnimation(0.8f, 1.0f, 0.8f, 1.0f, 
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f, 
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f).apply {
                duration = 1000
                interpolator = AccelerateDecelerateInterpolator()
            })
            addAnimation(AlphaAnimation(0.0f, 1.0f).apply {
                duration = 800
            })
        }

        binding.ivSplashLogo.startAnimation(logoAnimation)
        binding.tvSplashTitle.startAnimation(logoAnimation)
        
        // Animation for the tagline (delayed)
        val taglineAnimation = AlphaAnimation(0.0f, 1.0f).apply {
            duration = 1000
            startOffset = 600
            fillAfter = true
        }
        binding.tvSplashTagline.startAnimation(taglineAnimation)
        
        // Animate background shapes
        val shapeAnimation = AlphaAnimation(0.0f, 0.5f).apply {
            duration = 1500
            startOffset = 200
            fillAfter = true
        }
        binding.splashShapeTop.startAnimation(shapeAnimation)
        binding.splashShapeLeft.startAnimation(shapeAnimation)
        binding.splashShapeRight.startAnimation(shapeAnimation)
    }

    private fun navigateToLogin() {
        if (_binding != null) {
            findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
