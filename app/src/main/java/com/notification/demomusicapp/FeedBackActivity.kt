package com.notification.demomusicapp

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.notification.demomusicapp.databinding.ActivityFeedBackBinding
import java.util.*
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class FeedBackActivity : AppCompatActivity() {

    lateinit var binding: ActivityFeedBackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_DemoMusicApp)
        binding = ActivityFeedBackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title ="Feedback"
        binding.sendFEA.setOnClickListener {
            val feedbackMsg = binding.feedbackMsgFEA.text.toString() + "\n" + binding.emailFEA.text.toString()
            val subject = binding.topicFEA.text.toString()
            val userName = "ujjawal.dudani@twoiq.com"
            val pass = "7069938563@twoiq"
            val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if(feedbackMsg.isNotEmpty() && subject.isNotEmpty() && (cm.activeNetworkInfo?.isConnectedOrConnecting == true)){
                Thread{
                    try {
                        val properties = Properties()
                        properties["mail.smtp.auth"] = "true"
                        properties["mail.smtp.starttls.enable"] = "true"
                        properties["mail.smtp.host"] = "smtp.gmail.com" // smtp.twoiq.com
                        properties["mail.smtp.port"] = "587"
                        val session = Session.getInstance(properties, object : javax.mail.Authenticator(){
                            override fun getPasswordAuthentication(): javax.mail.PasswordAuthentication {
                                return javax.mail.PasswordAuthentication(userName, pass)
                            }
                        })
                        val mail = MimeMessage(session)
                        mail.subject = subject
                        mail.setText(feedbackMsg)
                        mail.setFrom(InternetAddress(userName))
                        mail.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userName))
                        Transport.send(mail)
                    }catch (e: Exception){Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()}
                }.start()
                Toast.makeText(this, "Thanks For Feedback!!", Toast.LENGTH_SHORT).show()
                finish()
            }
            else Toast.makeText(this, "Went Something Wrong!!", Toast.LENGTH_SHORT).show()
        }
    }
}