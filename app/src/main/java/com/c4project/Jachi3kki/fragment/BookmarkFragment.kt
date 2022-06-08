package com.c4project.Jachi3kki.fragment

import VerticalItemDecorator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.c4project.Jachi3kki.*
import com.c4project.Jachi3kki.Adapter.MainFragmentAdapter
import com.c4project.Jachi3kki.Class.Recipe
import com.c4project.Jachi3kki.Class.SelectedListItem
import com.c4project.Jachi3kki.OuterDB.recipeInfo
import com.c4project.Jachi3kki.activity.MainActivity
import com.c4project.Jachi3kki.activity.PagerActivity
import com.c4project.Jachi3kki.databinding.FragmentBookmarkBinding
import com.c4project.Jachi3kki.log.L
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bookmark_list_item.*
import kotlinx.android.synthetic.main.fragment_bookmark.*

class BookmarkFragment : Fragment() {

    var bookmarkList = mutableListOf<Recipe>()
    private lateinit var binding: FragmentBookmarkBinding
    lateinit var navController: NavController
    private lateinit var BookmarkCategoryAdapter: BookmarkAdapter
    private val selectedAlignItem by lazy { arguments?.getParcelableArrayList<SelectedListItem>("alignmentItem") }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bookmarkList = recipeInfo.BOOKMARKLIST
        binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = nav_host_fragment.findNavController()

        // 조회된 레시피 수 출력
        tv_count.text = "${bookmarkList.count()}건"

        // 북마크에 추가된 레시피가 없을 경우 레시피가 없다는 사진 보여줌
        if (bookmarkList.count() == 0) {
            background_bookmark?.visibility = View.VISIBLE
        }
        if (bookmarkList.count() != 0) {
            background_bookmark?.visibility = View.GONE
            if (!selectedAlignItem.isNullOrEmpty()) {
                bookmarkList = sortedRecipe()
            }
        }

        // recyclerView에 layout Manger 설정
        rv_data_list.layoutManager = LinearLayoutManager(
            activity,
            LinearLayoutManager.VERTICAL,
            false
        )

        // 리사이클러 뷰 출력을 위한 adapter 설정
        BookmarkCategoryAdapter = BookmarkAdapter(requireContext())

        binding.rvDataList.run {
            setHasFixedSize(true)
            adapter = BookmarkCategoryAdapter
        }

        BookmarkCategoryAdapter.replaceAll(bookmarkList)

        // 안전성을 위해 사이즈 고정
        rv_data_list.setHasFixedSize(true)

        // 아이템간의 구분선 추가
        activity?.let { VerticalItemDecorator(it, R.drawable.horizontal_line_divider, 0, 0) }?.let {
            rv_data_list.addItemDecoration(
                it
            )
        }

