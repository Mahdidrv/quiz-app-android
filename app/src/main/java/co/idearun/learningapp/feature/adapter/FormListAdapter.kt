package co.idearun.learningapp.feature.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import co.idearun.learningapp.R
import co.idearun.learningapp.data.model.form.Form
import co.idearun.learningapp.databinding.LessonContentBinding
import org.koin.core.KoinComponent
import java.io.Serializable


class FormListAdapter(private val listener: FormListListener) :
    PagingDataAdapter<Form, FormListAdapter.BtnsViewHolder>(DiffUtilCallBack()),
    Serializable {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): BtnsViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.lesson_content, parent, false)
        return BtnsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BtnsViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            holder.bindItems(item, listener)
        }

    }

    class BtnsViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView), Serializable, KoinComponent {
        val binding = LessonContentBinding.bind(itemView)

        fun bindItems(form: Form?, listener: FormListListener) {
            binding.item = form

            itemView.setOnClickListener {
                listener.openForm(form, binding.formItemLay)

            }
        }

    }

}


