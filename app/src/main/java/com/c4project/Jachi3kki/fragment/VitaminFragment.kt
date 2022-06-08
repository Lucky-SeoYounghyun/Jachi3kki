package com.c4project.Jachi3kki.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.c4project.Jachi3kki.Adapter.ClassListAdapter
import com.c4project.Jachi3kki.Adapter.SelectedListAdapter
import com.c4project.Jachi3kki.OuterDB.Category
import com.c4project.Jachi3kki.Class.CategoryListItem
import com.c4project.Jachi3kki.Class.SelectedListItem
import com.c4project.Jachi3kki.log.L
import com.c4project.Jachi3kki.R
import com.c4project.Jachi3kki.databinding.FragmentVitaminBinding
import kotlinx.android.synthetic.main.fragment_vitamin.*

class VitaminFragment : Fragment() {
    // private val vitaminList = arrayListOf<Vitamin>()
    private lateinit var navController: NavController
    private lateinit var binding: FragmentVitaminBinding

    // 카테고리 리사이클러 뷰, 선택 리사클러 뷰의 어뎁터
    private lateinit var MainCategoryAdapter: ClassListAdapter
    private lateinit var SelectedCategoryAdapter: SelectedListAdapter


    //선택한 item의 중복방지와 순서유지를위해 hashmap을 사용
    // 카테고리에서 재료를 선택하면, 아래에 출력되는 아이템 Map
    private var selectedCategorys = mutableSetOf<String>()

    //카테고리 디폴트 데이터
    private val deFaultClass1 by lazy {
        arrayListOf<CategoryListItem>().also { list ->
            val vitaminData = Category.VITAMINDATA

            vitaminData.forEach {
                if (it.key == "비타민A") {
                    list.add(CategoryListItem(it.key, true))
                } else {
                    list.add(CategoryListItem(it.key, false))
                }
            }
        }
    }

    //layout을 inflate(xml의 내용을 실제 view 객체로 만듦)하는 단계. 여기서 view와 관련된 버튼 등을 초기화
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentVitaminBinding.inflate(inflater, container, false)
        return binding.root
    }

    //activity와 fragment의 view가 모두 생성된 상태로, view를 변경하는 작업이 가능한 단계
    //여기다가 recyclerView 만들어줘야됨
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        //디폴트데이터를 초반에 설정해주세요.

        // 비타민 선택 버튼 누르면, 아래의 선택된 비타민 목록에 추가하는 비타민이 뜨도록
        btn_vitamin_select.setOnClickListener {
            val itemText = tv_middle_category.text
            val item = Category.VITAMINDATA[itemText]
            if (item != null) {
                if (!selectedCategorys.contains(item.name)) {
                    selectedCategorys.add(item.name)
                    val selectedDataSet = arrayListOf<CategoryListItem>()

                    selectedCategorys.forEach {
                        selectedDataSet.add(CategoryListItem(it, false))
                    }
                    L.i("::::selectedDataSet " + selectedDataSet)
                    SelectedCategoryAdapter.replaceAll(selectedDataSet)
                }
            }
        }

        btn_goIngredient.setOnClickListener {       //이제 여기가 비타민 선택 완료하고 그에 따른 재료창으로 이동하는 부분
            val selectedDataSet = arrayListOf<SelectedListItem>()
            if (selectedCategorys.isEmpty()) {
                Toast.makeText(requireContext(), "최소 1개 이상의 비타민을 선택해 주세요.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                selectedCategorys.forEach {
                    selectedDataSet.add(
                        SelectedListItem(
                            it
                        )
                    )
                }
                selectedDataSet.sortBy { it.data }

                navController.navigate(
                    R.id.action_vitaminFragment_to_ingredientFragment,
                    bundleOf("vitaminItem" to selectedDataSet)
                )
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        MainCategoryAdapter = object : ClassListAdapter(requireContext()) {
            override fun onItemClick(index: Int, item: CategoryListItem) {
                //대분류에서 항목설정시 이벤트가 떨어진다.
                binding.tvMiddleCategory.text = item.data
                val vitaminData = Category.VITAMINDATA

                // 전체 데이터에서 선택된 데이터를 찾아내어 값을 바꿔준다.
                MainCategoryAdapter.replaceAll(
                    // 선택된 데이터의 selected 상태를 true로 변경
                    changeSelectedState(
                        MainCategoryAdapter.getItemList(),
                        item.data
                    )
                )
                if (vitaminData.containsKey(item.data)) {
                    val vitamin = vitaminData[item.data]
                    if (vitamin != null) {
                        binding.vitDescription.text = vitamin.description
                        if (vitamin.img_src != "") {
                            val nutritionImg: ImageView = binding.imgVitamin
                            val imageUrl = "http://" + vitamin.img_src
                            Glide.with(requireContext()).load(imageUrl).into(nutritionImg)
                        } else {
                            binding.imgVitamin.setImageResource(R.mipmap.ic_launcher)
                        }
                    }
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

        binding.rvSelectedCategory.run {
            setHasFixedSize(true)
            adapter = SelectedCategoryAdapter
        }
        MainCategoryAdapter.replaceAll(deFaultClass1)
        MainCategoryAdapter.onItemClick(0, CategoryListItem("비타민A", true))

        reLoadSelectedCategory()
    }

    private fun reLoadSelectedCategory() {
        //다시 카테고리 화면 돌아올시.. 선택한 메뉴들 ListUp 오른쪽에 back 화살표 모양
        val selectedDataSet = arrayListOf<CategoryListItem>()
        selectedCategorys.forEach {
            selectedDataSet.add(CategoryListItem(it, false))
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

}
