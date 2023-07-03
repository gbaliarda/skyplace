import { useCallback, useEffect, useState } from "react"
import { useRouter } from "next/router"
import { useTranslation } from "next-export-i18n"
import queryString from "query-string"
import ProfileNftTab from "../ProfileNftTab"
import ErrorBox from "../../../atoms/ErrorBox"
import Loader from "../../../atoms/Loader"
import { FavoritesURL, useFavorites } from "../../../../services/users"
import { api } from "../../../../services/endpoints"

interface Props {
  userId: number
}

export default function FavoritedTab({ userId }: Props) {
  const { t } = useTranslation()
  const router = useRouter()
  const [page, setPage] = useState<number>()
  const defaultURL = {
    baseUrl: `${api}/users/${userId}/favorites?page=${page}`,
    sort: "",
  } as FavoritesURL
  const [url, setUrl] = useState<FavoritesURL>(defaultURL)
  const [sort, setSort] = useState("")
  const { favorites, links, totalPages, loading, error, refetchData } = useFavorites(url)

  useEffect(() => {
    if (!router.isReady) return

    if (router.query.page)
      setPage(
        Array.isArray(router.query.page)
          ? parseInt(router.query.page[0])
          : parseInt(router.query.page),
      )
  }, [router.isReady, router.query.page])

  useEffect(() => {
    setSort(Array.isArray(router.query.sort) ? router.query.sort[0] : router.query.sort ?? "")
  }, [router.query.sort])

  const updatePage = (pageNumber: string) => {
    const queryParams = new URLSearchParams(queryString.stringify(router.query))

    setPage(parseInt(pageNumber))
    queryParams.set("page", pageNumber)

    const newURL = `${router.pathname}?${queryParams.toString()}`

    router.push(newURL)
  }

  const buildFavoritesUrlFromUrl = (_url: string) => {
    const urlAux = new URL(_url)
    const params = urlAux.searchParams
    const sortParam = params.get("sort")

    const favoritesUrl: FavoritesURL = {
      baseUrl: `${urlAux.origin}${urlAux.pathname}?page=${params.get("page")}`,
      sort: sortParam != null ? sortParam : sort,
    }

    return favoritesUrl
  }

  const applySort = (_sort: string) => {
    const queryParams = new URLSearchParams(queryString.stringify(router.query))

    queryParams.set("sort", _sort)

    if (_sort === "priceDsc" || _sort === "priceAsc") queryParams.set("page", "1")

    const newURL = `${router.pathname}?${queryParams.toString()}`

    router.push(newURL)
    setSort(_sort)
  }

  const updateUrl = useCallback(
    (_url: string) => {
      const params = new URL(_url).searchParams
      const pageNumber = params.get("page") ?? "1"
      updatePage(pageNumber)

      const favoritesUrl = buildFavoritesUrlFromUrl(_url)
      setUrl(favoritesUrl)
    },
    [url],
  )

  useEffect(() => {
    if (router.isReady && page === undefined && router.query.page === undefined) setPage(1)
    setUrl({
      ...url,
      baseUrl: `${api}/users/${userId}/favorites?page=${page}`,
      sort,
    })
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [sort, page])

  if (error)
    return (
      <ErrorBox errorMessage={t("errors.errorLoadingTab")} retryAction={() => refetchData(url)} />
    )
  if (loading || favorites === undefined)
    return <Loader className="grow flex items-center justify-center mb-32" />

  return (
    <ProfileNftTab
      updateUrl={updateUrl}
      sort={sort}
      setSort={applySort}
      nfts={favorites}
      totalPages={totalPages}
      links={links ?? undefined}
    />
  )
}
