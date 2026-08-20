'use client'

import Link from 'next/link'
import { useEffect, useState } from 'react'
import {
  AreaChart, Area, BarChart, Bar, XAxis, YAxis,
  CartesianGrid, Tooltip, ResponsiveContainer, Cell,
} from 'recharts'
import { ArrowUpRight, ArrowDownRight, Users, IndianRupee, ShoppingBag, TrendingUp, Coins } from 'lucide-react'

type KPIs = {
  totalUsers: number; totalOrderVolume: string; totalCommission: string
  totalCashbackCoins: number; totalTransactions: number
  approvedCount: number; pendingCount: number; rejectedCount: number
  totalUnpaidCommission: string
}
type RecentUser  = { id: string; full_name: string | null; email: string; total_coins: number | null; created_at: string }
type RecentTx    = {
  id: string; merchant_name: string | null
  commission_earned: number; status: string
  profiles?: { full_name: string | null; email: string } | null
}

const weekData = [
  { d: 'M', v: 4200 }, { d: 'T', v: 6800 }, { d: 'W', v: 3900 },
  { d: 'T', v: 8200 }, { d: 'F', v: 11000 }, { d: 'S', v: 14500 }, { d: 'S', v: 9700 },
]
const storeData = [
  { n: 'Amazon', v: 45 }, { n: 'Flipkart', v: 28 }, { n: 'Myntra', v: 14 }, { n: 'Others', v: 13 },
]

