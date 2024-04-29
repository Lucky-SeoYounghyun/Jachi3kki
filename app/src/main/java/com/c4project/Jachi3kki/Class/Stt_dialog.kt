package com.c4project.Jachi3kki.Class
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.c4project.Jachi3kki.R
// STT 음성을 텍스트로 바꿔줄때 사용되는 class
class Stt_dialog {

    private var mAlertDialog: AlertDialog? = null
    var mLoadView: View? = null


    var okbutton: Button? = null

    fun show(context: Context?) {
        mAlertDialog = AlertDialog.Builder(context!!).create()
        mLoadView = LayoutInflater.from(context).inflate(R.layout.dialog_stt, null)
        mAlertDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mAlertDialog!!.setView(mLoadView, 0, 0, 0, 0)
        mAlertDialog!!.setCanceledOnTouchOutside(true)

        okbutton = mLoadView?.findViewById(R.id.ok_button)

        okbutton?.setOnClickListener{
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