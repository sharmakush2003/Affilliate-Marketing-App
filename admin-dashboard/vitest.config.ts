import { defineConfig } from 'vitest/config'
import { resolve } from 'path'
import { config } from 'dotenv'

// Load .env.local before vitest starts so process.env is populated for all tests
config({ path: resolve(__dirname, '.env.local') })

const BASE_URL = (process.env.ADMIN_BASE_URL || 'http://localhost:3000').replace(/\s+/g, '').trim()

export default defineConfig({
  test: {
    globals: true,
    environment: 'node',
    testTimeout: 30_000,
    hookTimeout: 30_000,
    reporters: ['verbose'],
    include: ['__tests__/**/*.test.ts'],
    sequence: { shuffle: false },
    env: {
      ADMIN_BASE_URL: BASE_URL,
    },
  },
  resolve: {
    alias: {
      '@': resolve(__dirname, '.'),
    },
  },
})
