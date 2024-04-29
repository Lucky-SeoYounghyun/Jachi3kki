package com.c4project.Jachi3kki.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.c4project.Jachi3kki.R
import com.c4project.Jachi3kki.fragment.MainFragment
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.vitamin_list_item.*

//메인 비타민 어뎁터
class MainVitaminAdapter(
    val items: ArrayList<MainFragment.MainVitamin>,
    val context: Context,
    val itemSelect: (MainFragment.MainVitamin) -> Unit
) : RecyclerView.Adapter<MainVitaminAdapter.MainVitaminViewHolder>() {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MainVitaminViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.vitamin_list_item,
            parent,
            false
        )
        return MainVitaminViewHolder(view, itemSelect)
    }

    override fun onBindViewHolder(holder: MainVitaminViewHolder, position: Int) {
        holder.bind(items[position], context, position)

    }

    inner class MainVitaminViewHolder(
        override val containerView: View,
        itemSelect: (MainFragment.MainVitamin) -> Unit
    ) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(vitamin: MainFragment.MainVitamin, context: Context, position: Int) {
            //Glide.with(context).load(vitamin.img_src).into(img_vitamin_icon)
            if (vitamin.img_src != "") {
                val resourceId = context.resources.getIdentifier(
                    vitamin.img_src,
                    "drawable",
                    context.packageName
                )
                img_vitamin_icon?.setImageResource(resourceId)
            } else {
                img_vitamin_icon?.setImageResource(R.mipmap.ic_launcher)
            }
            tv_vitamin_name.text = vitamin.name
            itemView.setOnClickListener() { itemSelect(vitamin) }
        }
    }

}
