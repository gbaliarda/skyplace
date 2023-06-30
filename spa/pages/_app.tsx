import type { AppProps } from "next/app"
import Head from "next/head"
import { SWRConfig } from "swr"

import "../styles/globals.css"
import { getResourceUrl } from "../services/endpoints"

function MyApp({ Component, pageProps }: AppProps) {
  return (
    <>
      <Head>
        <title>Skyplace</title>
        <link rel="icon" href={getResourceUrl("/favicon.ico")} />
      </Head>
      <SWRConfig
        value={{
          revalidateOnFocus: false,
          revalidateOnReconnect: false,
          // refreshInterval: 120000, // invalidate cache every 2 minutes
          revalidateIfStale: false, // disables all automatic revalidation
        }}
      >
        <Component {...pageProps} />
      </SWRConfig>
    </>
  )
}

export default MyApp
