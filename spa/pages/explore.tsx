import { useCallback, useEffect, useState } from "react"
import { useRouter } from "next/router"
import { useTranslation } from "next-export-i18n"
import queryString from 'query-string'
import FilterSideBar from "../components/molecules/FilterSideBar"
import ExploreContent from "../components/molecules/ExploreContent"
import Layout from "../components/Layout"
import { NftsURL, useNfts } from "../services/nfts"
import Loader from "../components/atoms/Loader"
import { NftsFilter, SaleStatus, SearchFilter, SearchType } from "../types/Filters"
import ErrorBox from "../components/atoms/ErrorBox"
import { api } from "../services/endpoints"
import { Category, Chain } from "../types/Nft"

const Explore = () => {
  const router = useRouter()

  const [search, setSearch] = useState<SearchFilter>()
  const [filters, setFilters] = useState<NftsFilter>({})
  const [sort, setSort] = useState("")
  const [page, setPage] = useState<number>()

  useEffect(() => {
    const querySearch = router.query.search
    setSearch({
      searchFor: (router.query.searchFor as SearchType) ?? SearchType.Nft,
      search: Array.isArray(querySearch) ? querySearch[0] : querySearch ?? "",
    })
    setFilters({})
  }, [router.query.search, router.query.searchFor])

  useEffect(() => {
    setFilters((prevFilters) => {
      const newFilters = {...prevFilters}

      newFilters.status = router.query.status as SaleStatus[]
      newFilters.category = router.query.category as Category[]
      newFilters.chain = router.query.chain as Chain[]

      const queryMinPrice = router.query.minPrice
      const queryMaxPrice = router.query.maxPrice

      newFilters.minPrice = Array.isArray(queryMinPrice) ? parseInt(queryMinPrice[0]) : parseInt(queryMinPrice ?? "0")
      newFilters.maxPrice = Array.isArray(queryMaxPrice) ? parseInt(queryMaxPrice[0]) : parseInt(queryMaxPrice ?? "0")
      
      return newFilters
    })
  }, [router.query.status, router.query.category, router.query.chain, router.query.minPrice, router.query.maxPrice])

  useEffect(() => {
    setSort(Array.isArray(router.query.sort) ? router.query.sort[0] : router.query.sort ?? "")
  }, [router.query.sort])

  useEffect(() => {
    if (!router.isReady) return

    if (router.query.page)
      setPage(Array.isArray(router.query.page) ? parseInt(router.query.page[0]) : parseInt(router.query.page))
  }, [router.query.page])

  const defaultURL: NftsURL = {
    baseUrl: `${api}/nfts?page=${page}`,
    filters: filters,
    sort: "",
  }
  const [isFilterClosed, setIsFilterClosed] = useState(false)
  const [url, setUrl] = useState(defaultURL)
  const { nfts, total, totalPages, links, loading, error, refetchData } = useNfts(url)
  const { t } = useTranslation()

  const buildNftsUrlFromUrl = (_url: string) => {
    const urlAux = new URL(_url)
    const params = urlAux.searchParams
    const minPrice = params.get("minPrice")
    const maxPrice = params.get("maxPrice")
    const status = params.getAll("status")
    const category = params.getAll("category")
    const chain = params.getAll("chain")
    const sortParam = params.get("sort")
    const searchParam = params.get("search")
    const searchForParam = params.get("searchFor")

    const nftsUrl:NftsURL = {
      baseUrl: `${urlAux.origin}${urlAux.pathname}?page=${params.get("page")}`,
      filters: {
        minPrice: minPrice != null ? parseInt(minPrice) : filters.minPrice,
        maxPrice: maxPrice != null ? parseInt(maxPrice) : filters.maxPrice,
        status: status != null ? status as SaleStatus[] : filters.status,
        category: category != null ? category as Category[] : filters.category,
        chain: chain != null ? chain as Chain[] : filters.chain
      },
      sort: sortParam != null ? sortParam : sort,
    }

    if (search != undefined || (searchParam != null && searchForParam != null)) {
      nftsUrl.search = {
        searchFor: searchForParam != null ? searchForParam as SearchType : search!!.searchFor,
        search: searchParam != null ? searchParam : search!!.search
      }
    }

    return nftsUrl
  }

  const updateUrl = useCallback(
    (_url: string) => {
      const params = new URL(_url).searchParams
      const pageNumber = params.get('page') ?? "1"
      updatePage(pageNumber)
      
      const nftsUrl = buildNftsUrlFromUrl(_url)
      setUrl(nftsUrl)
    },
    [url],
  )

  useEffect(() => {
    if (router.isReady && page === undefined)
      setPage(1)
    setUrl({
      ...url,
      baseUrl: `${api}/nfts?page=${page}`,
      filters,
      sort,
      search,
    })
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [filters, sort, search, page])

  const updatePage = (pageNumber: string) => {
    const queryParams = new URLSearchParams(queryString.stringify(router.query))

    setPage(parseInt(pageNumber))
    queryParams.set("page", pageNumber)

    const newURL = `${router.pathname}?${queryParams.toString()}`

    router.push(newURL)
  }

  const applySort = (sort: string) => {
    const queryParams = new URLSearchParams(queryString.stringify(router.query))

    queryParams.set("sort", sort)
    
    if (!queryParams.getAll("status").includes(SaleStatus.ONSALE) && (sort === "priceDsc" || sort === "priceAsc")) {
      queryParams.append("status", SaleStatus.ONSALE)
      queryParams.set("page", "1")
    }

    const newURL = `${router.pathname}?${queryParams.toString()}`
    
    router.push(newURL)
    setSort(sort)
  }

  return (
    <Layout>
      <div className="grow flex pt-16 max-h-[calc(100vh-5rem)] divide-x divide-slate-300">
        <FilterSideBar
          isClosed={isFilterClosed}
          setIsClosed={setIsFilterClosed}
          filters={filters}
          totalResults={total}
        />
        {error ? (
          <div className="flex justify-center grow mt-24">
            <ErrorBox errorMessage={t("errors.loadingNfts")} retryAction={() => refetchData(url)} />
          </div>
        ) : loading || nfts === undefined ? (
          <Loader className="grow flex items-center justify-center mb-32" />
        ) : (
          <ExploreContent
            nfts={nfts}
            links={links ?? undefined}
            updateUrl={updateUrl}
            sortDefaultValue={sort}
            setSort={applySort}
            amountPages={totalPages}
          />
        )}
      </div>
    </Layout>
  )
}

export default Explore
