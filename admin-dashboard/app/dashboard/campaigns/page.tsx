'use client'

import { useEffect, useState } from 'react'
import {
  Megaphone,
  Search,
  ExternalLink,
  Copy,
  Check,
  RefreshCw,
  Zap,
  Info,
  ChevronLeft,
  ChevronRight,
  X,
  HelpCircle
} from 'lucide-react'

type Campaign = {
  id: string | number
  name: string
  domain: string
  url: string
  payout: number | string
  payout_type: string
  payout_currency: string
  image: string | null
  category: string
  reporting_type: string
  status: string
  cookie_duration: string
  is_free_to_test: boolean
  test_type: string
  conversion_flow: Record<string, string>
  affiliate_url: string
}

const CATEGORY_TABS = [
  'All',
  'Finance',
  'Health & Beauty',
  'Fashion',
  'Electronics',
  'Travel',
  'Food & Grocery',
  'Services',
] as const

const PAYOUT_TABS = [
  { label: 'All Models', value: 'all' },
  { label: '⚡ Free Test (Leads/Cards)', value: 'free_lead' },
  { label: '🛍️ Per Sale (%)', value: 'per_sale' },
] as const

function formatPayout(payout: number | string, payoutType: string): string {
  if (typeof payout === 'number') {
    if (payoutType.includes('%') || !payoutType.toLowerCase().includes('click')) {
      const rounded = Number.isInteger(payout) ? payout : parseFloat(payout.toFixed(2))
      return payoutType.includes('%') ? `${rounded}%` : `₹${rounded}`
    }
    return `₹${parseFloat(payout.toFixed(2))}`
  }
  const num = parseFloat(String(payout))
  if (!isNaN(num)) {
    const rounded = Number.isInteger(num) ? num : parseFloat(num.toFixed(2))
    return payoutType.includes('%') ? `${rounded}%` : `₹${rounded}`
  }
  return String(payout)
}

