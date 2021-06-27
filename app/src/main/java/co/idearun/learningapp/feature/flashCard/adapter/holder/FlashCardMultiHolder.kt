package co.idearun.learningapp.feature.flashCard.adapter.holder

import android.R
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import co.idearun.learningapp.data.model.form.ChoiceItem
import co.idearun.learningapp.data.model.form.Fields
import co.idearun.learningapp.data.model.form.Form
import co.idearun.learningapp.databinding.LayoutFlashCardMultiItemBinding
import co.idearun.learningapp.feature.Binding
import co.idearun.learningapp.feature.flashCard.ViewsListener
import co.idearun.learningapp.feature.viewmodel.UIViewModel
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import org.apache.commons.lang3.StringUtils

class FlashCardMultiHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = LayoutFlashCardMultiItemBinding.bind(view)
    private var valuesList = arrayListOf<String>()

    fun bindItems(
        item: Fields,
        pos: Int,
        listener: ViewsListener,
        form: Form,
        uiViewModel: UIViewModel
    ) {
        binding.field = item
        binding.form = form
        binding.listener = listener
        binding.fieldUiHeader.field = item
        binding.fieldUiHeader.form = form
        binding.fieldUiFooter.viewmodel = uiViewModel
        binding.fieldUiFooter.field = item
        binding.viewmodel = uiViewModel
        binding.lifecycleOwner = binding.choicesListLay.context as LifecycleOwner


        if (item.required == true) {
            uiViewModel.reuiredField(item)

        } else {

        }

        item.choice_items?.let { choiceList ->
            for (choice in choiceList) {
                createNewChoice(
                    choice,
                    item,
                    binding.choicesListLay,
                    form
                ,uiViewModel
                )

            }
        }

    }

    private fun createNewChoice(
        choice: ChoiceItem,
        field: Fields,
        choicesListLay: LinearLayout,
        form: Form,
        uiViewModel: UIViewModel
    ) {
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.bottomMargin = 24

        val ll = LinearLayout(choicesListLay.context)
        ll.layoutParams = layoutParams
        ll.orientation = LinearLayout.HORIZONTAL
        val boxLay =
            createCheckBox(
                choice,
                field,
                choicesListLay,
                form,uiViewModel
            )
        (ll as ViewGroup).addView(boxLay)

        (binding.choicesListLay as ViewGroup).addView(ll)

    }

    private fun createCheckBox(
        choice: ChoiceItem,
        item: Fields,
        ll: LinearLayout,
        form: Form,
        uiViewModel: UIViewModel
    ): View {

        val lp = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        lp.bottomMargin = 48

        val checkBox = CheckBox(ll.context)
        checkBox.apply {
            layoutParams = lp
            setPadding(48, 48, 48, 48)
            minLines=2
            setButtonDrawable(R.color.transparent);

            choice.title?.let {
                text = it
            }
            choice.image?.let {
                setImage(it, this)
            }

            val context = context
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                context.resources.getDimension(co.idearun.learningapp.R.dimen.font_xlarge)
            )

            Binding.fieldBackground(this, form, false)
            Binding.setTextColor(this,form.text_color)

            setOnCheckedChangeListener { compoundButton, b ->
                if (b){
                    Binding.selectedFieldBackground(this, form, false)
                    Binding.setSelectedTextColor(this,form)

                }else{
                    Binding.fieldBackground(this, form, false)
                    Binding.setTextColor(this,form.text_color)

                }
                choice.slug?.let { choiceSlug ->
                    if (b) {
                        valuesList.add(choiceSlug)

                    } else {
                        if (valuesList.contains(choiceSlug)) {
                            valuesList.remove(choiceSlug)

                        } else {

                        }
                    }

                    uiViewModel.addKeyValueToReq(item.slug!!, StringUtils.join(valuesList, ","))

                }

            }
        }


        return checkBox
    }

    private fun setImage(it: String, box: CheckBox) {
        Picasso.get().load(it).into(object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                bitmap?.let {
                    val padding =
                        box.resources.getDimensionPixelSize(co.idearun.learningapp.R.dimen.padding_2xsmall)
                    val height = box.resources.getDimensionPixelSize(co.idearun.learningapp.R.dimen.btn_h)

                    val drawable = BitmapDrawable(
                        box.resources,
                        Bitmap.createScaledBitmap(bitmap, 150, height, false)
                    )
                    box.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

                    box.compoundDrawablePadding = padding

                }
            }

            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                Log.e("TAG", "onBitmapFailed: $e")
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }

        })

    }
}