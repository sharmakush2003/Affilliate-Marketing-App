'use client'

import { useCallback, useEffect, useState } from 'react'
import { Search, ChevronLeft, ChevronRight, X, Plus, Minus } from 'lucide-react'
import type { Profile } from '@/lib/supabase'

function Avatar({ name, email }: { name: string | null; email: string }) {
  const letter = (name || email)[0].toUpperCase()
  return (
    <div style={{ width: 28, height: 28, borderRadius: '50%', background: '#f3f4f6', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 12, fontWeight: 700, color: '#374151', flexShrink: 0 }}>
      {letter}
    </div>
  )
}

function CoinModal({ user, onClose, onUpdate }: { user: Profile; onClose: () => void; onUpdate: () => void }) {
  const [delta, setDelta] = useState(0)
  const [reason, setReason] = useState('')
  const [loading, setLoading] = useState(false)
  const [done, setDone] = useState(false)
  const [err, setErr] = useState('')

  async function apply() {
    if (!delta || !reason.trim()) { setErr('Enter amount and reason.'); return }
    setLoading(true); setErr('')
    const res = await fetch('/api/users', {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ userId: user.id, coinsDelta: delta, reason }),
    })
    const data = await res.json()
    if (!res.ok) { setErr(data.error ?? 'Error'); setLoading(false); return }
    setDone(true); onUpdate(); setTimeout(onClose, 1000)
  }

  return (
    <div style={{ position: 'fixed', inset: 0, zIndex: 50, display: 'flex', alignItems: 'center', justifyContent: 'center', background: 'rgba(0,0,0,0.3)', backdropFilter: 'blur(2px)' }}>
      <div className="card" style={{ width: 420, padding: 24, position: 'relative' }}>
        <button onClick={onClose} style={{ position: 'absolute', top: 16, right: 16, background: 'none', border: 'none', cursor: 'pointer', color: '#9ca3af' }}>
          <X size={16} />
        </button>

        <div style={{ marginBottom: 20 }}>
          <div style={{ fontSize: 15, fontWeight: 700, color: '#111827' }}>{user.full_name || user.email}</div>
          <div style={{ fontSize: 12, color: '#9ca3af', marginTop: 2 }}>{user.email}</div>
        </div>

        {/* Coin summary */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3,1fr)', gap: 8, marginBottom: 20 }}>
          {[
            { label: 'Total Coins', val: user.total_coins ?? 0 },
            { label: 'Redeemed',    val: user.redeemed_coins ?? 0 },
            { label: 'Savings ₹',  val: user.total_savings ?? 0 },
          ].map(({ label, val }) => (
            <div key={label} style={{ background: '#f9fafb', border: '1px solid #e5e7eb', borderRadius: 6, padding: '10px 12px', textAlign: 'center' }}>
              <div style={{ fontSize: 17, fontWeight: 700, color: '#111827' }}>{val}</div>
              <div style={{ fontSize: 11, color: '#9ca3af', marginTop: 2 }}>{label}</div>
            </div>
          ))}
        </div>

        <div style={{ fontSize: 12, fontWeight: 600, color: '#374151', marginBottom: 8 }}>Adjust Coins</div>
        <div style={{ display: 'flex', gap: 8, marginBottom: 10 }}>
          <button onClick={() => setDelta(d => d - 10)} style={{ padding: '7px 12px', border: '1px solid #e5e7eb', borderRadius: 6, background: '#fff', cursor: 'pointer', display: 'flex', alignItems: 'center' }}>
            <Minus size={14} />
          </button>
          <input type="number" value={delta} onChange={e => setDelta(parseInt(e.target.value) || 0)}
            className="input" style={{ textAlign: 'center' }} />
          <button onClick={() => setDelta(d => d + 10)} style={{ padding: '7px 12px', border: '1px solid #e5e7eb', borderRadius: 6, background: '#fff', cursor: 'pointer', display: 'flex', alignItems: 'center' }}>
            <Plus size={14} />
          </button>
        </div>
        <input type="text" placeholder="Reason (required)" value={reason} onChange={e => setReason(e.target.value)}
          className="input" style={{ marginBottom: 12 }} />
        {err && <p style={{ fontSize: 12, color: '#dc2626', marginBottom: 8 }}>{err}</p>}
        <button className="btn-dark btn" onClick={apply} disabled={loading || done} style={{ width: '100%', justifyContent: 'center' }}>
          {done ? '✓ Applied' : loading ? 'Applying…' : 'Apply Adjustment'}
        </button>
      </div>
    </div>
  )
}

