'use client'

import { useState, useEffect, useRef } from 'react'
import { useRouter } from 'next/navigation'
import Link from 'next/link'
import { supabase } from '@/lib/supabase'
import { Lock, Mail, Eye, EyeOff, ShieldAlert, CheckCircle2, RotateCw, ShieldCheck } from 'lucide-react'

export default function LoginPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(false)
  const [captchaCode, setCaptchaCode] = useState('')
  const [captchaInput, setCaptchaInput] = useState('')

  // 2FA state
  const [step, setStep] = useState<'login' | 'otp'>('login')
  const [sessionId, setSessionId] = useState('')
  const [otpDigits, setOtpDigits] = useState(['', '', '', '', '', ''])
  const [otpLoading, setOtpLoading] = useState(false)
  const [otpError, setOtpError] = useState('')
  const [resendCooldown, setResendCooldown] = useState(0)
  const otpRefs = useRef<(HTMLInputElement | null)[]>([])

  const router = useRouter()

  const generateCaptcha = () => {
    const chars = '23456789ABCDEFGHJKLMNPQRSTUVWXYZ'
    let code = ''
    for (let i = 0; i < 5; i++) {
      code += chars.charAt(Math.floor(Math.random() * chars.length))
    }
    setCaptchaCode(code)
    setCaptchaInput('')
  }

  useEffect(() => {
    const checkUser = async () => {
      const { data: { session } } = await supabase.auth.getSession()
      if (session) {
        const { isAllowedAdminEmail } = await import('@/lib/adminAuth')
        if (isAllowedAdminEmail(session.user.email)) {
          router.push('/dashboard')
        }
      }
    }
    checkUser()
    generateCaptcha()

    if (typeof window !== 'undefined') {
      const params = new URLSearchParams(window.location.search)
      if (params.get('error') === 'unauthorized') {
        setError('Access Denied: You are not authorized to view the admin dashboard.')
      }
    }
  }, [router])

  // OTP resend cooldown timer
  useEffect(() => {
    if (resendCooldown <= 0) return
    const t = setTimeout(() => setResendCooldown(c => c - 1), 1000)
    return () => clearTimeout(t)
  }, [resendCooldown])

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    setError('')
    setSuccess(false)

    if (captchaInput.trim().toUpperCase() !== captchaCode) {
      setError('Verification code (CAPTCHA) is incorrect. Please try again.')
      generateCaptcha()
      setLoading(false)
      return
    }

    if (!navigator.geolocation) {
      setError('Access Denied: Your browser does not support location services.')
      setLoading(false)
      return
    }

    navigator.geolocation.getCurrentPosition(
      async (position) => {
        const { latitude, longitude } = position.coords
        let locationName = `Lat: ${latitude.toFixed(4)}, Lon: ${longitude.toFixed(4)}`

        try {
          const geoRes = await fetch(
            `https://nominatim.openstreetmap.org/reverse?format=json&lat=${latitude}&lon=${longitude}&zoom=10`,
            { headers: { 'User-Agent': 'RewardClubAdminPortal/1.0' } }
          )
          if (geoRes.ok) {
            const geoData = await geoRes.json()
            if (geoData?.display_name) locationName = geoData.display_name
          }
        } catch {}

        try {
          const prepRes = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email: email.trim().toLowerCase(), password }),
          })

          if (!prepRes.ok) {
            const prepData = await prepRes.json()
            setError(prepData.error || 'Authentication failed')
            generateCaptcha()
            setLoading(false)
            return
          }

          const { error: authError, data } = await supabase.auth.signInWithPassword({
            email: email.trim().toLowerCase(),
            password,
          })

          if (authError) {
            setError(authError.message)
            generateCaptcha()
            setLoading(false)
            return
          }

          if (data?.session) {
            const userAgent = typeof navigator !== 'undefined' ? navigator.userAgent : 'Unknown'
            const logRes = await fetch('/api/auth/log-session', {
              method: 'POST',
              headers: { 'Content-Type': 'application/json' },
              body: JSON.stringify({
                email: email.trim().toLowerCase(),
                location: locationName,
                userAgent,
              }),
            })

            if (logRes.ok) {
              const logData = await logRes.json()
              if (logData.pending2FA && logData.sessionId) {
                // Store session id and move to OTP step
                sessionStorage.setItem('current_admin_session_id', logData.sessionId)
                setSessionId(logData.sessionId)
                setResendCooldown(30)
                setLoading(false)
                setStep('otp')
                setTimeout(() => otpRefs.current[0]?.focus(), 100)
                return
              }
              // Fallback: no 2FA (shouldn't happen but handle gracefully)
              if (logData.sessionId) {
                sessionStorage.setItem('current_admin_session_id', logData.sessionId)
              }
            }

            setSuccess(true)
            setTimeout(() => router.push('/dashboard'), 800)
          }
        } catch (err) {
          console.error(err)
          setError('An unexpected error occurred. Please try again.')
          generateCaptcha()
          setLoading(false)
        }
      },
      (geoError) => {
        console.error('Geolocation error:', geoError)
        setError('Access Denied: You must grant location permission to access the admin portal.')
        generateCaptcha()
        setLoading(false)
      },
      { enableHighAccuracy: true, timeout: 8000 }
    )
  }

  const handleOtpChange = (index: number, value: string) => {
    if (!/^\d?$/.test(value)) return
    const newDigits = [...otpDigits]
    newDigits[index] = value
    setOtpDigits(newDigits)
    setOtpError('')
    if (value && index < 5) {
      otpRefs.current[index + 1]?.focus()
    }
  }

  const handleOtpKeyDown = (index: number, e: React.KeyboardEvent) => {
    if (e.key === 'Backspace' && !otpDigits[index] && index > 0) {
      otpRefs.current[index - 1]?.focus()
    }
  }

  const handleOtpPaste = (e: React.ClipboardEvent) => {
    e.preventDefault()
    const pasted = e.clipboardData.getData('text').replace(/\D/g, '').slice(0, 6)
    if (pasted.length === 6) {
      setOtpDigits(pasted.split(''))
      otpRefs.current[5]?.focus()
    }
  }

  const handleVerifyOtp = async () => {
    const code = otpDigits.join('')
    if (code.length < 6) {
      setOtpError('Please enter the complete 6-digit verification code.')
      return
    }

    setOtpLoading(true)
    setOtpError('')

    try {
      const res = await fetch('/api/auth/sessions/verify-otp', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ sessionId, otpCode: code }),
      })

      const data = await res.json()

      if (!res.ok) {
        setOtpError(data.error || 'Verification failed.')
        setOtpDigits(['', '', '', '', '', ''])
        otpRefs.current[0]?.focus()
        if (data.error?.toLowerCase().includes('expired')) {
          // Force re-login if expired
          setTimeout(() => {
            supabase.auth.signOut()
            setStep('login')
            setSessionId('')
            setOtpDigits(['', '', '', '', '', ''])
          }, 2000)
        }
        setOtpLoading(false)
        return
      }

      setSuccess(true)
      setTimeout(() => router.push('/dashboard'), 700)
    } catch {
      setOtpError('An unexpected error occurred. Please try again.')
      setOtpLoading(false)
    }
  }

  const handleResendOtp = async () => {
    if (resendCooldown > 0) return
    setResendCooldown(30)
    setOtpError('')
    setOtpDigits(['', '', '', '', '', ''])

    try {
      const res = await fetch('/api/auth/log-session', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          email: email.trim().toLowerCase(),
          location: 'Resend Request',
          userAgent: navigator.userAgent,
        }),
      })
      const data = await res.json()
      if (data.sessionId) {
        sessionStorage.setItem('current_admin_session_id', data.sessionId)
        setSessionId(data.sessionId)
      }
    } catch {}
    otpRefs.current[0]?.focus()
  }

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        {/* Header */}
        <div style={styles.header}>
          <div style={styles.logoContainer}>
            <img src="/logo.webp" alt="Logo" style={styles.logo} />
          </div>
          <h1 style={styles.title}>Reward Club</h1>
          <span style={styles.subtitle}>{step === 'otp' ? '2FA VERIFICATION' : 'ADMIN PORTAL'}</span>
        </div>

        {/* ─── STEP 1: Login Form ─── */}
        {step === 'login' && (
          <form onSubmit={handleLogin} style={styles.form}>
            {error && (
              <div style={styles.errorAlert}>
                <ShieldAlert size={18} style={styles.alertIcon} />
                <span>{error}</span>
              </div>
            )}
            {success && (
              <div style={styles.successAlert}>
                <CheckCircle2 size={18} style={styles.alertIcon} />
                <span>Login successful! Redirecting...</span>
              </div>
            )}

            <div style={styles.inputGroup}>
              <label htmlFor="email" style={styles.label}>Email Address</label>
              <div style={styles.inputWrapper}>
                <Mail size={16} style={styles.inputIcon} />
                <input id="email" type="email" required value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="admin@rewardclub.com" style={styles.input} />
              </div>
            </div>

            <div style={styles.inputGroup}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <label htmlFor="password" style={styles.label}>Password</label>
                <Link href="/forgot-password" style={styles.forgotLink}>Forgot password?</Link>
              </div>
              <div style={styles.inputWrapper}>
                <Lock size={16} style={styles.inputIcon} />
                <input id="password" type={showPassword ? 'text' : 'password'} required
                  value={password} onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••" style={{ ...styles.input, paddingRight: '40px' }} />
                <button type="button" onClick={() => setShowPassword(!showPassword)} style={styles.eyeButton}>
                  {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                </button>
              </div>
            </div>

            <div style={styles.inputGroup}>
              <label htmlFor="captcha" style={styles.label}>Security Verification</label>
              <div style={styles.captchaRow}>
                <div style={styles.captchaBox}>{captchaCode}</div>
                <button type="button" onClick={generateCaptcha} style={styles.refreshButton} title="Refresh Captcha">
                  <RotateCw size={16} />
                </button>
                <input id="captcha" type="text" required value={captchaInput}
                  onChange={(e) => setCaptchaInput(e.target.value)}
                  placeholder="Enter code" style={{ ...styles.input, flex: 1, paddingLeft: '12px' }} />
              </div>
            </div>

            <button type="submit" disabled={loading || success}
              style={{ ...styles.submitButton, ...(loading || success ? styles.submitButtonDisabled : {}) }}>
              {loading ? <span style={styles.spinner} /> : success ? 'Authorized ✓' : 'Access Dashboard'}
            </button>
          </form>
        )}

        {/* ─── STEP 2: OTP Verification ─── */}
        {step === 'otp' && (
          <div style={styles.form}>
            {/* Shield icon */}
            <div style={{ textAlign: 'center', marginBottom: 4 }}>
              <div style={styles.otpIconWrap}>
                <ShieldCheck size={28} color="#10b981" />
              </div>
            </div>

            <div style={{ textAlign: 'center', marginBottom: 4 }}>
              <p style={styles.otpTitle}>Check your email</p>
              <p style={styles.otpSub}>
                A 6-digit code was sent to <strong style={{ color: '#0f172a' }}>{email}</strong>
              </p>
            </div>

            {otpError && (
              <div style={styles.errorAlert}>
                <ShieldAlert size={18} style={styles.alertIcon} />
                <span>{otpError}</span>
              </div>
            )}
            {success && (
              <div style={styles.successAlert}>
                <CheckCircle2 size={18} style={styles.alertIcon} />
                <span>Verified! Redirecting to dashboard...</span>
              </div>
            )}

            {/* OTP digit inputs */}
            <div style={styles.otpRow} onPaste={handleOtpPaste}>
              {otpDigits.map((digit, i) => (
                <input
                  key={i}
                  ref={(el) => { otpRefs.current[i] = el }}
                  type="text"
                  inputMode="numeric"
                  maxLength={1}
                  value={digit}
                  onChange={(e) => handleOtpChange(i, e.target.value)}
                  onKeyDown={(e) => handleOtpKeyDown(i, e)}
                  style={{
                    ...styles.otpDigitInput,
                    ...(digit ? styles.otpDigitFilled : {}),
                  }}
                />
              ))}
            </div>

            <button
              onClick={handleVerifyOtp}
              disabled={otpLoading || success || otpDigits.join('').length < 6}
              style={{
                ...styles.submitButton,
                ...((otpLoading || success || otpDigits.join('').length < 6) ? styles.submitButtonDisabled : {}),
              }}
            >
              {otpLoading ? <span style={styles.spinner} /> : success ? 'Verified ✓' : 'Verify & Access Dashboard'}
            </button>

            <div style={{ textAlign: 'center', marginTop: 4 }}>
              <button onClick={handleResendOtp} disabled={resendCooldown > 0}
                style={styles.resendButton}>
                {resendCooldown > 0 ? `Resend code in ${resendCooldown}s` : 'Resend verification code'}
              </button>
              <br />
              <button onClick={() => { supabase.auth.signOut(); setStep('login'); setOtpDigits(['', '', '', '', '', '']); setOtpError('') }}
                style={{ ...styles.resendButton, color: '#94a3b8', marginTop: 4 }}>
                ← Back to login
              </button>
            </div>
          </div>
        )}

        <div style={styles.footer}>
          <p style={styles.footerText}>Secure Admin Access Panel</p>
          <p style={styles.footerSubText}>
            Managed & protected by{' '}
            <a href="https://chittortech.online" target="_blank" rel="noopener noreferrer"
              style={{ color: '#10b981', textDecoration: 'none', fontWeight: '600' }}>
              ChittorTech
            </a>
          </p>
        </div>
      </div>

      <style>{`
        @keyframes spin { to { transform: rotate(360deg); } }
      `}</style>
    </div>
  )
}

