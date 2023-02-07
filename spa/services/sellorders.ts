import useSWR from "swr"
import Sellorder from "../types/Sellorder"
import { fetcher, genericFetcher } from "./endpoints"
import Buyorder, { BuyOrderApi } from "../types/Buyorder"

export const useSellorder = (id: number | undefined) => {
  // second generic is error type, which will be the statusCode
  const {
    data: sellorder,
    error,
    mutate,
  } = useSWR<Sellorder, number>(id ? `/sellorders/${id}` : null, fetcher)
  const loading = !error && !sellorder
  return { sellorder, loading, error, mutate }
}

export const useSellorderUrl = (url: string | undefined) => {
  // second generic is error type, which will be the statusCode
  const {
    data: sellorder,
    error,
    mutate,
  } = useSWR<Sellorder, number>(url ? [url, ""] : null, genericFetcher)
  const loading = !error && !sellorder
  return { sellorder, loading, error, mutate }
}

export const useBuyOrders = (url: string | undefined, page: number = 1) => {
  const {
    data: buyorders,
    error,
    isLoading,
    mutate,
  } = useSWR<BuyOrderApi>(url ? [`${url}?page=${page}`, ""] : null, genericFetcher)
  return { buyorders, loading: isLoading, error, mutate }
}

export const usePendingBuyOrder = (sellOrderId: number | undefined) => {
  const { data, error, isLoading, mutate } = useSWR<BuyOrderApi>(
    sellOrderId ? `/sellorders/${sellOrderId}/buyorders?status=PENDING` : null,
    fetcher,
  )
  const pendingBuyOrder = data?.buyorders[0] as Buyorder | undefined
  return { pendingBuyOrder, isLoading, error, mutate }
}
