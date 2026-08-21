'use client'

import { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import { supabase } from '@/lib/supabase'
import { ShieldCheck, History, Monitor, Smartphone, Globe, AlertTriangle, LogOut, Search, Clock } from 'lucide-react'

interface AdminSession {
  id: string
  email: string
  ip_address: string
  location: string
  user_agent: string
  status: 'active' | 'logged_out'
  last_active: string
  created_at: string
}

export default function AdminSessionsPage() {
  const [sessions, setSessions] = useState<AdminSession[]>([])
  const [searchQuery, setSearchQuery] = useState('')
  const [loading, setLoading] = useState(true)
  const [terminatingId, setTerminatingId] = useState<string | null>(null)
  const [error, setError] = useState('')
  const router = useRouter()

  const fetchSessions = async () => {
    try {
      setError('')
      const res = await fetch('/api/auth/sessions')
      if (!res.ok) throw new Error('Failed to retrieve log details')
      const data = await res.json()
      setSessions(data.sessions || [])
    } catch (err: any) {
      setError(err.message || 'Unable to connect to database.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchSessions()
  }, [])

  const handleTerminateSession = async (sessionId: string) => {
    setTerminatingId(sessionId)
    try {
      const res = await fetch('/api/auth/sessions/terminate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ sessionId }),
      })

      if (!res.ok) throw new Error('Termination request failed')

      // Check if the terminated session was the current browser session
      const currentSessionId = localStorage.getItem('current_admin_session_id')
      if (currentSessionId === sessionId) {
        localStorage.removeItem('current_admin_session_id')
        await supabase.auth.signOut()
        router.push('/login')
        return
      }

      // Reload sessions list
      await fetchSessions()
    } catch (err: any) {
      alert(err.message || 'Failed to terminate session.')
    } finally {
      setTerminatingId(null)
    }
  }

  // Helper to parse simple OS/Browser name from user agent
  const parseUserAgent = (ua: string) => {
    const lower = ua.toLowerCase()
    let os = 'Unknown OS'
    let browser = 'Unknown Browser'

    if (lower.includes('win')) os = 'Windows'
    else if (lower.includes('mac')) os = 'macOS'
    else if (lower.includes('linux')) os = 'Linux'
    else if (lower.includes('android')) os = 'Android'
    else if (lower.includes('iphone') || lower.includes('ipad')) os = 'iOS'

    if (lower.includes('firefox')) browser = 'Firefox'
    else if (lower.includes('chrome')) browser = 'Chrome'
    else if (lower.includes('safari') && !lower.includes('chrome')) browser = 'Safari'
    else if (lower.includes('edge')) browser = 'Edge'
    else if (lower.includes('opera')) browser = 'Opera'

    return { os, browser, isMobile: lower.includes('mobile') || lower.includes('android') }
  }

  const filteredSessions = sessions.filter(session => 
    session.email.toLowerCase().includes(searchQuery.toLowerCase()) ||
    session.ip_address.includes(searchQuery) ||
    session.location.toLowerCase().includes(searchQuery.toLowerCase())
  )

  const currentSessionId = typeof window !== 'undefined' ? localStorage.getItem('current_admin_session_id') : null

  return (
    <div style={styles.container}>
      {/* Header */}
      <div style={styles.header}>
        <div>
          <h1 style={styles.title}>Admin Login Sessions</h1>
          <p style={styles.subtitle}>Audit logs of active logins, geolocations, and devices accessing this portal.</p>
        </div>
      </div>

      {error && (
        <div style={styles.errorBanner}>
          <AlertTriangle size={18} />
          <span>{error}</span>
        </div>
      )}

      {/* Toolbar / Search */}
      <div style={styles.toolbar}>
        <div style={styles.searchWrapper}>
          <Search size={16} style={styles.searchIcon} />
          <input
            type="text"
            placeholder="Search by email, IP, or location..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            style={styles.searchInput}
          />
        </div>
      </div>

      {/* Main Sessions Table card */}
      <div style={styles.tableCard}>
        {loading ? (
          <div style={styles.loadingWrapper}>
            <div style={styles.spinner} />
            <p style={styles.loadingText}>Fetching login history logs...</p>
          </div>
        ) : filteredSessions.length === 0 ? (
          <div style={styles.emptyState}>
            <History size={48} style={{ color: '#94a3b8', marginBottom: 12 }} />
            <p style={styles.emptyText}>No matching login sessions found.</p>
          </div>
        ) : (
          <div style={styles.tableContainer}>
            <table style={styles.table}>
              <thead>
                <tr style={styles.thRow}>
                  <th style={styles.th}>ADMIN</th>
                  <th style={styles.th}>LOCATION / IP</th>
                  <th style={styles.th}>DEVICE & BROWSER</th>
                  <th style={styles.th}>LOGIN ATTEMPT</th>
                  <th style={styles.th}>STATUS</th>
                  <th style={styles.th} className="text-right">ACTIONS</th>
                </tr>
              </thead>
              <tbody>
                {filteredSessions.map((session) => {
                  const uaInfo = parseUserAgent(session.user_agent)
                  const isCurrent = currentSessionId === session.id

                  return (
                    <tr key={session.id} style={styles.tr}>
                      <td style={styles.td}>
                        <div style={styles.adminInfo}>
                          <span style={styles.adminEmail}>{session.email}</span>
                          {isCurrent && <span style={styles.currentBadge}>Current Session</span>}
                        </div>
                      </td>
                      <td style={styles.td}>
                        <div style={styles.locationInfo}>
                          <div style={styles.locationName}>
                            <Globe size={13} style={{ marginRight: 4, color: '#64748b' }} />
                            <span>{session.location}</span>
                          </div>
                          <span style={styles.ipAddress}>IP: {session.ip_address}</span>
                        </div>
                      </td>
                      <td style={styles.td}>
                        <div style={styles.deviceInfo}>
                          <div style={styles.deviceName}>
                            {uaInfo.isMobile ? <Smartphone size={14} style={styles.deviceIcon} /> : <Monitor size={14} style={styles.deviceIcon} />}
                            <span>{uaInfo.os}</span>
                          </div>
                          <span style={styles.browserName}>{uaInfo.browser} Browser</span>
                        </div>
                      </td>
                      <td style={styles.td}>
                        <div style={styles.timeInfo}>
                          <span style={styles.timeText}>
                            {new Date(session.created_at).toLocaleDateString('en-IN', {
                              day: 'numeric',
                              month: 'short',
                              year: 'numeric'
                            })}
                          </span>
                          <span style={styles.timeSub}>
                            {new Date(session.created_at).toLocaleTimeString('en-IN', {
                              hour: '2-digit',
                              minute: '2-digit'
                            })}
                          </span>
                        </div>
                      </td>
                      <td style={styles.td}>
                        {session.status === 'active' ? (
                          <span style={styles.statusActive}>Active</span>
                        ) : (
                          <span style={styles.statusLoggedOut}>Terminated</span>
                        )}
                      </td>
                      <td style={styles.td} className="text-right">
                        {session.status === 'active' ? (
                          <button
                            disabled={terminatingId !== null}
                            onClick={() => handleTerminateSession(session.id)}
                            style={isCurrent ? styles.terminateSelfButton : styles.terminateButton}
                          >
                            <LogOut size={12} style={{ marginRight: 4 }} />
                            {terminatingId === session.id ? 'Ending...' : isCurrent ? 'Log Out' : 'Force End'}
                          </button>
                        ) : (
                          <span style={styles.terminatedText}>Closed</span>
                        )}
                      </td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}

const styles: Record<string, React.CSSProperties> = {
  container: {
    display: 'flex',
    flexDirection: 'column',
    gap: '24px',
    maxWidth: '1200px',
    margin: '0 auto',
    fontFamily: 'system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif'
  },
  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  title: {
    fontSize: '24px',
    fontWeight: '800',
    color: '#0f172a',
    margin: '0 0 4px 0',
    letterSpacing: '-0.5px',
  },
  subtitle: {
    fontSize: '14px',
    color: '#64748b',
    margin: 0,
  },
  errorBanner: {
    background: '#fef2f2',
    border: '1px solid #fee2e2',
    borderRadius: '12px',
    padding: '12px 16px',
    display: 'flex',
    alignItems: 'center',
    gap: '8px',
    color: '#991b1b',
    fontSize: '13.5px'
  },
  toolbar: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    gap: '16px',
  },
  searchWrapper: {
    position: 'relative',
    flex: 1,
    maxWidth: '400px',
  },
  searchIcon: {
    position: 'absolute',
    left: '12px',
    top: '50%',
    transform: 'translateY(-50%)',
    color: '#94a3b8',
    pointerEvents: 'none',
  },
  searchInput: {
    width: '100%',
    background: '#ffffff',
    border: '1px solid #cbd5e1',
    borderRadius: '10px',
    padding: '8px 12px 8px 36px',
    fontSize: '13.5px',
    color: '#0f172a',
    outline: 'none',
    transition: 'all 0.15s ease',
  },
  tableCard: {
    background: '#ffffff',
    border: '1px solid #cbd5e1',
    borderRadius: '16px',
    overflow: 'hidden',
    boxShadow: '0 4px 6px -1px rgba(0,0,0,0.05)',
  },
  tableContainer: {
    overflowX: 'auto',
  },
  table: {
    width: '100%',
    borderCollapse: 'collapse',
    textAlign: 'left',
  },
  thRow: {
    borderBottom: '1px solid #e2e8f0',
    background: '#f8fafc',
  },
  th: {
    padding: '14px 20px',
    fontSize: '11px',
    fontWeight: '700',
    color: '#475569',
    textTransform: 'uppercase',
    letterSpacing: '0.8px',
  },
  tr: {
    borderBottom: '1px solid #f1f5f9',
    transition: 'background 0.15s ease',
  },
  td: {
    padding: '16px 20px',
    verticalAlign: 'middle',
  },
  adminInfo: {
    display: 'flex',
    flexDirection: 'column',
    gap: '4px',
  },
  adminEmail: {
    fontSize: '13.5px',
    fontWeight: '600',
    color: '#0f172a',
  },
  currentBadge: {
    alignSelf: 'flex-start',
    background: '#ecfdf5',
    border: '1px solid #a7f3d0',
    color: '#047857',
    fontSize: '10px',
    fontWeight: '700',
    padding: '1px 6px',
    borderRadius: '6px',
  },
  locationInfo: {
    display: 'flex',
    flexDirection: 'column',
    gap: '2px',
  },
  locationName: {
    display: 'flex',
    alignItems: 'center',
    fontSize: '13px',
    fontWeight: '500',
    color: '#334155',
  },
  ipAddress: {
    fontSize: '11.5px',
    color: '#94a3b8',
    paddingLeft: '17px',
  },
  deviceInfo: {
    display: 'flex',
    flexDirection: 'column',
    gap: '2px',
  },
  deviceName: {
    display: 'flex',
    alignItems: 'center',
    gap: '4px',
    fontSize: '13px',
    fontWeight: '600',
    color: '#334155',
  },
  deviceIcon: {
    color: '#64748b',
  },
  browserName: {
    fontSize: '11.5px',
    color: '#94a3b8',
    paddingLeft: '18px',
  },
  timeInfo: {
    display: 'flex',
    flexDirection: 'column',
    gap: '2px',
  },
  timeText: {
    fontSize: '13px',
    fontWeight: '600',
    color: '#334155',
  },
  timeSub: {
    fontSize: '11px',
    color: '#94a3b8',
  },
  statusActive: {
    display: 'inline-block',
    background: '#ecfdf5',
    color: '#065f46',
    fontSize: '11.5px',
    fontWeight: '700',
    padding: '2px 8px',
    borderRadius: '8px',
  },
  statusLoggedOut: {
    display: 'inline-block',
    background: '#f1f5f9',
    color: '#64748b',
    fontSize: '11.5px',
    fontWeight: '600',
    padding: '2px 8px',
    borderRadius: '8px',
  },
  terminateButton: {
    background: '#fef2f2',
    border: '1px solid #fee2e2',
    borderRadius: '8px',
    color: '#991b1b',
    fontSize: '12px',
    fontWeight: '700',
    padding: '6px 12px',
    cursor: 'pointer',
    display: 'inline-flex',
    alignItems: 'center',
    transition: 'all 0.15s ease',
  },
  terminateSelfButton: {
    background: '#f8fafc',
    border: '1px solid #cbd5e1',
    borderRadius: '8px',
    color: '#475569',
    fontSize: '12px',
    fontWeight: '700',
    padding: '6px 12px',
    cursor: 'pointer',
    display: 'inline-flex',
    alignItems: 'center',
    transition: 'all 0.15s ease',
  },
  terminatedText: {
    fontSize: '12px',
    color: '#94a3b8',
    fontWeight: '500',
  },
  loadingWrapper: {
    padding: '48px',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
  },
  spinner: {
    width: '32px',
    height: '32px',
    border: '3px solid #f1f5f9',
    borderTopColor: '#0f172a',
    borderRadius: '50%',
    animation: 'spin 0.8s linear infinite',
  },
  loadingText: {
    marginTop: '12px',
    fontSize: '13.5px',
    color: '#64748b',
    fontWeight: '500',
  },
  emptyState: {
    padding: '48px',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
  },
  emptyText: {
    fontSize: '13.5px',
    color: '#64748b',
    fontWeight: '500',
    margin: 0,
  }
}
