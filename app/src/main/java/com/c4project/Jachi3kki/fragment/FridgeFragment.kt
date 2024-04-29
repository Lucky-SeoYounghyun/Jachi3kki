package com.c4project.Jachi3kki.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.c4project.Jachi3kki.Adapter.RefrigeratorAdapter
import com.c4project.Jachi3kki.OuterDB.Category
import com.c4project.Jachi3kki.Class.SelectedListItem
import com.c4project.Jachi3kki.Helper.RefrigeratorDeleteButton
import com.c4project.Jachi3kki.Helper.RefrigeratorSwiperHelper
import com.c4project.Jachi3kki.InternalDB.AppDatabase
import com.c4project.Jachi3kki.InternalDB.FridgeIngredient
import com.c4project.Jachi3kki.Listener.MyButtonClickListener
import com.c4project.Jachi3kki.R
import kotlinx.android.synthetic.main.fragment_fridge.*


class FridgeFragment : Fragment() {

    lateinit var navController: NavController
    val itemlist = arrayListOf<FridgeIngredient>()
    lateinit var adapter: RefrigeratorAdapter
    lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fridge, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = Navigation.findNavController(view)

        super.onViewCreated(view, savedInstanceState)

        recycler_test.setHasFixedSize(true)
        layoutManager = GridLayoutManager(activity, 2, LinearLayoutManager.VERTICAL, false)
        recycler_test.layoutManager = layoutManager

        //내부db에서 데이터 받아오고, itemlist에 데이터 넣는 코드
        var db: AppDatabase? = AppDatabase.getInstance(requireContext())
        var ingredientList: MutableList<FridgeIngredient>

        ingredientList = db?.ingredientDAO()?.getAll()!!
        generateItem(ingredientList)

        // Add Swipe
        val swipe =
            object : RefrigeratorSwiperHelper(activity?.applicationContext, recycler_test, 200) {
                override fun instantiateMyButton(
                    viewHolder: RecyclerView.ViewHolder,
                    buffer: MutableList<RefrigeratorDeleteButton>
                ) {
                    //Add button
                    activity?.let {
                        RefrigeratorDeleteButton(
                            it.applicationContext, "삭제", 50, 0, Color.parseColor("#FF3c30"),
                            object : MyButtonClickListener {
                                override fun onclick(pos: Int) {
                                    var tempName: String = itemlist[pos].name  //삭제할 재료의 이름을 받아옴
                                    db.ingredientDAO()
                                        .deactivate(tempName)     //삭제버튼 누르면 activation이 0으로 바뀌어야함. db에 잘 반영됨
                                    adapter.selectlist.select_check(pos)
                                    itemlist.removeAt(pos)
                                    buttonList?.clear()
                                    if (itemlist.size == 0) {
                                        background_fridge.visibility = View.VISIBLE
                                    }
                                    Category.fetchJson_Ingredient(itemlist)     //냉장고의 데이터를 갱신해줘야함. 더 깔끔한 방법은 모르겠음.
                                    adapter.notifyDataSetChanged()
                                }
                            })
                    }?.let { buffer.add(it) }
                }
            }

        button1.setOnClickListener {
            for (i in 0..itemlist.size) {
                adapter.selectlist.click(i)
                adapter.selectlist.select_print_all()
                adapter.notifyDataSetChanged()
            }
        }

        button2.setOnClickListener {
            adapter.selectlist.click_del()
            adapter.notifyDataSetChanged()
        }

        //냉장고에서 레시피로 가는 버튼
        btn_go_fridge_Recipe.setOnClickListener {
            val selectedDataSet by lazy {
                arrayListOf<SelectedListItem>().also { list ->
                    adapter.selectlist.selectList.forEach {
                        list.add(SelectedListItem(itemlist[it.toInt()].name))
                        if (itemlist.size == 0) {
                            background_fridge.visibility = View.VISIBLE
                        }
                    }
                }
            }
            navController.navigate(
                R.id.action_FridgeFragment_to_recipeFragment,
                bundleOf("item" to selectedDataSet)
            )
        }

        textView10.setOnClickListener {
            navController.navigate(R.id.action_FridgeFragment_to_addFragment)
        }
    }

    //데이터 넣는 창
    //이건 그냥 디폴트 데이터 넣는 함수
    private fun generateItem(ingredientList: MutableList<FridgeIngredient>) {
        var size = ingredientList.size
        //db에서 받아온 냉장고 재료 리스트를 itemList에 넣는거. 어차피 존재하는 재료 다 넣는거임
        for (i in 0 until size) {
            if (ingredientList[i].activation != 0) { // 중복 비허용
                if (!itemlist.contains(ingredientList[i]))
                    itemlist.add(ingredientList[i])   //activation이 1인(자기가 소유한) 재료만 itemlist에 추가해서 냉장고에 보이게 함
            }
        }                                           //나머지는 activation이 0이라 보이지 않음(ingredientList엔 여전히 들어있음)

        if (itemlist.size == 0) {
            background_fridge.visibility = View.VISIBLE
        }
        adapter = activity?.let {
            RefrigeratorAdapter(it.applicationContext, itemlist)
        }!!
        recycler_test.adapter = adapter
    }
}