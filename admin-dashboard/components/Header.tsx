'use client'

import { useState, useEffect } from 'react'
import { RefreshCcw, Check, AlertCircle, Clock } from 'lucide-react'

export function Header() {
  const [syncing, setSyncing] = useState(false)
  const [status, setStatus] = useState<'idle' | 'success' | 'error'>('idle')
  const [message, setMessage] = useState('')

  const handleSync = async () => {
    setSyncing(true)
    setStatus('idle')
    try {
      const res = await fetch('/api/sync-cuelinks?secret=reward_club_admin_2026')
      const data = await res.json()
      if (!res.ok) {
        throw new Error(data.error || 'Sync failed')
      }
      setStatus('success')
      setMessage(`Synced ${data.synced ?? 0} transactions!`)
      // Refresh current page context to show updated data
      setTimeout(() => {
        window.location.reload()
      }, 1500)
    } catch (err: unknown) {
      setStatus('error')
      setMessage(err instanceof Error ? err.message : 'Error syncing')
    } finally {
      setSyncing(false)
    }
  }

  const [timeString, setTimeString] = useState('')

  useEffect(() => {
    const updateClock = () => {
      const d = new Date()
      const datePart = d.toLocaleDateString('en-IN', {
        day: 'numeric',
        month: 'short',
        year: 'numeric',
      })
      const timePart = d.toLocaleTimeString('en-IN', {
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: true,
      })
      setTimeString(`${datePart} • ${timePart}`)
    }

    updateClock()
    const timer = setInterval(updateClock, 1000)
    return () => clearInterval(timer)
  }, [])

  return (
    <div
      style={{
        position: 'sticky',
        top: 0,
        zIndex: 10,
        background: '#ffffff',
        borderBottom: '1px solid #e5e7eb',
        padding: '10px 24px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
      }}
    >
      <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 7 }}>
          <span
            className="pulse"
            style={{ width: 6, height: 6, borderRadius: '50%', background: '#22c55e', display: 'inline-block' }}
          />
          <span style={{ fontSize: 12, color: '#9ca3af', fontWeight: 500 }}>Live Connection</span>
        </div>

        {/* Dynamic Sync Status Message */}
        {status === 'success' && (
          <span style={{ fontSize: 12, color: '#16a34a', display: 'flex', alignItems: 'center', gap: 4, fontWeight: 500 }}>
            <Check size={13} /> {message}
          </span>
        )}
        {status === 'error' && (
          <span style={{ fontSize: 12, color: '#dc2626', display: 'flex', alignItems: 'center', gap: 4, fontWeight: 500 }}>
            <AlertCircle size={13} /> {message}
          </span>
        )}
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
        <button
          onClick={handleSync}
          disabled={syncing}
          className="btn"
          style={{
            padding: '5px 12px',
            fontSize: '12px',
            display: 'flex',
            alignItems: 'center',
            gap: 6,
            background: syncing ? '#f9fafb' : '#ffffff',
          }}
        >
          <RefreshCcw size={12} className={syncing ? 'animate-spin' : ''} />
          {syncing ? 'Syncing...' : 'Sync CueLinks'}
        </button>
        <div style={{ display: 'flex', alignItems: 'center', gap: 6, fontSize: '12px', color: '#64748b', fontWeight: 600, fontVariantNumeric: 'tabular-nums' }}>
          <Clock size={13} style={{ color: '#94a3b8' }} />
          <span>{timeString}</span>
        </div>
      </div>

      <style>{`
        @keyframes spin {
          from { transform: rotate(0deg); }
          to { transform: rotate(360deg); }
        }
        .animate-spin {
          animation: spin 1s linear infinite;
        }
      `}</style>
    </div>
  )
}
export default Header
