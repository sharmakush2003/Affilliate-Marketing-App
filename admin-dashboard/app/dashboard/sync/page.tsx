'use client'

import { useState } from 'react'
import { RefreshCcw, CheckCircle, AlertCircle } from 'lucide-react'

type SyncResult = { message?: string; synced?: number; coinsAwarded?: number; errors?: string[]; error?: string }

export default function SyncPage() {
  const [running, setRunning] = useState(false)
  const [result, setResult]   = useState<SyncResult | null>(null)
  const [err, setErr]         = useState('')

  async function runSync() {
    setRunning(true); setResult(null); setErr('')
    try {
      const res = await fetch('/api/sync-cuelinks?secret=reward_club_admin_2026')
      const d: SyncResult = await res.json()
      if (!res.ok) setErr(d.error ?? 'Sync failed')
      else setResult(d)
    } catch (e: unknown) {
      setErr(e instanceof Error ? e.message : 'Network error')
    } finally {
      setRunning(false)
    }
  }

  return (
    <div style={{ maxWidth: 600 }}>
      <div style={{ marginBottom: 28 }}>
        <h1 style={{ fontSize: 20, fontWeight: 700, color: '#111827', margin: 0, letterSpacing: '-0.02em' }}>Sync & Settings</h1>
        <p style={{ fontSize: 13, color: '#6b7280', marginTop: 4 }}>Pull the latest CueLinks transactions into Supabase.</p>
      </div>

      {/* Config */}
      <div className="card" style={{ padding: 20, marginBottom: 16 }}>
        <div style={{ fontSize: 12, fontWeight: 600, color: '#111827', marginBottom: 14, textTransform: 'uppercase', letterSpacing: '0.06em' }}>Configuration</div>
        {[
          { label: 'CueLinks Channel ID', value: '301603' },
          { label: 'CueLinks API Base',   value: 'api.cuelinks.com/v2' },
          { label: 'Supabase Project',    value: 'pexrjsvpbhfbfxbyzegc.supabase.co' },
        ].map(({ label, value }) => (
          <div key={label} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '10px 0', borderBottom: '1px solid #f3f4f6' }}>
            <span style={{ fontSize: 13, color: '#6b7280' }}>{label}</span>
            <span style={{ fontSize: 12.5, fontFamily: 'monospace', color: '#111827', background: '#f9fafb', padding: '2px 8px', borderRadius: 4, border: '1px solid #e5e7eb' }}>
              {value}
            </span>
          </div>
        ))}
      </div>

      {/* Sync */}
      <div className="card" style={{ padding: 20, marginBottom: 16 }}>
        <div style={{ fontSize: 12, fontWeight: 600, color: '#111827', marginBottom: 8, textTransform: 'uppercase', letterSpacing: '0.06em' }}>Manual Sync</div>
        <p style={{ fontSize: 13, color: '#6b7280', marginBottom: 16, lineHeight: 1.6 }}>
          Fetches transactions from CueLinks, matches <code style={{ background: '#f3f4f6', padding: '1px 5px', borderRadius: 3, fontSize: 12 }}>sub_id</code> to user profiles, and auto-credits cashback coins for approved orders.
        </p>

        <button className="btn-dark btn" onClick={runSync} disabled={running}>
          <RefreshCcw size={13} style={{ animation: running ? 'spin 1s linear infinite' : 'none' }} />
          {running ? 'Syncing…' : 'Run Sync Now'}
        </button>

        {result && (
          <div style={{ marginTop: 16, padding: '14px 16px', background: '#f0fdf4', border: '1px solid #bbf7d0', borderRadius: 8 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 10 }}>
              <CheckCircle size={14} style={{ color: '#16a34a' }} />
              <span style={{ fontSize: 13, fontWeight: 600, color: '#15803d' }}>Sync complete</span>
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
              <div style={{ background: '#fff', border: '1px solid #bbf7d0', borderRadius: 6, padding: '12px 16px', textAlign: 'center' }}>
                <div style={{ fontSize: 22, fontWeight: 700, color: '#111827' }}>{result.synced ?? 0}</div>
                <div style={{ fontSize: 11, color: '#6b7280', marginTop: 2 }}>Transactions synced</div>
              </div>
              <div style={{ background: '#fff', border: '1px solid #bbf7d0', borderRadius: 6, padding: '12px 16px', textAlign: 'center' }}>
                <div style={{ fontSize: 22, fontWeight: 700, color: '#111827' }}>{result.coinsAwarded ?? 0}</div>
                <div style={{ fontSize: 11, color: '#6b7280', marginTop: 2 }}>Coins auto-credited</div>
              </div>
            </div>
            {result.errors && result.errors.length > 0 && (
              <div style={{ marginTop: 10, padding: '10px 12px', background: '#fefce8', border: '1px solid #fde68a', borderRadius: 6, fontSize: 12, color: '#854d0e' }}>
                {result.errors.length} warning(s): {result.errors[0]}
              </div>
            )}
          </div>
        )}

        {err && (
          <div style={{ marginTop: 14, padding: '12px 14px', background: '#fef2f2', border: '1px solid #fecaca', borderRadius: 8, display: 'flex', gap: 8, alignItems: 'flex-start' }}>
            <AlertCircle size={14} style={{ color: '#dc2626', flexShrink: 0, marginTop: 1 }} />
            <span style={{ fontSize: 13, color: '#b91c1c' }}>{err}</span>
          </div>
        )}
      </div>

      {/* Cron */}
      <div className="card" style={{ padding: 20 }}>
        <div style={{ fontSize: 12, fontWeight: 600, color: '#111827', marginBottom: 10, textTransform: 'uppercase', letterSpacing: '0.06em' }}>
          Automate with Vercel Cron
        </div>
        <p style={{ fontSize: 13, color: '#6b7280', marginBottom: 14, lineHeight: 1.6 }}>
          Add this to <code style={{ background: '#f3f4f6', padding: '1px 5px', borderRadius: 3, fontSize: 12 }}>vercel.json</code> to auto-sync every 6 hours:
        </p>
        <pre style={{ background: '#f9fafb', border: '1px solid #e5e7eb', borderRadius: 6, padding: '14px 16px', fontSize: 12, color: '#374151', overflowX: 'auto', margin: 0 }}>{`{
  "crons": [{
    "path": "/api/sync-cuelinks?secret=YOUR_SECRET",
    "schedule": "0 */6 * * *"
  }]
}`}</pre>
      </div>

      <style>{`@keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }`}</style>
    </div>
  )
}
