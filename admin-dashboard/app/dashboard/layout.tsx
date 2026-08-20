import { Sidebar } from '@/components/Sidebar'
import { Header } from '@/components/Header'
import type { ReactNode } from 'react'

export default function Layout({ children }: { children: ReactNode }) {
  return (
    <div style={{ display: 'flex', height: '100vh', overflow: 'hidden', background: '#ffffff' }}>
      <Sidebar />
      <main style={{ flex: 1, overflowY: 'auto', background: '#f9fafb' }}>
        <Header />
        <div style={{ padding: '24px' }}>
          {children}
        </div>
      </main>
    </div>
  )
}
