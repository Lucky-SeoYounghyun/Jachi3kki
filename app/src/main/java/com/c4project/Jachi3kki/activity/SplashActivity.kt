package com.c4project.Jachi3kki.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.c4project.Jachi3kki.InternalDB.AppDatabase
import com.c4project.Jachi3kki.InternalDB.FridgeIngredient
import com.c4project.Jachi3kki.OuterDB.Category
import com.c4project.Jachi3kki.OuterDB.recipeInfo
import com.c4project.Jachi3kki.R
import kotlinx.android.synthetic.main.splash_view.*


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_view);

        // 레시피 데이터
        recipeInfo.fetchJson_RecipeInfo()
        //냉장고 데이터 넣기
        var db: AppDatabase? = null
        db = AppDatabase.getInstance(this)
        var ingredientList = mutableListOf<FridgeIngredient>()
        ingredientList = db?.ingredientDAO()?.getAll()!!
        //비타민, 식재료 데이터 가져오기. ingredientList를 매개변수로 줘서 냉장고 재료까지 추가함
        Category.fetchJson_Ingredient(
            ingredientList
        )
        Category.fetchJson_vitamin()
        Category.fetchJson_vitaminToIngredient()

        Glide.with(this).load(R.raw.splash_final).into(splash_view)

        startLoading()
    }

    private fun startLoading() {
        val handler = Handler()
        handler.postDelayed({
            val intent = Intent(baseContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3150)
    }
}