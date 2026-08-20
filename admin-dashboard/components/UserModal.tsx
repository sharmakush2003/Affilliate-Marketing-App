'use client'

import { useState, useEffect, useCallback } from 'react'
import { X, Coins, Minus, Plus, Check } from 'lucide-react'
import type { Profile } from '@/lib/supabase'

type UserModalProps = {
  user: Profile | null
  onClose: () => void
  onUpdate: () => void
}

export function UserModal({ user, onClose, onUpdate }: UserModalProps) {
  const [coinsDelta, setCoinsDelta] = useState(0)
  const [reason, setReason] = useState('')
  const [loading, setLoading] = useState(false)
  const [success, setSuccess] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    setCoinsDelta(0)
    setReason('')
    setSuccess(false)
    setError('')
  }, [user])

  const handleKeyDown = useCallback(
    (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose()
    },
    [onClose]
  )

  useEffect(() => {
    window.addEventListener('keydown', handleKeyDown)
    return () => window.removeEventListener('keydown', handleKeyDown)
  }, [handleKeyDown])

  if (!user) return null

  async function handleAdjust() {
    if (!coinsDelta || !reason.trim()) {
      setError('Please enter a valid coin amount and reason.')
      return
    }
    setLoading(true)
    setError('')
    try {
      const res = await fetch('/api/users', {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId: user!.id, coinsDelta, reason }),
      })
      const data = await res.json()
      if (!res.ok) throw new Error(data.error)
      setSuccess(true)
      onUpdate()
      setTimeout(onClose, 1200)
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to update coins')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm">
      <div className="glass rounded-2xl w-full max-w-lg p-6 border border-slate-700 shadow-2xl">
        {/* Header */}
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-lg font-bold text-white">{user.full_name || 'Unknown User'}</h2>
            <p className="text-sm text-slate-400">{user.email}</p>
          </div>
          <button onClick={onClose} className="p-2 rounded-lg hover:bg-slate-700/60 text-slate-400 hover:text-white transition">
            <X size={18} />
          </button>
        </div>

        {/* User Stats */}
        <div className="grid grid-cols-3 gap-3 mb-6">
          {[
            { label: 'Total Coins', value: user.total_coins ?? 0, color: 'text-amber-400' },
            { label: 'Redeemed', value: user.redeemed_coins ?? 0, color: 'text-violet-400' },
            { label: 'Savings ₹', value: user.total_savings ?? 0, color: 'text-emerald-400' },
          ].map(({ label, value, color }) => (
            <div key={label} className="bg-slate-800/60 rounded-xl p-3 text-center">
              <p className={`text-xl font-bold ${color}`}>{value}</p>
              <p className="text-xs text-slate-500 mt-0.5">{label}</p>
            </div>
          ))}
        </div>

        {/* Mobile */}
        {user.mobile && (
          <p className="text-sm text-slate-400 mb-4">📱 {user.mobile}</p>
        )}

        {/* Coin Adjustment */}
        <div className="space-y-3">
          <p className="text-sm font-semibold text-slate-300">Adjust Coins</p>
          <div className="flex items-center gap-3">
            <button
              onClick={() => setCoinsDelta(d => d - 10)}
              className="p-2 rounded-lg bg-red-500/10 hover:bg-red-500/20 text-red-400 transition"
            >
              <Minus size={16} />
            </button>
            <input
              type="number"
              value={coinsDelta}
              onChange={e => setCoinsDelta(parseInt(e.target.value) || 0)}
              className="flex-1 bg-slate-800 border border-slate-600 rounded-lg px-3 py-2 text-white text-center text-sm focus:outline-none focus:border-indigo-500"
            />
            <button
              onClick={() => setCoinsDelta(d => d + 10)}
              className="p-2 rounded-lg bg-emerald-500/10 hover:bg-emerald-500/20 text-emerald-400 transition"
            >
              <Plus size={16} />
            </button>
          </div>
          <input
            type="text"
            placeholder="Reason (e.g. Manual cashback for order #123)"
            value={reason}
            onChange={e => setReason(e.target.value)}
            className="w-full bg-slate-800 border border-slate-600 rounded-lg px-3 py-2 text-sm text-white placeholder:text-slate-500 focus:outline-none focus:border-indigo-500"
          />
          {error && <p className="text-xs text-red-400">{error}</p>}
          <button
            onClick={handleAdjust}
            disabled={loading || success}
            className={`w-full flex items-center justify-center gap-2 py-2.5 rounded-lg text-sm font-semibold transition-all ${
              success
                ? 'bg-emerald-500/20 text-emerald-400'
                : 'bg-indigo-600 hover:bg-indigo-500 text-white'
            } disabled:opacity-60`}
          >
            {success ? (
              <><Check size={16} /> Applied!</>
            ) : loading ? (
              'Applying...'
            ) : (
              <><Coins size={16} /> Apply Coin Adjustment</>
            )}
          </button>
        </div>
      </div>
    </div>
  )
}
