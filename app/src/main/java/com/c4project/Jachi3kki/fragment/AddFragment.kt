package com.c4project.Jachi3kki.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.c4project.Jachi3kki.Adapter.ClassListAdapter
import com.c4project.Jachi3kki.Adapter.SelectedListAdapter
import com.c4project.Jachi3kki.OuterDB.Category
import com.c4project.Jachi3kki.Class.CategoryListItem
import com.c4project.Jachi3kki.Class.SelectedListItem
import com.c4project.Jachi3kki.InternalDB.AppDatabase
import com.c4project.Jachi3kki.InternalDB.FridgeIngredient
import com.c4project.Jachi3kki.log.L
import com.c4project.Jachi3kki.R
import com.c4project.Jachi3kki.databinding.FragmentIngredientBinding
import kotlinx.android.synthetic.main.fragment_ingredient.*

// 냉장고 재료추가 프래그먼트
class AddFragment : Fragment() {
    private lateinit var navController: NavController
    private lateinit var binding: FragmentIngredientBinding
    var fridgeSet = mutableSetOf<String>()

    //대분류 중분류 소분류 선택 리사이클러뷰의 어뎁터
    private lateinit var MainCategoryAdapter: ClassListAdapter
    private lateinit var MiddleCategoryAdapter: ClassListAdapter
    private lateinit var SubjectCategoryAdapter: ClassListAdapter
    private lateinit var SelectedCategoryAdapter: SelectedListAdapter

    //선택한 item의 중복방지와 순서유지를위해 hashmap을 사용
    // 카테고리에서 재료를 선택하면, 아래에 출력되는 아이템 Map
    private var selectedCategorys = LinkedHashMap<String, String>()

    //대분류 디폴트 데이터
    private val deFaultClass1 by lazy {
        arrayListOf<CategoryListItem>().also { list ->
            // mutate 하니까 안전빵 딥카피
            val class1 = mutableMapOf<String, String>()
            class1.putAll(Category.CLASS1)
            // 냉장고 삭제
            class1.remove("냉장고")

            class1.forEach {
                if (it.key == "육류") {
                    list.add(CategoryListItem(it.key, true))
                } else {
                    list.add(CategoryListItem(it.key, false))
                }
            }
        }
    }

    //중분류 디폴트데이터 육류
    private val deFaultClass2 by lazy {
        arrayListOf<CategoryListItem>().also { list ->
            // mutate 하니까 안전빵 딥카피
            val class2 = mutableMapOf<String, Category>()
            class2.putAll(Category.CLASS2)
            requireNotNull(class2["육류"]).src.forEach {
                list.add(CategoryListItem(it, false))
            }
        }
    }

    //layout을 inflate(xml의 내용을 실제 view 객체로 만듦)하는 단계. 여기서 view와 관련된 버튼 등을 초기화
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentIngredientBinding.inflate(inflater, container, false)
        return binding.root
    }

    //activity와 fragment의 view가 모두 생성된 상태로, view를 변경하는 작업이 가능한 단계
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        btn_goRecipe.setOnClickListener {
            //얘가 냉장고로 돌아가는 버튼인듯
            val selectedDataSet = arrayListOf<SelectedListItem>() //arraylistof로 아이템을 담아서

            selectedCategorys.entries.map {
                selectedDataSet.add(
                    SelectedListItem(
                        it.key
                    )
                )
            }
            val size: Int = selectedDataSet.size    //고른 재료를 냉장고에서 활성화시키기 위함
            for (i in 0 until size) {
                fridgeSet.add(selectedDataSet.get(i).data)  //data에서 고른 재료의 이름만 뽑아냄
            }
            val fridgeArray: Array<String> = fridgeSet.toTypedArray()   //다시 배열로 변환

            activateIngredient(fridgeArray)   //냉장고에 재료들을 활성화시키는 함수 호출

            navController.navigate(
                R.id.action_addFragment_to_FridgeFragment
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
                val class2 = Category.CLASS2
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

                if (class2.containsKey(item.data)) {
                    val dataSet = arrayListOf<CategoryListItem>().also { list ->
                        class2[item.data]!!.src.forEach {
                            list.add(CategoryListItem(it, false))
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

                val class3 = Category.CLASS3
                MiddleCategoryAdapter.replaceAll(
                    changeSelectedState(
                        MiddleCategoryAdapter.getItemList(),
                        item.data
                    )
                )
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
            }
        }
        SubjectCategoryAdapter = object : ClassListAdapter(requireContext()) {
            override fun onItemClick(index: Int, item: CategoryListItem) {
                //소분류에서 항목설정시 이벤트가 떨어진다.
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
                //선택한 리스트뷰 클릭시 떨어지는 이벤트트
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

        //디폴트데이터를 초반에 설정해주세요.
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

    private fun activateIngredient(ingredient_list: Array<String>) {     //냉장고에 추가할 재료들을 activate시켜 냉장고에 보이게 함
        var ingredientList = mutableListOf<FridgeIngredient>()
        var tempSet = mutableSetOf<String>()
        val db: AppDatabase? = AppDatabase.getInstance(requireContext())
        ingredientList = db?.ingredientDAO()?.getAll()!!

        for (i in 0 until ingredientList.size) {
            if (ingredientList[i].activation != 0)
                tempSet.add(ingredientList[i].name)
        }

        for (i in ingredient_list.indices) {
            for (j in 0 until ingredientList.size) {
                if (ingredient_list[i].equals(ingredientList[j].name)) {
                    db.ingredientDAO().activate(ingredient_list[i])
                    tempSet.add(ingredient_list[i])
                    continue
                }
            }
        }
        val tempArray: Array<String> = tempSet.toTypedArray()
        Category.addFrom_fridge(tempArray)
    }
}
