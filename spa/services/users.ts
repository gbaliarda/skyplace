import { useEffect } from "react"
import useSWR from "swr"
import { fetcher, genericFetcher, sendJson } from "./endpoints"
import User from "../types/User"
import Nft from "../types/Nft"
import Buyorder from "../types/Buyorder"
import usePagination from "../hooks/usePagination"
import { FetchError } from "../types/FetchError"

export type BuyordersURL = {
  baseUrl: string
  status: string
}

export type FavoritesURL = {
  baseUrl: string
  sort: string
}

export const useUser = (id: number | undefined) => {
  const {
    data: user,
    error: errors,
    mutate,
  } = useSWR<User, FetchError[]>(id ? `/users/${id}` : null, fetcher)
  const loading = !errors && !user
  return { user, loading, errors, mutate }
}

export const useUserUrl = (url: string | undefined) => {
  const {
    data: user,
    error: errors,
    mutate,
  } = useSWR<User, FetchError[]>(url ? [url, ""] : null, genericFetcher)
  const loading = !errors && !user
  return { user, loading, errors, mutate }
}

export const useFavorites = (url: FavoritesURL) => {
  const {
    elem: favorites,
    loading,
    links,
    totalPages,
    error,
    fetchData,
  } = usePagination<Nft[]>(true)

  const refetchData = (_url: FavoritesURL) => {
    fetchData(`${_url.baseUrl}&sort=${_url.sort}`)
  }

  useEffect(() => {
    refetchData(url)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [JSON.stringify(url)])

  return { favorites, totalPages, links, loading, error, refetchData }
}

export const useUserBuyorders = (url: BuyordersURL) => {
  const {
    elem: buyorders,
    loading,
    links,
    totalPages,
    error,
    fetchData,
  } = usePagination<Buyorder[]>(true)

  const refetchData = (_url: BuyordersURL) => {
    fetchData(`${_url.baseUrl}&status=${_url.status}`)
  }

  useEffect(() => {
    refetchData(url)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [JSON.stringify(url)])

  return { buyorders, totalPages, links, loading, error, refetchData }
}

export const createUser = ({
  email,
  wallet,
  walletChain,
  username,
  password,
  confirmPassword,
}: Omit<User, "id"> & { walletChain: string; password: string; confirmPassword: string }) =>
  sendJson("POST", "/users", {
    email,
    walletAddress: wallet,
    walletChain,
    username,
    password,
    passwordRepeat: confirmPassword,
  })

export const loginUser = async (email: string, password: string, rememberMe = true) => {
  const authBasic = Buffer.from(`${email}:${password}`).toString("base64")
  const res = (await fetcher(
    "/nfts",
    {
      headers: {
        Authorization: `Basic ${authBasic}`,
      },
    },
    true,
  )) as Response
  // assuming the server returns the tokens in the headers
  const accessToken = res.headers.get("x-access-token")!!
  const refreshToken = res.headers.get("x-renewal-token")!!
  if (rememberMe) {
    localStorage.setItem("access-token", accessToken)
    localStorage.setItem("refresh-token", refreshToken)
  } else {
    localStorage.removeItem("access-token")
    localStorage.removeItem("refresh-token")
    sessionStorage.setItem("access-token", accessToken)
    sessionStorage.setItem("refresh-token", refreshToken)
  }
}