export default function CampaignsPage() {
  const [campaigns, setCampaigns] = useState<Campaign[]>([])
  const [loading, setLoading] = useState(true)
  const [search, setSearch] = useState('')
  const [category, setCategory] = useState<string>('All')
  const [payoutFilter, setPayoutFilter] = useState('all')
  const [copiedId, setCopiedId] = useState<string | number | null>(null)
  const [isSyncing, setIsSyncing] = useState(false)
  const [selectedCampaign, setSelectedCampaign] = useState<Campaign | null>(null)
  const [page, setPage] = useState(1)
  const [testSubId, setTestSubId] = useState('ADMIN_TEST_USER')
  const [showGuide, setShowGuide] = useState(false)
  const limit = 15

  const fetchCampaigns = async () => {
    setLoading(true)
    try {
      const catParam = category === 'All' ? 'all' : category
      const res = await fetch(
        `/api/campaigns?search=${encodeURIComponent(search)}&category=${encodeURIComponent(catParam)}&payoutType=${payoutFilter}`
      )
      const data = await res.json()
      if (data.campaigns) {
        setCampaigns(data.campaigns)
      }
    } catch (err) {
      console.error('Error fetching campaigns:', err)
    } finally {
      setLoading(false)
      setIsSyncing(false)
    }
  }

  useEffect(() => {
    fetchCampaigns()
  }, [search, category, payoutFilter])

  const copyToClipboard = async (text: string, camp: Campaign, e?: React.MouseEvent) => {
    if (e) e.stopPropagation()
    navigator.clipboard.writeText(text)
    setCopiedId(camp.id)
    setTimeout(() => setCopiedId(null), 2000)
    try {
      await fetch('/api/clicks', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          campaignName: camp.name,
          brandName: camp.name,
          userId: testSubId || 'ADMIN_TEST_USER'
        })
      })
    } catch {}
  }

  const handleTestLinkClick = async (camp: Campaign, e?: React.MouseEvent) => {
    try {
      await fetch('/api/clicks', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          campaignName: camp.name,
          brandName: camp.name,
          userId: testSubId || 'ADMIN_TEST_USER'
        })
      })
    } catch {}
  }

  const getAffiliateTestUrl = (baseAffiliateUrl: string) => {
    if (!baseAffiliateUrl) return ''
    try {
      const urlObj = new URL(baseAffiliateUrl)
      urlObj.searchParams.set('subid', testSubId || 'ADMIN_TEST_USER')
      return urlObj.toString()
    } catch {
      return baseAffiliateUrl
    }
  }

  // Pagination
  const total = campaigns.length
  const totalPages = Math.max(1, Math.ceil(total / limit))
  const paginatedCampaigns = campaigns.slice((page - 1) * limit, page * limit)

  return (
    <div style={{ width: '100%' }}>
      {/* Top Header */}
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
            <Megaphone size={20} style={{ color: '#2563eb' }} />
            Active Campaigns
          </h1>
          <p style={{ fontSize: 13, color: '#6b7280', marginTop: 3 }}>
            {total} pre-approved CueLinks merchant campaigns available in your channel (CID: 301603)
          </p>
        </div>

        <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
          <button
            onClick={() => setShowGuide(!showGuide)}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: 6,
              fontSize: 12.5,
              fontWeight: 600,
              color: showGuide ? '#15803d' : '#2563eb',
              background: showGuide ? '#dcfce7' : '#eff6ff',
              border: `1px solid ${showGuide ? '#86efac' : '#bfdbfe'}`,
              padding: '6px 12px',
              borderRadius: 6,
              cursor: 'pointer',
              transition: 'all 0.15s ease',
            }}
          >
            <HelpCircle size={14} />
            {showGuide ? 'Hide Testing Guide' : 'Free Test Guide (₹0)'}
          </button>

          <button
            onClick={() => {
              setIsSyncing(true)
              fetchCampaigns()
            }}
            className="btn"
            style={{ display: 'flex', alignItems: 'center', gap: 6, padding: '6px 14px', fontSize: 12.5 }}
          >
            <RefreshCw size={13} className={isSyncing ? 'animate-spin' : ''} />
            Refresh
          </button>
        </div>
      </div>

      {/* Free Test Guide Box (Collapsible) */}
      {showGuide && (
        <div
          style={{
            background: '#f0fdf4',
            border: '1px solid #bbf7d0',
            borderRadius: 8,
            padding: '14px 18px',
            marginBottom: 16,
            fontSize: 13,
            color: '#166534',
          }}
        >
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
            <span style={{ fontWeight: 700, fontSize: 13.5 }}>
              💡 Free Testing Guide (Bina Paisa Kharch Kare Earnings Test Karein)
            </span>
            <button
              onClick={() => setShowGuide(false)}
              style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#16a34a' }}
            >
              <X size={15} />
            </button>
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: 10 }}>
            <div style={{ background: '#ffffff', padding: '10px 12px', borderRadius: 6, border: '1px solid #dcfce7' }}>
              <strong>1. Credit Cards & Loans (CPL):</strong> BankBazaar, SBI Card, AU Bank ke lead form me eligibility check karne par free commission generate hota hai.
            </div>
            <div style={{ background: '#ffffff', padding: '10px 12px', borderRadius: 6, border: '1px solid #dcfce7' }}>
              <strong>2. Demat & Finance Apps:</strong> Free Demat account open karne par flat ₹200-₹500 payout milta hai.
            </div>
            <div style={{ background: '#ffffff', padding: '10px 12px', borderRadius: 6, border: '1px solid #dcfce7' }}>
              <strong>3. SubID Verification:</strong> Kisi bhi campaign ka <em>"Test Link"</em> click karke CueLinks dashboard par click reflect verify karein.
            </div>
          </div>
        </div>
      )}

      {/* Filter and Search Bar */}
      <div style={{ display: 'flex', gap: 10, marginBottom: 14, flexWrap: 'wrap', alignItems: 'center' }}>
        {/* Search */}
        <div style={{ position: 'relative', flex: '0 0 260px' }}>
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
            placeholder="Search merchant or domain…"
            value={search}
            onChange={(e) => {
              setSearch(e.target.value)
              setPage(1)
            }}
            style={{ paddingLeft: 32 }}
          />
        </div>

        {/* Payout Model Tabs */}
        <div style={{ display: 'flex', border: '1px solid #e5e7eb', borderRadius: 6, overflow: 'hidden' }}>
          {PAYOUT_TABS.map((t) => (
            <button
              key={t.value}
              onClick={() => {
                setPayoutFilter(t.value)
                setPage(1)
              }}
              style={{
                padding: '6px 12px',
                fontSize: 12,
                fontWeight: 500,
                border: 'none',
                cursor: 'pointer',
                borderRight: '1px solid #e5e7eb',
                background: payoutFilter === t.value ? '#111827' : '#ffffff',
                color: payoutFilter === t.value ? '#ffffff' : '#6b7280',
                transition: 'background 0.1s',
                whiteSpace: 'nowrap',
              }}
            >
              {t.label}
            </button>
          ))}
        </div>

        {/* Category Select */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
          <select
            value={category}
            onChange={(e) => {
              setCategory(e.target.value)
              setPage(1)
            }}
            style={{
              padding: '6px 10px',
              fontSize: 12.5,
              border: '1px solid #e5e7eb',
              borderRadius: 6,
              background: '#ffffff',
              color: '#374151',
              fontWeight: 500,
              outline: 'none',
            }}
          >
            {CATEGORY_TABS.map((c) => (
              <option key={c} value={c}>
                {c === 'All' ? 'All Categories' : c}
              </option>
            ))}
          </select>
        </div>

        {/* Test SubID Input */}
        <div style={{ marginLeft: 'auto', display: 'flex', alignItems: 'center', gap: 6 }}>
          <span style={{ fontSize: 11.5, color: '#6b7280', fontWeight: 500 }}>Test SubID:</span>
          <input
            type="text"
            value={testSubId}
            onChange={(e) => setTestSubId(e.target.value)}
            style={{
              padding: '4px 8px',
              fontSize: 11.5,
              fontFamily: 'monospace',
              border: '1px solid #e5e7eb',
              borderRadius: 6,
              background: '#ffffff',
              width: 140,
            }}
            title="Tracking SubID appended to test links"
          />
        </div>
      </div>

      {/* Campaigns Table */}
      <div className="card" style={{ overflow: 'hidden' }}>
        <div style={{ overflowX: 'auto' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
            <thead>
              <tr style={{ background: '#f9fafb', borderBottom: '1px solid #e5e7eb' }}>
                <th style={{ padding: '10px 16px', fontSize: 11.5, fontWeight: 600, color: '#6b7280' }}>
                  Merchant / Campaign
                </th>
                <th style={{ padding: '10px 16px', fontSize: 11.5, fontWeight: 600, color: '#6b7280' }}>
                  Category
                </th>
                <th style={{ padding: '10px 16px', fontSize: 11.5, fontWeight: 600, color: '#6b7280' }}>
                  Payout Rate
                </th>
                <th style={{ padding: '10px 16px', fontSize: 11.5, fontWeight: 600, color: '#6b7280' }}>
                  Payout Type
                </th>
                <th style={{ padding: '10px 16px', fontSize: 11.5, fontWeight: 600, color: '#6b7280' }}>
                  Status
                </th>
                <th style={{ padding: '10px 16px', fontSize: 11.5, fontWeight: 600, color: '#6b7280', textAlign: 'right' }}>
                  Actions
                </th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                Array.from({ length: 6 }).map((_, i) => (
                  <tr key={i} style={{ borderBottom: '1px solid #f3f4f6' }}>
                    {Array.from({ length: 6 }).map((_, j) => (
                      <td key={j} style={{ padding: '12px 16px' }}>
                        <div className="skeleton" style={{ height: 16 }} />
                      </td>
                    ))}
                  </tr>
                ))
              ) : paginatedCampaigns.length === 0 ? (
                <tr>
                  <td
                    colSpan={6}
                    style={{
                      padding: 36,
                      textAlign: 'center',
                      color: '#9ca3af',
                      fontSize: 13,
                    }}
                  >
                    No active campaigns match your criteria.
                  </td>
                </tr>
              ) : (
                paginatedCampaigns.map((camp, i) => {
                  const testLink = getAffiliateTestUrl(camp.affiliate_url)
                  const isCopied = copiedId === camp.id
                  const payoutText = formatPayout(camp.payout, camp.payout_type)

                  return (
                    <tr
                      key={camp.id || i}
                      onClick={() => setSelectedCampaign(camp)}
                      style={{
                        borderBottom: i < paginatedCampaigns.length - 1 ? '1px solid #f3f4f6' : 'none',
                        fontSize: 13,
                        color: '#111827',
                        cursor: 'pointer',
                        transition: 'background 0.1s ease',
                      }}
                      className="hover:bg-slate-50"
                    >
                      {/* Merchant Logo & Info */}
                      <td style={{ padding: '12px 16px' }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                          {camp.image ? (
                            <img
                              src={camp.image}
                              alt={camp.name}
                              style={{
                                width: 32,
                                height: 32,
                                borderRadius: 6,
                                objectFit: 'contain',
                                background: '#ffffff',
                                border: '1px solid #e5e7eb',
                                padding: 2,
                                flexShrink: 0,
                              }}
                            />
                          ) : (
                            <div
                              style={{
                                width: 32,
                                height: 32,
                                borderRadius: 6,
                                background: '#eff6ff',
                                color: '#2563eb',
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'center',
                                fontWeight: 700,
                                fontSize: 13,
                                flexShrink: 0,
                              }}
                            >
                              {camp.name[0]}
                            </div>
                          )}
                          <div style={{ minWidth: 0 }}>
                            <div style={{ fontWeight: 600, color: '#111827', fontSize: 13, lineHeight: 1.2 }}>
                              {camp.name}
                            </div>
                            <div style={{ fontSize: 11, color: '#9ca3af', fontFamily: 'monospace', marginTop: 2 }}>
                              {camp.domain}
                            </div>
                          </div>
                        </div>
                      </td>

                      {/* Category */}
                      <td style={{ padding: '12px 16px' }}>
                        <span
                          style={{
                            fontSize: 11.5,
                            fontWeight: 500,
                            padding: '2px 8px',
                            borderRadius: 5,
                            background: '#f3f4f6',
                            color: '#4b5563',
                            whiteSpace: 'nowrap',
                          }}
                        >
                          {camp.category}
                        </span>
                      </td>

                      {/* Payout */}
                      <td style={{ padding: '12px 16px' }}>
                        <span
                          style={{
                            fontSize: 12.5,
                            fontWeight: 700,
                            color: '#15803d',
                            background: '#dcfce7',
                            padding: '3px 7px',
                            borderRadius: 5,
                            display: 'inline-block',
                          }}
                        >
                          {payoutText}
                        </span>
                      </td>

                      {/* Payout Type / Testing Badge */}
                      <td style={{ padding: '12px 16px' }}>
                        {camp.is_free_to_test ? (
                          <span
                            style={{
                              fontSize: 11,
                              fontWeight: 600,
                              padding: '2px 7px',
                              borderRadius: 5,
                              background: '#dbeafe',
                              color: '#1d4ed8',
                              display: 'inline-flex',
                              alignItems: 'center',
                              gap: 3,
                              whiteSpace: 'nowrap',
                            }}
                          >
                            <Zap size={10} />
                            Free Lead / Card
                          </span>
                        ) : (
                          <span
                            style={{
                              fontSize: 11,
                              color: '#6b7280',
                              whiteSpace: 'nowrap',
                            }}
                          >
                            {camp.payout_type}
                          </span>
                        )}
                      </td>

                      {/* Status */}
                      <td style={{ padding: '12px 16px' }}>
                        <span
                          style={{
                            fontSize: 11,
                            fontWeight: 600,
                            padding: '2px 7px',
                            borderRadius: 5,
                            background: '#ecfdf5',
                            color: '#059669',
                            display: 'inline-block',
                            whiteSpace: 'nowrap',
                          }}
                        >
                          ✓ Pre-Approved
                        </span>
                      </td>

                      {/* Actions */}
                      <td style={{ padding: '12px 16px', textAlign: 'right' }}>
                        <div style={{ display: 'flex', gap: 6, justifyContent: 'flex-end', alignItems: 'center' }}>
                          <button
                            onClick={(e) => copyToClipboard(testLink, camp, e)}
                            title="Copy link with test SubID"
                            style={{
                              display: 'inline-flex',
                              alignItems: 'center',
                              gap: 4,
                              padding: '4px 8px',
                              fontSize: 11.5,
                              fontWeight: 500,
                              color: isCopied ? '#15803d' : '#4b5563',
                              background: isCopied ? '#dcfce7' : '#f3f4f6',
                              border: '1px solid #e5e7eb',
                              borderRadius: 5,
                              cursor: 'pointer',
                            }}
                          >
                            {isCopied ? <Check size={11} /> : <Copy size={11} />}
                            {isCopied ? 'Copied' : 'Copy'}
                          </button>

                          <a
                            href={testLink}
                            target="_blank"
                            rel="noopener noreferrer"
                            onClick={(e) => {
                              handleTestLinkClick(camp, e)
                            }}
                            style={{
                              display: 'inline-flex',
                              alignItems: 'center',
                              gap: 4,
                              padding: '4px 10px',
                              fontSize: 11.5,
                              fontWeight: 600,
                              color: '#ffffff',
                              background: '#2563eb',
                              borderRadius: 5,
                              textDecoration: 'none',
                            }}
                          >
                            Test Link
                            <ExternalLink size={10} />
                          </a>
                        </div>
                      </td>
                    </tr>
                  )
                })
              )}
            </tbody>
          </table>
        </div>

        {/* Table Footer / Pagination */}
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            padding: '12px 16px',
            borderTop: '1px solid #e5e7eb',
            background: '#ffffff',
          }}
        >
          <div style={{ fontSize: 12, color: '#6b7280' }}>
            Showing{' '}
            <strong>
              {total > 0 ? (page - 1) * limit + 1 : 0}–{Math.min(page * limit, total)}
            </strong>{' '}
            of <strong>{total}</strong> campaigns
          </div>

          <div style={{ display: 'flex', gap: 6, alignItems: 'center' }}>
            <button
              onClick={() => setPage((p) => Math.max(1, p - 1))}
              disabled={page === 1}
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: 4,
                padding: '4px 10px',
                fontSize: 12,
                border: '1px solid #e5e7eb',
                borderRadius: 5,
                background: '#ffffff',
                cursor: page === 1 ? 'not-allowed' : 'pointer',
                opacity: page === 1 ? 0.5 : 1,
              }}
            >
              <ChevronLeft size={13} /> Prev
            </button>

            <span style={{ fontSize: 12, color: '#4b5563', padding: '0 6px' }}>
              Page {page} of {totalPages}
            </span>

            <button
              onClick={() => setPage((p) => Math.min(totalPages, p + 1))}
              disabled={page === totalPages}
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: 4,
                padding: '4px 10px',
                fontSize: 12,
                border: '1px solid #e5e7eb',
                borderRadius: 5,
                background: '#ffffff',
                cursor: page === totalPages ? 'not-allowed' : 'pointer',
                opacity: page === totalPages ? 0.5 : 1,
              }}
            >
              Next <ChevronRight size={13} />
            </button>
          </div>
        </div>
      </div>

      {/* Campaign Details Modal */}
      {selectedCampaign && (
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
          onClick={() => setSelectedCampaign(null)}
        >
          <div
            className="card"
            style={{
              width: '100%',
              maxWidth: 560,
              padding: 22,
              background: '#ffffff',
              borderRadius: 12,
              boxShadow: '0 20px 40px -10px rgba(0, 0, 0, 0.25)',
              maxHeight: '90vh',
              overflowY: 'auto',
            }}
            onClick={(e) => e.stopPropagation()}
          >
            {/* Modal Header */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 16 }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                {selectedCampaign.image ? (
                  <img
                    src={selectedCampaign.image}
                    alt={selectedCampaign.name}
                    style={{
                      width: 42,
                      height: 42,
                      borderRadius: 8,
                      objectFit: 'contain',
                      background: '#ffffff',
                      border: '1px solid #e5e7eb',
                      padding: 3,
                    }}
                  />
                ) : (
                  <div
                    style={{
                      width: 42,
                      height: 42,
                      borderRadius: 8,
                      background: '#eff6ff',
                      color: '#2563eb',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      fontWeight: 700,
                      fontSize: 16,
                    }}
                  >
                    {selectedCampaign.name[0]}
                  </div>
                )}
                <div>
                  <div style={{ fontSize: 16, fontWeight: 700, color: '#111827' }}>
                    {selectedCampaign.name}
                  </div>
                  <div style={{ fontSize: 12, color: '#6b7280', fontFamily: 'monospace' }}>
                    {selectedCampaign.domain}
                  </div>
                </div>
              </div>

              <button
                onClick={() => setSelectedCampaign(null)}
                style={{
                  background: '#f3f4f6',
                  border: 'none',
                  borderRadius: '50%',
                  width: 28,
                  height: 28,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  cursor: 'pointer',
                  color: '#6b7280',
                }}
              >
                <X size={15} />
              </button>
            </div>

            {/* Payout & Validity Cards */}
            <div
              style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(2, 1fr)',
                gap: 10,
                marginBottom: 14,
              }}
            >
              <div style={{ background: '#f0fdf4', border: '1px solid #bbf7d0', padding: 12, borderRadius: 8 }}>
                <div style={{ fontSize: 11, fontWeight: 600, color: '#166534', textTransform: 'uppercase' }}>
                  Commission Rate
                </div>
                <div style={{ fontSize: 18, fontWeight: 800, color: '#15803d', marginTop: 2 }}>
                  {formatPayout(selectedCampaign.payout, selectedCampaign.payout_type)}
                </div>
                <div style={{ fontSize: 11, color: '#166534', marginTop: 2 }}>
                  {selectedCampaign.payout_type}
                </div>
              </div>

              <div style={{ background: '#f9fafb', border: '1px solid #e5e7eb', padding: 12, borderRadius: 8 }}>
                <div style={{ fontSize: 11, fontWeight: 600, color: '#6b7280', textTransform: 'uppercase' }}>
                  Cookie Duration
                </div>
                <div style={{ fontSize: 18, fontWeight: 800, color: '#111827', marginTop: 2 }}>
                  {selectedCampaign.cookie_duration}
                </div>
                <div style={{ fontSize: 11, color: '#6b7280', marginTop: 2 }}>
                  Status: Pre-Approved
                </div>
              </div>
            </div>

            {/* Conversion Flow */}
            <div style={{ background: '#f9fafb', padding: 12, borderRadius: 8, marginBottom: 14, border: '1px solid #e5e7eb' }}>
              <div style={{ fontSize: 11.5, fontWeight: 700, color: '#374151', textTransform: 'uppercase', marginBottom: 6, display: 'flex', alignItems: 'center', gap: 5 }}>
                <Info size={13} style={{ color: '#2563eb' }} />
                Conversion Flow & Rules
              </div>
              <div style={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
                {Object.entries(selectedCampaign.conversion_flow).map(([step, desc]) => (
                  <div key={step} style={{ display: 'flex', gap: 8, fontSize: 12, color: '#4b5563' }}>
                    <span style={{ fontWeight: 600, color: '#2563eb', minWidth: 46 }}>{step}:</span>
                    <span>{desc}</span>
                  </div>
                ))}
              </div>
            </div>

            {/* Generated Link */}
            <div style={{ background: '#eff6ff', border: '1px solid #bfdbfe', padding: 12, borderRadius: 8, marginBottom: 16 }}>
              <div style={{ fontSize: 11, fontWeight: 700, color: '#1e40af', textTransform: 'uppercase', marginBottom: 4 }}>
                Test Affiliate URL (Channel ID: 301603)
              </div>
              <div
                style={{
                  fontSize: 11.5,
                  fontFamily: 'monospace',
                  color: '#1e3a8a',
                  wordBreak: 'break-all',
                  background: '#ffffff',
                  padding: '6px 8px',
                  borderRadius: 5,
                  border: '1px solid #dbeafe',
                }}
              >
                {getAffiliateTestUrl(selectedCampaign.affiliate_url)}
              </div>
            </div>

            {/* Footer */}
            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 8 }}>
              <button
                onClick={() => setSelectedCampaign(null)}
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
                Close
              </button>
              <a
                href={getAffiliateTestUrl(selectedCampaign.affiliate_url)}
                target="_blank"
                rel="noopener noreferrer"
                onClick={(e) => {
                  handleTestLinkClick(selectedCampaign, e)
                }}
                style={{
                  padding: '7px 16px',
                  fontSize: 12.5,
                  fontWeight: 600,
                  color: '#ffffff',
                  background: '#2563eb',
                  borderRadius: 6,
                  textDecoration: 'none',
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: 5,
                }}
              >
                Open Test Link
                <ExternalLink size={12} />
              </a>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
