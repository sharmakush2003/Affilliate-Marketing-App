'use client'

import { useState } from 'react'
import Link from 'next/link'
import { supabase } from '@/lib/supabase'
import { Mail, ShieldAlert, CheckCircle2, ArrowLeft, Send } from 'lucide-react'

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(false)

  const handleReset = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    setError('')
    setSuccess(false)

    try {
      const res = await fetch('/api/auth/forgot-password', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email }),
      })

      const data = await res.json()
      if (!res.ok) {
        setError(data.error || 'Failed to send recovery email')
      } else {
        setSuccess(true)
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
          <h1 style={styles.title}>Password Recovery</h1>
          <span style={styles.subtitle}>SECURE RESET</span>
        </div>

        {!success ? (
          <form onSubmit={handleReset} style={styles.form}>
            <p style={styles.helperText}>
              Enter your registered administrator email address below. We will send you a secure link to reset your account password.
            </p>

            {error && (
              <div style={styles.errorAlert}>
                <ShieldAlert size={18} style={styles.alertIcon} />
                <span>{error}</span>
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

            <button
              type="submit"
              disabled={loading}
              style={{
                ...styles.submitButton,
                ...(loading ? styles.submitButtonDisabled : {}),
              }}
            >
              {loading ? <span style={styles.spinner} /> : 'Send Recovery Link'}
            </button>

            <Link href="/login" style={styles.backButton}>
              <ArrowLeft size={16} />
              <span>Back to Login</span>
            </Link>
          </form>
        ) : (
          <div style={styles.successWrapper}>
            <div style={styles.successBadge}>
              <Send size={24} style={styles.sendIcon} />
            </div>
            <h2 style={styles.successTitle}>Recovery Link Sent</h2>
            <p style={styles.successMessage}>
              We have dispatched a secure password reset link to <strong>{email}</strong>. Please check your inbox and follow the instructions to set up your new credentials.
            </p>
            <div style={{ width: '100%', height: '1px', background: '#e2e8f0', margin: '16px 0' }} />
            <Link href="/login" style={styles.submitButton}>
              Return to Sign In
            </Link>
          </div>
        )}

        <div style={styles.footer}>
          <p style={styles.footerText}>Secure Admin Access Panel</p>
          <p style={styles.footerSubText}>Managed & protected by ChittorTech</p>
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
  helperText: {
    fontSize: '13px',
    color: '#64748b',
    lineHeight: '1.6',
    margin: '0 0 4px 0',
    textAlign: 'center',
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
    textDecoration: 'none',
  },
  submitButtonDisabled: {
    background: '#94a3b8',
    cursor: 'not-allowed',
  },
  backButton: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: '8px',
    color: '#64748b',
    fontSize: '13px',
    fontWeight: '600',
    textDecoration: 'none',
    marginTop: '10px',
    transition: 'color 0.15s ease',
  },
  spinner: {
    width: '18px',
    height: '18px',
    border: '2px solid rgba(255, 255, 255, 0.2)',
    borderTopColor: '#ffffff',
    borderRadius: '50%',
    animation: 'spin 0.6s linear infinite',
  },
  successWrapper: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    textAlign: 'center',
    gap: '12px',
  },
  successBadge: {
    width: '48px',
    height: '48px',
    borderRadius: '50%',
    background: 'rgba(16, 185, 129, 0.1)',
    border: '1px solid rgba(16, 185, 129, 0.2)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
  sendIcon: {
    color: '#10b981',
  },
  successTitle: {
    fontSize: '18px',
    fontWeight: '800',
    color: '#0f172a',
    margin: 0,
  },
  successMessage: {
    fontSize: '13px',
    color: '#64748b',
    lineHeight: '1.6',
    margin: 0,
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
