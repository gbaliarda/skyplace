import { useRouter } from "next/router"
import { useTranslation } from "next-export-i18n"
import queryString from "query-string"
import { FunnelIcon, ArrowSmallLeftIcon, ArrowSmallRightIcon } from "@heroicons/react/24/outline"
import { Category, Chain } from "../../types/Nft"
import { FilterType, NftsFilter, SaleStatus } from "../../types/Filters"
import StatusFilter from "../atoms/StatusFilter"
import CategoryFilter from "../atoms/CategoryFilter"
import ChainFilter from "../atoms/ChainFilter"
import PriceFilter from "../atoms/PriceFilter"

interface Props {
  isClosed?: boolean
  setIsClosed?: (isClosed: boolean) => void
  filters: NftsFilter
  totalResults?: number
}

type FilterValueType = SaleStatus | Category | Chain

const FilterSideBar = ({ isClosed = false, setIsClosed, filters, totalResults }: Props) => {
  const router = useRouter()
  const { t } = useTranslation()

  const addQueryParam = (name: string, value: string) => {
    const queryParams = new URLSearchParams(queryString.stringify(router.query))

    if (name === "category" && !queryParams.getAll("status").includes(SaleStatus.ONSALE))
      queryParams.append("status", SaleStatus.ONSALE)
    queryParams.set("page", "1")
    queryParams.append(name, value)

    const newURL = `${router.pathname}?${queryParams.toString()}`

    router.push(newURL)
  }

  const setPriceParam = (minPrice: number, maxPrice: number) => {
    const queryParams = new URLSearchParams(queryString.stringify(router.query))

    queryParams.set("minPrice", Number.isNaN(minPrice) ? "0" : minPrice.toString())
    queryParams.set("maxPrice", Number.isNaN(maxPrice) ? "0" : maxPrice.toString())

    const newURL = `${router.pathname}?${queryParams.toString()}`

    router.push(newURL)
  }

  const removeQueryParam = (name: string, value: string) => {
    const queryParams = new URLSearchParams(queryString.stringify(router.query))

    if (queryParams.has(name)) {
      if (name === "status" && value === SaleStatus.ONSALE) {
        if (queryParams.get("sort") === "priceAsc" || queryParams.get("sort") === "priceDsc")
          queryParams.delete("sort")

        if (queryParams.get("category")) queryParams.delete("category")
      }

      const values = queryParams.getAll(name)
      const filteredValues = values.filter((val: string) => val !== value)

      queryParams.delete(name)

      filteredValues.forEach((val) => queryParams.append(name, val))
    }

    const newURL = `${router.pathname}?${queryParams.toString()}`

    router.push(newURL)
  }

  const changeValueFilters = (
    filterType: FilterType,
    toAdd: boolean,
    newValue: FilterValueType,
  ) => {
    if (filterType === FilterType.PRICE) return

    if (toAdd) addQueryParam(filterType.toLowerCase(), newValue)
    else removeQueryParam(filterType.toLowerCase(), newValue)
  }

  const applyPriceFilter = (minPrice: number, maxPrice: number) => {
    setPriceParam(minPrice, maxPrice)
  }

  return (
    <div className="flex">
      {!isClosed ? (
        <div className="flex flex-col w-72 min-w-[250px] items-center">
          <span suppressHydrationWarning className="text-4xl">
            {filters.category === undefined || filters.category.length === 0
              ? t("explore.all")
              : filters.category.length === 1
              ? filters.category[0]
              : t("explore.various")}
          </span>
          <span suppressHydrationWarning>
            {t("explore.results", { amount: totalResults !== undefined ? totalResults : 0 })}
          </span>
          <div className="grow w-full overflow-y-scroll mt-2">
            <div className="py-4 flex flex-col w-full">
              <div className="flex items-center justify-between pb-4 px-4">
                <div className="flex items-center">
                  <FunnelIcon className="w-6 h-6 mr-2" />
                  <span suppressHydrationWarning className="text-2xl">
                    {t("explore.filter")}
                  </span>
                </div>
                <ArrowSmallLeftIcon
                  className="w-10 h-8 cursor-pointer rounded-full px-2 hover:bg-slate-200"
                  onClick={() => setIsClosed !== undefined && setIsClosed(true)}
                />
              </div>

              <StatusFilter changeValueFilters={changeValueFilters} status={filters.status} />

              <CategoryFilter
                changeValueFilters={changeValueFilters}
                categories={filters.category}
              />

              <ChainFilter changeValueFilters={changeValueFilters} chains={filters.chain} />

              <PriceFilter
                applyPriceFilter={applyPriceFilter}
                minPrice={filters.minPrice}
                maxPrice={filters.maxPrice}
              />
            </div>
          </div>
        </div>
      ) : (
        <div className="flex mx-2">
          <ArrowSmallRightIcon
            className="w-10 h-8 cursor-pointer rounded-full px-2 hover:bg-slate-200"
            onClick={() => setIsClosed !== undefined && setIsClosed(false)}
          />
        </div>
      )}
    </div>
  )
}

FilterSideBar.defaultProps = {
  isClosed: false,
  setIsClosed: () => {},
}

export default FilterSideBar
