package com.c4project.Jachi3kki.timer

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import com.c4project.Jachi3kki.R

/**
 * Created by maochuns.sun@gmail.com on 2020/7/9
 * */
class NumberPickerDialog {
    private var mAlertDialog: AlertDialog? = null
    var mLoadView: View? = null


    var okbutton: Button? = null
    var cancelbutton: Button? = null

    var mMax = 100
    var mMax_60 = 59

    var mMin = 0
    var mCurrent = 0
    var mCurrent2 = 0
    var mCurrent3 = 0
    fun show(context: Context?) {
        mAlertDialog = AlertDialog.Builder(context!!).create()
        mLoadView = LayoutInflater.from(context).inflate(R.layout.dialog_number_picker, null)
        mAlertDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mAlertDialog!!.setView(mLoadView, 0, 0, 0, 0)
        mAlertDialog!!.setCanceledOnTouchOutside(true)

        val numPicket = mLoadView?.findViewById<NumberPicker>(R.id.numberPicker)

        numPicket?.maxValue = mMax
        numPicket?.minValue = mMin
        numPicket?.value = mCurrent

        val numPicket2 = mLoadView?.findViewById<NumberPicker>(R.id.numberPicker2)

        numPicket2?.maxValue = mMax_60
        numPicket2?.minValue = mMin
        numPicket2?.value = mCurrent2

        val numPicket3 = mLoadView?.findViewById<NumberPicker>(R.id.numberPicker3)
        numPicket3?.maxValue = mMax_60
        numPicket3?.minValue = mMin
        numPicket3?.value = mCurrent3

        numPicket?.setOnValueChangedListener { picker, oldVal, newVal -> mCurrent = newVal }
        numPicket2?.setOnValueChangedListener { picker2, oldVal2, newVal2 -> mCurrent2 = newVal2 }
        numPicket3?.setOnValueChangedListener { picker3, oldVal3, newVal3 -> mCurrent3 = newVal3 }

        cancelbutton = mLoadView?.findViewById(R.id.button_common_alert_cancel)

        cancelbutton?.setOnClickListener{
            dismiss()
        }


        try {
            mAlertDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun dismiss() {
        if (mAlertDialog != null) {
            mAlertDialog!!.dismiss()
        }
    }
}