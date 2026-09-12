'use client'

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { supabase } from '@/lib/supabase'
import { Lock, Eye, EyeOff, ShieldAlert, CheckCircle2 } from 'lucide-react'

export default function ResetPasswordPage() {
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(false)
  const router = useRouter()

  useEffect(() => {
    // Check if URL hash has a redirect error from Supabase
    if (typeof window !== 'undefined') {
      const hash = window.location.hash
      if (hash && hash.includes('error=')) {
        const params = new URLSearchParams(hash.replace('#', '?'))
        const errorDescription = params.get('error_description') || 'Invalid or expired recovery link.'
        setError(errorDescription.replace(/\+/g, ' '))
      }
    }
  }, [])

  const handleUpdatePassword = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    setError('')
    setSuccess(false)

    if (password !== confirmPassword) {
      setError('Passwords do not match. Please verify.')
      setLoading(false)
      return
    }

    if (password.length < 6) {
      setError('Password must be at least 6 characters long.')
      setLoading(false)
      return
    }

    try {
      const { error: updateError } = await supabase.auth.updateUser({
        password: password,
      })

      if (updateError) {
        setError(updateError.message)
      } else {
        setSuccess(true)
        // Delay slightly for success animation and user feedback
        setTimeout(() => {
          router.push('/login')
        }, 1500)
      }
    } catch (err) {
      console.error(err)
      setError('An unexpected error occurred. Please try again.')
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
          <h1 style={styles.title}>Reset Password</h1>
          <span style={styles.subtitle}>SECURE UPDATE</span>
        </div>

        <form onSubmit={handleUpdatePassword} style={styles.form}>
          {error && (
            <div style={styles.errorAlert}>
              <ShieldAlert size={18} style={styles.alertIcon} />
              <span>{error}</span>
            </div>
          )}

          {success && (
            <div style={styles.successAlert}>
              <CheckCircle2 size={18} style={styles.alertIcon} />
              <span>Password updated! Redirecting to login...</span>
            </div>
          )}

          <div style={styles.inputGroup}>
            <label htmlFor="password" style={styles.label}>New Password</label>
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
            <label htmlFor="confirmPassword" style={styles.label}>Confirm New Password</label>
            <div style={styles.inputWrapper}>
              <Lock size={16} style={styles.inputIcon} />
              <input
                id="confirmPassword"
                type={showPassword ? 'text' : 'password'}
                required
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                placeholder="••••••••"
                style={{ ...styles.input, paddingRight: '40px' }}
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
            {loading ? <span style={styles.spinner} /> : 'Save New Password'}
          </button>
        </form>

        <div style={styles.footer}>
          <p style={styles.footerText}>Secure Admin Access Panel</p>
          <p style={styles.footerSubText}>
            Managed & protected by{' '}
            <a
              href="https://chittortech.in"
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
    width: '56px',
    height: '56px',
    background: '#ffffff',
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
    background: '#ffffff',
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
    background: '#0f172a',
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
