import useSWR from "swr"
import { useEffect } from "react"
import { genericFetcher, fetcher } from "./endpoints"
import Nft from "../types/Nft"
import { FetchError } from "../types/FetchError"
import { NftsFilter, SearchFilter } from "../types/Filters"
import usePagination from "../hooks/usePagination"

export type NftsURL = {
  baseUrl: string
  filters: NftsFilter
  sort: string
  search: SearchFilter
}

export const encodeQueryParam = (filter?: Object) => {
  if (filter === undefined) return ""
  return Object.entries(filter)
    .map((entry) => {
      if (!Array.isArray(entry[1])) return `${entry[0]}=${encodeURIComponent(entry[1])}`
      return entry[1].map((v) => `${entry[0]}=${encodeURIComponent(v)}`).join("&")
    })
    .join("&")
}

export const useNfts = (url: NftsURL) => {
  const { elem: nfts, loading, links, total, totalPages, error, fetchData } = usePagination<Nft[]>()

  const refetchData = (_url: NftsURL) => {
    const filterParams = encodeQueryParam(_url.filters)
    const searchParams = encodeQueryParam(_url.search)
    fetchData(`${_url.baseUrl}&${filterParams}&${searchParams}&sort=${_url.sort}`)
  }

  useEffect(() => {
    refetchData(url)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [JSON.stringify(url)])

  return { nfts, total, totalPages, links, loading, error, refetchData }
}

export const useRecommendedNfts = (nftId: number | undefined) => {
  const {
    data: recommendations,
    isLoading: loading,
    error: errors,
    mutate,
  } = useSWR<Nft[], FetchError[]>(nftId ? `/nfts/${nftId}/recommendations` : null, fetcher)
  return { recommendations, loading, errors, mutate }
}

export const useNft = (id: string | number) => {
  // second generic is error type
  if (typeof id === "string") id = parseInt(id)
  const {
    data: nft,
    isLoading: loading,
    error: errors,
    mutate,
  } = useSWR<Nft, FetchError[]>(id && !Number.isNaN(id) ? `/nfts/${id}` : null, fetcher)
  return { nft, loading, errors, mutate }
}

export const useNftUrl = (url: string | undefined) => {
  // second generic is error type, which will be the statusCode
  const {
    data: nft,
    isLoading: loading,
    error: errors,
    mutate,
  } = useSWR<Nft, FetchError[]>(url ? [url, ""] : null, genericFetcher)
  return { nft, loading, errors, mutate }
}
