import type { Metadata } from 'next'
import { Inter } from 'next/font/google'
import './globals.css'

const inter = Inter({
  subsets: ['latin'],
  variable: '--font-inter',
})

export const metadata: Metadata = {
  title: 'Reward Club — Admin',
  description: 'Admin Dashboard',
}

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en" className={inter.variable} style={{ colorScheme: 'light', background: '#f9fafb' }}>
      <body className={inter.className} style={{ margin: 0, background: '#f9fafb', color: '#111827' }}>
        {children}
      </body>
    </html>
  )
}
