package com.c4project.Jachi3kki.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.c4project.Jachi3kki.Adapter.ClassListAdapter
import com.c4project.Jachi3kki.Adapter.SelectedListAdapter
import com.c4project.Jachi3kki.OuterDB.Category
import com.c4project.Jachi3kki.Class.CategoryListItem
import com.c4project.Jachi3kki.Class.SelectedListItem
import com.c4project.Jachi3kki.log.L
import com.c4project.Jachi3kki.R
import com.c4project.Jachi3kki.databinding.FragmentIngredientBinding
import kotlinx.android.synthetic.main.fragment_ingredient.*

class IngredientFragment : Fragment() {
    private lateinit var navController: NavController
    private lateinit var binding: FragmentIngredientBinding

    // 비타민에서 전달한 데이터
    private val selectedVitaminItem by lazy { arguments?.getParcelableArrayList<SelectedListItem>("vitaminItem") }
    private var fromVitamin: Boolean = false
    private lateinit var nowVitamin: CategoryListItem

    //대분류 중분류 소분류 선택 리사이클러뷰의 어뎁터
    private lateinit var MainCategoryAdapter: ClassListAdapter // 대 분류
    private lateinit var MiddleCategoryAdapter: ClassListAdapter // 중 분류
    private lateinit var SubjectCategoryAdapter: ClassListAdapter // 소 분류
    private lateinit var SelectedCategoryAdapter: SelectedListAdapter // 선택한 재료를 리스트에 추가하는 selectedListAdpater

    //선택한 item의 중복방지와 순서유지를위해 hashmap을 사용
    // 카테고리에서 재료를 선택하면, 아래에 출력되는 아이템 Map
    private var selectedCategorys = LinkedHashMap<String, String>()

    //대분류 디폴트 데이터
    private val deFaultClass1 by lazy {
        arrayListOf<CategoryListItem>().also { list ->
            // 메인 -> 재료 선택인 경우
            if (!fromVitamin) {
                val class1 = Category.CLASS1

                class1.forEach {
                    if (it.key == "육류") {
                        list.add(CategoryListItem(it.key, true))
                    } else {
                        list.add(CategoryListItem(it.key, false))
                    }
                }
            } else {
                // 메인 -> 비타민 -> 재료선택 인 경우
                nowVitamin = CategoryListItem(selectedVitaminItem?.get(0)!!.data, true)
                selectedVitaminItem?.forEach {
                    if (it.data == nowVitamin.data) {
                        list.add(CategoryListItem(it.data, true))
                    } else {
                        list.add(CategoryListItem(it.data, false))
                    }
                }

            }

        }
    }

    //중분류 디폴트데이터 육류
    private val deFaultClass2 by lazy {

        arrayListOf<CategoryListItem>().also { list ->
            // 비타민으로부터 온게 아닌경우
            if (!fromVitamin) {
                val class2 = Category.CLASS2
                requireNotNull(class2["육류"]).src.forEach {
                    list.add(CategoryListItem(it, false))
                }
                binding.tvMiddleCategory.text = "육류"
            } else {
                val middleData = findMiddleData(Category.GETINGREDIENT[nowVitamin.data]?.src)
                middleData.forEach {
                    list.add(CategoryListItem(it, false))
                }
                binding.tvMiddleCategory.text = nowVitamin.data
            }
        }
    }

