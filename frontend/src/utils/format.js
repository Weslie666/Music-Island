export function formatTime(seconds) {
  if (!seconds || isNaN(seconds)) return '0:00'
  const m = Math.floor(seconds / 60)
  const s = Math.floor(seconds % 60)
  return m + ':' + (s < 10 ? '0' : '') + s
}

export function formatCount(num) {
  if (!num) return '0'
  if (num >= 10000) return (num / 10000).toFixed(1) + '万'
  return String(num)
}
