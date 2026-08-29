'use client'

import { useEffect, useState } from 'react'
import { CheckCircle2, ShieldCheck, Smartphone } from 'lucide-react'

export default function VerifyEmailPage() {
  const [countdown, setCountdown] = useState(3)
  const [appOpened, setAppOpened] = useState(false)

  useEffect(() => {
    // Immediately try to open the app via deep link
    const tryOpenApp = () => {
      // Try custom deep link scheme first
      window.location.href = 'rewardclub://home'

      // Fallback: if app didn't open after 1.5s, show manual button
      setTimeout(() => {
        setAppOpened(true)
      }, 1500)
    }

    // Countdown then open
    const interval = setInterval(() => {
      setCountdown(prev => {
        if (prev <= 1) {
          clearInterval(interval)
          tryOpenApp()
          return 0
        }
        return prev - 1
      })
    }, 1000)

    return () => clearInterval(interval)
  }, [])

  const openApp = () => {
    window.location.href = 'rewardclub://home'
  }

  return (
    <div
      style={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: 'linear-gradient(135deg, #0f172a 0%, #1e293b 100%)',
        fontFamily: 'Inter, system-ui, sans-serif',
        padding: 20,
      }}
    >
      <div
        style={{
          width: '100%',
          maxWidth: 420,
          background: '#ffffff',
          borderRadius: 24,
          padding: '40px 28px',
          boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.4)',
          textAlign: 'center',
        }}
      >
        {/* Success Icon */}
        <div
          style={{
            width: 80,
            height: 80,
            borderRadius: '50%',
            background: 'linear-gradient(135deg, #ecfdf5, #d1fae5)',
            border: '2px solid #6ee7b7',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            margin: '0 auto 20px',
          }}
        >
          <CheckCircle2 size={44} style={{ color: '#059669' }} />
        </div>

        <h1 style={{ fontSize: 22, fontWeight: 800, color: '#0f172a', margin: '0 0 10px' }}>
          Email Verified! 🎉
        </h1>

        <p style={{ fontSize: 14, color: '#475569', lineHeight: '1.7', margin: '0 0 28px' }}>
          Your <strong>Reward Club</strong> account is now active. You can start earning coins and rewards!
        </p>

        {/* Countdown / Open App */}
        {!appOpened ? (
          <div
            style={{
              background: 'linear-gradient(135deg, #FF5722, #EE0979)',
              borderRadius: 14,
              padding: '18px 20px',
              marginBottom: 20,
              color: '#fff',
            }}
          >
            <Smartphone size={22} style={{ marginBottom: 6 }} />
            <div style={{ fontSize: 15, fontWeight: 700 }}>
              Opening Reward Club App...
            </div>
            <div style={{ fontSize: 28, fontWeight: 900, marginTop: 4 }}>
              {countdown}
            </div>
          </div>
        ) : (
          <div style={{ marginBottom: 20 }}>
            <button
              onClick={openApp}
              style={{
                width: '100%',
                background: 'linear-gradient(135deg, #FF5722, #EE0979)',
                color: '#fff',
                border: 'none',
                borderRadius: 12,
                padding: '16px 24px',
                fontSize: 15,
                fontWeight: 700,
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: 8,
              }}
            >
              <Smartphone size={18} />
              Open Reward Club App
            </button>
            <p style={{ fontSize: 12, color: '#94a3b8', marginTop: 12, lineHeight: 1.5 }}>
              If the app doesn&apos;t open, make sure Reward Club is installed on your device.
            </p>
          </div>
        )}

        {/* Security badge */}
        <div
          style={{
            background: '#f8fafc',
            border: '1px solid #e2e8f0',
            borderRadius: 12,
            padding: '12px 16px',
            display: 'flex',
            alignItems: 'center',
            gap: 10,
            textAlign: 'left',
            marginBottom: 20,
          }}
        >
          <ShieldCheck size={20} style={{ color: '#0284c7', flexShrink: 0 }} />
          <div style={{ fontSize: 12, color: '#475569' }}>
            <strong style={{ color: '#0f172a' }}>Secured by ChittorTech</strong><br />
            Your data is encrypted and protected.
          </div>
        </div>

        <div style={{ fontSize: 11, color: '#cbd5e1', textTransform: 'uppercase', letterSpacing: 1, fontWeight: 600 }}>
          © 2026 Reward Club • ChittorTech
        </div>
      </div>
    </div>
  )
}
