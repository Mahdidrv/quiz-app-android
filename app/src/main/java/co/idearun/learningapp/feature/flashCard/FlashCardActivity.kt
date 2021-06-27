package co.idearun.learningapp.feature.flashCard

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.databinding.DataBindingUtil.setContentView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.idearun.learningapp.R
import co.idearun.learningapp.common.extension.invisible
import co.idearun.learningapp.common.extension.visible
import co.idearun.learningapp.data.model.form.Fields
import co.idearun.learningapp.data.model.form.Form
import co.idearun.learningapp.databinding.ActivityFlashCardBinding
import co.idearun.learningapp.feature.viewmodel.SharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class FlashCardActivity : FlashCardBaseActivity(), FlashcardListener {

    private lateinit var binding: ActivityFlashCardBinding
    private val shardedVM: SharedViewModel by viewModel()
    private var fieldsFlashAdapter: FieldsFlashAdapter? = null
    private var form: Form? = null
    private var lessonsProgressMap = hashMapOf<String?, Int?>()
    private var fields: ArrayList<Fields> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setContentView(this, R.layout.activity_flash_card)
        binding.listener = this
        binding.flashcardListener = this
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        baseMethod.hideAB(supportActionBar)

        checkBundle()
        initView()
        initData()
    }


    private fun checkBundle() {
        intent.extras?.let {
            it.getSerializable("form")?.let {
                if (it is Form) {
                    binding.form = it
                    binding.executePendingBindings()

                    it.fields_list?.let {
                        this.fields = it
                    }
                    form = it

                } else {

                }
            }
        }
    }

    private fun initView() {
        fieldsFlashAdapter = FieldsFlashAdapter(
            this@FlashCardActivity,
            object : SwipeStackListener {
                override fun onSwipeEnd(position: Int) {
                    next()
                }
            }, form!!, viewModel
        )

        binding.flashcardFieldsRec.apply {
            adapter = fieldsFlashAdapter
            layoutManager =
                object : LinearLayoutManager(context, RecyclerView.HORIZONTAL, false) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }

                    override fun canScrollHorizontally(): Boolean {
                        return false
                    }

                }

        }

        fieldsFlashAdapter?.collection = fields
        updateTheme(form)

        checkLessonProgress()

    }

    private fun initData() {
        shardedVM.saveLastLesson(form?.slug ?: "")
        viewModel.initFormSlug(form?.slug ?: "")
        viewModel.getSubmitEntity()
    }


    override fun next() {
        with(binding.flashcardFieldsRec) {
            val visibleItemPosition =
                (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()

            updateLessonProgress(visibleItemPosition+1)

            if (fields.size > visibleItemPosition + 1) {
                Handler(Looper.getMainLooper()).postDelayed({
                    scrollToPosition(visibleItemPosition + 1)
                }, 150)

            } else {
                openCongView()
            }

            binding.flashPreBtn.visible()

            binding.progress = visibleItemPosition + 1

        }

    }

    override fun pre() {
        with(binding.flashcardFieldsRec) {
            val visibleItemPosition =
                (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()


            updateLessonProgress(visibleItemPosition-1)

            if (0 <= visibleItemPosition - 1) {
                scrollToPosition(visibleItemPosition - 1)

            }

            if (visibleItemPosition==1){
                binding.flashPreBtn.invisible()
            }

            binding.flashNextBtn.visible()
            binding.progress = visibleItemPosition
        }
    }

    override fun closePage() {
        onBackPressed()

    }

    override fun share() {
        val shareTxt =
            "I'm learning ${form?.title ?: ""} in Learning App. Check it out on your phone: ${
                getString(R.string.appAddress)
            }"
        baseMethod.shareVia(shareTxt, getString(R.string.app_name), this)

    }

    private fun openCongView() {
        updateLessonProgress(0)
        shardedVM.saveLastLesson("")

        callWorker()
        binding.flashCongView.visible()

    }

    private fun updateLessonProgress(pos: Int) {
        lessonsProgressMap[form?.slug] = pos
        shardedVM.saveLessonProgress(lessonsProgressMap)

    }

    private fun checkLessonProgress() {
        lessonsProgressMap = shardedVM.retrieveLessonProgress()

        val formSlug = form?.slug ?: ""
        val progress = lessonsProgressMap[form?.slug ?: ""]

        if (progress == null || progress==0) {
            lessonsProgressMap[formSlug] = 0
            shardedVM.saveLessonProgress(lessonsProgressMap)
        } else {
            binding.flashPreBtn.visible()
            binding.flashcardFieldsRec.scrollToPosition(progress +1)
        }

        if (progress?:0==0){
            binding.flashPreBtn.invisible()

        }

        binding.progress = (progress ?: 0)
        binding.executePendingBindings()

    }

}