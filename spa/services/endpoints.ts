import { FetchError } from "../types/FetchError"
import USErrorCodes from "../i18n/translations.en.json"
import ESErrorCodes from "../i18n/translations.es.json"
import ApiError from "../types/ApiError"

const errorTranslations = {
  en: { ...USErrorCodes.errorCodes },
  es: { ...ESErrorCodes.errorCodes },
}

export const api =
  process.env.NODE_ENV === "development"
    ? "http://localhost:8080/api"
    : "http://pawserver.it.itba.edu.ar/paw-2022a-09/api"

export const getResourceUrl = (resource: string) =>
  process.env.NODE_ENV === "development" ? resource : "/paw-2022a-09" + resource

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

export const checkStatus = async (res: Response) => {
  if (!res.ok) {
    const { errors } = await res.json()

    // @ts-ignore
    const userLang = errorTranslations[navigator.language.substring(0, 2)]
      ? navigator.language.substring(0, 2)
      : "en"

    let parsedErrors: FetchError[] = []

    errors.forEach((apiError: ApiError) => {
      const auxError = new Error() as FetchError
      auxError.name = res.statusText
      auxError.message =
        // @ts-ignore
        errorTranslations[userLang][apiError.code] ??
        // @ts-ignore
        errorTranslations[userLang][res.status] ??
        "Error :("
      auxError.cause = {
        statusCode: res.status,
        errorCode: apiError.code,
        description: apiError.title,
        field: apiError.source?.pointer,
      }
      parsedErrors.push(auxError)
    })

    throw parsedErrors
  }
  return res
}

// Docs: https://swr.vercel.app/docs/error-handling
export const genericFetcher = (
  [baseUrl, resource]: [string, string],
  options?: RequestInit,
  withHeaders: boolean = false,
  retryWithRefreshToken: boolean = true,
): any =>
  fetch(baseUrl + resource, options)
    .then(checkStatus)
    // Do not throw if the response's status is 204 (no content, body empty)
    .then((res) => {
      // update accessToken if a refresh was requested
      const accessToken = res.headers.get("X-Access-Token")
      if (accessToken) {
        if (localStorage.getItem("access-token")) {
          localStorage.setItem("access-token", accessToken)
        } else {
          sessionStorage.setItem("access-token", accessToken)
        }
      }
      return withHeaders ? res : res.json().catch(() => {})
    })
    .catch((errs: FetchError[]) => {
      // JWT expired
      if (errs.length === 1 && errs[0].cause?.errorCode == "14") {
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
        throw errs
      }
    })

export const fetcher = (resource: string, options?: RequestInit, withHeaders: boolean = false) =>
  genericFetcher([api, resource], options, withHeaders)

export const fetcherWithAuth = (resource: string, accessToken: string = "") =>
  fetcher(resource, { ...jsonHeader(accessToken) })

export const sendJson = (
  method: "PUT" | "POST",
  resource: string,
  data: Object,
  accessToken?: string | null,
) =>
  fetcher(resource, {
    method,
    ...jsonOptions(data, accessToken || ""),
  })
