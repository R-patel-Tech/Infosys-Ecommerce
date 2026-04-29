function hashString(value) {
  let hash = 0

  for (let index = 0; index < value.length; index += 1) {
    hash = (hash << 5) - hash + value.charCodeAt(index)
    hash |= 0
  }

  return Math.abs(hash)
}

function escapeXml(value) {
  return value
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&apos;')
}

function makeDataUri(svg) {
  return `data:image/svg+xml;charset=UTF-8,${encodeURIComponent(svg)}`
}

function genericFallback(name, category) {
  const seed = hashString(`${name}-${category}`)
  const hueA = seed % 360
  const hueB = (seed + 48) % 360
  const label = escapeXml(name.length > 28 ? `${name.slice(0, 25)}...` : name)
  const categoryLabel = escapeXml(category)

  const svg = `
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 420" role="img" aria-label="${label}">
      <defs>
        <linearGradient id="bg" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="hsl(${hueA} 70% 52%)" />
          <stop offset="100%" stop-color="hsl(${hueB} 70% 38%)" />
        </linearGradient>
        <radialGradient id="glow" cx="30%" cy="28%" r="65%">
          <stop offset="0%" stop-color="#ffffff" stop-opacity="0.45" />
          <stop offset="100%" stop-color="#ffffff" stop-opacity="0" />
        </radialGradient>
      </defs>
      <rect width="640" height="420" rx="34" fill="url(#bg)" />
      <circle cx="175" cy="120" r="160" fill="url(#glow)" />
      <circle cx="520" cy="80" r="100" fill="rgba(255,255,255,0.12)" />
      <rect x="56" y="254" width="528" height="106" rx="22" fill="rgba(15,23,42,0.18)" />
      <rect x="56" y="246" width="528" height="106" rx="22" fill="rgba(255,255,255,0.18)" stroke="rgba(255,255,255,0.22)" />
      <text x="88" y="304" fill="#ffffff" font-family="Arial, Helvetica, sans-serif" font-size="34" font-weight="700">${label}</text>
      <text x="88" y="342" fill="rgba(255,255,255,0.9)" font-family="Arial, Helvetica, sans-serif" font-size="22">${categoryLabel}</text>
    </svg>
  `

  return makeDataUri(svg)
}

function headphoneIllustration(name) {
  const label = escapeXml(name)
  const svg = `
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 420" role="img" aria-label="${label}">
      <defs>
        <linearGradient id="bg" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="#1d4ed8" />
          <stop offset="100%" stop-color="#0f172a" />
        </linearGradient>
        <linearGradient id="accent" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="#f8fafc" />
          <stop offset="100%" stop-color="#cbd5e1" />
        </linearGradient>
      </defs>
      <rect width="640" height="420" rx="34" fill="url(#bg)" />
      <circle cx="160" cy="92" r="150" fill="rgba(255,255,255,0.12)" />
      <circle cx="520" cy="86" r="110" fill="rgba(255,255,255,0.08)" />
      <path d="M180 246c0-78 62-141 140-141s140 63 140 141" fill="none" stroke="rgba(255,255,255,0.9)" stroke-width="24" stroke-linecap="round" />
      <rect x="130" y="228" width="70" height="120" rx="28" fill="url(#accent)" />
      <rect x="440" y="228" width="70" height="120" rx="28" fill="url(#accent)" />
      <rect x="106" y="218" width="118" height="140" rx="58" fill="rgba(255,255,255,0.12)" />
      <rect x="416" y="218" width="118" height="140" rx="58" fill="rgba(255,255,255,0.12)" />
      <circle cx="165" cy="298" r="32" fill="#94a3b8" />
      <circle cx="475" cy="298" r="32" fill="#94a3b8" />
      <path d="M268 164h104c28 0 52 24 52 52v20H216v-20c0-28 24-52 52-52z" fill="rgba(255,255,255,0.18)" />
      <text x="56" y="370" fill="#ffffff" font-family="Arial, Helvetica, sans-serif" font-size="30" font-weight="700">${label}</text>
    </svg>
  `

  return makeDataUri(svg)
}

