import { useEffect } from "react"
import Purchase from "../types/Purchase"
import usePagination from "../hooks/usePagination"

export type PurchasesURL = {
  baseUrl: string
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
    const purchaserFilter = _url.purchaser ? `&purchaser=${_url.purchaser}` : ""
    fetchData(`${_url.baseUrl}${purchaserFilter}`)
  }

  useEffect(() => {
    refetchData(url)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [JSON.stringify(url)])

  return { purchases, totalPages, links, loading, errors, refetchData }
}

// export const usePurchases = (
//   userId: number | undefined,
//   page: number | undefined,
//   accessToken: string | undefined,
//   purchaser?: number | undefined,
// ) => {
//   const {
//     data: purchases,
//     error,
//     mutate,
//   } = useSWR<PurchaseApi>(
//     [
//       `/users/${userId}/purchases?page=${page === undefined ? "1" : page}${
//         purchaser === undefined ? "" : `&purchaser=${purchaser}`
//       }`,
//       { headers: { Authorization: `Bearer ${accessToken === undefined ? "" : accessToken}` } },
//     ],
//     ([resource, options]) => fetcher(resource, options),
//   )
//   return {
//     purchases,
//     loading: !error && !purchases,
//     error,
//     mutate,
//   }
// }
