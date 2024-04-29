package com.c4project.Jachi3kki.Adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.c4project.Jachi3kki.*
import com.c4project.Jachi3kki.Class.Recipe
import com.c4project.Jachi3kki.OuterDB.recipeInfo
import com.c4project.Jachi3kki.activity.MainActivity
import com.c4project.Jachi3kki.activity.PagerActivity
import com.c4project.Jachi3kki.log.L
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.main_recipe_list_item.*
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import kotlin.collections.ArrayList

class MainFragmentAdapter(
    val items: ArrayList<Recipe>,
    val context: Context,
    val itemSelect: (Recipe) -> Unit
) : RecyclerView.Adapter<MainFragmentAdapter.MainFragmentViewHolder>() {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MainFragmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.main_recipe_list_item,
            parent,
            false
        )
        return MainFragmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainFragmentViewHolder, position: Int) {
        holder.bind(items[position], context, position)
        holder.itemView.setOnClickListener() {
            L.i("조회수")
            //Toast.makeText(context,"조회수",Toast.LENGTH_SHORT).show()

            // 매개변수 전달 TODO
            val recipeNum = recipeInfo.RECIPELIST.indexOf(items[position])
            val intent = Intent(MainActivity.instance, PagerActivity::class.java)   //여기서 뷰페이저 연결하는 거 같은데

            //조회수 증가
            val recipeName = items[position].name   //증가하려는 레시피의 이름
            items[position].viewCnt += 1    //증가하려는 레시피의 총 조회수. +1시켜줘야 하므로 +1된 값을 저장
            items[position].todayView += 1  //오늘자 조회수도 +1 시켜줘야하므로 +1된 오늘자 조회수 값을 저장
            val viewCnt = (items[position].viewCnt).toString()
            val todayView = (items[position].todayView).toString()
            val task = IncreaseView()   //조회수 증가를 위한 클래스

            //해당 클래스에 파라미터(url, 레시피이름, 총 조회수, 오늘자 조회수)를 입력하여 조회수 증가 실행
            task.execute("http://118.67.132.138/increaseView.php", recipeName, viewCnt, todayView)

            intent.putExtra("recipeNum",recipeNum) //넘겨줄 데이터(intent)에 해당 레시피의 인덱스를 저장
            MainActivity.instance.startActivity(intent) //메인 액티비티에서 뷰페이저 액티비티로 연결
        }

        holder.main_img_bookmark_icon.setOnClickListener {
            L.i("북마크")

            if(MainActivity.id != null){    //이건 로그인 되어있을 때 외부DB에 북마크 추가하는 코드.
                if(recipeInfo.BOOKMARKLIST.contains(items[position])) {//이미 추가되어있다면 반대로 북마크 제거
                    //회원이면서 이미 북마크 추가된거면 북마크 제거하고 외부db의 추천수 감소
                    val userId = MainActivity.id    //북마크를 제거하려는 유저의 아이디
                    val recipeName = items[position].name   //북마크를 제거하려는 레시피 이름
                    items[position].likeCnt -=1 //북마크에서 제거해야하므로 -1시킴
                    val likeCnt = (items[position].likeCnt).toString()
                    val task = DeleteData() //북마크 제거용 클래스

                    //파라미터(url, 유저 아이디, 레시피 이름, 추천수(likeCnt))를 입력하여 북마크 제거 수행
                    task.execute("http://118.67.132.138/deleteDb.php", userId, recipeName, likeCnt)
                    recipeInfo.BOOKMARKLIST.remove(items[position]) //새로고침인 것처럼 코드에서 제거

                    // 북마크 체크 제거 및 토스트 메세지
                    holder.main_img_bookmark_icon.setImageResource(R.drawable.icon_bookmark)
                    Toast.makeText(context, "북마크에서 제거되었습니다", Toast.LENGTH_SHORT).show()

                }
                else {
                    //회원이면서 북마크 추가되지않은 레시피면 외부 db에 북마크 추가하고 추천수 증가
                    val userId = MainActivity.id    //북마크를 추가하려는 유저의 아이디
                    val recipeName = items[position].name   //북마크를 추가하려는 레시피 이름
                    items[position].likeCnt +=1     //북마크를 추가하는 것이므로 +1 시켜줌
                    val likeCnt = (items[position].likeCnt).toString()
                    val task = InsertData() //북마크 추가용 클래스

                    //파라미터(url, 유저 아이디, 레시피 이름, 추천수(likeCnt))를 입력하여 북마크 추가 수행
                    task.execute("http://118.67.132.138/insertDb.php", userId, recipeName, likeCnt)
                    recipeInfo.BOOKMARKLIST.add(items[position])

                    // 북마크 체크 !  토스트 메세지
                    holder.main_img_bookmark_icon.setImageResource(R.drawable.icon_bookmark_check)
                    Toast.makeText(context, "북마크에 추가되었습니다", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(context,"로그인이 필요합니다", Toast.LENGTH_SHORT).show()
            }
        }

    }

    //조회수 증가 적용을 위한 클래스. php와 연결하여 증가된 값을 외부 db에 적용
    class IncreaseView : AsyncTask<String, Void, String>(){
        override fun doInBackground(vararg params: String?): String {
            val serverURL: String? = params[0]//연결하려는 url 경로
            val recipeName: String? = params[1]//레시피 이름
            val viewCnt: String? = params[2]//총 조회수
            val todayView: String? = params[3]//오늘자 조회수
            val postParameters: String = "recipeName=$recipeName&viewCnt=$viewCnt&todayView=$todayView"
            //외부 db에 변경된 값을 적용하기 위해 필요한 파라미터들을 post 연결방식으로 저장

            //이 밑은 서버에 연결(post 방식)하여 작업을 수행하는 코드
            try{
                val url = URL(serverURL)
                val httpURLConnection: HttpURLConnection = url.openConnection() as HttpURLConnection

                httpURLConnection.readTimeout = 5000
                httpURLConnection.connectTimeout = 5000
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.connect()

                val outputStream: OutputStream = httpURLConnection.outputStream

                outputStream.write(postParameters.toByteArray(charset("UTF-8")))
                outputStream.flush()
                outputStream.close()

                val responseStatusCode: Int = httpURLConnection.responseCode

                val inputStream: InputStream
                inputStream = if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    httpURLConnection.inputStream
                } else {
                    httpURLConnection.errorStream
                }

                val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
                val bufferedReader = BufferedReader(inputStreamReader)
                val sb = StringBuilder()
                var line: String? = null

                while (bufferedReader.readLine().also({ line = it }) != null) {
                    sb.append(line)
                }

                bufferedReader.close();

                return sb.toString();
            } catch (e: Exception) {
                return "Error" + e.message
            }
        }
    }

    //북마크 제거용 클래스
    class DeleteData : AsyncTask<String, Void, String>(){
        override fun doInBackground(vararg params: String?): String {
            val serverURL: String? = params[0]  //연결하려는 url
            val userId: String? = params[1]     //유저의 아이디
            val recipeName: String? = params[2] //레시피 이름
            val likeCnt: String? = params[3]    //추천수
            val postParameters: String = "userId=$userId&recipeName=$recipeName&likeCnt=$likeCnt"
            //외부 db에 변경된 값을 적용하기 위해 필요한 파라미터들을 post 연결방식으로 저장

            //이 밑은 서버에 연결(post 방식)하여 작업을 수행하는 코드
            try{
                val url = URL(serverURL)
                val httpURLConnection: HttpURLConnection = url.openConnection() as HttpURLConnection

                httpURLConnection.readTimeout = 5000
                httpURLConnection.connectTimeout = 5000
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.connect()

                val outputStream: OutputStream = httpURLConnection.outputStream

                outputStream.write(postParameters.toByteArray(charset("UTF-8")))
                outputStream.flush()
                outputStream.close()

                val responseStatusCode: Int = httpURLConnection.responseCode

                val inputStream: InputStream
                inputStream = if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    httpURLConnection.inputStream
                } else {
                    httpURLConnection.errorStream
                }

                val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
                val bufferedReader = BufferedReader(inputStreamReader)
                val sb = StringBuilder()
                var line: String? = null

                while (bufferedReader.readLine().also({ line = it }) != null) {
                    sb.append(line)
                }

                bufferedReader.close();

                return sb.toString();
            } catch (e: Exception) {
                return "Error" + e.message
            }
        }
    }

    class InsertData : AsyncTask<String, Void, String>(){
        override fun doInBackground(vararg params: String?): String {
            val serverURL: String? = params[0]  //연결하려는 url
            val userId: String? = params[1]     //유저 아이디
            val recipeName: String? = params[2] //레시피 이름
            val likeCnt: String? = params[3]    //추천수
            val postParameters: String = "userId=$userId&recipeName=$recipeName&likeCnt=$likeCnt"
            //외부 db에 변경된 값을 적용하기 위해 필요한 파라미터들을 post 연결방식으로 저장

            //이 밑은 서버에 연결(post 방식)하여 작업을 수행하는 코드
            try{
                val url = URL(serverURL)
                val httpURLConnection: HttpURLConnection = url.openConnection() as HttpURLConnection

                httpURLConnection.readTimeout = 5000
                httpURLConnection.connectTimeout = 5000
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.connect()

                val outputStream: OutputStream = httpURLConnection.outputStream

                outputStream.write(postParameters.toByteArray(charset("UTF-8")))
                outputStream.flush()
                outputStream.close()

                val responseStatusCode: Int = httpURLConnection.responseCode

                val inputStream: InputStream
                inputStream = if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    httpURLConnection.inputStream
                } else {
                    httpURLConnection.errorStream
                }

                val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
                val bufferedReader = BufferedReader(inputStreamReader)
                val sb = StringBuilder()
                var line: String? = null

                while (bufferedReader.readLine().also({ line = it }) != null) {
                    sb.append(line)
                }

                bufferedReader.close();

                return sb.toString();
            } catch (e: Exception) {
                return "Error" + e.message
            }
        }
    }

    inner class MainFragmentViewHolder(
        override val containerView: View
    ) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(recipe: Recipe, context: Context, position: Int) {
            Glide.with(context).load(recipe.img_src).into(main_img_bookmark_picture)
            main_tv_bookmark_title.text = recipe.name
            main_tv_bookmark_content.text = recipe.content
            tv_main_title.text = when(position){
                0 -> "오늘의 추천 레시피"
                1 -> "Hot 레시피"
                else -> "나오면 안됨"
            }
            if (MainActivity.id != null) {
                if (recipeInfo.BOOKMARKLIST.contains(items[position])) {
                    main_img_bookmark_icon.setImageResource(R.drawable.icon_bookmark_check)
                } else {
                    main_img_bookmark_icon.setImageResource(R.drawable.icon_bookmark)
                }
            }
            main_tv_likeCnt.text = "${recipe.likeCnt}"
            main_tv_viewCnt.text = "${recipe.viewCnt} "
            itemSelect(recipe)
        }
    }

}
