package com.c4project.Jachi3kki.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.get
import com.c4project.Jachi3kki.OuterDB.recipeInfo
import com.c4project.Jachi3kki.R
import com.c4project.Jachi3kki.log.L
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.usermgmt.response.model.Profile
import com.kakao.util.OptionalBoolean
import com.kakao.util.exception.KakaoException
import kotlinx.android.synthetic.main.activity_main.*
import java.security.MessageDigest

private const val TAG = "MyBroadcastReceiver"

class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (Intent.ACTION_DATE_CHANGED == intent!!.action) {
            recipeInfo.fetchJson_RecipeInfo()
        }
    }
}

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    companion object {
        lateinit var instance: MainActivity
        var session: Session? = null
        private var sessionCallback: SessionCallback =
            SessionCallback()
        var email: String? = null
        var id: String? = null
        var profile: Profile? = null
        val br: BroadcastReceiver =
            MyBroadcastReceiver()
    }

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_DATE_CHANGED)
        registerReceiver(br, filter)
        setContentView(R.layout.activity_main)

        // 로그인 세션
        session = Session.getCurrentSession()
        session?.addCallback(
            sessionCallback
        );
        instance = this
        navController = nav_host_fragment.findNavController()

        // getHashKey()
        bottomNavigationView.setOnNavigationItemSelectedListener(this)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_toolbar, menu)
        return true
    }

    // 좌측 네비 바 옵션 선택
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> { // 메뉴 버튼
                val draw: DrawerLayout = findViewById(R.id.drawer)
                if (draw.isDrawerOpen(GravityCompat.START)) {
                    draw.closeDrawer(GravityCompat.START)
                }
                draw.openDrawer(GravityCompat.START)
                return true
            }
        }
        return false
    }

    // 하단 네비 바 옵션 선택
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.menu_home -> {
                navController.navigate(R.id.action_global_mainFragment)
            }
            R.id.menu_ingredient -> {
                navController.navigate(R.id.action_global_ingredientFragment)
            }
            R.id.menu_vitamin -> {
                navController.navigate(R.id.action_global_vitaminFragment)
            }
            R.id.menu_search -> {
                navController.navigate(R.id.action_global_searchFragment)
            }
            R.id.menu_fridge -> {
                navController.navigate(R.id.action_global_FridgeFragment)
            }
        }
        // navgation API를 쓰지 않고 Transaction으로 했을 경우 코드
        // transaction.addToBackStack(null) => 백스택에 저장해서 그 위에 프레그먼트를 덮는 형식 (카드 레이아웃과 비슷)
        // transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE) => 프레그먼트 넘길 때 잔상이 남는다.
        // transaction.commit()
        return true
    }

    var backPressedTime: Long = 0
    private var FINISH_INTERVAL_TIME: Long = 2000

    // 뒤로 가기 누르면 작동되는 함수. 만약 홈화면일 경우 한번 더 누르면 앱이 종료되도록 설정
    override fun onBackPressed() {
        if (!navController.popBackStack()) { // currentBackStackEntry가 비어있으면 false 리턴. 따라서, 비어있는 경우
            val currentTime = System.currentTimeMillis()
            val intervalTime = currentTime - backPressedTime
            if (!(0 > intervalTime || FINISH_INTERVAL_TIME < intervalTime)) {
                finishAffinity()
                System.runFinalization()
                System.exit(0)
            } else {
                backPressedTime = currentTime
                Toast.makeText(this, "'뒤로' 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                return
            }
        }
        // 홈화면으로 온 경우
        val temp = navController.currentDestination
        // val tag1: Fragment? = supportFragmentManager.findFragmentById(R.id.mainFragment)
        val tag1 = navController.graph.get(R.id.mainFragment)
        if (temp == tag1) {
            val bnv = findViewById<View>(R.id.bottomNavigationView) as BottomNavigationView
            updateBottomMenu(bnv)
        }


    }

    private fun updateBottomMenu(navigation: BottomNavigationView) {
        navigation.menu.findItem(R.id.menu_home).isChecked = true
    }

    fun getHashKey() {
        val packageName = "com.example.Jachi3kki"
        try {
            val info =
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.signingInfo.apkContentsSigners
            } else {
                TODO("VERSION.SDK_INT < P")
            }
            val md = MessageDigest.getInstance("SHA")
            for (signature in signatures) {
                val md: MessageDigest
                md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val key = String(Base64.encode(md.digest(), 0))
                Log.d("Hash key:", "!!!!!!!$key!!!!!!")
            }
        } catch (e: Exception) {
            Log.e("name not found", e.toString())
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        // 세션 콜백 삭제
        Session.getCurrentSession().removeCallback(sessionCallback)
    }

    fun pushNewFragment(fragment: Fragment?, name: String?) {
        L.i("::::Fragment : " + fragment)
        L.i(":::::name " + name)
        if (frame_layout != null) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            try {
                fragmentTransaction
                    .replace(R.id.frame_layout, fragment!!, name)
                    .addToBackStack(name)
                    .commit()
                supportFragmentManager.executePendingTransactions()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    // 카카오 계정 세션 구하는 SessionCallBack
    private class SessionCallback : ISessionCallback {

        override fun onSessionOpenFailed(exception: KakaoException?) {
            Log.e("SessionCallback :: ", "onSessionOpenFailed : " + exception.toString());
        }

        override fun onSessionOpened() {
            UserManagement.getInstance().me(object : MeV2ResponseCallback() {

                override fun onFailure(errorResult: ErrorResult?) {
                    Log.e("KAKAO_API", "사용자 정보 요청 실패: " + errorResult);
//                    Dlog.d("Session Call back :: on failed ${errorResult?.errorMessage}")
                }

                override fun onSessionClosed(errorResult: ErrorResult?) {
                    Log.e("KAKAO_API", "세션이 닫혀 있음: " + errorResult);
//                    Dlog.e("Session Call back :: onSessionClosed ${errorResult?.errorMessage}")

                }

                override fun onSuccess(result: MeV2Response?) {
                    val intent = Intent(instance, MainActivity::class.java)
                    instance.startActivity(intent)
                    recipeInfo.fetchJson_Bookmark()
                    if (result != null) {
                        id = result.id.toString()
                    }
                    checkNotNull(result) { "session response null" }
                    Log.i("KAKAO_API", "사용자 아이디: " + result.id);
                    val kakaoAccount = result!!.kakaoAccount
                    val kakaoNickname = result!!
                    if (kakaoAccount != null) {
                        // 이메일
                        email = kakaoAccount.email
                        if (email != null) {
                            Log.i("KAKAO_API", "email: " + email);

                        } else if (kakaoAccount.emailNeedsAgreement() == OptionalBoolean.TRUE) {
                            // 동의 요청 후 이메일 획득 가능
                            // 단, 선택 동의로 설정되어 있다면 서비스 이용 시나리오 상에서 반드시 필요한 경우에만 요청해야 합니다.

                        } else {
                            // 이메일 획득 불가
                        }

                        // 프로필

                        if (kakaoAccount.profile != null) {
                            profile = kakaoAccount.profile
                            Log.d("KAKAO_API", "nickname: " + profile!!.nickname);
                            Log.d("KAKAO_API", "profile image: " + profile!!.profileImageUrl);
                            Log.d("KAKAO_API", "thumbnail image: " + profile!!.thumbnailImageUrl);

                        } else if (kakaoAccount.profileNeedsAgreement() == OptionalBoolean.TRUE) {
                            // 동의 요청 후 프로필 정보 획득 가능
                            profile = kakaoAccount.profile
                        } else {
                            // 프로필 획득 불가
                        }
                    }

                }

                // register or login

            })
        }
    }


}