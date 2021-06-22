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
import co.idearun.learningapp.feature.flashCard.FlashcardListener
import co.idearun.learningapp.feature.flashCard.ViewsListener
import co.idearun.learningapp.feature.viewmodel.UIViewModel
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

class FlashCardMultiHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = LayoutFlashCardMultiItemBinding.bind(view)
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
                )

            }
        }

    }

    private fun createNewChoice(
        choice: ChoiceItem,
        field: Fields,
        choicesListLay: LinearLayout,
        form: Form
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
                form,
            )
        (ll as ViewGroup).addView(boxLay)

        (binding.choicesListLay as ViewGroup).addView(ll)

    }

    private fun createCheckBox(
        choice: ChoiceItem,
        item: Fields,
        ll: LinearLayout,
        form: Form
    ): View {

        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.bottomMargin = 20

        val checkBox = CheckBox(ll.context)
        checkBox.layoutParams = layoutParams

        choice.title?.let {
            checkBox.text = it
        }
        choice.image?.let {
            setImage(it, checkBox)
        }

        val context = checkBox.context
        checkBox.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            context.resources.getDimension(co.idearun.learningapp.R.dimen.font_large)
        )

        Binding.getHexColor(form.text_color)?.let {
            checkBox.setTextColor(Color.parseColor(it))

            val colorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(-R.attr.state_enabled),
                    intArrayOf(R.attr.state_enabled)
                ), intArrayOf(
                    Color.parseColor(it) //disabled
                    , Color.parseColor(it) //enabled
                )
            )
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                checkBox.buttonTintList = colorStateList
            }
        }


        checkBox.setOnCheckedChangeListener { compoundButton, b ->

            choice.slug?.let { choiceSlug ->
                if (b) {
//                    valuesList.add(choiceSlug)

                } else {
//                    if (valuesList.contains(choiceSlug)) {
//                        valuesList.remove(choiceSlug)

//                    } else {
//
//                    }
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