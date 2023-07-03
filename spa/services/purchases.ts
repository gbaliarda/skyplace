import { useEffect } from "react"
import Purchase from "../types/Purchase"
import usePagination from "../hooks/usePagination"

export type PurchasesURL = {
  baseUrl: string | null
  purchaser?: number
}

export const usePurchases = (url: PurchasesURL) => {
  const {
    elem: purchases,
    loading,
    links,
    totalPages,
    error: errors,
    fetchData,
  } = usePagination<Purchase[]>(true)

  const refetchData = (_url: PurchasesURL) => {
    if (_url.baseUrl === null) return
    const params = new URL(_url.baseUrl).searchParams
    if (params.get("page") === "undefined") return
    const purchaserFilter = _url.purchaser ? `&purchaser=${_url.purchaser}` : ""
    fetchData(`${_url.baseUrl}${purchaserFilter}`)
  }

  useEffect(() => {
    refetchData(url)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [JSON.stringify(url)])

  return { purchases, totalPages, links, loading, errors, refetchData }
}
