'use client'

import { useEffect, useState } from 'react'
import { usePathname } from 'next/navigation'
import { MonitorOff, ShieldAlert } from 'lucide-react'

export function DeviceGuard({ children }: { children: React.ReactNode }) {
  const [isMobile, setIsMobile] = useState<boolean | null>(null)
  const pathname = usePathname()

  // Allow public verification & confirmation pages on any device (Mobile or Desktop)
  const isPublicPath = pathname?.startsWith('/verify') || 
                       pathname?.startsWith('/confirm') || 
                       pathname?.startsWith('/auth') ||
                       pathname === '/'

  useEffect(() => {
    const checkDevice = () => {
      const ua = navigator.userAgent || navigator.vendor || (window as any).opera
      const isMobileUA = /android.+mobile|iphone|ipod|blackberry|iemobile|opera mini/i.test(ua.toLowerCase())
      setIsMobile(isMobileUA)
    }

    checkDevice()
    window.addEventListener('resize', checkDevice)
    return () => window.removeEventListener('resize', checkDevice)
  }, [])

  if (isPublicPath) {
    return <>{children}</>
  }

  if (isMobile === null) {
    return null
  }

  if (isMobile) {
    return (
      <div style={styles.container}>
        {/* Decorative background gradients */}
        <div style={styles.blob1} />
        <div style={styles.blob2} />

        <div style={styles.card}>
          <div style={styles.iconWrapper}>
            <MonitorOff size={32} style={styles.icon} />
          </div>
          <h1 style={styles.title}>Desktop Access Required</h1>
          <p style={styles.description}>
            The Reward Club Admin Portal is restricted to wider screens for security, layout integrity, and data management.
          </p>
          <div style={styles.infoBox}>
            <ShieldAlert size={16} style={styles.infoIcon} />
            <span>Mobile phone browsers (including Mobile Desktop Agent view) are not supported.</span>
          </div>
          <p style={styles.footer}>
            Please log in from a Laptop, Desktop, or Tablet device.
          </p>
        </div>
      </div>
    )
  }

  return <>{children}</>
}

const styles: Record<string, React.CSSProperties> = {
  container: {
    minHeight: '100vh',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    background: '#090a0f',
    position: 'relative',
    overflow: 'hidden',
    fontFamily: 'Inter, system-ui, -apple-system, sans-serif',
    padding: '20px',
    textAlign: 'center',
  },
  blob1: {
    position: 'absolute',
    width: '300px',
    height: '300px',
    background: 'radial-gradient(circle, rgba(239, 68, 68, 0.08) 0%, rgba(239, 68, 68, 0) 70%)',
    top: '10%',
    left: '10%',
    filter: 'blur(30px)',
  },
  blob2: {
    position: 'absolute',
    width: '300px',
    height: '300px',
    background: 'radial-gradient(circle, rgba(16, 185, 129, 0.05) 0%, rgba(16, 185, 129, 0) 70%)',
    bottom: '10%',
    right: '10%',
    filter: 'blur(30px)',
  },
  card: {
    width: '100%',
    maxWidth: '400px',
    background: 'rgba(17, 18, 25, 0.8)',
    backdropFilter: 'blur(20px)',
    border: '1px solid rgba(255, 255, 255, 0.08)',
    borderRadius: '24px',
    padding: '40px 24px',
    boxShadow: '0 20px 40px rgba(0, 0, 0, 0.4)',
    zIndex: 1,
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    gap: '20px',
  },
  iconWrapper: {
    width: '64px',
    height: '64px',
    background: 'rgba(239, 68, 68, 0.1)',
    border: '1px solid rgba(239, 68, 68, 0.2)',
    borderRadius: '20px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
  icon: {
    color: '#ef4444',
  },
  title: {
    fontSize: '20px',
    fontWeight: '800',
    color: '#ffffff',
    margin: 0,
    letterSpacing: '-0.02em',
  },
  description: {
    fontSize: '13.5px',
    color: '#94a3b8',
    lineHeight: '1.6',
    margin: 0,
  },
  infoBox: {
    background: 'rgba(255, 255, 255, 0.03)',
    border: '1px solid rgba(255, 255, 255, 0.06)',
    borderRadius: '12px',
    padding: '12px 16px',
    color: '#94a3b8',
    fontSize: '12px',
    fontWeight: '500',
    display: 'flex',
    alignItems: 'flex-start',
    gap: '10px',
    textAlign: 'left',
    lineHeight: '1.5',
  },
  infoIcon: {
    color: '#f59e0b',
    flexShrink: 0,
    marginTop: '2px',
  },
  footer: {
    fontSize: '12px',
    color: '#64748b',
    fontWeight: '600',
    margin: 0,
  },
}
