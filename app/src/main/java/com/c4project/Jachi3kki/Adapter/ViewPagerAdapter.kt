package com.c4project.Jachi3kki.Adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.ScrollingMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.c4project.Jachi3kki.R
import com.c4project.Jachi3kki.OuterDB.recipeInfo
import kotlinx.android.synthetic.main.viewpager_content.view.*
import java.io.FileNotFoundException


class ViewPagerAdapter(
    recipeNum: Int
) :
    RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder>() {

    var recipeId: Int = recipeNum
    var id: Int = 0    //레시피 아이디
    lateinit var name: String   //레시피 이름
    lateinit var content: String    //레시피 조리 방법(변수명 바꾸고싶은데 바꾸면 다른 것도 바꿔야되므로 안바꿈)
    lateinit var category: String   //음식 종류(밥, 반찬 등등)
    lateinit var img_src: String   //음식 이미지
    lateinit var ingredients: String    //재료들 한줄로 나열
    lateinit var ingredientArr: ArrayList<String>//재료 리스트
    lateinit var manualList: ArrayList<String>  //조리 방법 메뉴얼
    lateinit var imgList: ArrayList<String>  //조리 단계별 이미지
    var likeCnt: Int = 0  //좋아요 수
    var viewCnt: Int = 0    //조회수

    fun setData(recipeId: Int) {
        //외부에서 읽어온 레시피 데이터인 recipelist 중, 매개변수로 주어진 recipeId에 해당하는 인덱스 값을 가진 레시피를 읽어옴
        var recipe = recipeInfo.RECIPELIST[recipeId]
        name = recipe.name
        content = recipe.content
        category = recipe.category
        img_src = recipe.img_src
        ingredients = recipe.ingredients
        ingredientArr = recipe.ingredientArr
        manualList = recipe.manualList
        imgList = recipe.imgList
        likeCnt = recipe.likeCnt
        viewCnt = recipe.viewCnt
    }

    inner class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val pageNameView: TextView = itemView.pageName
        private val nameView: TextView = itemView.name
        private val recipeView: TextView = itemView.recipe
        private val contentView: TextView = itemView.content
        private val contentImageView: ImageView = itemView.contentImage

        @SuppressLint("SetTextI18n")
        fun bind(content: String, imageUrl: String, position: Int) {
            if (position == 0) {
                pageNameView.text = "${position + 1} 페이지"
                contentView.text = "" //레시피 단계 설명 부분.
                nameView.text = name   //레시피 이름
                recipeView.setMovementMethod(ScrollingMovementMethod())

                val spannable = SpannableStringBuilder("재료\n\n" + ingredients)
                spannable.setSpan(
                    RelativeSizeSpan(1.6f),
                    0, // start
                    2, // end
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )
                spannable.setSpan(
                    ForegroundColorSpan(Color.rgb(250, 205, 102)),
                    0, // start
                    2, // end
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )

                recipeView.text = spannable    //레시피에 들어가는 재료

                try {
                    if (img_src.equals("no_image"))//만약 이미지 경로가 no_image인 경우 해당 단계의 이미지가 없는 것이므로 대체 이미지를 설정
                        contentImageView.setImageResource(R.drawable.no_image)
                    else
                        Glide.with(itemView.context).load(img_src).into(contentImageView)
                    //glide를 이용하여 외부 이미지(경로는 imageUrl)를 화면으로 가져옴
                } catch (e: FileNotFoundException) {
                    false
                }
            }
            else {
                pageNameView.text = "${position + 1} 페이지"
                contentView.text = content  //레시피 단계 들어감
                nameView.text = ""    //레시피 이름부분. 첫번째 페이지 아니라 그냥 없앰
                recipeView.text = "" //레시피 들어가는 재료 부분. 첫번째 페이지 아니라 그냥 없앰

                try {
                    if (imageUrl.equals("no_image"))//만약 이미지 경로가 no_image인 경우 해당 단계의 이미지가 없는 것이므로 대체 이미지를 설정
                        contentImageView.setImageResource(R.drawable.no_image)
                    else
                        Glide.with(itemView.context).load(imageUrl).into(contentImageView)
                        //glide를 이용하여 외부 이미지(경로는 imageUrl)를 화면으로 가져옴
               } catch (e: FileNotFoundException) {
                    false
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.viewpager_content,
            parent,
            false
        )

        return PagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        val pos = position

        setData(recipeId)
        if (pos == 0) {//첫 번째 페이지에 해당하는 경우
            holder.bind(
                manualList[pos],
                imgList[pos],
                pos
            )
        } else {    //첫 번째 페이지가 아닐 경우
            holder.bind(
                manualList[pos - 1],//인덱스를 맞추기 위해 -1 적용
                imgList[pos - 1],//인덱스를 맞추기 위해 -1 적용
                pos
            )
        }

    }
    //아이템 가져오기 레시피 사이즈 추가

    override fun getItemCount(): Int {
        setData(recipeId)
        return manualList.size + 1
    }
}
