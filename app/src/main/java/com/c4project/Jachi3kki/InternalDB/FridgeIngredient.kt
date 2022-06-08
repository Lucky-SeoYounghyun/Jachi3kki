package com.c4project.Jachi3kki.InternalDB

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
//내부 db에서 읽어올 테이블의 형태를 지정
@Entity(tableName = "fridgeIngredient")
data class FridgeIngredient(
    @PrimaryKey @NonNull @ColumnInfo(name = "id") val id: Int,                       //이미지 쉽게 가져오려고 넣은 거
    @NonNull @ColumnInfo(name = "name") val name: String,                               //재료 이름(기본키)
    @ColumnInfo(name = "img_src") val img_src: String?,         //재료 이미지 경로
    @NonNull @ColumnInfo(name = "activate_check") val activation: Int    //해당 재료 활성화 여부(냉장고에 있는지 0 or 1)
)

