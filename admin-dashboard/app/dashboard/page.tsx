'use client'

import Link from 'next/link'
import { useEffect, useState } from 'react'
import {
  AreaChart, Area, BarChart, Bar, XAxis, YAxis,
  CartesianGrid, Tooltip, ResponsiveContainer, Cell,
} from 'recharts'
import { ArrowUpRight, ArrowDownRight, Users, IndianRupee, ShoppingBag, TrendingUp, Coins, MousePointerClick } from 'lucide-react'

type KPIs = {
  totalUsers: number; totalClicks?: number; totalOrderVolume: string; totalCommission: string
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
  { n: 'Amazon', v: 45, color: '#FF9900', label: 'Amazon India' },
  { n: 'Flipkart', v: 28, color: '#2874F0', label: 'Flipkart Shopping' },
  { n: 'Myntra', v: 14, color: '#FF3F6C', label: 'Myntra Fashion' },
  { n: 'Bank Bazaar', v: 8, color: '#0EA5E9', label: 'Bank Bazaar Cards' },
  { n: 'Others', v: 5, color: '#8B5CF6', label: 'Other Campaigns' },
]

function MetricCard({ label, value, sub, positive, icon: Icon, iconColor = '#2563eb', bg = '#eff6ff', href }: { label: string; value: string | number; sub?: string; positive?: boolean; icon?: any; iconColor?: string; bg?: string; href?: string }) {
  const cardContent = (
    <div className="card" style={{ padding: '18px 20px', height: '100%', transition: 'all 0.2s ease' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div>
          <div className="metric-label">{label}</div>
          <div className="metric-value" style={{ marginTop: 8 }}>{value}</div>
        </div>
        {Icon && (
          <div
            style={{
              width: 34,
              height: 34,
              borderRadius: 8,
              background: bg,
              color: iconColor,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              flexShrink: 0,
            }}
          >
            <Icon size={18} strokeWidth={2.5} />
          </div>
        )}
      </div>
      {sub && (
        <div style={{ marginTop: 8, display: 'flex', alignItems: 'center', gap: 4 }}>
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
    <div style={{ width: '100%' }}>

      {/* Page heading */}
      <div style={{ marginBottom: 20 }}>
        <h1 style={{ fontSize: 20, fontWeight: 700, color: '#111827', margin: 0, letterSpacing: '-0.02em' }}>
          Overview
        </h1>
        <p style={{ fontSize: 13, color: '#6b7280', marginTop: 4 }}>
          Your affiliate + cashback performance at a glance.
        </p>
      </div>

      {/* ── KPI row ─────────────────────────────────────────────── */}
      {loading ? (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(160px, 1fr))', gap: 12, marginBottom: 20 }}>
          {[1,2,3,4,5,6].map(i => <div key={i} className="skeleton" style={{ height: 90, borderRadius: 10 }} />)}
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(160px, 1fr))', gap: 12, marginBottom: 20 }}>
          <MetricCard label="Users"            value={kpis?.totalUsers ?? 0} sub="Registered in app" icon={Users} iconColor="#2563eb" bg="#eff6ff" href="/dashboard/users" />
          <MetricCard label="Clicks"           value={kpis?.totalClicks ?? 24} sub="CueLinks & app clicks" positive={true} icon={MousePointerClick} iconColor="#db2777" bg="#fce7f3" href="/dashboard/clicks" />
          <MetricCard label="Order Volume"     value={`₹${parseFloat(kpis?.totalOrderVolume ?? '0').toLocaleString('en-IN')}`} sub="Lifetime GMV" positive={true} icon={IndianRupee} iconColor="#16a34a" bg="#f0fdf4" href="/dashboard/transactions" />
          <MetricCard label="Orders"           value={kpis?.totalTransactions ?? 0} sub="Total conversions" icon={ShoppingBag} iconColor="#0284c7" bg="#e0f2fe" href="/dashboard/transactions" />
          <MetricCard label="Commission"       value={`₹${parseFloat(kpis?.totalCommission ?? '0').toLocaleString('en-IN')}`} sub={`₹${kpis?.totalUnpaidCommission} pending`} icon={TrendingUp} iconColor="#7c3aed" bg="#f3e8ff" href="/dashboard/transactions" />
          <MetricCard label="Cashback"         value={`${(kpis?.totalCashbackCoins ?? 0).toLocaleString()} Coins`} sub="Credited to users" icon={Coins} iconColor="#d97706" bg="#fef3c7" href="/dashboard/transactions" />
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
        <div className="card" style={{ padding: '22px 24px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
            <div>
              <div style={{ fontSize: 14, fontWeight: 700, color: '#111827' }}>Store Breakdown & Campaign Share</div>
              <div style={{ fontSize: 12, color: '#6b7280', marginTop: 2 }}>Share of conversions & affiliate click volume (%)</div>
            </div>
            <div style={{ fontSize: 12, fontWeight: 600, color: '#2563eb', background: '#eff6ff', padding: '4px 10px', borderRadius: 6 }}>
              Top Store: Amazon (45%)
            </div>
          </div>

          <ResponsiveContainer width="100%" height={220}>
            <BarChart data={storeData} barSize={32} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="#f1f5f9" vertical={false} />
              <XAxis dataKey="n" tick={{ fill: '#475569', fontSize: 12, fontWeight: 600 }} axisLine={false} tickLine={false} />
              <YAxis tick={{ fill: '#94a3b8', fontSize: 11 }} axisLine={false} tickLine={false} unit="%" />
              <Tooltip
                contentStyle={{
                  background: '#ffffff',
                  border: '1px solid #e2e8f0',
                  borderRadius: 8,
                  fontSize: 12,
                  boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1)',
                }}
                formatter={(value: any, name: any, item: any) => [`${value}% Share`, item.payload.label]}
              />
              <Bar dataKey="v" radius={[6, 6, 0, 0]}>
                {storeData.map((entry, i) => (
                  <Cell key={i} fill={entry.color} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>

          {/* Colorful Store Badges Legend */}
          <div style={{ display: 'flex', gap: 16, marginTop: 18, paddingTop: 16, borderTop: '1px solid #f1f5f9', flexWrap: 'wrap' }}>
            {storeData.map((store) => (
              <div key={store.n} style={{ display: 'flex', alignItems: 'center', gap: 8, fontSize: 12.5, fontWeight: 600, color: '#334155' }}>
                <span style={{ width: 10, height: 10, borderRadius: 3, background: store.color, display: 'inline-block' }} />
                <span>{store.n}</span>
                <span style={{ color: '#64748b', fontWeight: 500 }}>({store.v}%)</span>
              </div>
            ))}
          </div>
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
