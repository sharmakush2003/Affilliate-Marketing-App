'use client'

import { useCallback, useEffect, useState } from 'react'
import { Search, ChevronLeft, ChevronRight, Download } from 'lucide-react'
import type { Transaction } from '@/lib/supabase'

const STATUS_TABS = ['all', 'pending', 'approved', 'rejected'] as const
const PAYMENT_TABS = ['all', 'unpaid', 'paid'] as const

type StatusTab  = typeof STATUS_TABS[number]
type PaymentTab = typeof PAYMENT_TABS[number]

const statusCfg = {
  approved: 'badge-green',
  pending:  'badge-yellow',
  rejected: 'badge-red',
} as const

export default function TransactionsPage() {
  const [rows, setRows]         = useState<Transaction[]>([])
  const [total, setTotal]       = useState(0)
  const [page, setPage]         = useState(1)
  const [search, setSearch]     = useState('')
  const [dSearch, setDSearch]   = useState('')
  const [status, setStatus]     = useState<StatusTab>('all')
  const [payment, setPayment]   = useState<PaymentTab>('all')
  const [loading, setLoading]   = useState(true)
  const [toggling, setToggling] = useState<string | null>(null)

  useEffect(() => { const t = setTimeout(() => setDSearch(search), 350); return () => clearTimeout(t) }, [search])

  const load = useCallback(async () => {
    setLoading(true)
    const p = new URLSearchParams({
      page: String(page), limit: '15', search: dSearch,
      ...(status !== 'all' && { status }),
      ...(payment !== 'all' && { payment_status: payment }),
    })
    const d = await fetch(`/api/transactions?${p}`).then(r => r.json())
    setRows(d.transactions ?? []); setTotal(d.total ?? 0); setLoading(false)
  }, [page, dSearch, status, payment])

  useEffect(() => { load() }, [load])

  async function togglePayment(id: string, current: string) {
    const next = current === 'paid' ? 'unpaid' : 'paid'
    setToggling(id)
    await fetch('/api/transactions', {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ transactionId: id, paymentStatus: next }),
    })
    setRows(prev => prev.map(r => r.id === id ? { ...r, payment_status: next as 'paid' | 'unpaid' } : r))
    setToggling(null)
  }

  function exportCSV() {
    const csv = [
      ['ID','User','Store','Order ₹','Commission ₹','Coins','Status','Payment','Date'],
      ...rows.map(r => {
        const p = r.profiles as { full_name?: string; email?: string } | undefined
        return [r.cuelinks_transaction_id, p?.full_name ?? p?.email ?? '', r.merchant_name ?? '', r.order_amount, r.commission_earned, r.cashback_credited_coins, r.status, r.payment_status, r.transaction_time ? new Date(r.transaction_time).toLocaleDateString('en-IN') : '']
      }),
    ].map(r => r.join(',')).join('\n')
    const a = document.createElement('a')
    a.href = URL.createObjectURL(new Blob([csv], { type: 'text/csv' }))
    a.download = `transactions_${Date.now()}.csv`; a.click()
  }

  const pages = Math.ceil(total / 15)

  const TH = ({ children }: { children: React.ReactNode }) => (
    <th style={{ textAlign: 'left', padding: '10px 16px', fontSize: 11.5, fontWeight: 600, color: '#6b7280', borderBottom: '1px solid #e5e7eb', whiteSpace: 'nowrap', background: '#f9fafb' }}>
      {children}
    </th>
  )

  function TabBar<T extends string>({ tabs, value, onChange }: { tabs: readonly T[]; value: T; onChange: (v: T) => void }) {
    return (
      <div style={{ display: 'flex', border: '1px solid #e5e7eb', borderRadius: 6, overflow: 'hidden' }}>
        {tabs.map(t => (
          <button key={t} onClick={() => { onChange(t); setPage(1) }}
            style={{ padding: '6px 14px', fontSize: 12.5, fontWeight: 500, border: 'none', cursor: 'pointer', borderRight: '1px solid #e5e7eb', background: value === t ? '#111827' : '#ffffff', color: value === t ? '#ffffff' : '#6b7280', transition: 'background 0.1s', textTransform: 'capitalize' }}>
            {t}
          </button>
        ))}
      </div>
    )
  }

  return (
    <div style={{ maxWidth: 1100 }}>
      <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', marginBottom: 20 }}>
        <div>
          <h1 style={{ fontSize: 20, fontWeight: 700, color: '#111827', margin: 0, letterSpacing: '-0.02em' }}>Transactions</h1>
          <p style={{ fontSize: 13, color: '#6b7280', marginTop: 4 }}>{total} conversions synced from CueLinks</p>
        </div>
        <button className="btn" onClick={exportCSV} style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
          <Download size={13} /> Export CSV
        </button>
      </div>

      {/* Filters row */}
      <div style={{ display: 'flex', gap: 10, marginBottom: 16, flexWrap: 'wrap', alignItems: 'center' }}>
        <div style={{ position: 'relative', flex: '0 0 260px' }}>
          <Search size={13} style={{ position: 'absolute', left: 10, top: '50%', transform: 'translateY(-50%)', color: '#9ca3af' }} />
          <input className="input" placeholder="Search store or campaign…" value={search}
            onChange={e => { setSearch(e.target.value); setPage(1) }} style={{ paddingLeft: 32 }} />
        </div>
        <TabBar tabs={STATUS_TABS} value={status} onChange={setStatus} />
        <TabBar tabs={PAYMENT_TABS} value={payment} onChange={setPayment} />
      </div>

      <div className="card" style={{ overflow: 'hidden' }}>
        <div style={{ overflowX: 'auto' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr>
                <TH>ID</TH>
                <TH>User</TH>
                <TH>Store / Campaign</TH>
                <TH>Order ₹</TH>
                <TH>Commission ₹</TH>
                <TH>Coins</TH>
                <TH>Status</TH>
                <TH>Payment</TH>
                <TH>Date</TH>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                Array.from({ length: 6 }).map((_, i) => (
                  <tr key={i}>
                    {Array.from({ length: 9 }).map((_, j) => (
                      <td key={j} style={{ padding: '12px 16px' }}>
                        <div className="skeleton" style={{ height: 14 }} />
                      </td>
                    ))}
                  </tr>
                ))
              ) : rows.length === 0 ? (
                <tr>
                  <td colSpan={9} style={{ padding: '40px 16px', textAlign: 'center', color: '#9ca3af', fontSize: 13 }}>
                    No transactions found. Run a CueLinks sync first.
                  </td>
                </tr>
              ) : rows.map((tx, i) => {
                const profile = tx.profiles as { full_name?: string; email?: string } | undefined
                const sCls = statusCfg[tx.status as keyof typeof statusCfg] ?? 'badge-gray'
                const isPaid = tx.payment_status === 'paid'
                return (
                  <tr key={tx.id} className="table-row"
                    style={{ borderTop: i === 0 ? 'none' : '1px solid #f3f4f6', opacity: toggling === tx.id ? 0.5 : 1 }}>
                    <td style={{ padding: '11px 16px', fontFamily: 'monospace', fontSize: 11, color: '#9ca3af' }}>
                      {tx.cuelinks_transaction_id.slice(0, 12)}…
                    </td>
                    <td style={{ padding: '11px 16px' }}>
                      <div style={{ fontSize: 13, fontWeight: 500, color: '#111827', maxWidth: 130, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                        {profile?.full_name ?? '—'}
                      </div>
                      <div style={{ fontSize: 11, color: '#9ca3af', maxWidth: 130, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                        {profile?.email ?? 'No match'}
                      </div>
                    </td>
                    <td style={{ padding: '11px 16px' }}>
                      <div style={{ fontSize: 13, fontWeight: 500, color: '#111827' }}>{tx.merchant_name ?? '—'}</div>
                      <div style={{ fontSize: 11, color: '#9ca3af', maxWidth: 160, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{tx.campaign_name}</div>
                    </td>
                    <td style={{ padding: '11px 16px', fontSize: 13, fontWeight: 600, color: '#111827' }}>
                      ₹{tx.order_amount.toLocaleString('en-IN')}
                    </td>
                    <td style={{ padding: '11px 16px', fontSize: 13, fontWeight: 600, color: '#111827' }}>
                      ₹{tx.commission_earned.toFixed(2)}
                    </td>
                    <td style={{ padding: '11px 16px', fontSize: 13, color: tx.cashback_credited_coins > 0 ? '#111827' : '#9ca3af' }}>
                      {tx.cashback_credited_coins > 0 ? tx.cashback_credited_coins.toLocaleString() : '—'}
                    </td>
                    <td style={{ padding: '11px 16px' }}>
                      <span className={`badge ${sCls}`} style={{ textTransform: 'capitalize' }}>{tx.status}</span>
                    </td>
                    <td style={{ padding: '11px 16px' }}>
                      <button
                        className="toggle-paid"
                        onClick={() => togglePayment(tx.id, tx.payment_status)}
                        style={{ background: isPaid ? '#f0fdf4' : '#fef2f2', color: isPaid ? '#15803d' : '#b91c1c' }}
                      >
                        {isPaid ? '✓ Paid' : '✗ Unpaid'}
                      </button>
                    </td>
                    <td style={{ padding: '11px 16px', fontSize: 12, color: '#9ca3af', whiteSpace: 'nowrap' }}>
                      {tx.transaction_time ? new Date(tx.transaction_time).toLocaleDateString('en-IN', { day: 'numeric', month: 'short' }) : '—'}
                    </td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        </div>
        {total > 15 && (
          <div style={{ padding: '12px 16px', borderTop: '1px solid #e5e7eb', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <span style={{ fontSize: 12, color: '#9ca3af' }}>Page {page} of {pages} — {total} total</span>
            <div style={{ display: 'flex', gap: 6 }}>
              <button className="btn" onClick={() => setPage(p => Math.max(1, p - 1))} disabled={page === 1} style={{ padding: '5px 10px' }}><ChevronLeft size={14} /></button>
              <button className="btn" onClick={() => setPage(p => Math.min(pages, p + 1))} disabled={page === pages} style={{ padding: '5px 10px' }}><ChevronRight size={14} /></button>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
