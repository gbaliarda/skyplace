import { useEffect } from "react"
import useSWR from "swr"
import Sellorder from "../types/Sellorder"
import { fetcher, genericFetcher } from "./endpoints"
import Buyorder, { BuyOrderApi } from "../types/Buyorder"
import { BuyordersURL } from "./users"
import usePagination from "../hooks/usePagination"
import {FetchError} from "../types/FetchError";

/*
export const useSellorder = (id: number | undefined) => {
  // second generic is error type, which will be the statusCode
  const {
    data: sellorder,
    error: errors,
    mutate,
  } = useSWR<Sellorder, number>(id ? `/sellorders/${id}` : null, fetcher)
  const loading = !errors && !sellorder
  return { sellorder, loading, errors, mutate }
}

 */

export const useSellorderUrl = (url: string | undefined) => {
  // second generic is error type, which will be the statusCode
  const {
    data: sellorder,
    error: errors,
    mutate,
  } = useSWR<Sellorder, FetchError[]>(url ? [url, ""] : null, genericFetcher)
  const loading = !errors && !sellorder
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

// export const useBuyOrders = (url: string | undefined, page: number = 1) => {
//   const {
//     data: buyorders,
//     error,
//     isLoading,
//     mutate,
//   } = useSWR<BuyOrderApi>(url ? [`${url}?page=${page}`, ""] : null, genericFetcher)
//   return { buyorders, loading: isLoading, error, mutate }
// }

export const usePendingBuyOrder = (sellOrderId: number | undefined) => {
  const { data, error: errors, isLoading, mutate } = useSWR<BuyOrderApi, FetchError[]>(
    sellOrderId ? `/sellorders/${sellOrderId}/buyorders?status=PENDING` : null,
    fetcher,
  )
  const pendingBuyOrder = data?.buyorders[0] as Buyorder | undefined
  return { pendingBuyOrder, isLoading, errors, mutate }
}
