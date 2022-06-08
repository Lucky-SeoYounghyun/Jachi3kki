package com.c4project.Jachi3kki.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.c4project.Jachi3kki.Adapter.ExtensionRecipeAdapter
import com.c4project.Jachi3kki.Adapter.SelectedListAdapter
import com.c4project.Jachi3kki.Class.CategoryListItem
import com.c4project.Jachi3kki.Class.Recipe
import com.c4project.Jachi3kki.Class.SelectedListItem
import com.c4project.Jachi3kki.log.L
import com.c4project.Jachi3kki.R
import com.c4project.Jachi3kki.OuterDB.recipeInfo
import kotlinx.android.synthetic.main.fragment_main.rv_data_list
import kotlinx.android.synthetic.main.fragment_recipe.*

class RecipeFragment : Fragment() {
    lateinit var recipeList: ArrayList<Recipe>
    lateinit var navController: NavController
    var selectedMenuItems = mutableSetOf<String>()
    private lateinit var selectedAdapter: SelectedListAdapter
    private val selectedKeyword by lazy { arguments?.getString("keyword") }
    private val selectedMenuItem by lazy { arguments?.getParcelableArrayList<SelectedListItem>("item") }
    private val selectedAlignItem by lazy { arguments?.getParcelableArrayList<SelectedListItem>("alignmentItem") }
    private val selectedDetailItem1 by lazy { arguments?.getParcelableArrayList<SelectedListItem>("detailItem1") }
    private val selectedDetailItem2 by lazy { arguments?.getParcelableArrayList<SelectedListItem>("detailItem2") }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        L.i("::::intent " + selectedMenuItem)

        // 레시피 검색
        if(!selectedKeyword.isNullOrEmpty()){
            recipeList = searchedRecipe(selectedKeyword) 
        } else{
            // 선택된 재료로 레시피 검색, 편의를 위한 데이터 형변환
            selectedMenuItem?.forEach {
                selectedMenuItems.add(it.data)
            }
            // DB에 레시피 데이터가 있어야됨
            recipeList = findRecipe(selectedMenuItems.toMutableList() as ArrayList<String>)
        }


        // 필터 조건 반영
        if (!selectedDetailItem1.isNullOrEmpty()) {
            recipeList = filteredRecipe()
        }


        // 정렬 조건 반영
        if (!selectedAlignItem.isNullOrEmpty()) {
            recipeList = sortedRecipe()
        }

        // 조회된 레시피 수 출력
        rc_count.text = "${recipeList.count()}건"

        // 상세조건 버튼클릭
        btn_detail.setOnClickListener {
            val selectedDataSet by lazy {
                ArrayList<SelectedListItem>().also { list ->
                    selectedMenuItems.forEach {
                        list.add(SelectedListItem(it))
                    }
                }
            }
            navController.navigate(
                R.id.action_recipeFragment_to_detailFragment,
                bundleOf(
                    "item" to selectedDataSet,
                    "alignmentItem" to selectedAlignItem,
                    "detailItem1" to selectedDetailItem1,
                    "detailItem2" to selectedDetailItem2,
                    "keyword" to selectedKeyword
                )
            )
        }

