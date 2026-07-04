package com.example.xyz.utils

import java.util.Properties
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object EmailSender {
    private const val SENDER_EMAIL = "rewardclub.team@gmail.com"
    private const val APP_PASSWORD = "trwqwwxttewvvuoi"

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
                    
                    You have requested a secure One-Time Password (OTP) to verify your account session on Reward Club.
                    
                    Please use the security code detailed inside this email to authorize your access.
                    
                    ------------------------------------------------------------
                    SECURITY WARNING: For security reasons, please do NOT share this code with anyone. Reward Club support representatives or agents will never ask for this code.
                    ------------------------------------------------------------
                    
                    Your authorization code is:
                    
                    $otpCode
                    
                    This code is valid for 5 minutes. If you did not request this verification, please secure your account immediately.
                    
                    
                    Best regards,
                    Reward Club Team
                    Developed by ChittorTech
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
