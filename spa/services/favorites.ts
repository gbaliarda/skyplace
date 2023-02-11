import useSWR from "swr"
import { fetcherWithAuth } from "./endpoints"
import Nft from "../types/Nft"
import {FetchError} from "../types/FetchError";

export const useFavoritedNfts = (
  userId: string | number | null,
  nftId: number[],
  accessToken?: string | null,
) => {
  const nftIdToString = nftId.map((id) => `nftId=${id}`).join("&")
  const {
    data: favorites,
    error: errors,
    mutate,
  } = useSWR<Nft[], FetchError[]>(userId ? `/users/${userId}/favorites?${nftIdToString}` : null, (url) =>
    fetcherWithAuth(url, accessToken ?? ""),
  )
  const loading = !errors && !favorites
  return { favorites, loading, errors, mutate }
}

export const useFavoritedNft = (
  userId: string | number | null,
  nftId: number | undefined,
  accessToken: string | null | undefined,
) => {
  const {
    data: favorite,
    error: errors,
    mutate,
  } = useSWR<Nft[], FetchError[]>(userId && nftId ? `/users/${userId}/favorites?nftId=${nftId}` : null, (url) =>
    fetcherWithAuth(url, accessToken ?? ""),
  )
  const loading = !errors && !favorite
  return { favorite, loading, errors, mutate }
}
