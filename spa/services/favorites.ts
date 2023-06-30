import useSWR from "swr"
import { fetcherWithAuth } from "./endpoints"
import Nft from "../types/Nft"
import { FetchError } from "../types/FetchError"

export const useFavoritedNfts = (
  userId: string | number | null,
  nftId: number[],
  accessToken?: string | null,
) => {
  const nftIdToString = nftId.map((id) => `nftId=${id}`).join("&")
  const {
    data: favorites,
    isLoading: loading,
    error: errors,
    mutate,
  } = useSWR<Nft[], FetchError[]>(
    userId ? `/users/${userId}/favorites?${nftIdToString}` : null,
    (url) => fetcherWithAuth(url, accessToken ?? ""),
    { revalidateIfStale: true }
  )
  return { favorites, loading, errors, mutate }
}

export const useFavoritedNft = (
  userId: string | number | null,
  nftId: number | undefined,
  accessToken: string | null | undefined,
) => {
  const {
    data: favorite,
    isLoading: loading,
    error: errors,
    mutate,
  } = useSWR<Nft[], FetchError[]>(
    userId && nftId ? `/users/${userId}/favorites?nftId=${nftId}` : null,
    (url) => fetcherWithAuth(url, accessToken ?? ""),
    { revalidateIfStale: true }
  )
  return { favorite, loading, errors, mutate }
}
