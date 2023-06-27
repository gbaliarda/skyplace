import { useCallback, useEffect, useState } from "react"
import { useRouter } from "next/router"
import { useTranslation } from "next-export-i18n"
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

      if (router.query.status !== undefined)
        newFilters.status = router.query.status as SaleStatus[];
  
      if (router.query.category !== undefined)
        newFilters.category = router.query.category as Category[];
    
      if (router.query.chain !== undefined)
        newFilters.chain = router.query.chain as Chain[];

      
      const queryMinPrice = router.query.minPrice
      const queryMaxPrice = router.query.maxPrice

      if (queryMinPrice !== undefined)
        newFilters.minPrice = Array.isArray(queryMinPrice) ? parseInt(queryMinPrice[0]) : parseInt(queryMinPrice ?? "0")

      if (queryMaxPrice !== undefined)
        newFilters.maxPrice = Array.isArray(queryMaxPrice) ? parseInt(queryMaxPrice[0]) : parseInt(queryMaxPrice ?? "0")
      
      return newFilters
    })
  }, [router.query.status, router.query.category, router.query.chain, router.query.minPrice, router.query.maxPrice])

  const defaultURL: NftsURL = {
    baseUrl: `${api}/nfts?page=1`,
    filters: filters,
    sort: "",
  }
  const [isFilterClosed, setIsFilterClosed] = useState(false)
  const [sort, setSort] = useState("")
  const [url, setUrl] = useState(defaultURL)
  const { nfts, total, totalPages, links, loading, error, refetchData } = useNfts(url)
  const { t } = useTranslation()

  const updateUrl = useCallback(
    (_url: string) => {
      setUrl({
        ...url,
        baseUrl: _url,
      })
    },
    [url],
  )

  useEffect(() => {
    setUrl({
      ...url,
      baseUrl: `${api}/nfts?page=1`,
      filters,
      sort,
      search,
    })
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [filters, sort, search])

  return (
    <Layout>
      <div className="grow flex pt-16 max-h-[calc(100vh-5rem)] divide-x divide-slate-300">
        <FilterSideBar
          isClosed={isFilterClosed}
          setIsClosed={setIsFilterClosed}
          filters={filters}
          setFilters={setFilters}
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
            setSort={setSort}
            amountPages={totalPages}
          />
        )}
      </div>
    </Layout>
  )
}

export default Explore
