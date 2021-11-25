package co.idearun.game.feature.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.collection.ArrayMap
import androidx.navigation.fragment.findNavController
import co.idearun.data.model.FieldData
import co.idearun.data.model.TopFieldsItem
import co.idearun.game.base.BaseFragment
import co.idearun.game.model.ParentItem
import co.idearun.game.R
import co.idearun.game.feature.adapter.PlayerParentAdapter
import co.idearun.game.feature.viewmodel.FormViewModel
import kotlinx.android.synthetic.main.fragment_result.*
import timber.log.Timber
import androidx.activity.OnBackPressedCallback
import co.idearun.game.feature.PlayerInfo
import org.koin.android.viewmodel.ext.android.viewModel


class PlayerResultFragment : BaseFragment() {

    lateinit var adapterParentParent: PlayerParentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_result, container, false)
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val formVm: FormViewModel by viewModel()
        val slug = arguments?.getString("slug")
        val liveDashboardAddress = PlayerInfo.playerFormInfo?.form?.liveDashboardAddress

        Timber.i("Live Dashboard $liveDashboardAddress")


        formVm.initLessonAddress(PlayerInfo.playerFormInfo?.form?.address!!)
        formVm.getFormData()

        formVm.form1.observe(this,{
            resultFormName.text = it.title
        })

        startAction.visibility = View.VISIBLE
        startAction.text = getString(R.string.play_again)
        startAction.setOnClickListener {
            findNavController().navigate(R.id.action_playerResultFragment_to_mainFragment)
        }

        resultAction.setOnClickListener {
            formVm.getSubmitsRow(liveDashboardAddress!!)
        }


        formVm.getSubmitsRow(liveDashboardAddress!!)


        adapterParentParent = PlayerParentAdapter(requireContext())
        parentRecyclerView.adapter = adapterParentParent

        formVm.submits.observe(this, {
            var fieldDataMapList = arrayListOf<ArrayMap<String, FieldData>>()
            var topFieldsItem = arrayListOf<List<TopFieldsItem>>()
            var parentItem = arrayListOf<ParentItem>()

            Timber.i("field size " + it?.data?.rows?.size)

            val topFieldData = it?.data?.topFields

            it.data?.rows?.forEach {
                var fieldDataMap = ArrayMap<String, FieldData>()
                it?.renderedData?.entries?.forEach {
                    Timber.i("rendred ${it.key} vs ${it.value.rawValue}")
                    fieldDataMap.put(it.key, it.value)
                }
                fieldDataMapList.add(fieldDataMap)
                topFieldsItem.add(topFieldData as List<TopFieldsItem>)
                parentItem.add(ParentItem(it?.slug!!, topFieldsItem, fieldDataMapList))
            }
            adapterParentParent.setItemList(parentItem)
        })



        formVm.failure.observe(this, {
            formVm.hideLoading()
            checkFailureStatus(it)
        })

        formVm.isLoading.observe(this, {
            if (it) loading.visibility = View.VISIBLE else loading.visibility = View.GONE
        })

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }

        })

    }

}