export default function UsersPage() {
  const [users, setUsers] = useState<Profile[]>([])
  const [total, setTotal] = useState(0)
  const [page, setPage] = useState(1)
  const [search, setSearch] = useState('')
  const [dSearch, setDSearch] = useState('')
  const [loading, setLoading] = useState(true)
  const [selected, setSelected] = useState<Profile | null>(null)

  useEffect(() => { const t = setTimeout(() => setDSearch(search), 350); return () => clearTimeout(t) }, [search])

  const load = useCallback(async () => {
    setLoading(true)
    const p = new URLSearchParams({ page: String(page), limit: '15', search: dSearch })
    const d = await fetch(`/api/users?${p}`).then(r => r.json())
    setUsers(d.users ?? []); setTotal(d.total ?? 0); setLoading(false)
  }, [page, dSearch])

  useEffect(() => { load() }, [load])

  const pages = Math.ceil(total / 15)

  const TH = ({ children }: { children: React.ReactNode }) => (
    <th style={{ textAlign: 'left', padding: '10px 16px', fontSize: 11.5, fontWeight: 600, color: '#6b7280', borderBottom: '1px solid #e5e7eb', whiteSpace: 'nowrap', background: '#f9fafb' }}>
      {children}
    </th>
  )

  return (
    <div style={{ width: '100%' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 20 }}>
        <div>
          <h1 style={{ fontSize: 20, fontWeight: 700, color: '#111827', margin: 0, letterSpacing: '-0.02em' }}>Users</h1>
          <p style={{ fontSize: 13, color: '#6b7280', marginTop: 4 }}>{total} registered users</p>
        </div>
      </div>

      {/* Search */}
      <div style={{ position: 'relative', maxWidth: 320, marginBottom: 16 }}>
        <Search size={14} style={{ position: 'absolute', left: 12, top: '50%', transform: 'translateY(-50%)', color: '#9ca3af' }} />
        <input className="input" placeholder="Search by name or email…" value={search}
          onChange={e => { setSearch(e.target.value); setPage(1) }}
          style={{ paddingLeft: 36 }} />
      </div>

      {/* Table */}
      <div className="card" style={{ overflow: 'hidden' }}>
        <div style={{ overflowX: 'auto' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr>
                <TH>User</TH>
                <TH>Email</TH>
                <TH>Mobile</TH>
                <TH>Coins</TH>
                <TH>Redeemed</TH>
                <TH>Savings</TH>
                <TH>Joined</TH>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                Array.from({ length: 6 }).map((_, i) => (
                  <tr key={i}>
                    {Array.from({ length: 7 }).map((_, j) => (
                      <td key={j} style={{ padding: '12px 16px' }}>
                        <div className="skeleton" style={{ height: 14 }} />
                      </td>
                    ))}
                  </tr>
                ))
              ) : users.length === 0 ? (
                <tr>
                  <td colSpan={7} style={{ padding: '32px 16px', textAlign: 'center', color: '#9ca3af', fontSize: 13 }}>
                    No users found.
                  </td>
                </tr>
              ) : users.map((u, i) => (
                <tr key={u.id} className="table-row" style={{ borderTop: i === 0 ? 'none' : '1px solid #f3f4f6', cursor: 'pointer' }}
                  onClick={() => setSelected(u)}>
                  <td style={{ padding: '11px 16px' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                      <Avatar name={u.full_name} email={u.email} />
                      <span style={{ fontSize: 13, fontWeight: 500, color: '#111827' }}>{u.full_name || '—'}</span>
                    </div>
                  </td>
                  <td style={{ padding: '11px 16px', fontSize: 13, color: '#6b7280', maxWidth: 160, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{u.email}</td>
                  <td style={{ padding: '11px 16px', fontSize: 13, color: '#9ca3af' }}>{u.mobile || '—'}</td>
                  <td style={{ padding: '11px 16px', fontSize: 13, fontWeight: 600, color: '#111827' }}>{(u.total_coins ?? 0).toLocaleString()}</td>
                  <td style={{ padding: '11px 16px', fontSize: 13, color: '#6b7280' }}>{u.redeemed_coins ?? 0}</td>
                  <td style={{ padding: '11px 16px', fontSize: 13, color: '#6b7280' }}>₹{u.total_savings ?? 0}</td>
                  <td style={{ padding: '11px 16px', fontSize: 12, color: '#9ca3af', whiteSpace: 'nowrap' }}>
                    {u.created_at ? (
                      <div style={{ display: 'flex', flexDirection: 'column', gap: '2px' }}>
                        <span style={{ fontWeight: '500', color: '#374151' }}>
                          {new Date(u.created_at).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' })}
                        </span>
                        <span style={{ fontSize: '10.5px', color: '#94a3b8' }}>
                          {new Date(u.created_at).toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit' })}
                        </span>
                      </div>
                    ) : '—'}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        {total > 15 && (
          <div style={{ padding: '12px 16px', borderTop: '1px solid #e5e7eb', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <span style={{ fontSize: 12, color: '#9ca3af' }}>Page {page} of {pages} — {total} users</span>
            <div style={{ display: 'flex', gap: 6 }}>
              <button className="btn" onClick={() => setPage(p => Math.max(1, p - 1))} disabled={page === 1} style={{ padding: '5px 10px' }}>
                <ChevronLeft size={14} />
              </button>
              <button className="btn" onClick={() => setPage(p => Math.min(pages, p + 1))} disabled={page === pages} style={{ padding: '5px 10px' }}>
                <ChevronRight size={14} />
              </button>
            </div>
          </div>
        )}
      </div>

      {selected && <CoinModal user={selected} onClose={() => setSelected(null)} onUpdate={load} />}
    </div>
  )
}