function watchIllustration(name) {
  const label = escapeXml(name)
  const svg = `
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 420" role="img" aria-label="${label}">
      <defs>
        <linearGradient id="bg" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="#0f766e" />
          <stop offset="100%" stop-color="#134e4a" />
        </linearGradient>
      </defs>
      <rect width="640" height="420" rx="34" fill="url(#bg)" />
      <rect x="270" y="56" width="100" height="110" rx="30" fill="rgba(255,255,255,0.16)" />
      <rect x="270" y="254" width="100" height="110" rx="30" fill="rgba(255,255,255,0.16)" />
      <rect x="198" y="150" width="244" height="120" rx="38" fill="#e2e8f0" />
      <rect x="224" y="176" width="192" height="68" rx="24" fill="#0f172a" />
      <circle cx="320" cy="210" r="28" fill="#38bdf8" />
      <path d="M320 210l14-24" stroke="#e0f2fe" stroke-width="8" stroke-linecap="round" />
      <path d="M320 210h22" stroke="#e0f2fe" stroke-width="8" stroke-linecap="round" />
      <text x="56" y="370" fill="#ffffff" font-family="Arial, Helvetica, sans-serif" font-size="30" font-weight="700">${label}</text>
    </svg>
  `

  return makeDataUri(svg)
}

function speakerIllustration(name) {
  const label = escapeXml(name)
  const svg = `
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 420" role="img" aria-label="${label}">
      <defs>
        <linearGradient id="bg" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="#7c3aed" />
          <stop offset="100%" stop-color="#312e81" />
        </linearGradient>
      </defs>
      <rect width="640" height="420" rx="34" fill="url(#bg)" />
      <rect x="200" y="74" width="240" height="272" rx="32" fill="#111827" />
      <circle cx="320" cy="140" r="54" fill="#e2e8f0" />
      <circle cx="320" cy="140" r="28" fill="#0f172a" />
      <circle cx="320" cy="244" r="82" fill="#e2e8f0" />
      <circle cx="320" cy="244" r="46" fill="#0f172a" />
      <circle cx="320" cy="244" r="18" fill="#94a3b8" />
      <text x="56" y="370" fill="#ffffff" font-family="Arial, Helvetica, sans-serif" font-size="30" font-weight="700">${label}</text>
    </svg>
  `

  return makeDataUri(svg)
}

function backpackIllustration(name) {
  const label = escapeXml(name)
  const svg = `
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 420" role="img" aria-label="${label}">
      <defs>
        <linearGradient id="bg" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="#d97706" />
          <stop offset="100%" stop-color="#78350f" />
        </linearGradient>
      </defs>
      <rect width="640" height="420" rx="34" fill="url(#bg)" />
      <rect x="214" y="92" width="212" height="236" rx="54" fill="#1f2937" />
      <path d="M252 126c0-26 22-48 48-48h40c26 0 48 22 48 48v36H252z" fill="#111827" />
      <rect x="246" y="172" width="148" height="122" rx="22" fill="#374151" />
      <path d="M246 204h148" stroke="#9ca3af" stroke-width="8" />
      <path d="M280 92v-18c0-16 13-30 30-30s30 14 30 30v18" fill="none" stroke="#e5e7eb" stroke-width="10" stroke-linecap="round" />
      <text x="56" y="370" fill="#ffffff" font-family="Arial, Helvetica, sans-serif" font-size="30" font-weight="700">${label}</text>
    </svg>
  `

  return makeDataUri(svg)
}

