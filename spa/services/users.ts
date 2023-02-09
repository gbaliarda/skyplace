import useSWR from "swr"
import { fetcher, genericFetcher, sendJson } from "./endpoints"
import User from "../types/User"
import { NftApi } from "../types/Nft"
import { BuyOrderApi } from "../types/Buyorder"

export const useUser = (id: number | undefined) => {
  const { data: user, error, mutate } = useSWR<User, number>(id ? `/users/${id}` : null, fetcher)
  const loading = !error && !user
  return { user, loading, error, mutate }
}

export const useUserUrl = (url: string | undefined) => {
  const { data: user, error, mutate } = useSWR<User>(url ? [url, ""] : null, genericFetcher)
  const loading = !error && !user
  return { user, loading, error, mutate }
}

export const useFavorites = (
  userId: string | number,
  page: number | undefined,
  sort: string | undefined,
) => {
  let accessToken = localStorage.getItem("access-token")
  if (accessToken === null) accessToken = sessionStorage.getItem("access-token")
  const {
    data: nfts,
    error,
    mutate,
  } = useSWR<NftApi>(
    [
      `/users/${userId}/favorites?page=${page === undefined ? "1" : page}${
        sort === undefined ? "" : `&sort=${sort}`
      }`,
      { headers: { Authorization: `Bearer ${accessToken}` } },
    ],
    ([resource, options]) => fetcher(resource, options),
  )
  const loading = !error && !nfts
  return { nfts, loading, error, mutate }
}

export const useUserBuyorders = (
  userId: number | undefined,
  page: number | undefined,
  status: string | undefined,
) => {
  let accessToken = localStorage.getItem("access-token")
  if (accessToken === null) accessToken = sessionStorage.getItem("access-token")
  const {
    data: buyorders,
    error,
    mutate,
  } = useSWR<BuyOrderApi>(
    [
      `/users/${userId}/buyorders?page=${page === undefined ? "1" : page}${
        status === undefined ? "" : `&status=${status}`
      }`,
      { headers: { Authorization: `Bearer ${accessToken}` } },
    ],
    ([resource, options]) => fetcher(resource, options),
  )
  const loading = !error && !buyorders
  return { buyorders, loading, error, mutate }
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
