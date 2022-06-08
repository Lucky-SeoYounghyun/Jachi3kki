package com.c4project.Jachi3kki.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.c4project.Jachi3kki.*
import com.c4project.Jachi3kki.Class.Recipe
import com.c4project.Jachi3kki.OuterDB.recipeInfo
import com.c4project.Jachi3kki.activity.MainActivity
import com.c4project.Jachi3kki.activity.PagerActivity
import com.c4project.Jachi3kki.log.L
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.bookmark_list_item.*

class ExtensionRecipeAdapter(
    val items: ArrayList<Recipe>,
    val context: Context
) : RecyclerView.Adapter<ExtensionRecipeAdapter.RecipeViewHolder>() {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.bookmark_list_item,
            parent,
            false
        )

        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(items[position], context, position)
        holder.itemView.setOnClickListener() {
            L.i("조회수")

            // 매개변수 전달
            var recipeNum = recipeInfo.RECIPELIST.indexOf(items[position])

            // 뷰페이저 연결 엑티비티 전환
            val intent = Intent(MainActivity.instance, PagerActivity::class.java)

            //조회수 증가
            val recipeName = items[position].name   //증가하려는 레시피의 이름
            items[position].viewCnt += 1        //증가하려는 레시피의 총 조회수. +1시켜줘야 하므로 +1된 값을 저장
            items[position].todayView += 1      //오늘자 조회수도 +1 시켜줘야하므로 +1된 오늘자 조회수 값을 저장
            val viewCnt = (items[position].viewCnt).toString()
            val todayView = (items[position].todayView).toString()
            var task = MainFragmentAdapter.IncreaseView() //조회수 증가를 위한 클래스

            //해당 클래스에 파라미터(url, 레시피이름, 총 조회수, 오늘자 조회수)를 입력하여 조회수 증가 실행
            task.execute("http://118.67.132.138/increaseView.php", recipeName, viewCnt, todayView)

            intent.putExtra("recipeNum", recipeNum) //넘겨줄 데이터(intent)에 해당 레시피의 인덱스를 저장
            MainActivity.instance.startActivity(intent) //메인 액티비티에서 뷰페이저 액티비티로 연결
        }

        holder.img_bookmark_icon.setOnClickListener {
            L.i("북마크")

            if (MainActivity.id != null) {  //이건 로그인 되어있을 때 외부DB에 북마크 추가하는 코드.
                if (recipeInfo.BOOKMARKLIST.contains(items[position])) {//이미 추가되어있다면 반대로 북마크 제거
                    val userId = MainActivity.id    //북마크를 제거하려는 유저의 아이디
                    val recipeName = items[position].name   //북마크를 제거하려는 레시피 이름
                    items[position].likeCnt -= 1    //북마크에서 제거해야하므로 -1시킴
                    val likeCnt = (items[position].likeCnt).toString()
                    var task = MainFragmentAdapter.DeleteData() //북마크 제거용 클래스

                    //해당 클래스에 파라미터(url, 레시피이름, 총 조회수, 오늘자 조회수)를 입력하여 조회수 증가 실행
                    task.execute("http://118.67.132.138/deleteDb.php", userId, recipeName, likeCnt)
                    //코드 상에도 결과를 반영해줌
                    recipeInfo.BOOKMARKLIST.remove(items[position]) //새로고침인 것처럼 코드에서 제거

                    // 북마크 체크 제거 및 토스트 메세지
                    holder.img_bookmark_icon.setImageResource(R.drawable.icon_bookmark)
                    Toast.makeText(context, "북마크에서 제거되었습니다", Toast.LENGTH_SHORT).show()

                }
                else {
                    //회원이면서 북마크 추가되지않은 레시피면 외부 db에 북마크 추가하고 추천수 증가
                    val userId = MainActivity.id    //북마크를 추가하려는 유저의 아이디
                    var recipeName = items[position].name   //북마크를 추가하려는 레시피 이름
                    items[position].likeCnt += 1    //북마크를 추가하는 것이므로 +1 시켜줌
                    val likeCnt = (items[position].likeCnt).toString()
                    val task = MainFragmentAdapter.InsertData() //북마크 추가용 클래스

                    //해당 클래스에 파라미터(url, 레시피이름, 총 조회수, 오늘자 조회수)를 입력하여 조회수 증가 실행
                    task.execute("http://118.67.132.138/insertDb.php", userId, recipeName, likeCnt)
                    //코드 상에도 결과를 반영해줌
                    recipeInfo.BOOKMARKLIST.add(items[position])

                    // 북마크 체크 제거 및 토스트 메세지
                    holder.img_bookmark_icon.setImageResource(R.drawable.icon_bookmark_check)
                    Toast.makeText(context, "북마크에 추가되었습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "로그인이 필요합니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class RecipeViewHolder(
        override val containerView: View
    ) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(recipe: Recipe, context: Context, position: Int) {
            Glide.with(context).load(recipe.img_src).into(img_bookmark_picture)
            tv_bookmark_title.text = recipe.name
            tv_bookmark_content.text = recipe.content + recipe.category

            bookmark_tv_likeCnt.text = "${recipe.likeCnt}"
            bookmark_tv_viewCnt.text = "${recipe.viewCnt} "

            if (MainActivity.id != null) {
                if (recipeInfo.BOOKMARKLIST.contains(items[position])) {
                    img_bookmark_icon.setImageResource(R.drawable.icon_bookmark_check)
                } else {
                    img_bookmark_icon.setImageResource(R.drawable.icon_bookmark)
                }
            }

        }

    }
}
