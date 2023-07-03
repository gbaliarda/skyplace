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
  search?: SearchFilter
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
    if (_url.search === undefined) return
    if (_url.filters.category === undefined) delete _url.filters.category
    if (_url.filters.chain === undefined) delete _url.filters.chain
    if (_url.filters.status === undefined) delete _url.filters.status
    const params = new URL(_url.baseUrl).searchParams
    if (params.get("page") === "undefined") return

    const filterParams = encodeQueryParam(_url.filters)
    const searchParams = encodeQueryParam(_url.search)
    const sort = _url.sort ? `&sort=${_url.sort}` : ""
    fetchData(`${_url.baseUrl}&${filterParams}&${searchParams}${sort}`)
  }

  useEffect(() => {
    refetchData(url)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [JSON.stringify(url)])

  return { nfts, total, totalPages, links, loading, error, refetchData }
}

export const useRecommendedNfts = (nftUrl: string | undefined) => {
  const {
    data: recommendations,
    isLoading: loading,
    error: errors,
    mutate,
  } = useSWR<Nft[], FetchError[]>(nftUrl ? [nftUrl, ""] : null, genericFetcher)
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
  } = useSWR<Nft, FetchError[]>(id && !Number.isNaN(id) ? `/nfts/${id}` : null, fetcher, {
    revalidateIfStale: true,
  })
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
