'use client'

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import Link from 'next/link'
import { supabase } from '@/lib/supabase'
import { Lock, Mail, Eye, EyeOff, ShieldAlert, CheckCircle2, RotateCw } from 'lucide-react'

export default function LoginPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(false)
  const [captchaCode, setCaptchaCode] = useState('')
  const [captchaInput, setCaptchaInput] = useState('')
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

  // If already authenticated, redirect immediately to dashboard
  useEffect(() => {
    const checkUser = async () => {
      const { data: { session } } = await supabase.auth.getSession()
      if (session) {
        router.push('/dashboard')
      }
    }
    checkUser()
    generateCaptcha()
  }, [router])

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    setError('')
    setSuccess(false)

    // Verify Captcha
    if (captchaInput.trim().toUpperCase() !== captchaCode) {
      setError('Verification code (CAPTCHA) is incorrect. Please try again.')
      generateCaptcha()
      setLoading(false)
      return
    }

    try {
      const { error: authError, data } = await supabase.auth.signInWithPassword({
        email,
        password,
      })

      if (authError) {
        setError(authError.message)
        generateCaptcha()
      } else if (data?.session) {
        setSuccess(true)
        // Delay slightly for success animation
        setTimeout(() => {
          router.push('/dashboard')
        }, 800)
      }
    } catch (err) {
      console.error(err)
      setError('An unexpected error occurred. Please try again.')
      generateCaptcha()
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <div style={styles.header}>
          <div style={styles.logoContainer}>
            <img src="/logo.webp" alt="Logo" style={styles.logo} />
          </div>
          <h1 style={styles.title}>Reward Club</h1>
          <span style={styles.subtitle}>ADMIN PORTAL</span>
        </div>

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
              <input
                id="email"
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="admin@rewardclub.com"
                style={styles.input}
              />
            </div>
          </div>

          <div style={styles.inputGroup}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <label htmlFor="password" style={styles.label}>Password</label>
              <Link href="/forgot-password" style={styles.forgotLink}>
                Forgot password?
              </Link>
            </div>
            <div style={styles.inputWrapper}>
              <Lock size={16} style={styles.inputIcon} />
              <input
                id="password"
                type={showPassword ? 'text' : 'password'}
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                style={{ ...styles.input, paddingRight: '40px' }}
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                style={styles.eyeButton}
              >
                {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
              </button>
            </div>
          </div>

          <div style={styles.inputGroup}>
            <label htmlFor="captcha" style={styles.label}>Security Verification</label>
            <div style={styles.captchaRow}>
              <div style={styles.captchaBox}>
                {captchaCode}
              </div>
              <button
                type="button"
                onClick={generateCaptcha}
                style={styles.refreshButton}
                title="Refresh Captcha"
              >
                <RotateCw size={16} />
              </button>
              <input
                id="captcha"
                type="text"
                required
                value={captchaInput}
                onChange={(e) => setCaptchaInput(e.target.value)}
                placeholder="Verification code"
                style={{ ...styles.input, flex: 1, paddingLeft: '12px' }}
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading || success}
            style={{
              ...styles.submitButton,
              ...(loading || success ? styles.submitButtonDisabled : {}),
            }}
          >
            {loading ? (
              <span style={styles.spinner} />
            ) : success ? (
              'Authorized ✓'
            ) : (
              'Access Dashboard'
            )}
          </button>
        </form>

        <div style={styles.footer}>
          <p style={styles.footerText}>Secure Admin Access Panel</p>
          <p style={styles.footerSubText}>
            Managed & protected by{' '}
            <a
              href="https://chittortech.online"
              target="_blank"
              rel="noopener noreferrer"
              style={{ color: '#10b981', textDecoration: 'none', fontWeight: '600' }}
            >
              ChittorTech
            </a>
          </p>
        </div>
      </div>
    </div>
  )
}

