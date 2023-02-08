import { FetchError } from "../types/FetchError"
import USErrorCodes from "../i18n/translations.en.json"
import ESErrorCodes from "../i18n/translations.es.json"

const errorTranslations = {
  en: { ...USErrorCodes.errorCodes },
  es: { ...ESErrorCodes.errorCodes },
}

export const api = "http://localhost:8080/api"

const jsonHeader = (accessToken: string = "") => ({
  headers: {
    "Content-Type": "application/json; charset=UTF-8",
    Authorization: `Bearer ${accessToken}`,
  },
})

const jsonOptions = (data: Object, accessToken: string = "") => ({
  ...jsonHeader(accessToken),
  body: JSON.stringify(data),
})

const checkStatus = async (res: Response) => {
  if (!res.ok) {
    const { errors } = await res.json()
    // @ts-ignore
    const userLang = errorTranslations[navigator.language.substring(0, 2)]
      ? navigator.language.substring(0, 2)
      : "en"
    const error = new Error() as FetchError
    error.name = res.statusText
    // @ts-ignore
    error.message = errorTranslations[userLang][errors[0].code] ?? "Error :("
    error.cause = {
      statusCode: res.status,
      errorCode: errors[0].code,
      description: errors[0].title,
    }
    throw error
  }
  return res
}

// Docs: https://swr.vercel.app/docs/error-handling
export const genericFetcher: any = (
  [baseUrl, resource]: [string, string],
  options?: RequestInit,
  withHeaders: boolean = false,
  retryWithRefreshToken: boolean = true,
) =>
  fetch(baseUrl + resource, options)
    .then(checkStatus)
    // Do not throw if the response's status is 204 (no content, body empty)
    .then((res) => (withHeaders ? res : res.json().catch(() => {})))
    .catch((e: FetchError) => {
      // JWT expired
      if (e.cause?.errorCode === "14") {
        // Access token expired, try to refresh it
        if (retryWithRefreshToken) {
          const refreshToken =
            localStorage.getItem("refresh-token") || sessionStorage.getItem("refresh-token")
          const newOptions: RequestInit = {
            ...options,
            headers: {
              ...options?.headers,
              Authorization: `Bearer ${refreshToken}`,
            },
          }
          return genericFetcher([baseUrl, resource], newOptions, withHeaders, false)
        }

        // Refresh token expired, remove tokens and redirect to login
        localStorage.removeItem("access-token")
        localStorage.removeItem("refresh-token")
        sessionStorage.removeItem("access-token")
        sessionStorage.removeItem("refresh-token")
        window.location.replace("/login")
      } else {
        throw e
      }
    })

export const fetcher = (resource: string, options?: RequestInit, withHeaders: boolean = false) =>
  genericFetcher([api, resource], options, withHeaders)

export const fetcherWithAuth = (resource: string, accessToken: string = "") =>
  fetcher(resource, { ...jsonHeader(accessToken) })

export const postJson = (resource: string, data: Object, accessToken?: string | null) =>
  fetcher(resource, {
    method: "POST",
    ...jsonOptions(data, accessToken || ""),
  })

export const patchJson = (resource: string, data: Object) =>
  fetcher(resource, {
    method: "PATCH",
    ...jsonOptions(data),
  })
