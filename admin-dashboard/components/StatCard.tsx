import type { LucideIcon } from 'lucide-react'

type StatCardProps = {
  title: string
  value: string | number
  subtitle?: string
  icon: LucideIcon
  trend?: number
  glowColor?: 'emerald' | 'violet' | 'blue' | 'amber'
  iconColor?: string
  iconBg?: string
}

export function StatCard({
  title,
  value,
  subtitle,
  icon: Icon,
  trend,
  glowColor = 'violet',
  iconColor = 'text-violet-400',
  iconBg = 'bg-violet-500/10',
}: StatCardProps) {
  const glowClass = {
    emerald: 'card-glow-emerald',
    violet: 'card-glow-violet',
    blue: 'card-glow-blue',
    amber: 'card-glow-amber',
  }[glowColor]

  return (
    <div className={`glass rounded-xl p-5 ${glowClass} hover:scale-[1.02] transition-transform duration-200`}>
      <div className="flex items-start justify-between mb-4">
        <div className={`p-2.5 rounded-lg ${iconBg}`}>
          <Icon size={20} className={iconColor} />
        </div>
        {trend !== undefined && (
          <span
            className={`text-xs font-medium px-2 py-0.5 rounded-full ${
              trend >= 0 ? 'bg-emerald-500/10 text-emerald-400' : 'bg-red-500/10 text-red-400'
            }`}
          >
            {trend >= 0 ? '+' : ''}{trend}%
          </span>
        )}
      </div>
      <p className="text-2xl font-bold text-white tracking-tight">{value}</p>
      <p className="text-sm text-slate-400 mt-1">{title}</p>
      {subtitle && <p className="text-xs text-slate-500 mt-0.5">{subtitle}</p>}
    </div>
  )
}
