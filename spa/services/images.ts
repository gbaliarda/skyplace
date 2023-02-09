import useSWR from "swr"
import Image from "../types/Image"
import { fetcher, genericFetcher, sendJson } from "./endpoints"

export const createImage = (url: string) => sendJson("POST", "/images", { url })

export const useImage = (id: number) => {
  const { data: img, error, mutate } = useSWR<Image>(`/images/${id}`, fetcher)
  const loading = !error && !img
  return { img, loading, error, mutate }
}

export const useImageUrl = (url: string | undefined) => {
  const { data: img, error, mutate } = useSWR<Image>(url ? [url, ""] : null, genericFetcher)
  const loading = !error && !img
  return { img, loading, error, mutate }
}