        // 정렬조건 버튼클릭
        btn_alignment.setOnClickListener {
            val selectedDataSet by lazy {
                ArrayList<SelectedListItem>().also { list ->
                    selectedMenuItems.forEach {
                        list.add(SelectedListItem(it))
                    }
                }
            }
            val fromInt = 0
            navController.navigate(
                R.id.action_recipeFragment_to_alignmentFragment,
                bundleOf(
                    "item" to selectedDataSet,
                    "detailItem1" to selectedDetailItem1,
                    "detailItem2" to selectedDetailItem2,
                    "fromInt" to fromInt,
                    "keyword" to selectedKeyword
                )
            )
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // recyclerView에 layout Manger 설정
        rv_data_list.layoutManager = LinearLayoutManager(
            activity,
            LinearLayoutManager.VERTICAL,
            false
        )

        // 안전성을 위해 사이즈 고정
        rv_data_list.setHasFixedSize(true)

        rv_data_list.adapter = ExtensionRecipeAdapter(
            recipeList,
            requireContext()
        )

        // 셀렉티드 업덥터
        selectedAdapter = object : SelectedListAdapter(requireContext()) {
            override fun onItemClick(index: Int, item: CategoryListItem) {
                //선택한 리스트뷰 클릭시 떨어지는 이벤트트
                L.i(":::::::::::::::선택한 선택한 카테고리 " + item.data)
                selectedAdapter.removeItem(index)
                selectedMenuItems.remove(item.data)
                recipeList = findRecipe(selectedMenuItems.toMutableList() as ArrayList<String>)
                // 필터 조건 반영
                if (!selectedDetailItem1.isNullOrEmpty()) {
                    recipeList = filteredRecipe()
                }
                // 정렬 조건 반영
                if (!selectedAlignItem.isNullOrEmpty()) {
                    recipeList = sortedRecipe()
                }
                rc_count.text = "${recipeList.count()}건"

                rv_data_list.adapter = ExtensionRecipeAdapter(
                    recipeList,
                    requireContext()
                )
            }
        }

        rv_selected_item.run {
            setHasFixedSize(true)
            adapter = selectedAdapter
        }

        reLoadSelectedCategory()
    }

    private fun sortedRecipe(): ArrayList<Recipe> {
        val sortedRecipe = recipeList
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
        return sortedRecipe
    }

    private fun filteredRecipe(): ArrayList<Recipe> {
        val tempRecipeList = ArrayList<Recipe>()
        val tempRecipeList2 = ArrayList<Recipe>()

        // 조리방법, 음식방법
        val content = selectedDetailItem1!![0].data
        val category = selectedDetailItem2!![0].data
        if (content == "선택안함" && category == "선택안함") {
            return recipeList
        } else if (content == "선택안함" && category != "선택안함") {
            recipeList.forEach {
                val recipe = it
                selectedDetailItem2!!.forEach {
                    if (recipe.category == it.data) {
                        tempRecipeList.add(recipe)
                    }
                }
            }
            return tempRecipeList
        } else if (content != "선택안함" && category == "선택안함") {
            recipeList.forEach {
                val recipe = it
                selectedDetailItem1!!.forEach {
                    if (recipe.content == it.data) {
                        tempRecipeList.add(recipe)
                    }
                }
            }
            return tempRecipeList
        } else {
            recipeList.forEach {
                val recipe = it
                selectedDetailItem2!!.forEach {
                    if (recipe.category == it.data) {
                        tempRecipeList.add(recipe)
                    }
                }
            }

            tempRecipeList.forEach {
                val recipe = it
                selectedDetailItem1!!.forEach {
                    if (recipe.content == it.data) {
                        tempRecipeList2.add(recipe)
                    }
                }
            }
            return tempRecipeList2
        }

    }

    // 선택된 재료들을 모두 포함하는 모든 레시피를 찾음
    private fun findRecipe(ingredientArr: ArrayList<String>): ArrayList<Recipe> {
        val tempRecipeList = ArrayList<Recipe>()
        recipeInfo.RECIPELIST.forEach {
            if (it.ingredientArr.containsAll(ingredientArr))
                tempRecipeList.add(it)
        }
        return tempRecipeList
    }

    private fun reLoadSelectedCategory() {
        //다시 카테고리 화면 돌아올시.. 선택한 메뉴들 ListUp 오른쪽에 back화살표 모양
        val selectedDataSet = arrayListOf<CategoryListItem>()
        selectedMenuItems.forEach {
            selectedDataSet.add(CategoryListItem(it, false))
        }
        L.i("::::selectedDataSet " + selectedDataSet)
        selectedAdapter.replaceAll(selectedDataSet)
    }

    private fun searchedRecipe(str: String?): ArrayList<Recipe> {
        val tempRecipeList = ArrayList<Recipe>()
        recipeInfo.RECIPELIST.forEach {
            if (it.name.contains(str.toString())) {
                tempRecipeList.add(it)
            }
        }
        return tempRecipeList
    }
}