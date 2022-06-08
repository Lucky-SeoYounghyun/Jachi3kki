package com.c4project.Jachi3kki.Class

data class Recipe(
    val id: Int,    //레시피 아이디
    val name: String,   //레시피 이름
    val content: String,    //레시피 조리 방법(변수명 바꾸고싶은데 바꾸면 다른 것도 바꿔야되므로 안바꿈)
    val category: String,   //음식 종류(밥, 반찬 등등)
    val img_src: String,    //음식 이미지
    val ingredients: String,    //재료들 한줄로 나열
    val ingredientArr: ArrayList<String>,//재료 리스트
    val manualList: ArrayList<String>,  //조리 방법 메뉴얼
    val imgList: ArrayList<String>,  //조리 단계별 이미지
    var likeCnt: Int,   //좋아요 수
    var viewCnt: Int,    //조회수
    var todayView: Int, //오늘자 조회수
    var yesterdayView: Int  //어제자 조회수
)