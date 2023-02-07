import useSWR from "swr"

const fetcher = (currency: string) =>
  fetch(`https://api.coingecko.com/api/v3/simple/price?ids=${currency}&vs_currencies=usd`).then(
    (res) => res.json(),
  )

interface CoingeckoResponse {
  [key: string]: { usd: number }
}

export const useCryptoPrice = (currency: string) => {
  const { data, error, isLoading, mutate } = useSWR<CoingeckoResponse>(currency, fetcher)
  const price = data?.[currency]?.usd
  return { price, isLoading, error, mutate }
}
