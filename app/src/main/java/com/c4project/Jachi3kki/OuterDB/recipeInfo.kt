package com.c4project.Jachi3kki.OuterDB

import com.c4project.Jachi3kki.Class.Recipe
import com.c4project.Jachi3kki.activity.MainActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.net.URL
//레시피 관련 정보를 받아오는 클래스
class recipeInfo {
    companion object{
        fun fetchJson_Bookmark(){ //OkHttpClient를 이용하여 외부 db에서 데이터를 받아옴
            var bookmarkList: MutableList<String> = mutableListOf<String>()
            val targetIp = "118.67.132.138"
            val url = URL("http://"+targetIp+"/getBookmarkList.php") //지정된 ip주소, 페이지를 url에 저장
            val request = Request.Builder().url(url).build()    //지정된 url과 연결
            val client = OkHttpClient() //OkHttpClient를 사용
            client.newCall(request).enqueue(object: Callback{
                override fun onResponse(call: Call?, response: Response?) { //연결 성공시 해당 메소드 실행
                    val body = response?.body()?.string()//body는 php의 내용을 그대로 담고 있음
                    println("Success to execute nutrition request!")

                    //파싱을 위해 json 정보가 담긴 body를 jsonObject에 저장
                    var jsonObject = JSONObject(body)
                    val jsonArray = jsonObject.getJSONArray("bookmarkInfo") //페이지에서 json 형태의 데이터 중 bookmark 정보를 받아옴
                    val arrayNum = jsonArray.length() //json 배열의 길이이자 전체 영양소의 개수

                    for(i in 0 until arrayNum) {//북마크에서 자신의 아이디로 저장된 레시피 이름들을 가져옴
                        if(jsonArray.getJSONObject(i).getString("userId").equals(MainActivity.id))
                            bookmarkList.add(jsonArray.getJSONObject(i).getString("recipeName"))
                    }

                    BOOKMARKLIST.clear()    //새로운 데이터로 채우기 위해 디폴트로 설정해둔 데이터 제거

                    for(i in 0 until bookmarkList.size) {
                        for(j in 0 until RECIPELIST.size) { //가져온 북마크 레시피들의 이름과 전체 레시피들의 이름을 비교하여 북마크 리스트에 레시피를 추가
                            if(bookmarkList[i].equals(RECIPELIST[j].name))
                                BOOKMARKLIST.add(
                                    RECIPELIST[j])
                        }
                    }

                }
                override fun onFailure(call: Call?, e: IOException?) {
                    println("Fail to execute request!")
                }
            })
        }

        fun fetchJson_RecipeInfo() {    //외부 db에서 레시피 정보를 가져오는 메소드
            val targetIp = "118.67.132.138"
            val url = URL("http://"+targetIp+"/recipe_total.php")
            val request = Request.Builder().url(url).build()
            val client = OkHttpClient()
            client.newCall(request).enqueue(object: Callback {
                override fun onResponse(call: Call?, response: Response?) { //OkHttpClient로 연결 성공시 해당 메소드 수행
                    val body = response?.body()?.string()
                    println("Success to execute recipeInfo request!")

                    //파싱을 위해 json 정보가 담긴 body를 jsonObject에 저장
                    var jsonObject = JSONObject(body)
                    val jsonArray = jsonObject.getJSONArray("recipe_manual")   //레시피 상세정보가 들어감. 조리 방법 등등. null값도 있으므로 조심
                    val jsonArray2 = jsonObject.getJSONArray("recipe_ingredient")//(레시피에 들어가는 재료가 포함됨. null값도 있으므로 조심)
                    var arrayNum = jsonArray.length() //레시피의 개수

                    //recipe_manual 에서 가져올 데이터들
                    var idArray: ArrayList<Int> = ArrayList<Int>()  //레시피 아이디
                    var nameArray: ArrayList<String> = ArrayList<String>()  //레시피 이름
                    var contentArray: ArrayList<String> = ArrayList<String>()   //레시피 조리방법
                    var categoryArray: ArrayList<String> = ArrayList<String>()  //음식 종류(밥, 반찬 등등)
                    var imgSrcArray: ArrayList<String> = ArrayList<String>()    //음식 메인 이미지
                    var ingredientsArray: ArrayList<String> = ArrayList<String>()   //한줄로된 필요 재료들
                    var likeArray: ArrayList<Int> = ArrayList<Int>()    //좋아요수
                    var viewArray: ArrayList<Int> = ArrayList<Int>()    //조회수
                    var todayViewArray: ArrayList<Int> = ArrayList<Int>()   //오늘자 갱신된 좋아요 수
                    var yesterdayViewArray: ArrayList<Int> = ArrayList<Int>()   //어제의 총 좋아요 수
                    var tempIngredientArray = mutableSetOf<String>()    //재료들을 어레이리스트로 담을 배열
                    var tempManualArray = mutableListOf<String>()        //레시피 별 단계들을 어레이 리스트로 담을 배열열
                    var tempImgArray = mutableListOf<String>()           //단계별 이미지를 어레이리스트로 담을 배열. null일 경우 no_image로 설정할 것임

                    for(i in 0 until arrayNum) {    //읽어온 json 객체로부터 컬럼에 맞는 데이터들을 읽어와 안드로이드 변수에 저장
                        idArray.add(jsonArray.getJSONObject(i).getInt("recipeId"))  //레시피 아이디
                        nameArray.add(jsonArray.getJSONObject(i).getString("recipeName"))   //레시피 이름
                        contentArray.add(jsonArray.getJSONObject(i).getString("recipeMethod")) //조리방법
                        categoryArray.add(jsonArray.getJSONObject(i).getString("recipeType"))   //음식 종류
                        imgSrcArray.add(jsonArray.getJSONObject(i).getString("recipeImgSrc"))//음식 메인 이미지
                        ingredientsArray.add(jsonArray.getJSONObject(i).getString("recipeIngredients"))
                        likeArray.add(jsonArray.getJSONObject(i).getInt("recipeLikeCnt"))
                        viewArray.add(jsonArray.getJSONObject(i).getInt("recipeViewCnt"))
                        todayViewArray.add(jsonArray.getJSONObject(i).getInt("todayView"))
                        yesterdayViewArray.add(jsonArray.getJSONObject(i).getInt("yesterdayView"))
                    }

                    RECIPELIST.clear()  //새로운 데이터로 채우기 위해 디폴트로 넣어둔 데이터 삭제

                    //이 밑은 그냥 바로 위 for문처럼 다 가져오기 보다 그때그때 가져오려는 것임.
                    for(i in 0 until arrayNum) {//각 레시피에 맞는 식재료를 개별로 추가
                        for(j in 1 until 24) {
                            val temp = jsonArray2.getJSONObject(i).getString("ingredient$j")
                            if(!temp.equals("null")) {
                                tempIngredientArray.add(temp)       //null이 아닐 때까지 재료 추가
                            }
                            else
                                break;   //null이면 이미 재료 다 추가됐다는 것이므로 다음으로 이동
                        }
                        for(j in 1 until 21) {//각 레시피에 맞는 단계별 설명과 이미지 추가
                            val temp = jsonArray.getJSONObject(i).getString("Manual$j")
                            val temp2 = jsonArray.getJSONObject(i).getString("IMG$j")
                            if(!temp.equals("null")) {
                                tempManualArray.add(temp)       //null이 메뉴얼 추가
                                if(!temp2.equals("null"))
                                    tempImgArray.add(temp2)
                                else
                                    tempImgArray.add("no_image")//메뉴얼과 다르게 사진의 경우 없을수도 있으므로 no_image로 대체
                            }
                            else
                                break;  //null이면 메뉴얼도 다 추가됐다는 것이므로 다음으로 이동.
                        }

                        //레시피 리스트에 추가하기 위해 기존 배열을 형변환
                        val tempIngredientList: ArrayList<String> = ArrayList<String>(tempIngredientArray)
                        val tempManualList: ArrayList<String> = ArrayList<String>(tempManualArray)
                        val tempImgList: ArrayList<String> = ArrayList<String>(tempImgArray)

                        RECIPELIST.add(//mutablelist인 레시피리스트에 위에서 저장한 데이터를 추가
                            Recipe(idArray[i], nameArray[i], contentArray[i], categoryArray[i], imgSrcArray[i], ingredientsArray[i]
                                , tempIngredientList, tempManualList, tempImgList, likeArray[i], viewArray[i], todayViewArray[i], yesterdayViewArray[i])
                        )
                        tempIngredientArray.clear() //얘네는 다음 번의 메뉴얼, 재료, 이미지들이 들어가야하므로 비워줘야함.
                        tempManualArray.clear()
                        tempImgArray.clear()
                    }
                }
                override fun onFailure(call: Call?, e: IOException?) {
                    println("recipeInfo http 호출 실패")
                }
            })
        }

        //북마크 정보가 저장될 리스트
        var BOOKMARKLIST: MutableList<Recipe> = mutableListOf<Recipe>()

        //이 밑은 레시피 디폴트 데이터
        var RECIPELIST: MutableList<Recipe> = mutableListOf<Recipe>(
            Recipe(
                1,
                "김치찌개",
                "끓이기",
                "찌개",
                "이미지 url들어감",
                "김치, 두부, 고춧가루, 간장, 두부 등등",
                arrayListOf("조리 1단계", "조리 2단계", "조리 3단계", "조리 4단계"),
                arrayListOf("조리 1단계 이미지", "조리 2단계 이미지", "조리 3단계 이미지", "조리 4단계 이미지"),
                arrayListOf("김치", "두부", "고춧가루", "간장","두부","등등"),
                0,  //좋아요수
                0,   //조회수
                0,
                0
            ),
            Recipe(
                1,
                "김치찌개",
                "끓이기",
                "찌개",
                "이미지 url들어감",
                "김치, 두부, 고춧가루, 간장, 두부 등등",
                arrayListOf("조리 1단계", "조리 2단계", "조리 3단계", "조리 4단계"),
                arrayListOf("조리 1단계 이미지", "조리 2단계 이미지", "조리 3단계 이미지", "조리 4단계 이미지"),
                arrayListOf("김치", "두부", "고춧가루", "간장","두부","등등"),
                0,  //좋아요수
                0,  //조회수
                0,
                0
            )
        )
    }
}