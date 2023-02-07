import useSWR from "swr"
import { fetcherWithAuth } from "./endpoints"
import { NftApi } from "../types/Nft"

export const useFavoritedNfts = (
  userId: string | number | null,
  nftId: number[],
  accessToken?: string | null,
) => {
  const nftIdToString = nftId.map((id) => `nftId=${id}`).join("&")
  const {
    data: favorites,
    error,
    mutate,
  } = useSWR<NftApi>(userId ? `/users/${userId}/favorites?${nftIdToString}` : null, (url) =>
    fetcherWithAuth(url, accessToken ?? ""),
  )
  const loading = !error && !favorites
  return { favorites, loading, error, mutate }
}

export const useFavoritedNft = (
  userId: string | number | null,
  nftId: number | undefined,
  accessToken: string | null | undefined,
) => {
  const {
    data: favorite,
    error,
    mutate,
  } = useSWR<NftApi>(userId && nftId ? `/users/${userId}/favorites?nftId=${nftId}` : null, (url) =>
    fetcherWithAuth(url, accessToken ?? ""),
  )
  const loading = !error && !favorite
  return { favorite, loading, error, mutate }
}
