import { useCallback, useEffect, useState } from "react"
import { useRouter } from "next/router"
import { useTranslation } from "next-export-i18n"
import FilterSideBar from "../components/molecules/FilterSideBar"
import ExploreContent from "../components/molecules/ExploreContent"
import Layout from "../components/Layout"
import { useNfts } from "../services/nfts"
import Loader from "../components/atoms/Loader"
import { NftsFilter, SearchFilter, SearchType } from "../types/Filters"
import ErrorBox from "../components/atoms/ErrorBox"
import { api } from "../services/endpoints"

const Explore = () => {
  const router = useRouter()

  const [search, setSearch] = useState<SearchFilter>({} as SearchFilter)
  useEffect(() => {
    if (router.query.search === undefined || router.query.searchFor === undefined) return
    const querySearch = router.query.search
    setSearch({
      searchFor: (router.query.searchFor as SearchType) ?? SearchType.Nft,
      search: Array.isArray(querySearch) ? querySearch[0] : querySearch ?? "",
    })
  }, [router.query.search, router.query.searchFor])

  const defaultURL = {
    baseUrl: `${api}/nfts?page=1`,
    filters: {} as NftsFilter,
    sort: "",
    search,
  }
  const [isFilterClosed, setIsFilterClosed] = useState(false)
  const [filters, setFilters] = useState<NftsFilter>({})
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
