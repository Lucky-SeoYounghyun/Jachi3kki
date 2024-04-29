package com.c4project.Jachi3kki.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.c4project.Jachi3kki.Class.select_list
import com.c4project.Jachi3kki.InternalDB.FridgeIngredient
import com.c4project.Jachi3kki.R
import kotlinx.android.synthetic.main.refrigerator.view.*


class RefrigeratorAdapter(
    internal var context: Context,
    private var itemList: MutableList<FridgeIngredient>
) :
    RecyclerView.Adapter<RefrigeratorAdapter.RefrigeratorViewHolder>() {

    val selectlist = select_list()

    private var onItemSelectedListener: ((MutableList<Long>) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RefrigeratorViewHolder {
        val itemView = LayoutInflater.from(context)
            .inflate(R.layout.refrigerator, parent, false)


        //체크 상태(클릭)
        itemView.setOnClickListener { v ->
            val id = v?.tag
            if (selectlist.selectList.contains(id)) {
                selectlist.select_remove(id as Long)
            } else {
                selectlist.select_add(id as Long)
            }
            notifyDataSetChanged()
            onItemSelectedListener?.let { it(selectlist.selectList) }
        }


        return RefrigeratorViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    //식재료에 해당하는 이미지와 이름을 화면에 띄움
    override fun onBindViewHolder(holder: RefrigeratorViewHolder, position: Int) {
        val imageUrl = "http://118.67.132.138" + itemList[position].img_src
        //glide를 이용하여 imageUrl를 경로로 가진 이미지를 화면으로 가져옴
        Glide.with(context).load(imageUrl).into(holder.txt_cart_img)
        holder.txt_cart_name.text = itemList[position].name
        holder.itemView2.tag = getItemId(position)
        holder.itemView2.isActivated = selectlist.selectList.contains(getItemId(position))  //선택 여부 표시
    }

    inner class RefrigeratorViewHolder(var itemView2: View) : RecyclerView.ViewHolder(itemView2) {

        var txt_cart_name: TextView
        var txt_cart_img: ImageView

        init {
            txt_cart_img = itemView2.cart_image
            txt_cart_name = itemView2.cart_item_name;
        }
    }

}