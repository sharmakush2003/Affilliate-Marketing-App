'use client'

import { useEffect, useState } from 'react'
import { CheckCircle2, ShieldCheck, Smartphone } from 'lucide-react'

export default function VerifyEmailPage() {
  const [countdown, setCountdown] = useState(3)
  const [appOpened, setAppOpened] = useState(false)
  const [deepLink, setDeepLink] = useState('')
  const [isError, setIsError] = useState(false)

  useEffect(() => {
    // Supabase passes tokens in the URL fragment (#) not search string (?)
    const hash = window.location.hash.substring(1) // remove the '#'
    const params = new URLSearchParams(hash)
    
    // Fallback to search string if not found in hash
    const searchParams = new URLSearchParams(window.location.search)
    
    const accessToken = params.get('access_token') || searchParams.get('access_token')
    const refreshToken = params.get('refresh_token') || searchParams.get('refresh_token')
    const email = params.get('email') || searchParams.get('email') ?? ''
    const error = params.get('error') || searchParams.get('error')

    if (error) {
      setIsError(true)
      return
    }

    // Build deep link with tokens so Android app can auto-login via parseSessionFromUrl
    // Format: rewardclub://home#access_token=XXX&refresh_token=YYY&token_type=bearer&type=signup
    let link = 'rewardclub://home'
    if (accessToken && refreshToken) {
      link = `rewardclub://home#access_token=${encodeURIComponent(accessToken)}&refresh_token=${encodeURIComponent(refreshToken)}&token_type=bearer&type=signup`
    }
    setDeepLink(link)

    const interval = setInterval(() => {
      setCountdown(prev => {
        if (prev <= 1) {
          clearInterval(interval)
          // Open the app
          window.location.href = link
          setTimeout(() => setAppOpened(true), 1500)
          return 0
        }
        return prev - 1
      })
    }, 1000)

    return () => clearInterval(interval)
  }, [])

  const openApp = () => {
    if (deepLink) window.location.href = deepLink
  }

  if (isError) {
    return (
      <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: 'linear-gradient(135deg, #0f172a 0%, #1e293b 100%)', fontFamily: 'Inter, system-ui, sans-serif', padding: 20 }}>
        <div style={{ width: '100%', maxWidth: 420, background: '#ffffff', borderRadius: 24, padding: '40px 28px', boxShadow: '0 25px 50px -12px rgba(0,0,0,0.4)', textAlign: 'center' }}>
          <div style={{ fontSize: 48, marginBottom: 16 }}>❌</div>
          <h1 style={{ fontSize: 20, fontWeight: 800, color: '#0f172a', margin: '0 0 10px' }}>Verification Failed</h1>
          <p style={{ fontSize: 14, color: '#475569', lineHeight: 1.7, margin: '0 0 20px' }}>The verification link may have expired. Please register again in the Reward Club app.</p>
          <div style={{ fontSize: 11, color: '#cbd5e1', textTransform: 'uppercase', letterSpacing: 1, fontWeight: 600 }}>© 2026 Reward Club • ChittorTech</div>
        </div>
      </div>
    )
  }

  return (
    <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: 'linear-gradient(135deg, #0f172a 0%, #1e293b 100%)', fontFamily: 'Inter, system-ui, sans-serif', padding: 20 }}>
      <div style={{ width: '100%', maxWidth: 420, background: '#ffffff', borderRadius: 24, padding: '40px 28px', boxShadow: '0 25px 50px -12px rgba(0,0,0,0.4)', textAlign: 'center' }}>
        {/* Success Icon */}
        <div style={{ width: 80, height: 80, borderRadius: '50%', background: 'linear-gradient(135deg, #ecfdf5, #d1fae5)', border: '2px solid #6ee7b7', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 20px' }}>
          <CheckCircle2 size={44} style={{ color: '#059669' }} />
        </div>

        <h1 style={{ fontSize: 22, fontWeight: 800, color: '#0f172a', margin: '0 0 10px' }}>Email Verified! 🎉</h1>

        <p style={{ fontSize: 14, color: '#475569', lineHeight: '1.7', margin: '0 0 28px' }}>
          Your <strong>Reward Club</strong> account is now active. Opening your app automatically...
        </p>

        {/* Countdown / Open App */}
        {!appOpened ? (
          <div style={{ background: 'linear-gradient(135deg, #FF5722, #EE0979)', borderRadius: 14, padding: '18px 20px', marginBottom: 20, color: '#fff' }}>
            <Smartphone size={22} style={{ marginBottom: 6 }} />
            <div style={{ fontSize: 15, fontWeight: 700 }}>Opening Reward Club App...</div>
            <div style={{ fontSize: 32, fontWeight: 900, marginTop: 4 }}>{countdown}</div>
          </div>
        ) : (
          <div style={{ marginBottom: 20 }}>
            <button
              onClick={openApp}
              style={{ width: '100%', background: 'linear-gradient(135deg, #FF5722, #EE0979)', color: '#fff', border: 'none', borderRadius: 12, padding: '16px 24px', fontSize: 15, fontWeight: 700, cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 8 }}
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
        <div style={{ background: '#f8fafc', border: '1px solid #e2e8f0', borderRadius: 12, padding: '12px 16px', display: 'flex', alignItems: 'center', gap: 10, textAlign: 'left', marginBottom: 20 }}>
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
