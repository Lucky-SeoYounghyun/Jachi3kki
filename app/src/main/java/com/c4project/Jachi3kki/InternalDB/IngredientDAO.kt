package com.c4project.Jachi3kki.InternalDB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update

//RoomDB에 접근하는 메소드들
@Dao
interface IngredientDAO {
    @Query("SELECT * FROM fridgeIngredient")    //다 읽어오는 쿼리
    fun getAll(): MutableList<FridgeIngredient>

    @Query("SELECT * FROM fridgeIngredient WHERE name IN (:ingredientsNames)")  //이름으로 다 읽어오는 쿼리
    fun loadAllByNames(ingredientsNames: Array<String>): List<FridgeIngredient>

    @Query("UPDATE fridgeIngredient SET activate_check = 0 WHERE name = (:givenName)")
    fun deactivate(givenName: String)//냉장고 선택 재료 삭제시 activate_check를 0으로 변경하여 비활성화(화면에 보이지 않게 함)

    @Query("UPDATE fridgeIngredient SET activate_check = 1 WHERE name = (:givenName)")
    fun activate(givenName: String)//냉장고 선택 재료 삭제시 activate_check를 1로 변경하여 활성화(화면에 보이게 함)

    @Update////활성화여부만 바꾸게 할 메소드.
    fun update(new: FridgeIngredient)

    @Delete//삭제 쿼리
    fun delete(ingredient: FridgeIngredient)

}