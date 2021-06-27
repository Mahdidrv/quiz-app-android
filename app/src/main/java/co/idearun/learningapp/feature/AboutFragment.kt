package co.idearun.learningapp.feature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.idearun.learningapp.common.base.BaseFragment
import co.idearun.learningapp.common.base.BaseViewModel
import co.idearun.learningapp.databinding.FragmentAboutBinding
import co.idearun.learningapp.feature.viewmodel.SharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class AboutFragment : BaseFragment() {
    lateinit var binding: FragmentAboutBinding
    private val shardedVM: SharedViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun getViewModel(): BaseViewModel = shardedVM


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =FragmentAboutBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root

    }
}