const styles: Record<string, React.CSSProperties> = {
  container: {
    minHeight: '100vh',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    background: '#ffffff',
    fontFamily: 'Inter, system-ui, -apple-system, sans-serif',
    padding: '20px',
  },
  card: {
    width: '100%',
    maxWidth: '400px',
    background: '#f8fafc',
    border: '1px solid #e2e8f0',
    borderRadius: '20px',
    padding: '36px 30px',
    boxShadow: '0 8px 30px rgba(0, 0, 0, 0.04)',
    display: 'flex',
    flexDirection: 'column',
    gap: '24px',
  },
  header: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    textAlign: 'center',
  },
  logoContainer: {
    width: '56px', height: '56px',
    background: '#ffffff',
    border: '1px solid #e2e8f0',
    borderRadius: '14px',
    display: 'flex', alignItems: 'center', justifyContent: 'center',
    marginBottom: '12px',
    boxShadow: '0 1px 3px rgba(0,0,0,0.02)',
  },
  logo: { width: '32px', height: '32px', objectFit: 'contain' },
  title: { fontSize: '24px', fontWeight: '800', color: '#0f172a', margin: '0 0 2px 0', letterSpacing: '-0.03em' },
  subtitle: { fontSize: '10px', fontWeight: '800', color: '#10b981', letterSpacing: '2.5px', textTransform: 'uppercase' },
  form: { display: 'flex', flexDirection: 'column', gap: '16px' },
  errorAlert: {
    background: '#fef2f2', border: '1px solid #fee2e2', borderRadius: '10px',
    padding: '10px 14px', color: '#991b1b', fontSize: '12.5px', fontWeight: '500',
    display: 'flex', alignItems: 'center', gap: '8px', lineHeight: '1.4',
  },
  successAlert: {
    background: '#ecfdf5', border: '1px solid #d1fae5', borderRadius: '10px',
    padding: '10px 14px', color: '#065f46', fontSize: '12.5px', fontWeight: '500',
    display: 'flex', alignItems: 'center', gap: '8px',
  },
  alertIcon: { flexShrink: 0 },
  inputGroup: { display: 'flex', flexDirection: 'column', gap: '6px' },
  label: { fontSize: '12px', fontWeight: '600', color: '#475569', paddingLeft: '2px' },
  forgotLink: { fontSize: '11.5px', fontWeight: '600', color: '#10b981', textDecoration: 'none' },
  captchaRow: { display: 'flex', alignItems: 'center', gap: '8px' },
  captchaBox: {
    background: '#f1f5f9', border: '1px dashed #cbd5e1', borderRadius: '10px',
    padding: '10px 16px', fontSize: '18px', fontWeight: 'bold', letterSpacing: '4px',
    fontFamily: 'monospace', color: '#0f172a', textDecoration: 'line-through',
    userSelect: 'none', fontStyle: 'italic',
    backgroundImage: 'radial-gradient(circle, #e2e8f0 10%, transparent 11%), radial-gradient(circle, #e2e8f0 10%, transparent 11%)',
    backgroundSize: '8px 8px', backgroundPosition: '0 0, 4px 4px',
  },
  refreshButton: {
    background: '#ffffff', border: '1px solid #cbd5e1', borderRadius: '10px',
    width: '38px', height: '38px', display: 'flex', alignItems: 'center', justifyContent: 'center',
    color: '#64748b', cursor: 'pointer', flexShrink: 0, outline: 'none',
  },
  inputWrapper: { position: 'relative', display: 'flex', alignItems: 'center' },
  inputIcon: { position: 'absolute', left: '12px', color: '#94a3b8', pointerEvents: 'none' },
  input: {
    width: '100%', background: '#ffffff', border: '1px solid #cbd5e1', borderRadius: '10px',
    padding: '10px 12px 10px 38px', color: '#0f172a', fontSize: '13.5px',
    outline: 'none', transition: 'all 0.15s ease', fontFamily: 'inherit',
  },
  eyeButton: {
    position: 'absolute', right: '10px', background: 'transparent', border: 'none',
    color: '#94a3b8', cursor: 'pointer', width: '28px', height: '28px',
    display: 'flex', alignItems: 'center', justifyContent: 'center', borderRadius: '50%', outline: 'none',
  },
  submitButton: {
    background: '#0f172a', border: 'none', borderRadius: '10px', padding: '12px',
    color: '#ffffff', fontSize: '14px', fontWeight: '700', cursor: 'pointer',
    transition: 'all 0.15s ease', display: 'flex', alignItems: 'center', justifyContent: 'center',
    marginTop: '6px', fontFamily: 'inherit',
  },
  submitButtonDisabled: { background: '#94a3b8', cursor: 'not-allowed' },
  spinner: {
    width: '18px', height: '18px', border: '2px solid rgba(255,255,255,0.2)',
    borderTopColor: '#ffffff', borderRadius: '50%', animation: 'spin 0.6s linear infinite',
  },
  footer: { textAlign: 'center', borderTop: '1px solid #f1f5f9', paddingTop: '16px' },
  footerText: { fontSize: '10px', color: '#94a3b8', margin: '0 0 2px 0', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.8px' },
  footerSubText: { fontSize: '9.5px', color: '#64748b', margin: 0, fontWeight: '500' },
  // OTP styles
  otpIconWrap: {
    width: '56px', height: '56px', background: '#ecfdf5', border: '1px solid #d1fae5',
    borderRadius: '16px', display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
    marginBottom: '8px',
  },
  otpTitle: { fontSize: '17px', fontWeight: '800', color: '#0f172a', margin: '0 0 4px 0' },
  otpSub: { fontSize: '12.5px', color: '#64748b', margin: 0, lineHeight: '1.5' },
  otpRow: { display: 'flex', gap: '10px', justifyContent: 'center' },
  otpDigitInput: {
    width: '44px', height: '52px', textAlign: 'center', fontSize: '22px', fontWeight: '800',
    background: '#ffffff', border: '2px solid #e2e8f0', borderRadius: '12px',
    color: '#0f172a', outline: 'none', fontFamily: 'monospace',
    transition: 'border-color 0.15s ease',
  },
  otpDigitFilled: { border: '2px solid #10b981', background: '#f0fdf4' },
  resendButton: {
    background: 'none', border: 'none', cursor: 'pointer',
    fontSize: '12px', color: '#10b981', fontWeight: '600', padding: '4px 0',
  },
}
