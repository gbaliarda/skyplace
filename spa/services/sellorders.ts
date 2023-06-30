import { useEffect } from "react"
import useSWR from "swr"
import Sellorder from "../types/Sellorder"
import { fetcher, genericFetcher } from "./endpoints"
import Buyorder from "../types/Buyorder"
import { BuyordersURL } from "./users"
import usePagination from "../hooks/usePagination"
import { FetchError } from "../types/FetchError"

export const useSellorderUrl = (url: string | undefined) => {
  const {
    data: sellorder,
    error: errors,
    isLoading: loading,
    mutate,
  } = useSWR<Sellorder, FetchError[]>(url ? [url, ""] : null, genericFetcher)
  return { sellorder, loading, errors, mutate }
}

export const useBuyOrders = (url: BuyordersURL) => {
  const {
    elem: buyorders,
    loading,
    links,
    totalPages,
    error,
    fetchData,
  } = usePagination<Buyorder[]>()

  const refetchData = (_url: BuyordersURL) => {
    fetchData(`${_url.baseUrl}`)
  }

  useEffect(() => {
    refetchData(url)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [JSON.stringify(url)])

  return { buyorders, totalPages, links, loading, error, refetchData }
}

export const usePendingBuyOrder = (sellOrderId: number | undefined) => {
  const {
    data,
    error: errors,
    isLoading,
    mutate,
  } = useSWR<Buyorder[], FetchError[]>(
    sellOrderId ? `/sellorders/${sellOrderId}/buyorders?status=PENDING` : null,
    fetcher,
    { revalidateIfStale: true }
  )
  const pendingBuyOrder = data !== undefined && data.length > 0 ? data[0] : undefined
  return { pendingBuyOrder, isLoading, errors, mutate }
}
