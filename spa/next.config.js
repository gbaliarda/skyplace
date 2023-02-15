const isProd = process.env.NODE_ENV === 'production'

/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  swcMinify: true,
  images: {
    domains: ['picsum.photos'],
  },
  experimental: {
    forceSwcTransforms: true,
  },
  trailingSlash: true,
  basePath: isProd ? '/paw-2022a-09' : undefined,
  assetPrefix: isProd ? '/paw-2022a-09' : undefined,
}

module.exports = nextConfig
