package com.example.xyz.utils

import java.util.Properties
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object EmailSender {
    private const val SENDER_EMAIL = "chittortech@gmail.com"
    private const val APP_PASSWORD = "yigvsszkozhvydma"

    fun sendOtpEmail(recipientEmail: String, otpCode: String): Boolean {
        val properties = Properties().apply {
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.socketFactory.port", "465")
            put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            put("mail.smtp.auth", "true")
            put("mail.smtp.port", "465")
        }

        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD)
            }
        })

        return try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(SENDER_EMAIL, "Reward Club"))
                addRecipient(Message.RecipientType.TO, InternetAddress(recipientEmail))
                subject = "Reward Club - Sign In Verification OTP"
                setText(
                    """
                    Hello,
                    
                    Your One Time Password (OTP) for verifying your login to the Reward Club app is:
                    
                    $otpCode
                    
                    This OTP is valid for 5 minutes. Please do not share this OTP with anyone.
                    
                    Best regards,
                    Reward Club Team
                    """.trimIndent()
                )
            }
            Transport.send(message)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
