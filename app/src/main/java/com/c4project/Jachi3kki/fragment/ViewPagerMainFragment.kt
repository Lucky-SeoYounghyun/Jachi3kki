package com.c4project.Jachi3kki.fragment

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.c4project.Jachi3kki.Adapter.ViewPagerAdapter
import com.c4project.Jachi3kki.DroidSpeech.DroidSpeech
import com.c4project.Jachi3kki.DroidSpeech.OnDSListener
import com.c4project.Jachi3kki.DroidSpeech.OnDSPermissionsListener
import com.c4project.Jachi3kki.timer.NumberPickerDialog
import com.c4project.Jachi3kki.R
import com.c4project.Jachi3kki.Class.Stt_dialog
import kotlinx.android.synthetic.main.viewpager_main.*
import java.util.*


open class ViewPagerMainFragment(private var recipeNum: Int) : Fragment(),
    OnDSListener,
    OnDSPermissionsListener {
    //ViewPager 부모프래그먼트
    //자식 프래그먼트도 포함되어있다
    //어뎁터도 연결되어있다
    //어뎁터는 Adapter > ViewPagerAdapter.kt
    //Activity_DroidSpeech를 TAG하여 보여지게 설정

    val TAG = "Activity_DroidSpeech"

    lateinit var voiceinput: String


    private var droidSpeech: DroidSpeech? = null
    var dialog = NumberPickerDialog()
    var question_dialog = Stt_dialog()

    var pass = 0


    open fun getXml(): Int {
        return R.layout.viewpager_main
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initializing the droid speech and setting the listener
        droidSpeech = DroidSpeech(
            requireActivity().applicationContext,
            requireActivity().fragmentManager
        ).apply {
            setOnDroidSpeechListener(this@ViewPagerMainFragment)
            setShowRecognitionProgressView(false)
            setOneStepResultVerify(true)
            setRecognitionProgressMsgColor(Color.WHITE)
            setOneStepVerifyConfirmTextColor(Color.WHITE)
            setOneStepVerifyRetryTextColor(Color.WHITE)
            setPreferredLanguage("ko-KR")
        }
    }

    // 뷰 페이저
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getXml(), container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewPager.adapter = ViewPagerAdapter(recipeNum)

        // ViewPager의 Paging 방향은 Horizontal
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            // Paging 완료되면 호출하기
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.d("ViewPagerFragment", "Page ${position + 1}")
                Log.d("ViewPagerFragment", "Page ${position + 1}")
                if (position == 0) {
                    Bottom_view?.visibility = View.INVISIBLE
                    Top_view?.visibility = View.INVISIBLE
                    Bottom_view_2?.visibility = View.INVISIBLE
                    timer_container?.visibility = View.GONE
                } else {
                    Bottom_view?.visibility = View.VISIBLE
                    Top_view?.visibility = View.VISIBLE
                    Bottom_view_2?.visibility = View.VISIBLE
                    timer_container?.visibility = View.VISIBLE
                }
            }


        })

    }
    // 뷰페이저의 클릭 버튼들 부분
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        start_record?.setOnClickListener {
            startClicked()
        }


        end_record?.setOnClickListener {
            stopClicked()
        }

        Text_view_countdown?.setOnClickListener {
            set_timer_Clicked()
        }

        question_dialog_button?.setOnClickListener {
            question_mark()
        }

        start_timer?.setOnClickListener {
            //start_timer_Clicked()
            if (mTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        imageButton_left?.setOnClickListener {
            //stop_timer_Clicked()
            previousPage()
        }

        imageButton_right?.setOnClickListener {
            //stop_timer_Clicked()
            nextPage()
        }

        stop_timer?.setOnClickListener {
            //stop_timer_Clicked()
            pauseTimer()
        }

        reset_timer?.setOnClickListener {
            resetTimer()
        }
    }


    //타이머 설정 부분 시작 , 중단, 리셋 등

    private var mCountDownTimer: CountDownTimer? = null
    private var mTimerRunning = false

    private var START_TIME_IN_MILLIS: Long = 0
    private var mTimeLeftInMillis = START_TIME_IN_MILLIS


    fun getNum(num: Long): Long {
        START_TIME_IN_MILLIS = num
        return START_TIME_IN_MILLIS
    }

    //타이머 시작
    private fun startTimer() {


        mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 10) {
            override fun onTick(millisUntilFinished: Long) {
                mTimeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                mTimerRunning = false
                start_timer!!.visibility = View.VISIBLE
                stop_timer?.visibility = View.GONE
                resetTimer()
            }
        }.start()

        stop_timer?.visibility = View.VISIBLE
        start_timer?.visibility = View.GONE

        mTimerRunning = true
    }

    //타이머 리셋
    fun resetTimer() {
        mCountDownTimer?.cancel()
        mTimerRunning = false
        mTimeLeftInMillis = START_TIME_IN_MILLIS
        start_timer!!.visibility = View.VISIBLE
        stop_timer?.visibility = View.GONE
        updateCountDownText()
    }

    //타이머 정지
    private fun pauseTimer() {
        mCountDownTimer!!.cancel()
        mTimerRunning = false
        start_timer!!.visibility = View.VISIBLE
        stop_timer?.visibility = View.GONE
    }


    //카운트다운 셋팅
    private fun updateCountDownText() {
        val hour = ((mTimeLeftInMillis / (1000 * 60 * 60)) % 24)
        val minutes = (mTimeLeftInMillis / (1000 * 60)).toInt() % 60
        val seconds = (mTimeLeftInMillis / 1000).toInt() % 60
        // val milliseconds = (mTimeLeftInMillis % 1000) / 10

        val timeLeftFormatted = String.format(
            Locale.getDefault(),
            "%02d:%02d:%02d",
            hour,
            minutes,
            seconds
        )
        Text_view_countdown!!.text = timeLeftFormatted
    }

    private fun set_timer_Clicked() {
        dialog.show(this.context)

        dialog.okbutton = dialog.mLoadView?.findViewById(R.id.button_common_alert_ok)

        dialog.okbutton?.setOnClickListener {
            dialog.dismiss()

            pass = dialog.mCurrent * 3600000 + dialog.mCurrent2 * 60000 + dialog.mCurrent3 * 1000

            customize(pass.toLong())
        }
    }

    // 도움말
    private fun question_mark() {
        question_dialog.show(this.context)
    }


    private fun customize(time: Long) {
        getNum(time)
        resetTimer()
    }



    private fun timer_function(voiceinput: String) { // 타이머 음성인식을 받습니다.   //타이머 설정
        val buf = voiceinput


        if ((voiceinput.contains("분")) and (voiceinput.contains("초")) and (voiceinput.contains("시간"))) {
            val set = buf.split("분", "시간").toTypedArray()
            for (i in 0..2)
                set[i] = set[i].replace("[^0-9]".toRegex(), "")
            "h:m:s-" + set[0] + ":" + set[1] + ":" + set[2]

            pass = set[0].toInt() * 3600000 + set[1].toInt() * 60000 + set[2].toInt() * 1000
            customize(pass.toLong())
        } else if ((voiceinput.contains("분")) and (voiceinput.contains("초"))) {
            val set = buf.split("분").toTypedArray()
            for (i in 0..1)
                set[i] = set[i].replace("[^0-9]".toRegex(), "")
            "m:s-" + set[0] + ":" + set[1]
            pass = set[0].toInt() * 60000 + set[1].toInt() * 1000
            customize(pass.toLong())
        } else if ((voiceinput.contains("시간")) and (voiceinput.contains("초"))) {
            val set = buf.split("시간").toTypedArray()
            for (i in 0..1)
                set[i] = set[i].replace("[^0-9]".toRegex(), "")
            "h:s-" + set[0] + ":" + set[1]
            pass = set[0].toInt() * 3600000 + set[1].toInt() * 1000
            customize(pass.toLong())
        } else if (voiceinput.contains("초")) {
            val data = buf.replace("[^0-9]".toRegex(), "")
            pass = data.toInt() * 1000
            customize(pass.toLong())
        } else if (voiceinput.contains("분")) {
            val data2 = buf.replace("[^0-9]".toRegex(), "")
            pass = data2.toInt() * 60000
            customize(pass.toLong())
        } else if (voiceinput.contains("시간")) {
            val data = buf.replace("[^0-9]".toRegex(), "")
            pass = data.toInt() * 3600000
            customize(pass.toLong())
        } else if (voiceinput.contains("시작")) {
            startTimer()
        } else if (voiceinput.contains("정지")) {
            pauseTimer()
        }
    }

    // page부분 current는 페이지수 -1이 마지막 입니다. (ex 현재 총 페이지 수 = 5 따라서 current = 4)
    private fun previousPage() {
        val current = viewPager.currentItem
        viewPager.setCurrentItem(current - 1, true)
    }

    private fun nextPage() {
        val current = viewPager.currentItem
        viewPager.setCurrentItem(current + 1, true)
    }


    override fun onPause() {
        super.onPause()

        if (end_record?.visibility == View.VISIBLE) {
            end_record?.performClick()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (end_record?.visibility == View.VISIBLE) {
            end_record?.performClick()
        }
    }

    private fun startClicked() {
        droidSpeech?.startDroidSpeechRecognition()

        // Setting the view visibilities when droid speech is running
        start_record?.visibility = View.GONE
        end_record?.visibility = View.VISIBLE
    }

    private fun stopClicked() {
        droidSpeech?.closeDroidSpeechOperations()

        end_record?.visibility = View.GONE
        start_record?.visibility = View.VISIBLE
    }


    // MARK: DroidSpeechListener Methods
    override fun onDroidSpeechSupportedLanguages(
        currentSpeechLanguage: String,
        supportedSpeechLanguages: List<String>
    ) {
        Log.i(TAG, "Current speech language = $currentSpeechLanguage")
        Log.i(TAG, "Supported speech languages = $supportedSpeechLanguages")
        if (supportedSpeechLanguages.contains("ko-KR")) {
            // Setting the droid speech preferred language as tamil if found
            droidSpeech?.setPreferredLanguage("ko-KR")

            // Setting the confirm and retry text in tamil
            droidSpeech?.setOneStepVerifyConfirmText("check")
            droidSpeech?.setOneStepVerifyRetryText("no")
        }
    }

    override fun onDroidSpeechRmsChanged(rmsChangedValue: Float) {
        // Log.i(TAG, "Rms change value = " + rmsChangedValue);
    }

    override fun onDroidSpeechLiveResult(liveSpeechResult: String) {
        Log.i(TAG, "Live speech result = $liveSpeechResult")
    }

    override fun onDroidSpeechFinalResult(finalSpeechResult: String) {
        // Setting the final speech result

        Toast.makeText(this.requireContext(), finalSpeechResult, Toast.LENGTH_SHORT).show()


        if (finalSpeechResult == "이전") {
            print("\n\n 이전 페이지 입니다.")
            previousPage()
        } else if (finalSpeechResult == "다음") {
            print("\n\n 다음 페이지 입니다.")
            nextPage()
        } else if (finalSpeechResult.contains("타이머")) {
            voiceinput = finalSpeechResult
            print("\n\n 타이머 입니다.")
            timer_function(voiceinput)
        }

        if (droidSpeech?.continuousSpeechRecognition == true) {
            val colorPallets1 = intArrayOf(
                Color.RED,
                Color.GREEN,
                Color.BLUE,
                Color.CYAN,
                Color.MAGENTA
            )
            val colorPallets2 = intArrayOf(
                Color.YELLOW,
                Color.RED,
                Color.CYAN,
                Color.BLUE,
                Color.GREEN
            )

            // Setting random color pallets to the recognition progress view
            droidSpeech?.setRecognitionProgressViewColors(if (Random().nextInt(2) == 0) colorPallets1 else colorPallets2)
        } else {
            end_record?.visibility = View.GONE
            start_record?.visibility = View.VISIBLE
        }

    }

    override fun onDroidSpeechClosedByUser() {
        end_record?.visibility = View.GONE
        start_record?.visibility = View.VISIBLE
    }

    override fun onDroidSpeechError(errorMsg: String) {
        // Speech error
        end_record?.post { // Stop listening
            end_record?.performClick()
        }
    }

    // MARK: DroidSpeechPermissionsListener Method
    override fun onDroidSpeechAudioPermissionStatus(
        audioPermissionGiven: Boolean,
        errorMsgIfAny: String
    ) {
        if (audioPermissionGiven) {
            start_record?.post { // Start listening
                start_record?.performClick()
            }
        } else {
            // Permissions error
            end_record?.post { // Stop listening
                end_record?.performClick()
            }
        }
    }


}
