package com.c4project.Jachi3kki.OuterDB

import com.c4project.Jachi3kki.Class.Vitamin
import com.c4project.Jachi3kki.InternalDB.FridgeIngredient
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.net.URL

class Category
private constructor(val src: Array<String>) {
    companion object {
        fun addFrom_fridge(ingredient_list: Array<String>) {//냉장고 데이터만 따로 추가하는 메소드
            CLASS3.put("냉장고",
                Category(ingredient_list)//냉장고 데이터는 외부 db가 아닌 내부 db를 사용하므로 외부와 연결할 필요 없음
            )
        }

        fun fetchJson_Ingredient(ingredientList: MutableList<FridgeIngredient>) {//외부 db에서 냉장고와 식재료 데이터를 받아오는 메소드
            //냉장고 데이터 추가
            CLASS1.clear()//읽어온 데이터를 저장하기 위해 디폴트 데이터 제거

            //첫 번째로 냉장고 데이터 추가
            CLASS1["냉장고"] = "냉장고"
            CLASS2["냉장고"] =
                Category(arrayOf("냉장고"))
            val fridgeSet = mutableSetOf<String>()
            for ( i in 0 until ingredientList.size) {
                if (ingredientList[i].activation != 0)
                    fridgeSet.add(ingredientList[i].name)
            }
            val fridgelist = fridgeSet.toTypedArray()
            CLASS3["냉장고"] =
                Category(fridgelist)

            //냉장고 데이터를 추가한 다음 외부 db의 식재료 데이터를 읽어옴. OkHttpClient 사용
            val url = URL("http://118.67.132.138/ingredient.php")
            val request = Request.Builder().url(url).build()
            val client = OkHttpClient()
            client.newCall(request).enqueue(object: Callback{
                override fun onResponse(call: Call?, response: Response?) {//OkHttpClient 연결 성공시 해당 메소드 수행
                    val body = response?.body()?.string()//body는 php의 내용을 그대로 담고 있음
                    println("Success to execute request!")

                    //파싱을 위해 json 정보가 담긴 body를 jsonObject에 저장
                    val jsonObject = JSONObject(body)
                    val jsonArray = jsonObject.getJSONArray("ingredient")
                    val arrayNum = jsonArray.length() //json 배열의 길이이자 소분류 개수와 같음
                    val largeSet = mutableSetOf<String>() //대분류 담을 set
                    val middleTempSet = mutableSetOf<String>()//각 대분류 별 중분류를 담을 set
                    val middleSet = mutableSetOf<String>() //모든 중분류 담을 set
                    val smallSet = mutableSetOf<String>() //소분류 담을 set
                    var middleNum: Int //중분류 개수담을 용도임

                    //식재료 대분류 데이터 읽어옴
                    for(i in 0 until arrayNum) {
                        largeSet.add(jsonArray.getJSONObject(i).getString("largeCategory"))
                    }
                    val largeList = largeSet.toList() //그냥 형변환 set to list
                    val largeNum = largeList.size //대분류 종류 개수
                    for(i in 0 until largeNum) {//대분류 데이터 교체
                        CLASS1[largeList[i]] = largeList[i]
                    }

                    //식재료 중분류 데이터 읽어옴
                    for (i in 0 until largeNum) {//중분류 데이터 교체
                        for (j in 0 until arrayNum) {//json 객체에서 얻어온 j번째 요소의 대분류 값이, 읽어온 대분류 데이터인 largeList의 i번째와 같으면
                            //임시 중분류 데이터 저장변수에 추가
                            if(largeList[i].equals(jsonArray.getJSONObject(j).getString("largeCategory"))) {
                                middleTempSet.add(jsonArray.getJSONObject(j).getString("middleCategory"))
                                middleSet.add(jsonArray.getJSONObject(j).getString("middleCategory"))
                            }
                        }
                        val middleArray: Array<String> = middleTempSet.toTypedArray()
                        middleTempSet.clear()//다음 for문을 위해 초기화
                        CLASS2[largeList[i]] = Category(middleArray)    //해당 대분류에 맞는 중분류를 추가
                    }
                    val middleList= middleSet.toList()
                    middleNum = middleList.size //모든 중분류 개수

                    // 식재료 소분류 데이터(식재료 이름) 읽어옴
                    for (i in 0 until middleNum) {
                        //위의 for문과 비슷하게 해당 json 객체의 j번째 요소의 소분류값이, 중분류 데이터의 i번째 요소의 소분류 값과 같다면
                            //해당 소분류 값을 임시 소분류 데이터 저장변수에 추가
                        for (j in 0 until arrayNum) {
                            if(middleList[i].equals(jsonArray.getJSONObject(j).getString("middleCategory")))
                                smallSet.add(jsonArray.getJSONObject(j).getString("smallCategory"))
                        }
                        val smallArray: Array<String> = smallSet.toTypedArray()
                        smallSet.clear()//다음 for문을 위해 초기화
                        CLASS3[middleList[i]] = Category(smallArray)//해당 중분류에 맞는 소분류를 추가
                    }
                }
                override fun onFailure(call: Call?, e: IOException?) {
                    println("Fail to execute request!")
                }
            })
        }

        //외부 db에서 비타민 정보를 읽어오는 함수. OkHttpClient 사용
        fun fetchJson_vitamin() {
            val targetIp = "118.67.132.138"
            val url = URL("http://"+targetIp+"/nutrition_info.php")
            val request = Request.Builder().url(url).build()
            val client = OkHttpClient()
            client.newCall(request).enqueue(object: Callback{
                override fun onResponse(call: Call?, response: Response?) {//OkHttpClient 연결 성공시 해당 메소드 수행
                    val body = response?.body()?.string()//body는 php의 내용을 그대로 담고 있음
                    println("Success to execute nutrition request!")

                    //파싱을 위해 json 정보가 담긴 body를 jsonObject에 저장
                    var jsonObject = JSONObject(body)
                    val jsonArray = jsonObject.getJSONArray("nutrition_info")//해당 php 페이지에서 nutrition_info에 해당하는 데이터를 읽어옴
                    val arrayNum = jsonArray.length() //json 배열의 길이이자 전체 영양소의 개수
                    var nutritionName: MutableList<String> = mutableListOf<String>()    //영양소 이름이 저장되는 리스트
                    var nutritionDesc: MutableList<String> = mutableListOf<String>()    //영양소 설명이 저장되는 리스트
                    var nutritionImgPath: MutableList<String> = mutableListOf<String>() //영양소 이미지 경로가 저장되는 리스트

                    for(i in 0 until arrayNum) {    //json 객체의 i번째 요소의 영양소의 정보를 추가
                        nutritionName.add(jsonArray.getJSONObject(i).getString("nutritionName"))
                        nutritionDesc.add(jsonArray.getJSONObject(i).getString("nutritionDescription"))
                        nutritionImgPath.add(targetIp+jsonArray.getJSONObject(i).getString("nutritionImage"))
                    }

                    VITAMINDATA.clear() //읽어온 데이터를 저장하기 위해 디폴트 데이터를 삭제

                    for(i in 0 until arrayNum) {//읽어온 데이터를 영양소 데이터 리스트인 VITAMINDATA에 추가
                        VITAMINDATA[nutritionName[i]] = Vitamin(nutritionName[i], nutritionDesc[i], nutritionImgPath[i])
                    }
                }
                override fun onFailure(call: Call?, e: IOException?) {
                    println("Fail to execute request!")
                }
            })
        }

        //그냥 형태만 다듬는 용도
        var VITAMINDATA = mutableMapOf(
            "비타민A" to Vitamin(
                "비타민A", "비타민A는 어쩌구 저쩌구 비타민A는 어쩌구 저쩌구 비타민A는 어쩌구 저쩌구 비타민A는 어쩌구 저쩌구 ",
                "gogi"
            )
        )

        //대분류 디폴트 데이터
        var CLASS1 = mutableMapOf(
            "육류" to ("육류")
        )

        //중분류 디폴트 데이터
        var CLASS2 = mutableMapOf(
            "육류" to Category(
                arrayOf(
                    "돼지고기",
                    "소고기",
                    "닭고기",
                    "오리고기",
                    "기타"
                )
            )
        )
        //소분류 디폴트 데이터
        var CLASS3 = mutableMapOf(
            "돼지고기" to Category(
                arrayOf(
                    "삼겹살",
                    "가브리살",
                    "뒷다리살",
                    "항정살",
                    "앞다리살",
                    "등심",
                    "사태",
                    "갈비",
                    "안심",
                    "갈매기살"
                )
            )
        )

        //영양소 별 식재료 데이터를 외부 db에서 읽어오는 메소드
        fun fetchJson_vitaminToIngredient() {
            GETINGREDIENT.clear()   //외부에서 데이터 읽어오므로 디폴트 데이터는 날림
            val targetIp = "118.67.132.138"
            val url = URL("http://"+targetIp+"/nutritionIngredient.php")   //재료별 비타민 가져오기
            val request = Request.Builder().url(url).build()
            val client = OkHttpClient()
            client.newCall(request).enqueue(object: Callback{
                override fun onResponse(call: Call?, response: Response?) {//OkHttpClient 연결 성공시 해당 메소드 수행
                    val body = response?.body()?.string()//body는 php의 내용을 그대로 담고 있음
                    println("Success to execute nutrition request!")

                    var jsonObject = JSONObject(body)
                    val jsonArray = jsonObject.getJSONArray("nutrition_info")
                    val arrayNum = jsonArray.length()
                    var vitaminSet = mutableSetOf<String>() //비타민 담을 set
                    var ingredientTempSet = mutableSetOf<String>()//각 비타민 별 포함 재료를 담을 set

                    for(i in 0 until arrayNum) { //비타민 이름 얻기
                        vitaminSet.add(jsonArray.getJSONObject(i).getString("nutritionName"))
                    }

                    //largeList는 비타민 구분이 담겨있는 리스트
                    val vitaminList = vitaminSet.toList() //형변환 set to list
                    val vitaminNum = vitaminList.size //비타민 종류 개수

                    //중분류 데이터 교체. 각 영양소별 이름에 맞는 식재료를 추가
                    for (i in 0 until vitaminNum) {
                        for (j in 0 until arrayNum) {
                            if(vitaminList[i].equals(jsonArray.getJSONObject(j).getString("nutritionName"))) {
                                ingredientTempSet.add(jsonArray.getJSONObject(j).getString("ingredientName"))
                            }
                        }
                        val ingredientArray: Array<String> = ingredientTempSet.toTypedArray()

                        ingredientTempSet.clear()
                        GETINGREDIENT[vitaminList[i]] = Category(ingredientArray)   //비타민 별 재료를 넣음
                    }
                }
                override fun onFailure(call: Call?, e: IOException?) {
                    println("Fail to execute request!")
                }
            })
        }

        //영양소 별 식재료 디폴트 데이터 리스트
        var GETINGREDIENT = mutableMapOf(
            "비타민A" to Category(
                arrayOf(
                    "시금치",
                    "닭가슴살",
                    "콩나물",
                    "숙주나물",
                    "고등어"
                )
            ),
            "비타민B1" to Category(
                arrayOf(
                    "시금치",
                    "미역",
                    "한치",
                    "새송이버섯",
                    "콩나물",
                    "숙주나물",
                    "고등어"
                )
            )
        )
    }
}