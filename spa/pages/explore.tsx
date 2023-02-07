import { useEffect, useState } from "react"
import { useRouter } from "next/router"
import { useTranslation } from "next-export-i18n"
import FilterSideBar from "../components/molecules/FilterSideBar"
import ExploreContent from "../components/molecules/ExploreContent"
import Layout from "../components/Layout"
import { useNfts } from "../services/nfts"
import Loader from "../components/atoms/Loader"
import { NftsFilter, SearchType } from "../types/Filters"
import ErrorBox from "../components/atoms/ErrorBox"

const Explore = () => {
  const router = useRouter()

  const [search, setSearch] = useState({
    search: "",
    searchFor: SearchType.Nft,
  })
  useEffect(() => {
    const querySearch = router.query.search
    setSearch({
      searchFor: (router.query.searchFor as SearchType) ?? SearchType.Nft,
      search: Array.isArray(querySearch) ? querySearch[0] : querySearch ?? "",
    })
  }, [router.query.search, router.query.searchFor])
  const [isFilterClosed, setIsFilterClosed] = useState(false)
  const [page, setPage] = useState(0)
  const [filters, setFilters] = useState<NftsFilter>({})
  const [sort, setSort] = useState("")
  const { nfts, loading, error, mutate } = useNfts(page, filters, sort, search)
  const { t } = useTranslation()

  return (
    <Layout>
      <div className="grow flex pt-16 max-h-[calc(100vh-5rem)] divide-x divide-slate-300">
        <FilterSideBar
          isClosed={isFilterClosed}
          setIsClosed={setIsFilterClosed}
          filters={filters}
          setFilters={setFilters}
          totalResults={nfts?.total}
        />
        {error ? (
          <div className="flex justify-center grow mt-24">
            <ErrorBox errorMessage={t("errors.loadingNfts")} retryAction={mutate} />
          </div>
        ) : loading ? (
          <Loader className="grow flex items-center justify-center mb-32" />
        ) : (
          <ExploreContent
            nfts={nfts!!.nfts}
            amountPages={nfts?.totalPages}
            setPage={setPage}
            sortDefaultValue={sort}
            setSort={setSort}
          />
        )}
      </div>
    </Layout>
  )
}

export default Explore
