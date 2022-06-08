package com.c4project.Jachi3kki.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.c4project.Jachi3kki.Class.CategoryListItem
import com.c4project.Jachi3kki.databinding.CategorySelectedListItemBinding

// 비타민 혹은 재료를 선택 한 후 추가되는 리스트. 한 번 더 누를 시 삭제됨
abstract class SelectedListAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    abstract fun onItemClick(index: Int, data: CategoryListItem)

    var items = mutableListOf<CategoryListItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CategotySelectedListViewHolder(
            CategorySelectedListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    // 삭제및 추가
    fun replaceAll(items: List<CategoryListItem>?) {
        items?.let {
            this.items.run {
                clear()
                addAll(it)
                notifyDataSetChanged()
            }
        }
    }

    fun removeItem(position: Int) {
        if (position < this.items.size) {
            items.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, items.size)
        }
    }
    //추가된 아이템들을 카운트
    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CategotySelectedListViewHolder) {
            val title = items[position].data
            holder.binding.tvSelectedTitle.text = title
            holder.itemView.setOnClickListener {
                try {
                    onItemClick(position, items[position])
                } catch (e: IndexOutOfBoundsException) {
                    print("IndexOutOfBoundsException 오류 발생")
                }
            }
        }
    }

    inner class CategotySelectedListViewHolder(val binding: CategorySelectedListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}