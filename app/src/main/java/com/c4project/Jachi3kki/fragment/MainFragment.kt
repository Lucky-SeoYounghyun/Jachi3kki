package com.c4project.Jachi3kki.fragment

import HorizontalItemDecorator
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.c4project.Jachi3kki.Adapter.MainFragmentAdapter
import com.c4project.Jachi3kki.Adapter.MainVitaminAdapter
import com.c4project.Jachi3kki.Class.Recipe
import com.c4project.Jachi3kki.Class.SelectedListItem
import com.c4project.Jachi3kki.activity.MainActivity
import com.c4project.Jachi3kki.R
import com.c4project.Jachi3kki.R.drawable
import com.c4project.Jachi3kki.R.layout
import com.c4project.Jachi3kki.OuterDB.recipeInfo
import com.google.android.material.navigation.NavigationView
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.LogoutResponseCallback
import kotlinx.android.synthetic.main.fragment_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    class MainVitamin(val name: String, val img_src: String)

    var recipeList = arrayListOf<Recipe>()
    var vitaminList = arrayListOf<MainVitamin>()
    lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inputFormat = SimpleDateFormat("HH:mm:ss.SSS")
        val date = Date()
        inputFormat.format(date.time)

        return inflater.inflate(layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.instance.setSupportActionBar(toolbar)
        MainActivity.instance.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        MainActivity.instance.supportActionBar!!.setHomeAsUpIndicator(drawable.menu)

        navController = Navigation.findNavController(view)
        if (recipeList.isEmpty()) {
            addRecipeArray()
        }
        if (vitaminList.isEmpty()) {
            addVitaminArray()
        }
        toolbar.title = "자취3끼"

        // recyclerView에 layout Manger 설정
        rv_data_list.layoutManager = LinearLayoutManager(
            activity,
            LinearLayoutManager.VERTICAL,
            false
        )
        rv_vitamin_list.layoutManager = LinearLayoutManager(
            activity,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        // 안전성을 위해 사이즈 고정
        rv_data_list.setHasFixedSize(true)
        rv_vitamin_list.setHasFixedSize(true)

        // 리사이클러 뷰 구부선 추가
        activity?.let { HorizontalItemDecorator(it, drawable.horizontal_line_divider, 0, 0) }?.let {
            rv_vitamin_list.addItemDecoration(
                it
            )
        }

        // 리사클러뷰의 어뎁터 설정
        rv_data_list.adapter = activity?.let { it ->
            MainFragmentAdapter(recipeList, it) {
                Log.e("Index", it.name) //어떤 아이템을 클릭했는지 확인하기위해 로그를 넣음
            }
        }

        // 리사이클러뷰의 어뎁터 설정
        rv_vitamin_list.adapter = activity?.let { it ->
            MainVitaminAdapter(vitaminList, it) {
                Log.e("Index", it.name) //어떤 아이템을 클릭했는지 확인하기위해 로그를 넣음

                val selectedDataSet = arrayListOf<SelectedListItem>()
                selectedDataSet.add(SelectedListItem(it.name))
                navController.navigate(
                    R.id.action_mainFragment_to_ingredientFragment,
                    bundleOf("vitaminItem" to selectedDataSet)
                )
            }
        }

        navigationView.setNavigationItemSelectedListener(this)



        // 네비바의 로그인기능 구현
        val headerView = navigationView.getHeaderView(0)
        val loginBtn = headerView.findViewById<Button>(R.id.login_btn)
        loginBtn.setOnClickListener {
            navController.navigate(R.id.action_mainFragment_to_loginFragment)
        }

        // 로그인 정보 업데이트
        if (MainActivity.id != null) {
            if (MainActivity.email != null) {
                headerView.findViewById<TextView>(R.id.email).text = "${MainActivity.email}"
            }
            headerView.findViewById<TextView>(R.id.nickName).text =
                "${MainActivity.profile?.nickname}"
            Glide.with(MainActivity.instance).load(
                MainActivity.profile?.thumbnailImageUrl)
                .into(headerView.findViewById<ImageView>(R.id.profileImage))

            headerView.findViewById<Button>(R.id.btn_logout).setVisibility(View.VISIBLE)
            headerView.findViewById<Button>(R.id.btn_logout).setEnabled(true)

            // 로그아웃 버튼 클릭 시
            headerView.findViewById<Button>(R.id.btn_logout)!!.setOnClickListener {
                Toast.makeText(MainActivity.instance, "정상적으로 로그아웃 되었습니다.", Toast.LENGTH_SHORT)
                    .show()
                headerView.findViewById<TextView>(R.id.email).text = null
                headerView.findViewById<TextView>(R.id.nickName).text = null
                headerView.findViewById<ImageView>(R.id.profileImage).background = null
                headerView.findViewById<Button>(R.id.login_btn).setVisibility(View.VISIBLE)
                headerView.findViewById<Button>(R.id.login_btn).setEnabled(true)
                headerView.findViewById<Button>(R.id.btn_logout).setVisibility(View.INVISIBLE)
                headerView.findViewById<Button>(R.id.btn_logout).setEnabled(false)


                MainActivity.profile = null
                MainActivity.id = null
                MainActivity.email = null
                UserManagement.getInstance()
                    .requestLogout(object : LogoutResponseCallback() {
                        override fun onCompleteLogout() {
                            navController.navigate(R.id.action_global_mainFragment)
                        }
                    })
            }

            // 로그인
            headerView.findViewById<Button>(R.id.login_btn).setVisibility(View.GONE)
            headerView.findViewById<Button>(R.id.login_btn).setEnabled(false)
        }

    }

    private fun addVitaminArray() {
        vitaminList.add(MainVitamin("비타민A", "icon_a"))
        vitaminList.add(MainVitamin("비타민B", "icon_b"))
        vitaminList.add(MainVitamin("비타민C", "icon_c"))
        vitaminList.add(MainVitamin("비타민D", "icon_d"))
        vitaminList.add(MainVitamin("비타민E", "icon_e"))
        vitaminList.add(MainVitamin("비타민K", "icon_k"))
        vitaminList.add(MainVitamin("칼슘", "icon_ca"))
        vitaminList.add(MainVitamin("인", "icon_p"))
        vitaminList.add(MainVitamin("마그네슘", "icon_mg"))
        vitaminList.add(MainVitamin("철분", "icon_fe"))
        vitaminList.add(MainVitamin("구리", "icon_cu"))
        vitaminList.add(MainVitamin("아연", "icon_zn"))
        /*
        vitaminList.add(MainVitamin("비타민A", "http://118.67.132.138/vitamin_icon/icon_a.png"))
        vitaminList.add(MainVitamin("비타민B", "http://118.67.132.138/vitamin_icon/icon_b.png"))
        vitaminList.add(MainVitamin("비타민C", "http://118.67.132.138/vitamin_icon/icon_c.png"))
        vitaminList.add(MainVitamin("비타민D", "http://118.67.132.138/vitamin_icon/icon_d.png"))
        vitaminList.add(MainVitamin("비타민E", "http://118.67.132.138/vitamin_icon/icon_e.png"))
        vitaminList.add(MainVitamin("비타민K", "http://118.67.132.138/vitamin_icon/icon_k.png"))
        vitaminList.add(MainVitamin("칼슘", "http://118.67.132.138/vitamin_icon/icon_ca.png"))
        vitaminList.add(MainVitamin("인", "http://118.67.132.138/vitamin_icon/icon_p.png"))
        vitaminList.add(MainVitamin("마그네슘", "http://118.67.132.138/vitamin_icon/icon_mg.png"))
        vitaminList.add(MainVitamin("철분", "http://118.67.132.138/vitamin_icon/icon_fe.png"))
        vitaminList.add(MainVitamin("구리", "http://118.67.132.138/vitamin_icon/icon_cu.png"))
        vitaminList.add(MainVitamin("아연", "http://118.67.132.138/vitamin_icon/icon_zn.png"))
        */
    }

    private fun addRecipeArray() { //레시피 추천 메소드
        //어제자 조회수로 가장 높았던 것 중 5개를 골라 그 중 랜덤한 2개를 선별
        val indexList: MutableList<Int> = mutableListOf(0, 0, 0, 0, 0)  //레시피 인덱스 리스트의 디폴트값
        val tempList: MutableList<Int> = mutableListOf(-1, -1, -1, -1, -1)//각 레시피의 어제자 조회수의 디폴트 값

        //레시피 리스트(레시피 정보)의 개수만큼의 반복문으로 어제자 조회수가 가장 높은 5개의 레시피를 골라냄
        for (i in 0 until recipeInfo.RECIPELIST.size) {
            if (tempList[0] < recipeInfo.RECIPELIST[i].yesterdayView) {//기존 데이터가 있어 뒤에 것들을 밀어내야 할 경우 기존 데이터를 뒤로 옮김
                if (tempList[0] != -1) {
                    for (j in 4 downTo 1) {
                        indexList[j] = indexList[j - 1]
                        tempList[j] = tempList[j - 1]
                    }
                }
                indexList[0] = i
                tempList[0] = recipeInfo.RECIPELIST[i].yesterdayView
            } else if (tempList[1] < recipeInfo.RECIPELIST[i].yesterdayView) {
                if (tempList[1] != -1) {//기존 데이터가 있어 뒤에 것들을 밀어내야 할 경우 기존 데이터를 뒤로 옮김
                    for (j in 4 downTo 2) {
                        indexList[j] = indexList[j - 1]
                        tempList[j] = tempList[j - 1]
                    }
                }
                indexList[1] = i
                tempList[1] = recipeInfo.RECIPELIST[i].yesterdayView
            } else if (tempList[2] < recipeInfo.RECIPELIST[i].yesterdayView) {
                if (tempList[2] != -1) {//기존 데이터가 있어 뒤에 것들을 밀어내야 할 경우 기존 데이터를 뒤로 옮김
                    for (j in 4 downTo 3) {
                        indexList[j] = indexList[j - 1]
                        tempList[j] = tempList[j - 1]
                    }
                }
                indexList[2] = i
                tempList[2] = recipeInfo.RECIPELIST[i].yesterdayView
            } else if (tempList[3] < recipeInfo.RECIPELIST[i].yesterdayView) {
                if (tempList[3] != -1) {//기존 데이터가 있어 뒤에 것들을 밀어내야 할 경우 기존 데이터를 뒤로 옮김
                    indexList[4] = indexList[3]
                    tempList[4] = tempList[3]
                }
                indexList[3] = i
                tempList[3] = recipeInfo.RECIPELIST[i].yesterdayView
            } else if (tempList[4] < recipeInfo.RECIPELIST[i].yesterdayView) {
                //기존 데이터가 있어도 어차피 마지막 데이터이므로 상관없이 데이터를 덮어씌움
                indexList[4] = i
                tempList[4] = recipeInfo.RECIPELIST[i].yesterdayView
            }
        }

        //0~4 중 랜덤한 인덱스 두개를 값이 겹치지 않도록 고름
        val random = Random()
        val num1 = random.nextInt(5)
        var num2 = random.nextInt(5)
        if (num1 == num2) {
            while (num1 == num2) {
                num2 = random.nextInt(5)
            }
        }
        //위에서 랜덤으로 뽑은 인덱스에 해당하는 레시피를 추천 레시피로 선택
        recipeList.add(recipeInfo.RECIPELIST[indexList[num1]])
        recipeList.add(recipeInfo.RECIPELIST[indexList[num2]])
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_bookmark -> {
                if (MainActivity.id == null) {
                    // 로그인 필요 다이얼로그 표시
                    alertLoginRequired()
                } else {
                    navController.navigate(R.id.action_mainFragment_to_bookmarkFragment)
                }
            }

        }
        return true
    }
    // 로그인 알림설정 dialog message
    private fun alertLoginRequired() {
        AlertDialog.Builder(context)
            .setTitle("로그인 필요")
            .setMessage("로그인이 필요한 기능입니다")
            .setPositiveButton("확인") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }
}