    //layout을 inflate(xml의 내용을 실제 view 객체로 만듦)하는 단계. 여기서 view와 관련된 버튼 등을 초기화
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (selectedVitaminItem != null) {
            fromVitamin = true
        }
        binding = FragmentIngredientBinding.inflate(inflater, container, false)
        return binding.root
    }

    //activity와 fragment의 view가 모두 생성된 상태로, view를 변경하는 작업이 가능한 단계
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        btn_goRecipe.setOnClickListener {

            val selectedDataSet = arrayListOf<SelectedListItem>() //arraylistof로 아이템을 담아서
            selectedCategorys.entries.map {
                selectedDataSet.add(
                    SelectedListItem(
                        it.key
                    )
                )
            }
            navController.navigate(
                R.id.action_ingredientFragment_to_recipeFragment,
                bundleOf("item" to selectedDataSet)
            )
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // 대분류 adapter 생성
        MainCategoryAdapter = object : ClassListAdapter(requireContext()) {
            override fun onItemClick(index: Int, item: CategoryListItem) {
                //대분류에서 항목설정시 이벤트가 떨어진다.
                binding.tvMiddleCategory.text = item.data

                MiddleCategoryAdapter.clearItems()
                SubjectCategoryAdapter.clearItems()

                // 전체 데이터에서 선택된 데이터를 찾아내어 값을 바꿔준다.
                MainCategoryAdapter.replaceAll(
                    // 선택된 데이터의 selected 상태를 true로 변경
                    changeSelectedState(
                        MainCategoryAdapter.getItemList(),
                        item.data
                    )
                )
                // 비타민에서 온 경우가 아니면
                if (!fromVitamin) {
                    val class2 = Category.CLASS2
                    if (class2.containsKey(item.data)) {
                        val dataSet = arrayListOf<CategoryListItem>().also { list ->
                            class2[item.data]!!.src.forEach {
                                list.add(CategoryListItem(it, false))
                            }
                        }
                        MiddleCategoryAdapter.replaceAll(dataSet)
                    }
                } else {
                    nowVitamin = item
                    val dataSet by lazy {
                        arrayListOf<CategoryListItem>().also { list ->
                            val middleData =
                                findMiddleData(Category.GETINGREDIENT[nowVitamin.data]?.src)
                            middleData.forEach {
                                list.add(CategoryListItem(it, false))
                            }
                        }
                    }
                    MiddleCategoryAdapter.replaceAll(dataSet)
                }
            }
        }
        MiddleCategoryAdapter = object : ClassListAdapter(requireContext()) {
            override fun onItemClick(index: Int, item: CategoryListItem) {
                //중분류에서 항목설정시 이벤트가 떨어진다.
                binding.tvSubjectCategory.text = item.data

                MiddleCategoryAdapter.replaceAll(
                    changeSelectedState(
                        MiddleCategoryAdapter.getItemList(),
                        item.data
                    )
                )
                // 메인에서 -> 재료 이동
                if (!fromVitamin) {
                    val class3 = Category.CLASS3
                    if (class3.containsKey(item.data)) {
                        val dataSet = arrayListOf<CategoryListItem>().also { list ->
                            class3[item.data]!!.src.forEach {
                                list.add(CategoryListItem(it, false))
                            }
                        }
                        SubjectCategoryAdapter.replaceAll(dataSet)
                    } else {
                        SubjectCategoryAdapter.clearItems()
                    }

                } else { // 메인 -> 비타민 -> 재료
                    val class2Array = Category.CLASS2[item.data]
                    // allIngredient: 대분류 범주에 해당되는 모든 소분류 아이템 담기
                    val allIngredient by lazy {
                        arrayListOf<String>().also { list ->
                            class2Array?.src?.forEach {
                                Category.CLASS3[it]?.src?.forEach {
                                    list.add(it)
                                }
                            }
                        }
                    }
                    // GETINGREDIENT는 비타민 - 재료 매핑된 데이터 형태
                    val getIngredient = Category.GETINGREDIENT

                    // 1.우리가 찾는 비타민에 포함된 재료와 2.선택한 대분류에 포함된 재료중 공통된 재료찾기
                    val dataSet = arrayListOf<CategoryListItem>().also { list ->
                        getIngredient[nowVitamin.data]?.src?.forEach {
                            if (allIngredient.contains(it)) list.add(
                                CategoryListItem(
                                    it,
                                    false
                                )
                            )
                        }
                    }
                    SubjectCategoryAdapter.replaceAll(dataSet)
                }
            }
        }
        SubjectCategoryAdapter = object : ClassListAdapter(requireContext()) {
            override fun onItemClick(index: Int, item: CategoryListItem) {
                //소분류에서 항목설정시 이벤트가 떨어지게된다.
                SubjectCategoryAdapter.replaceAll(
                    changeSelectedState(
                        SubjectCategoryAdapter.getItemList(),
                        item.data
                    )
                )

                if (!selectedCategorys.containsKey(item.data)) {
                    selectedCategorys[item.data] = item.data
                    val selectedDataSet = arrayListOf<CategoryListItem>()

                    selectedCategorys.entries.forEach {
                        selectedDataSet.add(CategoryListItem(it.key, false))
                    }
                    L.i("::::selectedDataSet " + selectedDataSet)
                    SelectedCategoryAdapter.replaceAll(selectedDataSet)
                }

            }
        }

        SelectedCategoryAdapter = object : SelectedListAdapter(requireContext()) {
            override fun onItemClick(index: Int, item: CategoryListItem) {
                //선택한 리스트뷰 클릭시 떨어지는 이벤트
                L.i(":::::::::::::::선택한 선택한 카테고리 " + item.data)
                SelectedCategoryAdapter.removeItem(index)
                selectedCategorys.remove(item.data)
            }
        }

        binding.rvMainCategory.run {
            setHasFixedSize(true)
            adapter = MainCategoryAdapter
        }

        binding.rvMiddleCategory.run {
            setHasFixedSize(true)
            adapter = MiddleCategoryAdapter
        }

        binding.rvSubjectCategory.run {
            setHasFixedSize(true)
            adapter = SubjectCategoryAdapter
        }

        binding.rvSelectedCategory.run {
            setHasFixedSize(true)
            adapter = SelectedCategoryAdapter
        }

        //초반의 디폴트 데이터
        MainCategoryAdapter.replaceAll(deFaultClass1)
        MiddleCategoryAdapter.replaceAll(deFaultClass2)


        reLoadSelectedCategory()
    }


    private fun reLoadSelectedCategory() {
        //다시 카테고리 화면 돌아올시.. 선택한 메뉴들 ListUp 오른쪽에 back화살표 모양
        val selectedDataSet = arrayListOf<CategoryListItem>()
        selectedCategorys.entries.forEach {
            selectedDataSet.add(CategoryListItem(it.key, false))
        }
        L.i("::::selectedDataSet " + selectedDataSet)
        SelectedCategoryAdapter.replaceAll(selectedDataSet)
    }

    //선택한 list의 클릭상태를 변경하기위한 함수
    private fun changeSelectedState(
        categorys: List<CategoryListItem>?,
        data: String
    ): List<CategoryListItem>? {
        // map 함수는 각 원소를 원하는 형태로 변환하는 기능을 합니다.
        // 변환된 결과를 모아서 새 컬렉션을 만듭니다.
        return categorys?.map { category ->
            category.selected = category.data == data
            category
        }?.toList()
    }

    private fun findMiddleData(vitaminArr: Array<String>?): MutableList<String> {
        val tempMiddleList = mutableSetOf<String>()
        Category.CLASS3.entries.map {
            if (vitaminArr != null) {
                for (ingredient in vitaminArr) {
                    if (it.value.src.contains(ingredient)) tempMiddleList.add(it.key)
                }
            }
        }

        val MiddleList = mutableSetOf<String>()
        Category.CLASS2.entries.map {
            for (ingredient in tempMiddleList) {
                if (it.value.src.contains(ingredient)) MiddleList.add(it.key)
            }
        }
        return MiddleList.toMutableList()
    }
}