function mugIllustration(name) {
  const label = escapeXml(name)
  const svg = `
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 420" role="img" aria-label="${label}">
      <defs>
        <linearGradient id="bg" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="#be123c" />
          <stop offset="100%" stop-color="#881337" />
        </linearGradient>
      </defs>
      <rect width="640" height="420" rx="34" fill="url(#bg)" />
      <rect x="204" y="120" width="176" height="170" rx="28" fill="#fff7ed" />
      <path d="M380 150h28c20 0 36 16 36 36s-16 36-36 36h-28" fill="none" stroke="#fff7ed" stroke-width="18" stroke-linecap="round" />
      <rect x="220" y="150" width="144" height="18" rx="9" fill="#fb7185" />
      <rect x="238" y="288" width="108" height="26" rx="13" fill="#fff7ed" />
      <path d="M246 92c12-18 16-34 8-48" stroke="#fda4af" stroke-width="8" stroke-linecap="round" fill="none" />
      <path d="M292 88c14-18 18-34 10-52" stroke="#fda4af" stroke-width="8" stroke-linecap="round" fill="none" />
      <path d="M340 92c12-18 16-34 8-48" stroke="#fda4af" stroke-width="8" stroke-linecap="round" fill="none" />
      <text x="56" y="370" fill="#ffffff" font-family="Arial, Helvetica, sans-serif" font-size="30" font-weight="700">${label}</text>
    </svg>
  `

  return makeDataUri(svg)
}

function jacketIllustration(name) {
  const label = escapeXml(name)
  const svg = `
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 420" role="img" aria-label="${label}">
      <defs>
        <linearGradient id="bg" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="#1e293b" />
          <stop offset="100%" stop-color="#334155" />
        </linearGradient>
      </defs>
      <rect width="640" height="420" rx="34" fill="url(#bg)" />
      <path d="M234 92h172l42 44-28 42v130H220V178l-28-42z" fill="#0f172a" />
      <path d="M288 92l-24 70 56 78 56-78-24-70" fill="#94a3b8" />
      <path d="M320 112v198" stroke="#cbd5e1" stroke-width="10" stroke-linecap="round" />
      <rect x="302" y="162" width="36" height="88" rx="16" fill="#e2e8f0" />
      <path d="M256 136h128" stroke="#e2e8f0" stroke-width="10" stroke-linecap="round" />
      <text x="56" y="370" fill="#ffffff" font-family="Arial, Helvetica, sans-serif" font-size="30" font-weight="700">${label}</text>
    </svg>
  `

  return makeDataUri(svg)
}

function detectTheme(product) {
  const text = `${product?.name ?? ''} ${product?.category ?? ''}`.toLowerCase()

  if (text.includes('headphone') || text.includes('headset') || text.includes('earphone') || text.includes('audio')) {
    return 'headphone'
  }

  if (text.includes('watch') || text.includes('smartwatch') || text.includes('fitness')) {
    return 'watch'
  }

  if (text.includes('speaker') || text.includes('sound')) {
    return 'speaker'
  }

  if (text.includes('backpack') || text.includes('bag') || text.includes('travel')) {
    return 'backpack'
  }

  if (text.includes('mug') || text.includes('cup') || text.includes('home')) {
    return 'mug'
  }

  if (text.includes('jacket') || text.includes('coat') || text.includes('apparel') || text.includes('clothing')) {
    return 'jacket'
  }

  return 'generic'
}

export function getProductImage(product) {
  if (typeof product?.imageUrl === 'string' && product.imageUrl.trim()) {
    return product.imageUrl.trim()
  }

  const name = product?.name?.trim() || 'Product'
  const category = product?.category?.trim() || 'Featured'

  switch (detectTheme(product)) {
    case 'headphone':
      return headphoneIllustration(name)
    case 'watch':
      return watchIllustration(name)
    case 'speaker':
      return speakerIllustration(name)
    case 'backpack':
      return backpackIllustration(name)
    case 'mug':
      return mugIllustration(name)
    case 'jacket':
      return jacketIllustration(name)
    default:
      return genericFallback(name, category)
  }
}
