package com.mediaset.gigyatest

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gigya.android.sdk.Gigya
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        UserManager.initialize(false)

        setUpButtons()

        login_btn.setOnClickListener {

            UserManager.login (
                onLogin = {

                    onDialog("LoggedUser")
                    setUpButtons()
                },
                onDismiss = {}
            )
        }

        logout_btn.setOnClickListener {

            UserManager.logout(false)
            onDialog("LoggedUser")
            setUpButtons()
        }
    }

    private fun setUpButtons() {

        if(UserManager.user != null) {

            login_btn.isEnabled = false
            logout_btn.isEnabled = true
        }else{

            login_btn.isEnabled = true
            logout_btn.isEnabled = false
        }
    }

    private fun onDialog( msg: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("$msg: ${UserManager.user?.uid}")
            .setCancelable(true)
        val dialog = builder.create()
        dialog.show()
    }
}