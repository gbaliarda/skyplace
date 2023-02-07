import useSWR from "swr"
import { PurchaseApi } from "../types/Purchase"
import { fetcher } from "./endpoints"

export const usePurchases = (
  userId: number | undefined,
  page: number | undefined,
  accessToken: string | undefined,
  purchaser?: number | undefined,
) => {
  const {
    data: purchases,
    error,
    mutate,
  } = useSWR<PurchaseApi>(
    [
      `/users/${userId}/purchases?page=${page === undefined ? "1" : page}${
        purchaser === undefined ? "" : `&purchaser=${purchaser}`
      }`,
      { headers: { Authorization: `Bearer ${accessToken === undefined ? "" : accessToken}` } },
    ],
    ([resource, options]) => fetcher(resource, options),
  )
  return {
    purchases,
    loading: !error && !purchases,
    error,
    mutate,
  }
}