        // 정렬버튼
        condition.setOnClickListener {
            val fromInt = 1
            navController.navigate(
                R.id.action_bookmarkFragment_to_alignmentFragment,
                bundleOf("fromInt" to fromInt)
            )
        }
    }

    private open inner class BookmarkAdapter(private val context: Context) :
        RecyclerView.Adapter<BookmarkAdapter.RecipeViewHolder>() {

        var items = mutableListOf<Recipe>()

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): BookmarkAdapter.RecipeViewHolder {
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
                val recipeNum = recipeInfo.RECIPELIST.indexOf(items[position])
                val intent = Intent(MainActivity.instance, PagerActivity::class.java)   //여기서 뷰페이저 연결하는 거 같은데

                //조회수 증가
                val recipeName = items[position].name
                items[position].viewCnt += 1
                items[position].todayView += 1
                val viewCnt = (items[position].viewCnt).toString()
                val todayView = (items[position].todayView).toString()
                var task = MainFragmentAdapter.IncreaseView();
                task.execute("http://118.67.132.138/increaseView.php", recipeName, viewCnt, todayView)

                intent.putExtra("recipeNum", recipeNum)
                MainActivity.instance.startActivity(intent)
            }

            holder.img_bookmark_icon.setOnClickListener {
                L.i("북마크")

                if (MainActivity.id != null) {
                    if (recipeInfo.BOOKMARKLIST.contains(items[position])) {//이미 추가되어있다면 반대로 북마크 제거
                        val userId = MainActivity.id
                        val recipeName = items[position].name
                        items[position].likeCnt -= 1
                        val likeCnt = (items[position].likeCnt).toString()
                        val task = MainFragmentAdapter.DeleteData()
                        task.execute(
                            "http://118.67.132.138/deleteDb.php",
                            userId,
                            recipeName,
                            likeCnt
                        )
                        recipeInfo.BOOKMARKLIST.remove(items[position]) //얘 안해주면 메인이나 다른 레시피 목록에서 북마크리스트 적용안됨

                        holder.img_bookmark_icon.setImageResource(R.drawable.icon_bookmark)
                        Toast.makeText(context, "북마크에서 제거되었습니다", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "로그인이 필요합니다", Toast.LENGTH_SHORT).show()
                }
                removeItem(position)
            }


        }


        fun removeItem(position: Int) {
            if (position < this.items.size) {
                items.removeAt(position) //이미 위에서 해당 레시피 제거해서, 여기서 또 하면 인덱스 오류남
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, items.size)
                //bookmarkList.removeAt(position) 얘도 제거하면 인덱스 오류남. 둘 다 recipeInfo.BOOKMARKLIST의 레퍼런스 주소로 값을 공유하기 때문

                if (bookmarkList.count() == 0) {
                    background_bookmark?.visibility = View.VISIBLE
                }
                binding.tvCount.text = "${items.count()}건"

            }
        }

        fun replaceAll(items: List<Recipe>?) {
            items?.let {
                this.items.run {
                    clear()
                    addAll(it)  //여기서 items에 BookmarkFragment의 bookmarkList를 모두 추가함
                    notifyDataSetChanged()
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
                tv_bookmark_content.text = recipe.content

                if (MainActivity.id != null) {
                    img_bookmark_icon.setImageResource(R.drawable.icon_bookmark_check)
                }
                bookmark_tv_likeCnt.text = "${recipe.likeCnt}"
                bookmark_tv_viewCnt.text = "${recipe.viewCnt}"

            }
        }
    }

    private fun sortedRecipe(): MutableList<Recipe> {
        val sortedRecipe = bookmarkList as ArrayList<Recipe>
        if (!selectedAlignItem.isNullOrEmpty()) {
            val likeCnt = selectedAlignItem!![0].data
            val viewCnt = selectedAlignItem!![1].data
            if (likeCnt == "선택안함" && viewCnt == "선택안함") {
            } else if (likeCnt == "추천높은순" && viewCnt == "조회수높은순") {
                sortedRecipe.sortWith(compareBy({ -it.likeCnt }, { -it.viewCnt }))
            } else if (likeCnt == "추천낮은순" && viewCnt == "조회수낮은순") {
                sortedRecipe.sortWith(compareBy({ it.likeCnt }, { it.viewCnt }))
            } else if (likeCnt == "추천높은순" && viewCnt == "조회수낮은순") {
                sortedRecipe.sortWith(compareBy({ -it.likeCnt }, { it.viewCnt }))
            } else if (likeCnt == "추천낮은순" && viewCnt == "조회수높은순") {
                sortedRecipe.sortWith(compareBy({ it.likeCnt }, { -it.viewCnt }))
            } else if (likeCnt == "선택안함" && viewCnt == "조회수높은순") {
                sortedRecipe.sortWith(compareBy { -it.viewCnt })
            } else if (likeCnt == "선택안함" && viewCnt == "조회수낮은순") {
                sortedRecipe.sortWith(compareBy { it.viewCnt })
            } else if (likeCnt == "추천높은순" && viewCnt == "선택안함") {
                sortedRecipe.sortWith(compareBy { -it.likeCnt })
            } else if (likeCnt == "추천낮은순" && viewCnt == "선택안함") {
                sortedRecipe.sortWith(compareBy { it.likeCnt })
            }
        }
        return sortedRecipe as MutableList<Recipe>
    }

}

