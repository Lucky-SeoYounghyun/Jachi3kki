package com.c4project.Jachi3kki.InternalDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
//내부 db
@Database(entities = [FridgeIngredient::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ingredientDAO(): IngredientDAO //내부 db 접근 메소드가 담긴 인터페이스

    companion object {  //싱글톤 패턴 적용(db 중복 생성 방지)
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,

                        AppDatabase::class.java,
                        "fridgeIngredient"
                    ).allowMainThreadQueries().createFromAsset("databases/fridgeIngredient.db").build()
                    //databases 폴더에서 내부 db인 fridgeIngredient.db 파일을 읽어옴
                }
            }
            return INSTANCE //INSTANCE는 읽어온 내부 db를 의미
        }
    }
}