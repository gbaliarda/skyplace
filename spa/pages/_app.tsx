import "../styles/globals.css"
import type { AppProps } from "next/app"
import Head from "next/head"
import { getResourceUrl } from "../services/endpoints"

function MyApp({ Component, pageProps }: AppProps) {
  return (
    <>
      <Head>
        <title>Skyplace</title>
        <link rel="icon" href={getResourceUrl("/favicon.ico")} />
      </Head>
      <Component {...pageProps} />
    </>
  )
}

export default MyApp
