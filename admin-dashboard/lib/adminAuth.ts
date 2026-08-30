export const DEFAULT_ADMIN_EMAILS = [
  'chittortech@gmail.com',
  'rewardclub.team@gmail.com'
]

export function isAllowedAdminEmail(email: string | null | undefined): boolean {
  if (!email) return false
  const cleanEmail = email.trim().toLowerCase()

  // Get allowed emails from environment variable (client and server side compat)
  const envAdmins = process.env.ALLOWED_ADMIN_EMAILS || process.env.NEXT_PUBLIC_ALLOWED_ADMIN_EMAILS
  const dynamicAdmins = envAdmins
    ? envAdmins.split(',').map(e => e.trim().toLowerCase())
    : []

  const allowedList = new Set([
    ...DEFAULT_ADMIN_EMAILS.map(e => e.toLowerCase()),
    ...dynamicAdmins
  ])

  return allowedList.has(cleanEmail)
}
