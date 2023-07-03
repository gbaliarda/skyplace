import { useRouter } from "next/router"
import { useTranslation } from "next-export-i18n"
import queryString from "query-string"
import { useCallback, useEffect, useState } from "react"
import { NftsURL, useNfts } from "../../../../services/nfts"
import ErrorBox from "../../../atoms/ErrorBox"
import Loader from "../../../atoms/Loader"
import ProfileNftTab from "../ProfileNftTab"
import { api } from "../../../../services/endpoints"
import { SearchFilter } from "../../../../types/Filters"

interface Props {
  userId: number
}

export default function InventoryTab({ userId }: Props) {
  const { t } = useTranslation()
  const router = useRouter()
  const [page, setPage] = useState<number>()
  const defaultURL = {
    baseUrl: `${api}/nfts?page=${page}`,
    filters: { owner: userId },
    search: {},
    sort: "",
  } as NftsURL

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

  const [sort, setSort] = useState("")
  const [url, setUrl] = useState<NftsURL>(defaultURL)
  const { nfts, links, totalPages, loading, error, refetchData } = useNfts(url)

  const updatePage = (pageNumber: string) => {
    const queryParams = new URLSearchParams(queryString.stringify(router.query))

    setPage(parseInt(pageNumber))
    queryParams.set("page", pageNumber)

    const newURL = `${router.pathname}?${queryParams.toString()}`

    router.push(newURL)
  }

  const buildNftsUrlFromUrl = (_url: string) => {
    const urlAux = new URL(_url)
    const params = urlAux.searchParams
    const sortParam = params.get("sort")

    const nftsUrl: NftsURL = {
      baseUrl: `${urlAux.origin}${urlAux.pathname}?page=${params.get("page")}`,
      filters: {
        owner: userId,
      },
      sort: sortParam != null ? sortParam : sort,
      search: {} as SearchFilter,
    }

    return nftsUrl
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

      const nftsUrl = buildNftsUrlFromUrl(_url)
      setUrl(nftsUrl)
    },
    [url],
  )

  useEffect(() => {
    if (router.isReady && page === undefined && router.query.page === undefined) setPage(1)
    setUrl({
      ...url,
      baseUrl: `${api}/nfts?page=${page}`,
      sort,
    })
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [sort, page])

  if (error)
    return (
      <ErrorBox errorMessage={t("errors.errorLoadingTab")} retryAction={() => refetchData(url)} />
    )
  if (loading || nfts === undefined)
    return <Loader className="grow flex items-center justify-center mb-32" />

  return (
    <ProfileNftTab
      links={links ?? undefined}
      updateUrl={updateUrl}
      sort={sort}
      setSort={applySort}
      nfts={nfts}
      totalPages={totalPages}
    />
  )
}
