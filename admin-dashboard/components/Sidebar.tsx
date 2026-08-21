'use client'

import Link from 'next/link'
import { usePathname } from 'next/navigation'
import { LayoutDashboard, Users, ArrowLeftRight, Settings2, LogOut, Globe, HelpCircle } from 'lucide-react'
import { supabase } from '@/lib/supabase'

const nav = [
  { href: '/dashboard',              label: 'Overview',       icon: LayoutDashboard },
  { href: '/dashboard/users',        label: 'Users',          icon: Users },
  { href: '/dashboard/transactions', label: 'Transactions',   icon: ArrowLeftRight },
  { href: '/dashboard/how-to-use',   label: 'How To Use',     icon: HelpCircle },
]

export function Sidebar() {
  const path = usePathname()

  return (
    <aside className="sidebar">
      {/* Brand Header */}
      <div className="sidebar-brand">
        <img
          src="/logo.webp"
          alt="Reward Club Logo"
          style={{
            width: 32,
            height: 32,
            borderRadius: '6px',
            objectFit: 'contain',
            flexShrink: 0,
          }}
        />
        <div style={{ display: 'flex', flexDirection: 'column' }}>
          <span className="sidebar-brand-title">Reward Club</span>
          <span
            style={{
              fontSize: '0.6rem',
              color: '#94a3af',
              fontWeight: 700,
              textTransform: 'uppercase',
              letterSpacing: '1px',
              marginTop: '4px',
            }}
          >
            ENTERPRISE EDITION
          </span>
        </div>
      </div>

      {/* Navigation (Tabs are wider and separated from edges) */}
      <nav style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 6 }}>
        {nav.map(({ href, label, icon: Icon }) => {
          const active = href === '/dashboard' ? path === href : path.startsWith(href)
          return (
            <Link
              key={href}
              href={href}
              className={`nav-link ${active ? 'active' : ''}`}
            >
              <Icon
                size={16}
                strokeWidth={active ? 2.5 : 2}
                style={{
                  flexShrink: 0,
                }}
              />
              <span>{label}</span>
            </Link>
          )
        })}

        {/* Separator before Logout */}
        <div style={{ height: '1px', background: 'rgba(255, 255, 255, 0.05)', margin: '14px 0' }} />

        {/* Logout Button */}
        <button
          onClick={async () => {
            try {
              await supabase.auth.signOut()
            } catch (err) {
              console.error('Error signing out:', err)
            }
          }}
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: 12,
            padding: '14px 16px',
            borderRadius: '14px',
            fontSize: '0.92rem',
            fontWeight: '700',
            background: 'transparent',
            border: 'none',
            color: '#fca5a5',
            cursor: 'pointer',
            textAlign: 'left',
            width: '100%',
            transition: 'all 0.2s ease',
            marginTop: 'auto', // Push it down like Dharamsala
            marginBottom: '8px',
          }}
          className="hover:bg-red-500/10"
        >
          <LogOut size={16} style={{ flexShrink: 0, color: '#fca5a5' }} />
          <span>Logout Admin</span>
        </button>
      </nav>

      {/* Powered By ChittorTech Footer */}
      <div className="developer-credits">
        <div className="dev-pill">
          <img src="/ct-logo.png" alt="ChittorTech Logo" className="dev-logo" />
          <div className="dev-info">
            <p className="dev-label">A Product Of</p>
            <p className="dev-brand">ChittorTech</p>
          </div>
        </div>
        <div className="dev-badges">
          <p className="dev-badge-item">Recognized by iStart Rajasthan</p>
          <p className="dev-badge-item">Registered MSME | Startup India</p>
        </div>
        <div className="dev-contact">
          <a href="https://chittortech.online" target="_blank" rel="dofollow">
            <Globe size={14} style={{ opacity: 0.7 }} /> chittortech.online
          </a>
        </div>
        <p className="dev-rights">
          © 2026 ChittorTech <br /> All Rights Reserved
        </p>
      </div>
    </aside>
  )
}
export default Sidebar
