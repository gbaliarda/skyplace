import useSWR from "swr"
import { genericFetcher, fetcher } from "./endpoints"
import Nft, { NftApi } from "../types/Nft"
import { NftsFilter, SearchFilter } from "../types/Filters"
import { FetchError } from "../types/FetchError"

const encodeQueryParam = (filter?: Object) => {
  if (filter === undefined) return ""
  return Object.entries(filter)
    .map((entry) => {
      if (!Array.isArray(entry[1])) return `${entry[0]}=${encodeURIComponent(entry[1])}`
      return entry[1].map((v) => `${entry[0]}=${encodeURIComponent(v)}`).join("&")
    })
    .join("&")
}

export const useNfts = (
  page: number = 0,
  filter?: NftsFilter,
  sort?: string,
  search?: SearchFilter,
) => {
  const filterParams = encodeQueryParam(filter)
  const searchParams = encodeQueryParam(search)
  const {
    data: nfts,
    error,
    mutate,
  } = useSWR<NftApi>(`/nfts?page=${page}&${filterParams}&sort=${sort}&${searchParams}`, fetcher)
  const loading = !error && !nfts
  return { nfts, loading, error, mutate }
}

export const useRecommendedNfts = (nftId: string | number) => {
  const {
    data: recommendations,
    error,
    mutate,
  } = useSWR<NftApi>(nftId ? `/nfts/${nftId}/recommendations` : null, fetcher)
  const loading = !error && !recommendations
  return { recommendations, loading, error, mutate }
}

export const useNft = (id: string | number) => {
  // second generic is error type
  const { data: nft, error, mutate } = useSWR<Nft, FetchError>(id ? `/nfts/${id}` : null, fetcher)
  const loading = !error && !nft
  return { nft, loading, error, mutate }
}

export const useNftUrl = (url: string | undefined) => {
  // second generic is error type, which will be the statusCode
  const { data: nft, error, mutate } = useSWR<Nft>([url, ""], genericFetcher)
  const loading = !error && !nft
  return { nft, loading, error, mutate }
}
