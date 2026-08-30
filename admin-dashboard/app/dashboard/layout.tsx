'use client'

import { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import { supabase } from '@/lib/supabase'
import { Sidebar } from '@/components/Sidebar'
import { Header } from '@/components/Header'
import { isAllowedAdminEmail } from '@/lib/adminAuth'
import type { ReactNode } from 'react'

export default function Layout({ children }: { children: ReactNode }) {
  const [loading, setLoading] = useState(true)
  const [authenticated, setAuthenticated] = useState(false)
  const router = useRouter()

  useEffect(() => {
    const checkAuth = async () => {
      try {
        const { data: { session } } = await supabase.auth.getSession()
        if (!session) {
          router.push('/login')
          return
        }

        // Verify if logged-in user is allowed to access the admin dashboard
        if (!isAllowedAdminEmail(session.user.email)) {
          console.warn(`[dashboard/layout] Access denied for user: ${session.user.email}`)
          sessionStorage.removeItem('current_admin_session_id')
          await supabase.auth.signOut()
          router.push('/login?error=unauthorized')
          return
        }

        const sessionId = sessionStorage.getItem('current_admin_session_id')
        if (sessionId) {
          const verifyRes = await fetch(`/api/auth/sessions/verify?id=${sessionId}`)
          if (verifyRes.ok) {
            const verifyData = await verifyRes.json()
            if (!verifyData.valid) {
              sessionStorage.removeItem('current_admin_session_id')
              await supabase.auth.signOut()
              router.push('/login')
              return
            }
          }
        }
        
        setAuthenticated(true)
        setLoading(false)
      } catch (err) {
        console.error('Auth verification failed:', err)
        router.push('/login')
      }
    }

    checkAuth()

    const { data: { subscription } } = supabase.auth.onAuthStateChange(async (event, session) => {
      if (event === 'SIGNED_OUT' || !session) {
        setAuthenticated(false)
        router.push('/login')
      } else if (session) {
        if (!isAllowedAdminEmail(session.user.email)) {
          setAuthenticated(false)
          sessionStorage.removeItem('current_admin_session_id')
          await supabase.auth.signOut()
          router.push('/login?error=unauthorized')
          return
        }
        setAuthenticated(true)
        setLoading(false)
      }
    })

    return () => subscription.unsubscribe()
  }, [router])

  if (loading || !authenticated) {
    return (
      <div
        style={{
          display: 'flex',
          height: '100vh',
          alignItems: 'center',
          justifyContent: 'center',
          background: '#f9fafb',
          flexDirection: 'column',
          gap: 16,
          fontFamily: 'system-ui, sans-serif'
        }}
      >
        <div
          style={{
            width: 40,
            height: 40,
            border: '4px solid #e5e7eb',
            borderTopColor: '#6366f1',
            borderRadius: '50%',
            animation: 'spin 1s linear infinite'
          }}
        />
        <p style={{ color: '#4b5563', fontSize: 14, fontWeight: 500, margin: 0 }}>
          Authenticating...
        </p>
        <style>{`
          @keyframes spin {
            to { transform: rotate(360deg); }
          }
        `}</style>
      </div>
    )
  }

  return (
    <div style={{ display: 'flex', height: '100vh', overflow: 'hidden', background: '#ffffff' }}>
      <Sidebar />
      <main style={{ flex: 1, overflowY: 'auto', background: '#f9fafb' }}>
        <Header />
        <div style={{ padding: '24px' }}>
          {children}
        </div>
      </main>
    </div>
  )
}
