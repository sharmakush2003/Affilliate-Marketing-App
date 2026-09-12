'use client'

import { useState, useEffect } from 'react'
import Link from 'next/link'
import { usePathname } from 'next/navigation'
import { 
  LayoutDashboard, 
  Users, 
  ArrowLeftRight, 
  LogOut, 
  Globe, 
  History, 
  MousePointerClick,
  ChevronLeft,
  ChevronRight,
  Megaphone,
  Wallet
} from 'lucide-react'
import { supabase } from '@/lib/supabase'

const nav = [
  { href: '/dashboard',              label: 'Overview',          icon: LayoutDashboard },
  { href: '/dashboard/campaigns',    label: 'Campaigns',         icon: Megaphone },
  { href: '/dashboard/users',        label: 'Users',             icon: Users },
  { href: '/dashboard/withdrawals',  label: 'User Payouts',      icon: Wallet },
  { href: '/dashboard/clicks',       label: 'Click Logs',        icon: MousePointerClick },
  { href: '/dashboard/transactions', label: 'Transactions',      icon: ArrowLeftRight },
  { href: '/dashboard/sessions',     label: 'Admin Sessions',    icon: History },
]

export function Sidebar() {
  const path = usePathname()
  const [collapsed, setCollapsed] = useState(false)
  const [mounted, setMounted] = useState(false)

  useEffect(() => {
    setMounted(true)
    const saved = localStorage.getItem('admin_sidebar_collapsed')
    if (saved !== null) {
      setCollapsed(saved === 'true')
    } else if (window.innerWidth < 1200) {
      setCollapsed(true)
    }

    const handleResize = () => {
      if (window.innerWidth < 1024 && !collapsed) {
        setCollapsed(true)
      }
    }

    window.addEventListener('resize', handleResize)
    return () => window.removeEventListener('resize', handleResize)
  }, [])

  const toggleCollapse = () => {
    const next = !collapsed
    setCollapsed(next)
    localStorage.setItem('admin_sidebar_collapsed', String(next))
  }

  return (
    <aside 
      className={`sidebar ${collapsed ? 'sidebar-collapsed' : ''}`}
      style={{
        width: collapsed ? 72 : 240,
        transition: 'width 0.25s cubic-bezier(0.4, 0, 0.2, 1), padding 0.25s ease',
      }}
    >
      {/* Brand Header */}
      <div 
        className="sidebar-brand" 
        style={{ 
          justifyContent: collapsed ? 'center' : 'space-between',
          padding: collapsed ? '0' : '0 2px',
          marginBottom: 14,
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: 9, minWidth: 0 }}>
          <img
            src="/logo.webp"
            alt="Reward Club Logo"
            style={{
              width: 30,
              height: 30,
              borderRadius: '7px',
              objectFit: 'contain',
              flexShrink: 0,
            }}
          />
          {!collapsed && (
            <div style={{ display: 'flex', flexDirection: 'column', minWidth: 0 }}>
              <span className="sidebar-brand-title" style={{ whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis', fontSize: '1.05rem' }}>
                Reward Club
              </span>
              <span
                style={{
                  fontSize: '0.55rem',
                  color: '#94a3b8',
                  fontWeight: 700,
                  textTransform: 'uppercase',
                  letterSpacing: '0.8px',
                  marginTop: '1px',
                  whiteSpace: 'nowrap',
                }}
              >
                ENTERPRISE EDITION
              </span>
            </div>
          )}
        </div>

        {/* Toggle Collapse Button */}
        <button
          onClick={toggleCollapse}
          title={collapsed ? "Expand Sidebar" : "Collapse Sidebar"}
          aria-label={collapsed ? "Expand Sidebar" : "Collapse Sidebar"}
          style={{
            background: 'rgba(255, 255, 255, 0.06)',
            border: '1px solid rgba(255, 255, 255, 0.1)',
            borderRadius: '6px',
            color: '#94a3b8',
            cursor: 'pointer',
            padding: '4px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            transition: 'all 0.2s',
            flexShrink: 0,
            marginLeft: collapsed ? 0 : 2,
          }}
          className="hover:text-white hover:bg-white/10"
        >
          {collapsed ? <ChevronRight size={13} /> : <ChevronLeft size={13} />}
        </button>
      </div>

      {/* Navigation Links */}
      <nav style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 2 }}>
        {nav.map(({ href, label, icon: Icon }) => {
          const active = href === '/dashboard' ? path === href : path.startsWith(href)
          return (
            <Link
              key={href}
              href={href}
              title={collapsed ? label : undefined}
              className={`nav-link ${active ? 'active' : ''}`}
              style={{
                justifyContent: collapsed ? 'center' : 'flex-start',
                padding: collapsed ? '9px' : '8px 11px',
                borderRadius: '8px',
                fontSize: '0.84rem',
              }}
            >
              <Icon
                size={16}
                strokeWidth={active ? 2.5 : 2}
                style={{
                  flexShrink: 0,
                }}
              />
              {!collapsed && (
                <span style={{ whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                  {label}
                </span>
              )}
            </Link>
          )
        })}

        {/* Separator before Logout */}
        <div style={{ height: '1px', background: 'rgba(255, 255, 255, 0.06)', margin: '4px 0' }} />

        {/* Logout Button */}
        <button
          onClick={async () => {
            try {
              await supabase.auth.signOut()
            } catch (err) {
              console.error('Error signing out:', err)
            }
          }}
          title={collapsed ? "Logout Admin" : undefined}
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: collapsed ? 'center' : 'flex-start',
            gap: 9,
            padding: collapsed ? '9px' : '8px 11px',
            borderRadius: '8px',
            fontSize: '0.82rem',
            fontWeight: '700',
            background: 'transparent',
            border: 'none',
            color: '#fca5a5',
            cursor: 'pointer',
            width: '100%',
            transition: 'all 0.2s ease',
            marginTop: 'auto',
          }}
          className="hover:bg-red-500/10"
        >
          <LogOut size={15} style={{ flexShrink: 0, color: '#fca5a5' }} />
          {!collapsed && <span>Logout Admin</span>}
        </button>
      </nav>

      {/* Powered By ChittorTech Footer */}
      {!collapsed ? (
        <div className="developer-credits">
          <div className="dev-pill">
            <img src="/ct-logo.png" alt="ChittorTech Logo" className="dev-logo" />
            <div className="dev-info">
              <p className="dev-label">A Product Of</p>
              <p className="dev-brand">ChittorTech</p>
            </div>
          </div>
          <div className="dev-badges">
            <p className="dev-badge-item">iStart Rajasthan • Startup India</p>
          </div>
          <div className="dev-contact">
            <a href="https://chittortech.in" target="_blank" rel="dofollow">
              <Globe size={11} style={{ opacity: 0.7 }} /> chittortech.in
            </a>
          </div>
          <p className="dev-rights">
            © 2026 ChittorTech
          </p>
        </div>
      ) : (
        <div 
          style={{ 
            marginTop: 'auto', 
            paddingTop: 8, 
            display: 'flex', 
            justifyContent: 'center' 
          }}
          title="A Product of ChittorTech — chittortech.in"
        >
          <a href="https://chittortech.in" target="_blank" rel="dofollow">
            <img 
              src="/ct-logo.png" 
              alt="ChittorTech" 
              style={{ width: 26, height: 26, borderRadius: 6, opacity: 0.8 }} 
            />
          </a>
        </div>
      )}
    </aside>
  )
}
export default Sidebar
