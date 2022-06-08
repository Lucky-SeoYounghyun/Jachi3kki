package com.c4project.Jachi3kki.Adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.c4project.Jachi3kki.Class.CategoryListItem
import com.c4project.Jachi3kki.databinding.CategoryGroupListItemBinding

// 비타민 혹은 재료의 선택할 수 있는 리스트를 보여주는 Adapter. 재료의 경우 대 중 소 리스트로 3개의 ClassListAdapter가 필요함.
abstract class ClassListAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    abstract fun onItemClick(index: Int, data: CategoryListItem)

    var items = mutableListOf<CategoryListItem>()

    // 만들어진 View가 없는 경우 xml 파일을 inflate해서 ViewHolder 생성
    // ViewHolder는 뷰를 보관하는 객체, 여러개 보관가능
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CategoryGroupListItemViewHolder(
            CategoryGroupListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    fun clearItems() {
        items.clear()
        notifyDataSetChanged()
    }

    fun replaceAll(items: List<CategoryListItem>?) {
        items?.let {
            this.items.run {
                clear()
                addAll(it)
                notifyDataSetChanged()
            }
        }
    }

    fun getItemList(): List<CategoryListItem> {
        return items
    }

    override fun getItemCount(): Int = items.size

    // 만들어진 뷰와 실제 입력되는 각각의 데이터를 연결한다.
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CategoryGroupListItemViewHolder) {
            val title = items[position].data
            val isSelected = items[position].selected
            holder.binding.root.text = title

            //클릭시 텍스트뷰의 배경과 글씨색상을 변경한다. isSelected true 시 클릭
            if (isSelected) {
                holder.binding.root.setBackgroundColor(Color.parseColor("#e8b943"))
                holder.binding.root.setTextColor(Color.parseColor("#fed966"))
            } else {
                holder.binding.root.setBackgroundColor(Color.parseColor("#ffffff"))
                holder.binding.root.setTextColor(Color.parseColor("#252525"))
            }
            holder.itemView.setOnClickListener {
                onItemClick(position, items[position])
            }
        }
    }

    // Data binding
    // layout 이름을 category_group_list_item으로 정의했다면 DataBinding에 의해 생성된 데이터는 CategoryGroupListItemBinding.java 이다.
    // 실제 DataBinding 해야 할 객체는 CategoryGroupListItemBinding을 초기화 해야한다. root(view)를 뷰 홀더에 넘겨주자
    // https://thdev.tech/androiddev/2020/05/25/Android-RecyclerView-Adapter-Use-DataBinding/
    inner class CategoryGroupListItemViewHolder(val binding: CategoryGroupListItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}