const styles: Record<string, React.CSSProperties> = {
  container: {
    minHeight: '100vh',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    background: '#ffffff', // Pure white page background
    fontFamily: 'Inter, system-ui, -apple-system, sans-serif',
    padding: '20px',
  },
  card: {
    width: '100%',
    maxWidth: '400px',
    background: '#f8fafc', // Modern soft off-white/light-slate card
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
    width: '56px',
    height: '56px',
    background: '#ffffff', // Pure white logo background to stand out
    border: '1px solid #e2e8f0',
    borderRadius: '14px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: '12px',
    boxShadow: '0 1px 3px rgba(0,0,0,0.02)',
  },
  logo: {
    width: '32px',
    height: '32px',
    objectFit: 'contain',
  },
  title: {
    fontSize: '24px',
    fontWeight: '800',
    color: '#0f172a',
    margin: '0 0 2px 0',
    letterSpacing: '-0.03em',
  },
  subtitle: {
    fontSize: '10px',
    fontWeight: '800',
    color: '#10b981',
    letterSpacing: '2.5px',
    textTransform: 'uppercase',
  },
  form: {
    display: 'flex',
    flexDirection: 'column',
    gap: '16px',
  },
  errorAlert: {
    background: '#fef2f2',
    border: '1px solid #fee2e2',
    borderRadius: '10px',
    padding: '10px 14px',
    color: '#991b1b',
    fontSize: '12.5px',
    fontWeight: '500',
    display: 'flex',
    alignItems: 'center',
    gap: '8px',
    lineHeight: '1.4',
  },
  successAlert: {
    background: '#ecfdf5',
    border: '1px solid #d1fae5',
    borderRadius: '10px',
    padding: '10px 14px',
    color: '#065f46',
    fontSize: '12.5px',
    fontWeight: '500',
    display: 'flex',
    alignItems: 'center',
    gap: '8px',
  },
  alertIcon: {
    flexShrink: 0,
  },
  inputGroup: {
    display: 'flex',
    flexDirection: 'column',
    gap: '6px',
  },
  label: {
    fontSize: '12px',
    fontWeight: '600',
    color: '#475569',
    paddingLeft: '2px',
  },
  forgotLink: {
    fontSize: '11.5px',
    fontWeight: '600',
    color: '#10b981',
    textDecoration: 'none',
  },
  captchaRow: {
    display: 'flex',
    alignItems: 'center',
    gap: '8px',
  },
  captchaBox: {
    background: '#f1f5f9',
    border: '1px dashed #cbd5e1',
    borderRadius: '10px',
    padding: '10px 16px',
    fontSize: '18px',
    fontWeight: 'bold',
    letterSpacing: '4px',
    fontFamily: 'monospace',
    color: '#0f172a',
    textDecoration: 'line-through',
    userSelect: 'none',
    fontStyle: 'italic',
    backgroundImage: 'radial-gradient(circle, #e2e8f0 10%, transparent 11%), radial-gradient(circle, #e2e8f0 10%, transparent 11%)',
    backgroundSize: '8px 8px',
    backgroundPosition: '0 0, 4px 4px',
  },
  refreshButton: {
    background: '#ffffff',
    border: '1px solid #cbd5e1',
    borderRadius: '10px',
    width: '38px',
    height: '38px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    color: '#64748b',
    cursor: 'pointer',
    flexShrink: 0,
    outline: 'none',
  },
  inputWrapper: {
    position: 'relative',
    display: 'flex',
    alignItems: 'center',
  },
  inputIcon: {
    position: 'absolute',
    left: '12px',
    color: '#94a3b8',
    pointerEvents: 'none',
  },
  input: {
    width: '100%',
    background: '#ffffff', // High-contrast pure white inputs
    border: '1px solid #cbd5e1',
    borderRadius: '10px',
    padding: '10px 12px 10px 38px',
    color: '#0f172a',
    fontSize: '13.5px',
    outline: 'none',
    transition: 'all 0.15s ease',
    fontFamily: 'inherit',
  },
  eyeButton: {
    position: 'absolute',
    right: '10px',
    background: 'transparent',
    border: 'none',
    color: '#94a3b8',
    cursor: 'pointer',
    width: '28px',
    height: '28px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: '50%',
    outline: 'none',
  },
  submitButton: {
    background: '#0f172a', // Solid corporate dark theme
    border: 'none',
    borderRadius: '10px',
    padding: '12px',
    color: '#ffffff',
    fontSize: '14px',
    fontWeight: '700',
    cursor: 'pointer',
    transition: 'all 0.15s ease',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    marginTop: '6px',
    fontFamily: 'inherit',
  },
  submitButtonDisabled: {
    background: '#94a3b8',
    cursor: 'not-allowed',
  },
  spinner: {
    width: '18px',
    height: '18px',
    border: '2px solid rgba(255, 255, 255, 0.2)',
    borderTopColor: '#ffffff',
    borderRadius: '50%',
    animation: 'spin 0.6s linear infinite',
  },
  footer: {
    textAlign: 'center',
    borderTop: '1px solid #f1f5f9',
    paddingTop: '16px',
  },
  footerText: {
    fontSize: '10px',
    color: '#94a3b8',
    margin: '0 0 2px 0',
    fontWeight: '700',
    textTransform: 'uppercase',
    letterSpacing: '0.8px',
  },
  footerSubText: {
    fontSize: '9.5px',
    color: '#64748b',
    margin: 0,
    fontWeight: '500',
  },
}
