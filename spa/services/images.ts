import useSWR from "swr"
import Image from "../types/Image"
import { fetcher, genericFetcher, sendJson } from "./endpoints"
import { FetchError } from "../types/FetchError"

export const createImage = (url: string) => sendJson("POST", "/images", { url })

export const useImage = (id: number) => {
  const {
    data: img,
    isLoading: loading,
    error: errors,
    mutate,
  } = useSWR<Image, FetchError[]>(`/images/${id}`, fetcher)
  return { img, loading, errors, mutate }
}

export const useImageUrl = (url: string | undefined) => {
  const {
    data: img,
    isLoading: loading,
    error: errors,
    mutate,
  } = useSWR<Image, FetchError[]>(url ? [url, ""] : null, genericFetcher)
  return { img, loading, errors, mutate }
}
