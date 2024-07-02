package com.kotlinflows.simplemvvmkotlinflows.views.currentcolor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.simplemvvmkotlinflows.databinding.FragmentCurrentColorBinding
import com.kotlinflows.foundation.views.BaseFragment
import com.kotlinflows.foundation.views.BaseScreen
import com.kotlinflows.foundation.views.screenViewModel
import com.kotlinflows.simplemvvmkotlinflows.views.onTryAgain
import com.kotlinflows.simplemvvmkotlinflows.views.renderSimpleResult

class CurrentColorFragment : BaseFragment() {
    private lateinit var binding: FragmentCurrentColorBinding

    // no arguments for this screen
    class Screen : BaseScreen

    override val viewModel by screenViewModel<CurrentColorViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCurrentColorBinding.inflate(layoutInflater, container, false)
        viewModel.currentColor.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                binding.root,
                result = result,
                onSuccess = {
                    binding.colorView.setBackgroundColor(it.value)
                })

        }
        binding.changeColorButton.setOnClickListener {
            viewModel.changeColor()
        }
        binding.askPermissionsButton.setOnClickListener {
            viewModel.requestPermission()
        }

        onTryAgain(binding.root) {
            viewModel.tryAgain()
        }

        return binding.root
    }


}
