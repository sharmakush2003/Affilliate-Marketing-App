'use client'

import { useEffect, useState } from 'react'
import {
  Wallet,
  Search,
  CheckCircle,
  Clock,
  XCircle,
  ArrowUpRight,
  RefreshCw,
  CreditCard,
  Send,
  AlertCircle,
  Check,
  X
} from 'lucide-react'

type WithdrawalRecord = {
  id: string
  user_id: string
  user_name: string
  user_email: string
  user_mobile: string
  amount_inr: number
  coins_deducted: number
  payment_method: string
  payout_details: string
  status: 'pending' | 'approved' | 'processed' | 'rejected'
  created_at: string
  processed_at?: string | null
  tx_hash_or_ref?: string | null
}

const STATUS_TABS = [
  { label: 'All Payouts', value: 'all' },
  { label: '⏳ Pending Approval', value: 'pending' },
  { label: '✅ Processed / Paid', value: 'processed' },
  { label: '❌ Rejected', value: 'rejected' },
] as const

export default function WithdrawalsPage() {
  const [withdrawals, setWithdrawals] = useState<WithdrawalRecord[]>([])
  const [stats, setStats] = useState<any>({ pendingAmount: 0, processedAmount: 0, pendingCount: 0 })
  const [loading, setLoading] = useState(true)
  const [search, setSearch] = useState('')
  const [statusFilter, setStatusFilter] = useState('all')
  const [updatingId, setUpdatingId] = useState<string | null>(null)
  const [selectedWithdrawal, setSelectedWithdrawal] = useState<WithdrawalRecord | null>(null)
  const [txRefInput, setTxRefInput] = useState('')

  const fetchWithdrawals = async () => {
    setLoading(true)
    try {
      const res = await fetch(`/api/withdrawals?status=${statusFilter}&search=${encodeURIComponent(search)}`)
      const data = await res.json()
      if (data.withdrawals) {
        setWithdrawals(data.withdrawals)
        setStats(data.stats)
      }
    } catch (err) {
      console.error('Error fetching withdrawals:', err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchWithdrawals()
  }, [statusFilter, search])

  const handleUpdateStatus = async (id: string, newStatus: string, ref?: string) => {
    setUpdatingId(id)
    try {
      const res = await fetch('/api/withdrawals', {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ id, status: newStatus, txHash: ref || txRefInput }),
      })
      if (res.ok) {
        setSelectedWithdrawal(null)
        setTxRefInput('')
        fetchWithdrawals()
      }
    } catch (err) {
      console.error('Error updating status:', err)
    } finally {
      setUpdatingId(null)
    }
  }

  return (
    <div style={{ width: '100%', maxWidth: 1250, margin: '0 auto' }}>
      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', marginBottom: 18 }}>
        <div>
          <h1
            style={{
              fontSize: 20,
              fontWeight: 700,
              color: '#111827',
              margin: 0,
              letterSpacing: '-0.02em',
              display: 'flex',
              alignItems: 'center',
              gap: 8,
            }}
          >
            <Wallet size={20} style={{ color: '#2563eb' }} />
            User Cashback Payouts & Withdrawals
          </h1>
          <p style={{ fontSize: 13, color: '#6b7280', marginTop: 3 }}>
            Manage and disburse user cashback withdrawal requests generated from the mobile app.
          </p>
        </div>

        <button
          onClick={fetchWithdrawals}
          className="btn"
          style={{ display: 'flex', alignItems: 'center', gap: 6, padding: '6px 14px', fontSize: 12.5 }}
        >
          <RefreshCw size={13} className={loading ? 'animate-spin' : ''} />
          Refresh Payouts
        </button>
      </div>

      {/* KPI Stats Header */}
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(4, 1fr)',
          gap: 14,
          marginBottom: 20,
        }}
      >
        <div className="card" style={{ padding: '16px 18px' }}>
          <div style={{ fontSize: 11.5, fontWeight: 600, color: '#6b7280', textTransform: 'uppercase' }}>
            Pending Payouts
          </div>
          <div style={{ fontSize: 22, fontWeight: 700, color: '#d97706', marginTop: 4 }}>
            ₹{stats.pendingAmount?.toFixed(2) || '0.00'}
          </div>
          <div style={{ fontSize: 12, color: '#6b7280', marginTop: 3 }}>
            {stats.pendingCount || 0} user requests awaiting payout
          </div>
        </div>

        <div className="card" style={{ padding: '16px 18px' }}>
          <div style={{ fontSize: 11.5, fontWeight: 600, color: '#6b7280', textTransform: 'uppercase' }}>
            Total Paid Out
          </div>
          <div style={{ fontSize: 22, fontWeight: 700, color: '#16a34a', marginTop: 4 }}>
            ₹{stats.processedAmount?.toFixed(2) || '0.00'}
          </div>
          <div style={{ fontSize: 12, color: '#16a34a', marginTop: 3, fontWeight: 500 }}>
            ✓ Successfully disbursed to UPI
          </div>
        </div>

        <div className="card" style={{ padding: '16px 18px' }}>
          <div style={{ fontSize: 11.5, fontWeight: 600, color: '#6b7280', textTransform: 'uppercase' }}>
            Minimum Withdrawal Limit
          </div>
          <div style={{ fontSize: 20, fontWeight: 700, color: '#111827', marginTop: 4 }}>
            ₹500 / 5,000 Coins
          </div>
          <div style={{ fontSize: 12, color: '#6b7280', marginTop: 3 }}>
            Auto-validated on app side
          </div>
        </div>

        <div className="card" style={{ padding: '16px 18px' }}>
          <div style={{ fontSize: 11.5, fontWeight: 600, color: '#6b7280', textTransform: 'uppercase' }}>
            Payout Methods
          </div>
          <div style={{ fontSize: 18, fontWeight: 700, color: '#2563eb', marginTop: 4 }}>
            UPI & Vouchers
          </div>
          <div style={{ fontSize: 12, color: '#6b7280', marginTop: 3 }}>
            GPay, PhonePe, Amazon Pay
          </div>
        </div>
      </div>

      {/* Filter and Search Bar */}
      <div style={{ display: 'flex', gap: 10, marginBottom: 14, flexWrap: 'wrap', alignItems: 'center' }}>
        <div style={{ position: 'relative', flex: '0 0 280px' }}>
          <Search
            size={13}
            style={{
              position: 'absolute',
              left: 10,
              top: '50%',
              transform: 'translateY(-50%)',
              color: '#9ca3af',
            }}
          />
          <input
            className="input"
            placeholder="Search user, email or UPI ID…"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            style={{ paddingLeft: 32 }}
          />
        </div>

        {/* Status Segmented Tabs */}
        <div style={{ display: 'flex', border: '1px solid #e5e7eb', borderRadius: 6, overflow: 'hidden' }}>
          {STATUS_TABS.map((t) => (
            <button
              key={t.value}
              onClick={() => setStatusFilter(t.value)}
              style={{
                padding: '6px 12px',
                fontSize: 12,
                fontWeight: 500,
                border: 'none',
                cursor: 'pointer',
                borderRight: '1px solid #e5e7eb',
                background: statusFilter === t.value ? '#111827' : '#ffffff',
                color: statusFilter === t.value ? '#ffffff' : '#6b7280',
                transition: 'background 0.1s',
                whiteSpace: 'nowrap',
              }}
            >
              {t.label}
            </button>
          ))}
        </div>

        <div style={{ marginLeft: 'auto', fontSize: 12.5, color: '#6b7280', fontWeight: 500 }}>
          Showing <strong>{withdrawals.length}</strong> withdrawal records
        </div>
      </div>

      {/* Withdrawals Table */}
      <div className="card" style={{ overflow: 'hidden' }}>
        <div style={{ overflowX: 'auto' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
            <thead>
              <tr style={{ background: '#f9fafb', borderBottom: '1px solid #e5e7eb' }}>
                <th style={{ padding: '10px 16px', fontSize: 11.5, fontWeight: 600, color: '#6b7280' }}>User</th>
                <th style={{ padding: '10px 16px', fontSize: 11.5, fontWeight: 600, color: '#6b7280' }}>Amount (₹)</th>
                <th style={{ padding: '10px 16px', fontSize: 11.5, fontWeight: 600, color: '#6b7280' }}>Coins Deducted</th>
                <th style={{ padding: '10px 16px', fontSize: 11.5, fontWeight: 600, color: '#6b7280' }}>Method / UPI ID</th>
                <th style={{ padding: '10px 16px', fontSize: 11.5, fontWeight: 600, color: '#6b7280' }}>Status</th>
                <th style={{ padding: '10px 16px', fontSize: 11.5, fontWeight: 600, color: '#6b7280' }}>Date</th>
                <th style={{ padding: '10px 16px', fontSize: 11.5, fontWeight: 600, color: '#6b7280', textAlign: 'right' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                Array.from({ length: 4 }).map((_, i) => (
                  <tr key={i} style={{ borderBottom: '1px solid #f3f4f6' }}>
                    {Array.from({ length: 7 }).map((_, j) => (
                      <td key={j} style={{ padding: '12px 16px' }}>
                        <div className="skeleton" style={{ height: 16 }} />
                      </td>
                    ))}
                  </tr>
                ))
              ) : withdrawals.length === 0 ? (
                <tr>
                  <td colSpan={7} style={{ padding: 40, textAlign: 'center', color: '#6b7280', fontSize: 13 }}>
                    <div style={{ fontWeight: 600, color: '#374151', fontSize: 14, marginBottom: 4 }}>
                      No Withdrawal Requests Found
                    </div>
                    <div>Jab bhi koi user Reward Club app se cashback payout request karega, wo yahan live show hoga.</div>
                  </td>
                </tr>
              ) : (
                withdrawals.map((w, i) => {
                  const dateStr = new Date(w.created_at).toLocaleString('en-IN', {
                    month: 'short',
                    day: 'numeric',
                    year: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit',
                  })

                  return (
                    <tr
                      key={w.id || i}
                      style={{
                        borderBottom: i < withdrawals.length - 1 ? '1px solid #f3f4f6' : 'none',
                        fontSize: 13,
                        color: '#111827',
                        transition: 'background 0.1s ease',
                      }}
                      className="hover:bg-slate-50"
                    >
                      {/* User */}
                      <td style={{ padding: '12px 16px' }}>
                        <div>
                          <div style={{ fontWeight: 600, color: '#111827', fontSize: 13 }}>{w.user_name}</div>
                          <div style={{ fontSize: 11, color: '#6b7280' }}>{w.user_email}</div>
                          <div style={{ fontSize: 11, color: '#9ca3af', fontFamily: 'monospace' }}>{w.user_mobile}</div>
                        </div>
                      </td>

                      {/* Amount */}
                      <td style={{ padding: '12px 16px' }}>
                        <span
                          style={{
                            fontSize: 14,
                            fontWeight: 700,
                            color: '#15803d',
                            background: '#dcfce7',
                            padding: '3px 8px',
                            borderRadius: 5,
                          }}
                        >
                          ₹{w.amount_inr.toFixed(2)}
                        </span>
                      </td>

                      {/* Coins */}
                      <td style={{ padding: '12px 16px', fontFamily: 'monospace', fontWeight: 600, color: '#d97706' }}>
                        🪙 {w.coins_deducted.toLocaleString()}
                      </td>

                      {/* Method & UPI */}
                      <td style={{ padding: '12px 16px' }}>
                        <div>
                          <span
                            style={{
                              fontSize: 11,
                              fontWeight: 600,
                              padding: '2px 6px',
                              borderRadius: 4,
                              background: '#eff6ff',
                              color: '#1d4ed8',
                              display: 'inline-block',
                            }}
                          >
                            {w.payment_method}
                          </span>
                          <div style={{ fontSize: 12, fontFamily: 'monospace', color: '#111827', marginTop: 3 }}>
                            {w.payout_details}
                          </div>
                          {w.tx_hash_or_ref && (
                            <div style={{ fontSize: 10.5, color: '#059669', marginTop: 1 }}>
                              Ref: {w.tx_hash_or_ref}
                            </div>
                          )}
                        </div>
                      </td>

                      {/* Status */}
                      <td style={{ padding: '12px 16px' }}>
                        {w.status === 'processed' ? (
                          <span
                            style={{
                              fontSize: 11,
                              fontWeight: 600,
                              padding: '3px 8px',
                              borderRadius: 5,
                              background: '#ecfdf5',
                              color: '#059669',
                              display: 'inline-flex',
                              alignItems: 'center',
                              gap: 3,
                            }}
                          >
                            <CheckCircle size={11} /> Paid
                          </span>
                        ) : w.status === 'pending' ? (
                          <span
                            style={{
                              fontSize: 11,
                              fontWeight: 600,
                              padding: '3px 8px',
                              borderRadius: 5,
                              background: '#fffbeb',
                              color: '#b45309',
                              display: 'inline-flex',
                              alignItems: 'center',
                              gap: 3,
                            }}
                          >
                            <Clock size={11} /> Pending
                          </span>
                        ) : w.status === 'approved' ? (
                          <span
                            style={{
                              fontSize: 11,
                              fontWeight: 600,
                              padding: '3px 8px',
                              borderRadius: 5,
                              background: '#eff6ff',
                              color: '#1d4ed8',
                              display: 'inline-flex',
                              alignItems: 'center',
                              gap: 3,
                            }}
                          >
                            Approved
                          </span>
                        ) : (
                          <span
                            style={{
                              fontSize: 11,
                              fontWeight: 600,
                              padding: '3px 8px',
                              borderRadius: 5,
                              background: '#fef2f2',
                              color: '#dc2626',
                              display: 'inline-flex',
                              alignItems: 'center',
                              gap: 3,
                            }}
                          >
                            <XCircle size={11} /> Rejected
                          </span>
                        )}
                      </td>

                      {/* Date */}
                      <td style={{ padding: '12px 16px', fontSize: 11.5, color: '#6b7280' }}>
                        {dateStr}
                      </td>

                      {/* Actions */}
                      <td style={{ padding: '12px 16px', textAlign: 'right' }}>
                        <div style={{ display: 'flex', gap: 6, justifyContent: 'flex-end', alignItems: 'center' }}>
                          {w.status === 'pending' && (
                            <>
                              <button
                                onClick={() => setSelectedWithdrawal(w)}
                                style={{
                                  display: 'inline-flex',
                                  alignItems: 'center',
                                  gap: 4,
                                  padding: '4px 9px',
                                  fontSize: 11.5,
                                  fontWeight: 600,
                                  color: '#ffffff',
                                  background: '#16a34a',
                                  border: 'none',
                                  borderRadius: 5,
                                  cursor: 'pointer',
                                }}
                              >
                                <Send size={11} /> Pay UPI
                              </button>
                              <button
                                onClick={() => handleUpdateStatus(w.id, 'rejected')}
                                style={{
                                  padding: '4px 8px',
                                  fontSize: 11.5,
                                  fontWeight: 500,
                                  color: '#dc2626',
                                  background: '#fef2f2',
                                  border: '1px solid #fee2e2',
                                  borderRadius: 5,
                                  cursor: 'pointer',
                                }}
                              >
                                Reject
                              </button>
                            </>
                          )}

                          {w.status === 'approved' && (
                            <button
                              onClick={() => setSelectedWithdrawal(w)}
                              style={{
                                display: 'inline-flex',
                                alignItems: 'center',
                                gap: 4,
                                padding: '4px 9px',
                                fontSize: 11.5,
                                fontWeight: 600,
                                color: '#ffffff',
                                background: '#2563eb',
                                border: 'none',
                                borderRadius: 5,
                                cursor: 'pointer',
                              }}
                            >
                              Mark Paid
                            </button>
                          )}

                          {w.status === 'processed' && (
                            <span style={{ fontSize: 11, color: '#059669', fontWeight: 600 }}>
                              ✓ Completed
                            </span>
                          )}
                        </div>
                      </td>
                    </tr>
                  )
                })
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Pay / Complete Modal */}
      {selectedWithdrawal && (
        <div
          style={{
            position: 'fixed',
            inset: 0,
            background: 'rgba(15, 23, 42, 0.5)',
            backdropFilter: 'blur(4px)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: 9999,
            padding: 20,
          }}
          onClick={() => setSelectedWithdrawal(null)}
        >
          <div
            className="card"
            style={{
              width: '100%',
              maxWidth: 480,
              padding: 22,
              background: '#ffffff',
              borderRadius: 12,
              boxShadow: '0 20px 40px -10px rgba(0, 0, 0, 0.25)',
            }}
            onClick={(e) => e.stopPropagation()}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 14 }}>
              <div>
                <h3 style={{ fontSize: 16, fontWeight: 700, color: '#111827', margin: 0 }}>
                  Disburse Payout to User
                </h3>
                <p style={{ fontSize: 12.5, color: '#6b7280', margin: '2px 0 0 0' }}>
                  Pay ₹{selectedWithdrawal.amount_inr} to {selectedWithdrawal.user_name}
                </p>
              </div>
              <button
                onClick={() => setSelectedWithdrawal(null)}
                style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#6b7280' }}
              >
                <X size={16} />
              </button>
            </div>

            <div style={{ background: '#f0fdf4', border: '1px solid #bbf7d0', padding: 12, borderRadius: 8, marginBottom: 14 }}>
              <div style={{ fontSize: 11, color: '#166534', fontWeight: 700, textTransform: 'uppercase' }}>
                UPI Payout Address
              </div>
              <div style={{ fontSize: 16, fontWeight: 800, color: '#15803d', fontFamily: 'monospace', marginTop: 2 }}>
                {selectedWithdrawal.payout_details}
              </div>
              <div style={{ fontSize: 11.5, color: '#166534', marginTop: 4 }}>
                Amount to transfer: <strong>₹{selectedWithdrawal.amount_inr.toFixed(2)}</strong> (5,000 Coins)
              </div>
            </div>

            <div style={{ marginBottom: 16 }}>
              <label style={{ fontSize: 12, fontWeight: 600, color: '#374151', display: 'block', marginBottom: 4 }}>
                UPI Transaction / Bank Reference ID (Optional):
              </label>
              <input
                type="text"
                placeholder="e.g. UPI/425519827391 or Bank UTR"
                value={txRefInput}
                onChange={(e) => setTxRefInput(e.target.value)}
                style={{
                  width: '100%',
                  padding: '7px 10px',
                  fontSize: 12.5,
                  border: '1px solid #d1d5db',
                  borderRadius: 6,
                  fontFamily: 'monospace',
                }}
              />
            </div>

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 8 }}>
              <button
                onClick={() => setSelectedWithdrawal(null)}
                style={{
                  padding: '7px 14px',
                  fontSize: 12.5,
                  fontWeight: 600,
                  color: '#4b5563',
                  background: '#f3f4f6',
                  border: '1px solid #e5e7eb',
                  borderRadius: 6,
                  cursor: 'pointer',
                }}
              >
                Cancel
              </button>
              <button
                onClick={() => handleUpdateStatus(selectedWithdrawal.id, 'processed')}
                disabled={updatingId === selectedWithdrawal.id}
                style={{
                  padding: '7px 16px',
                  fontSize: 12.5,
                  fontWeight: 600,
                  color: '#ffffff',
                  background: '#16a34a',
                  border: 'none',
                  borderRadius: 6,
                  cursor: 'pointer',
                  display: 'flex',
                  alignItems: 'center',
                  gap: 5,
                }}
              >
                <Check size={13} /> Confirm & Mark as Paid
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
