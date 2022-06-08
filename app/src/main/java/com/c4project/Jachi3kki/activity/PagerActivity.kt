package com.c4project.Jachi3kki.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.c4project.Jachi3kki.R
import com.c4project.Jachi3kki.fragment.ViewPagerMainFragment

class PagerActivity : AppCompatActivity() {
    //레시피 목록에서 개별 레시피 선택시 해당 액티비티로 넘어옴
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pager)
        val recipeNum = intent.getIntExtra("recipeNum", 0)//넘어온 데이터 intent로부터 recipeNum이라는 데이터의 값을 받음
        //Fragment 연결해주는 코드
        //해당 코드가 없으면 빈화면이 나옴
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragmentFrame, ViewPagerMainFragment(recipeNum)).commit()
    }
}