function MetricCard({ label, value, sub, positive, icon: Icon, iconColor = '#9ca3af', href }: { label: string; value: string | number; sub?: string; positive?: boolean; icon?: any; iconColor?: string; href?: string }) {
  const cardContent = (
    <div className="card" style={{ padding: '20px 22px', height: '100%' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div>
          <div className="metric-label">{label}</div>
          <div className="metric-value" style={{ marginTop: 8 }}>{value}</div>
        </div>
        {Icon && (
          <div style={{ color: iconColor, flexShrink: 0 }}>
            <Icon size={16} strokeWidth={2.5} />
          </div>
        )}
      </div>
      {sub && (
        <div style={{ marginTop: 6, display: 'flex', alignItems: 'center', gap: 4 }}>
          {positive !== undefined && (
            positive
              ? <ArrowUpRight size={12} style={{ color: '#16a34a' }} />
              : <ArrowDownRight size={12} style={{ color: '#dc2626' }} />
          )}
          <span className="metric-sub">{sub}</span>
        </div>
      )}
    </div>
  )

  if (href) {
    return (
      <Link href={href} className="metric-card-link" style={{ textDecoration: 'none', display: 'block' }}>
        {cardContent}
      </Link>
    )
  }

  return cardContent
}

export default function DashboardPage() {
  const [kpis, setKpis] = useState<KPIs | null>(null)
  const [users, setUsers]   = useState<RecentUser[]>([])
  const [txs, setTxs]       = useState<RecentTx[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetch('/api/dashboard').then(r => r.json()).then(d => {
      setKpis(d.kpis); setUsers(d.recentUsers ?? []); setTxs(d.recentTransactions ?? [])
    }).finally(() => setLoading(false))
  }, [])

  const statusCfg: Record<string, { cls: string; label: string }> = {
    approved: { cls: 'badge-green',  label: 'Approved' },
    pending:  { cls: 'badge-yellow', label: 'Pending'  },
    rejected: { cls: 'badge-red',    label: 'Rejected' },
  }

  return (
    <div style={{ maxWidth: 1100 }}>

      {/* Page heading */}
      <div style={{ marginBottom: 24 }}>
        <h1 style={{ fontSize: 20, fontWeight: 700, color: '#111827', margin: 0, letterSpacing: '-0.02em' }}>
          Overview
        </h1>
        <p style={{ fontSize: 13, color: '#6b7280', marginTop: 4 }}>
          Your affiliate + cashback performance at a glance.
        </p>
      </div>

      {/* ── KPI row ─────────────────────────────────────────────── */}
      {loading ? (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(5,1fr)', gap: 12, marginBottom: 20 }}>
          {[1,2,3,4,5].map(i => <div key={i} className="skeleton" style={{ height: 90 }} />)}
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(5,1fr)', gap: 12, marginBottom: 20 }}>
          <MetricCard label="Users"            value={kpis?.totalUsers ?? 0} sub="Registered in app" icon={Users} iconColor="#2563eb" href="/dashboard/users" />
          <MetricCard label="Order Volume"     value={`₹${parseFloat(kpis?.totalOrderVolume ?? '0').toLocaleString('en-IN')}`} sub="Lifetime GMV" positive={true} icon={IndianRupee} iconColor="#16a34a" href="/dashboard/transactions" />
          <MetricCard label="Orders"           value={kpis?.totalTransactions ?? 0} sub="Total conversions" icon={ShoppingBag} iconColor="#0ea5e9" href="/dashboard/transactions" />
          <MetricCard label="Commission"       value={`₹${parseFloat(kpis?.totalCommission ?? '0').toLocaleString('en-IN')}`} sub={`₹${kpis?.totalUnpaidCommission} pending`} icon={TrendingUp} iconColor="#8b5cf6" href="/dashboard/transactions" />
          <MetricCard label="Cashback"         value={`${(kpis?.totalCashbackCoins ?? 0).toLocaleString()} Coins`} sub="Credited to users" icon={Coins} iconColor="#eab308" href="/dashboard/transactions" />
        </div>
      )}

      {/* ── Transaction status bar ─────────────────────────────── */}
      {!loading && kpis && (
        <div className="card" style={{ padding: '16px 22px', marginBottom: 20, display: 'flex', alignItems: 'center', gap: 32 }}>
          <div>
            <div className="metric-label">Total Transactions</div>
            <div style={{ fontSize: 20, fontWeight: 700, color: '#111827', marginTop: 3 }}>{kpis.totalTransactions}</div>
          </div>
          <div style={{ width: 1, height: 36, background: '#e5e7eb' }} />
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <span style={{ width: 8, height: 8, borderRadius: '50%', background: '#16a34a', display: 'inline-block' }} />
            <span style={{ fontSize: 13, color: '#374151' }}><strong>{kpis.approvedCount}</strong> approved</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <span style={{ width: 8, height: 8, borderRadius: '50%', background: '#ca8a04', display: 'inline-block' }} />
            <span style={{ fontSize: 13, color: '#374151' }}><strong>{kpis.pendingCount}</strong> pending</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <span style={{ width: 8, height: 8, borderRadius: '50%', background: '#dc2626', display: 'inline-block' }} />
            <span style={{ fontSize: 13, color: '#374151' }}><strong>{kpis.rejectedCount}</strong> rejected</span>
          </div>
        </div>
      )}

      {/* ── Charts ─────────────────────────────────────────────── */}
      <div style={{ marginBottom: 20 }}>
        <div className="card" style={{ padding: '20px 22px' }}>
          <div style={{ marginBottom: 16 }}>
            <div style={{ fontSize: 13, fontWeight: 600, color: '#111827' }}>Store Breakdown</div>
            <div style={{ fontSize: 12, color: '#9ca3af', marginTop: 2 }}>Share of conversions (%)</div>
          </div>
          <ResponsiveContainer width="100%" height={220}>
            <BarChart data={storeData} barSize={24} margin={{ top: 0, right: 0, left: -20, bottom: 0 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="#f3f4f6" vertical={false} />
              <XAxis dataKey="n" tick={{ fill: '#9ca3af', fontSize: 11 }} axisLine={false} tickLine={false} />
              <YAxis tick={{ fill: '#9ca3af', fontSize: 11 }} axisLine={false} tickLine={false} />
              <Tooltip contentStyle={{ background: '#fff', border: '1px solid #e5e7eb', borderRadius: 6, fontSize: 12 }} />
              <Bar dataKey="v" radius={[4, 4, 0, 0]}>
                {storeData.map((_, i) => (
                  <Cell key={i} fill={i === 0 ? '#111827' : `rgba(17,24,39,${0.25 - i * 0.06})`} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* ── Recent activity ─────────────────────────────────────── */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
        {/* Recent users */}
        <div className="card">
          <div style={{ padding: '16px 20px', borderBottom: '1px solid #e5e7eb' }}>
            <div style={{ fontSize: 13, fontWeight: 600, color: '#111827' }}>Recent Signups</div>
          </div>
          {loading ? (
            <div style={{ padding: 16, display: 'flex', flexDirection: 'column', gap: 10 }}>
              {[1,2,3].map(i => <div key={i} className="skeleton" style={{ height: 36 }} />)}
            </div>
          ) : users.length === 0 ? (
            <div style={{ padding: 20, color: '#9ca3af', fontSize: 13 }}>No users yet.</div>
          ) : (
            <div>
              {users.map((u, i) => (
                <div key={u.id} style={{
                  padding: '12px 20px', display: 'flex', alignItems: 'center', gap: 12,
                  borderBottom: i < users.length - 1 ? '1px solid #f3f4f6' : 'none',
                }}>
                  <div style={{ width: 30, height: 30, borderRadius: '50%', background: '#f3f4f6', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 12, fontWeight: 700, color: '#374151', flexShrink: 0 }}>
                    {(u.full_name || u.email)[0].toUpperCase()}
                  </div>
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <div style={{ fontSize: 13, fontWeight: 500, color: '#111827', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                      {u.full_name || u.email}
                    </div>
                    <div style={{ fontSize: 11.5, color: '#9ca3af', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                      {u.email}
                    </div>
                  </div>
                  <div style={{ fontSize: 12, color: '#374151', fontWeight: 500, whiteSpace: 'nowrap' }}>
                    {u.total_coins ?? 0} coins
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Recent transactions */}
        <div className="card">
          <div style={{ padding: '16px 20px', borderBottom: '1px solid #e5e7eb' }}>
            <div style={{ fontSize: 13, fontWeight: 600, color: '#111827' }}>Recent Conversions</div>
          </div>
          {loading ? (
            <div style={{ padding: 16, display: 'flex', flexDirection: 'column', gap: 10 }}>
              {[1,2,3].map(i => <div key={i} className="skeleton" style={{ height: 36 }} />)}
            </div>
          ) : txs.length === 0 ? (
            <div style={{ padding: 20, color: '#9ca3af', fontSize: 13 }}>No conversions yet. Run a sync.</div>
          ) : (
            <div>
              {txs.map((tx, i) => {
                const cfg = statusCfg[tx.status] ?? statusCfg.pending
                return (
                  <div key={tx.id} style={{
                    padding: '12px 20px', display: 'flex', alignItems: 'center', gap: 12,
                    borderBottom: i < txs.length - 1 ? '1px solid #f3f4f6' : 'none',
                  }}>
                    <div style={{ flex: 1, minWidth: 0 }}>
                      <div style={{ fontSize: 13, fontWeight: 500, color: '#111827' }}>
                        {tx.merchant_name ?? 'Unknown Store'}
                      </div>
                      <div style={{ fontSize: 11.5, color: '#9ca3af', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                        {tx.profiles?.full_name ?? tx.profiles?.email ?? '—'}
                      </div>
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                      <span style={{ fontSize: 13, fontWeight: 600, color: '#111827' }}>
                        ₹{tx.commission_earned.toFixed(0)}
                      </span>
                      <span className={`badge ${cfg.cls}`}>{cfg.label}</span>
                    </div>
                  </div>
                )
              })}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
