'use client'

import { useEffect, useState } from 'react'
import {
  Tag,
  Search,
  Filter,
  ExternalLink,
  Copy,
  Check,
  RefreshCw,
  Gift,
  Calendar,
  Layers,
  Sparkles,
  X,
  Info,
} from 'lucide-react'

type Offer = {
  id: string | number
  cuelinks_offer_id: number
  campaign_name: string
  merchant_name: string
  title: string
  description: string
  coupon_code: string
  category: string
  image_url: string | null
  target_url: string
  affiliate_url: string
  valid_till: string
  type: string
  status: string
}

export default function OffersPage() {
  const [offers, setOffers] = useState<Offer[]>([])
  const [loading, setLoading] = useState(true)
  const [search, setSearch] = useState('')
  const [selectedCategory, setSelectedCategory] = useState('all')
  const [copiedId, setCopiedId] = useState<string | number | null>(null)
  const [isSyncing, setIsSyncing] = useState(false)
  const [selectedOffer, setSelectedOffer] = useState<Offer | null>(null)

  const fetchOffers = async () => {
    setLoading(true)
    try {
      const res = await fetch(`/api/offers?search=${encodeURIComponent(search)}&category=${selectedCategory}`)
      const data = await res.json()
      if (data.offers) {
        setOffers(data.offers)
      }
    } catch (err) {
      console.error('Error fetching offers:', err)
    } finally {
      setLoading(false)
      setIsSyncing(false)
    }
  }

  useEffect(() => {
    fetchOffers()
  }, [search, selectedCategory])

  const copyToClipboard = (text: string, id: string | number, e?: React.MouseEvent) => {
    if (e) e.stopPropagation()
    navigator.clipboard.writeText(text)
    setCopiedId(id)
    setTimeout(() => setCopiedId(null), 2000)
  }

  const categoryOptions = [
    { label: 'All Categories', value: 'all' },
    { label: 'Travel', value: 'travel' },
    { label: 'Electronics', value: 'electronics' },
    { label: 'Fashion', value: 'fashion' },
    { label: 'Health & Beauty', value: 'health' },
    { label: 'Mobiles', value: 'mobiles' },
    { label: 'Food & Grocery', value: 'food' },
    { label: 'Others', value: 'others' },
  ]

  return (
    <div style={{ maxWidth: 1150 }}>
      {/* Page Heading & Header Actions */}
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginBottom: 24,
        }}
      >
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
            <Tag size={22} style={{ color: '#2563eb' }} />
            Offers & Coupon Deals
          </h1>
          <p style={{ fontSize: 13, color: '#6b7280', marginTop: 4 }}>
            Live promotional deals, discount coupons & offer deep links directly synced from CueLinks. Click any offer row for full description.
          </p>
        </div>

        <button
          onClick={() => {
            setIsSyncing(true)
            fetchOffers()
          }}
          className="card"
          style={{
            padding: '8px 16px',
            fontSize: 13,
            fontWeight: 600,
            color: '#1f2937',
            display: 'flex',
            alignItems: 'center',
            gap: 6,
            cursor: 'pointer',
            border: '1px solid #e5e7eb',
            background: '#ffffff',
          }}
        >
          <RefreshCw size={14} className={isSyncing ? 'animate-spin' : ''} />
          Sync CueLinks Offers
        </button>
      </div>

      {/* KPI Cards Header */}
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(3, 1fr)',
          gap: 16,
          marginBottom: 24,
        }}
      >
        <div className="card" style={{ padding: '18px 20px' }}>
          <div style={{ fontSize: 12, fontWeight: 600, color: '#6b7280', textTransform: 'uppercase' }}>
            Total Live Offers
          </div>
          <div style={{ fontSize: 24, fontWeight: 700, color: '#111827', marginTop: 6 }}>
            {offers.length}
          </div>
          <div style={{ fontSize: 12, color: '#16a34a', marginTop: 4, fontWeight: 500 }}>
            🟢 Synced with CueLinks API v2
          </div>
        </div>

        <div className="card" style={{ padding: '18px 20px' }}>
          <div style={{ fontSize: 12, fontWeight: 600, color: '#6b7280', textTransform: 'uppercase' }}>
            Exclusive Coupons
          </div>
          <div style={{ fontSize: 24, fontWeight: 700, color: '#2563eb', marginTop: 6 }}>
            {offers.filter((o) => o.coupon_code && o.coupon_code !== 'No Coupon Required').length}
          </div>
          <div style={{ fontSize: 12, color: '#6b7280', marginTop: 4 }}>Active Promo Codes</div>
        </div>

        <div className="card" style={{ padding: '18px 20px' }}>
          <div style={{ fontSize: 12, fontWeight: 600, color: '#6b7280', textTransform: 'uppercase' }}>
            Active Categories
          </div>
          <div style={{ fontSize: 24, fontWeight: 700, color: '#111827', marginTop: 6 }}>8+</div>
          <div style={{ fontSize: 12, color: '#6b7280', marginTop: 4 }}>Electronics, Fashion, Travel, Mobiles</div>
        </div>
      </div>

      {/* Filter and Search Bar */}
      <div
        className="card"
        style={{
          padding: '14px 20px',
          marginBottom: 20,
          display: 'flex',
          gap: 16,
          alignItems: 'center',
          justifyContent: 'space-between',
        }}
      >
        <div style={{ display: 'flex', gap: 12, alignItems: 'center', flex: 1 }}>
          <div style={{ position: 'relative', flex: 1, maxWidth: 360 }}>
            <Search
              size={16}
              style={{
                position: 'absolute',
                left: 12,
                top: '50%',
                transform: 'translateY(-50%)',
                color: '#9ca3af',
              }}
            />
            <input
              type="text"
              placeholder="Search Offers, Merchant, or Coupon Code..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              style={{
                width: '100%',
                padding: '8px 12px 8px 36px',
                fontSize: 13,
                border: '1px solid #e5e7eb',
                borderRadius: 8,
                outline: 'none',
                background: '#f9fafb',
              }}
            />
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <Filter size={15} style={{ color: '#6b7280' }} />
            <select
              value={selectedCategory}
              onChange={(e) => setSelectedCategory(e.target.value)}
              style={{
                padding: '8px 12px',
                fontSize: 13,
                border: '1px solid #e5e7eb',
                borderRadius: 8,
                background: '#ffffff',
                color: '#374151',
                fontWeight: 500,
              }}
            >
              {categoryOptions.map((c) => (
                <option key={c.value} value={c.value}>
                  {c.label}
                </option>
              ))}
            </select>
          </div>
        </div>

        <div style={{ fontSize: 13, color: '#6b7280', fontWeight: 500 }}>
          Showing {offers.length} active deals
        </div>
      </div>

      {/* Offers Table */}
      <div className="card" style={{ overflow: 'hidden' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
          <thead>
            <tr
              style={{
                background: '#f9fafb',
                borderBottom: '1px solid #e5e7eb',
                fontSize: 12,
                color: '#4b5563',
                fontWeight: 600,
                textTransform: 'uppercase',
                letterSpacing: '0.04em',
              }}
            >
              <th style={{ padding: '14px 18px', width: '36%' }}>Title & Description</th>
              <th style={{ padding: '14px 18px' }}>Merchant</th>
              <th style={{ padding: '14px 18px' }}>Categories</th>
              <th style={{ padding: '14px 18px' }}>Coupon Code</th>
              <th style={{ padding: '14px 18px' }}>Valid Till</th>
              <th style={{ padding: '14px 18px', textAlign: 'right' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              [1, 2, 3, 4, 5].map((i) => (
                <tr key={i} style={{ borderBottom: '1px solid #f3f4f6' }}>
                  <td colSpan={6} style={{ padding: 14 }}>
                    <div className="skeleton" style={{ height: 32, width: '100%' }} />
                  </td>
                </tr>
              ))
            ) : offers.length === 0 ? (
              <tr>
                <td
                  colSpan={6}
                  style={{
                    padding: 36,
                    textAlign: 'center',
                    color: '#9ca3af',
                    fontSize: 14,
                  }}
                >
                  No active offers found matching your search.
                </td>
              </tr>
            ) : (
              offers.map((offer, i) => {
                const hasCoupon =
                  offer.coupon_code &&
                  offer.coupon_code !== 'No Coupon Required' &&
                  offer.coupon_code !== 'DEAL ACTIVATED'

                return (
                  <tr
                    key={offer.id || i}
                    onClick={() => setSelectedOffer(offer)}
                    style={{
                      borderBottom: i < offers.length - 1 ? '1px solid #f3f4f6' : 'none',
                      fontSize: 13,
                      color: '#111827',
                      cursor: 'pointer',
                    }}
                    className="hover:bg-blue-50/40 transition-colors"
                  >
                    {/* Title & Preview */}
                    <td style={{ padding: '14px 18px' }}>
                      <div style={{ display: 'flex', gap: 12, alignItems: 'flex-start' }}>
                        {offer.image_url ? (
                          <img
                            src={offer.image_url}
                            alt={offer.merchant_name}
                            style={{
                              width: 36,
                              height: 36,
                              borderRadius: 8,
                              objectFit: 'contain',
                              background: '#ffffff',
                              border: '1px solid #e2e8f0',
                              padding: 2,
                              flexShrink: 0,
                            }}
                          />
                        ) : (
                          <div
                            style={{
                              width: 36,
                              height: 36,
                              borderRadius: 8,
                              background: '#eff6ff',
                              color: '#2563eb',
                              display: 'flex',
                              alignItems: 'center',
                              justifyContent: 'center',
                              fontWeight: 700,
                              fontSize: 14,
                              flexShrink: 0,
                            }}
                          >
                            {offer.merchant_name[0]}
                          </div>
                        )}
                        <div>
                          <div style={{ fontWeight: 600, color: '#1d4ed8', lineHeight: 1.35 }}>
                            {offer.title}
                          </div>
                          {offer.description ? (
                            <div
                              style={{
                                fontSize: 11.5,
                                color: '#475569',
                                marginTop: 4,
                                display: '-webkit-box',
                                WebkitLineClamp: 2,
                                WebkitBoxOrient: 'vertical',
                                overflow: 'hidden',
                              }}
                            >
                              {offer.description}
                            </div>
                          ) : (
                            <div style={{ fontSize: 11, color: '#94a3b8', marginTop: 2, fontStyle: 'italic' }}>
                              Click row to view full details
                            </div>
                          )}
                        </div>
                      </div>
                    </td>

                    {/* Merchant */}
                    <td style={{ padding: '14px 18px', fontWeight: 600, color: '#334155' }}>
                      {offer.merchant_name}
                    </td>

                    {/* Categories */}
                    <td style={{ padding: '14px 18px' }}>
                      <span
                        style={{
                          fontSize: 11.5,
                          fontWeight: 600,
                          padding: '3px 8px',
                          borderRadius: 6,
                          background: '#f1f5f9',
                          color: '#475569',
                          display: 'inline-block',
                        }}
                      >
                        {offer.category}
                      </span>
                    </td>

                    {/* Coupon Code */}
                    <td style={{ padding: '14px 18px' }}>
                      {hasCoupon ? (
                        <div
                          onClick={(e) => copyToClipboard(offer.coupon_code, offer.id, e)}
                          style={{
                            display: 'inline-flex',
                            alignItems: 'center',
                            gap: 6,
                            padding: '4px 10px',
                            background: '#fef3c7',
                            border: '1px dashed #d97706',
                            borderRadius: 6,
                            fontSize: 12,
                            fontWeight: 700,
                            color: '#92400e',
                            fontFamily: 'monospace',
                            cursor: 'pointer',
                          }}
                          title="Click to copy coupon code"
                        >
                          {offer.coupon_code}
                          {copiedId === offer.id ? (
                            <Check size={12} style={{ color: '#16a34a' }} />
                          ) : (
                            <Copy size={12} style={{ opacity: 0.6 }} />
                          )}
                        </div>
                      ) : (
                        <span style={{ fontSize: 12, color: '#9ca3af', fontStyle: 'italic' }}>
                          {offer.coupon_code || 'No Coupon Required'}
                        </span>
                      )}
                    </td>

                    {/* Valid Till */}
                    <td style={{ padding: '14px 18px', color: '#475569', fontSize: 12, fontFamily: 'monospace' }}>
                      {offer.valid_till}
                    </td>

                    {/* Actions */}
                    <td style={{ padding: '14px 18px', textAlign: 'right' }}>
                      <div style={{ display: 'flex', gap: 6, justifyContent: 'flex-end' }}>
                        <button
                          onClick={(e) => {
                            e.stopPropagation()
                            setSelectedOffer(offer)
                          }}
                          style={{
                            padding: '5px 10px',
                            fontSize: 11.5,
                            fontWeight: 600,
                            color: '#334155',
                            background: '#f1f5f9',
                            border: 'none',
                            borderRadius: 6,
                            cursor: 'pointer',
                          }}
                        >
                          Details
                        </button>
                        <a
                          href={offer.affiliate_url}
                          target="_blank"
                          rel="noopener noreferrer"
                          onClick={(e) => e.stopPropagation()}
                          style={{
                            display: 'inline-flex',
                            alignItems: 'center',
                            gap: 4,
                            padding: '5px 10px',
                            fontSize: 11.5,
                            fontWeight: 600,
                            color: '#2563eb',
                            background: '#eff6ff',
                            borderRadius: 6,
                            textDecoration: 'none',
                          }}
                        >
                          Test Deal
                          <ExternalLink size={11} />
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

      {/* Offer Details Modal */}
      {selectedOffer && (
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
          onClick={() => setSelectedOffer(null)}
        >
          <div
            className="card"
            style={{
              width: '100%',
              maxWidth: 580,
              padding: 24,
              background: '#ffffff',
              borderRadius: 14,
              boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
              maxHeight: '90vh',
              overflowY: 'auto',
            }}
            onClick={(e) => e.stopPropagation()}
          >
            {/* Modal Header */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 16 }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                {selectedOffer.image_url ? (
                  <img
                    src={selectedOffer.image_url}
                    alt={selectedOffer.merchant_name}
                    style={{
                      width: 44,
                      height: 44,
                      borderRadius: 10,
                      objectFit: 'contain',
                      background: '#ffffff',
                      border: '1px solid #e2e8f0',
                      padding: 4,
                    }}
                  />
                ) : (
                  <div
                    style={{
                      width: 44,
                      height: 44,
                      borderRadius: 10,
                      background: '#eff6ff',
                      color: '#2563eb',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      fontWeight: 800,
                      fontSize: 18,
                    }}
                  >
                    {selectedOffer.merchant_name[0]}
                  </div>
                )}
                <div>
                  <div style={{ fontSize: 13, fontWeight: 700, color: '#1e293b' }}>
                    {selectedOffer.merchant_name}
                  </div>
                  <span
                    style={{
                      fontSize: 11,
                      fontWeight: 600,
                      padding: '2px 8px',
                      borderRadius: 4,
                      background: '#f1f5f9',
                      color: '#475569',
                      display: 'inline-block',
                      marginTop: 2,
                    }}
                  >
                    {selectedOffer.category}
                  </span>
                </div>
              </div>

              <button
                onClick={() => setSelectedOffer(null)}
                style={{
                  background: '#f1f5f9',
                  border: 'none',
                  borderRadius: '50%',
                  width: 32,
                  height: 32,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  cursor: 'pointer',
                  color: '#64748b',
                }}
              >
                <X size={16} />
              </button>
            </div>

            {/* Offer Title */}
            <h2 style={{ fontSize: 16, fontWeight: 700, color: '#0f172a', margin: 0, marginBottom: 16, lineHeight: 1.4 }}>
              {selectedOffer.title}
            </h2>

            {/* Description Section */}
            <div style={{ background: '#f8fafc', padding: 16, borderRadius: 10, marginBottom: 18, border: '1px solid #e2e8f0' }}>
              <div style={{ fontSize: 12, fontWeight: 700, color: '#334155', textTransform: 'uppercase', letterSpacing: '0.04em', marginBottom: 8, display: 'flex', alignItems: 'center', gap: 6 }}>
                <Info size={14} style={{ color: '#2563eb' }} />
                Offer Description & Terms
              </div>
              <div style={{ fontSize: 13, color: '#334155', lineHeight: 1.6, whiteSpace: 'pre-wrap' }}>
                {selectedOffer.description || 'No additional description terms provided for this offer.'}
              </div>
            </div>

            {/* Coupon Code Section */}
            {selectedOffer.coupon_code && selectedOffer.coupon_code !== 'No Coupon Required' && (
              <div style={{ background: '#fffbeb', border: '1px dashed #f59e0b', padding: 14, borderRadius: 10, marginBottom: 18, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <div style={{ fontSize: 11, fontWeight: 700, color: '#b45309', textTransform: 'uppercase' }}>
                    Coupon / Promo Code
                  </div>
                  <div style={{ fontSize: 18, fontWeight: 800, color: '#78350f', fontFamily: 'monospace', marginTop: 2 }}>
                    {selectedOffer.coupon_code}
                  </div>
                </div>

                <button
                  onClick={() => copyToClipboard(selectedOffer.coupon_code, 'modal')}
                  style={{
                    padding: '8px 14px',
                    fontSize: 12,
                    fontWeight: 700,
                    color: '#ffffff',
                    background: '#d97706',
                    border: 'none',
                    borderRadius: 6,
                    cursor: 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    gap: 6,
                  }}
                >
                  {copiedId === 'modal' ? <Check size={14} /> : <Copy size={14} />}
                  {copiedId === 'modal' ? 'Copied!' : 'Copy Code'}
                </button>
              </div>
            )}

            {/* Validity Meta */}
            <div style={{ display: 'flex', gap: 20, marginBottom: 20, fontSize: 12.5, color: '#64748b' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                <Calendar size={14} style={{ color: '#64748b' }} />
                Valid Till: <strong style={{ color: '#1e293b' }}>{selectedOffer.valid_till}</strong>
              </div>
            </div>

            {/* Actions Footer */}
            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 10, paddingTop: 16, borderTop: '1px solid #e2e8f0' }}>
              <button
                onClick={() => setSelectedOffer(null)}
                style={{
                  padding: '9px 16px',
                  fontSize: 13,
                  fontWeight: 600,
                  color: '#475569',
                  background: '#f1f5f9',
                  border: 'none',
                  borderRadius: 8,
                  cursor: 'pointer',
                }}
              >
                Close
              </button>
              <a
                href={selectedOffer.affiliate_url}
                target="_blank"
                rel="noopener noreferrer"
                style={{
                  padding: '9px 18px',
                  fontSize: 13,
                  fontWeight: 600,
                  color: '#ffffff',
                  background: '#2563eb',
                  borderRadius: 8,
                  textDecoration: 'none',
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: 6,
                }}
              >
                Test Affiliate Deal Link
                <ExternalLink size={14} />
              </a>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
