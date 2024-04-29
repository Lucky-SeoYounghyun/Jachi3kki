package com.c4project.Jachi3kki.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.c4project.Jachi3kki.R

// nav 컨트롤러에 로그인 존재
class LoginFragment : Fragment() {
    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

//        btn_login!!.setOnClickListener {
//            session?.open(
//                AuthType.KAKAO_LOGIN_ALL,
//                MainActivity.instance
//            )
//        }
    }
}