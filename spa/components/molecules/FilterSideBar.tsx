import { useTranslation } from "next-export-i18n"
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
  setFilters: (filter: NftsFilter) => void
  totalResults?: number
}

type FilterValueType = SaleStatus | Category | Chain

const FilterSideBar = ({
  isClosed = false,
  setIsClosed,
  filters,
  setFilters,
  totalResults,
}: Props) => {
  const { t } = useTranslation()

  const changeValueFilters = (
    filterType: FilterType,
    toAdd: boolean,
    newValue: FilterValueType,
  ) => {
    if (filterType === FilterType.PRICE) return

    let filter = filters[filterType]
    if (toAdd) {
      if (filter === undefined) filter = []
      setFilters({
        ...filters,
        [filterType]: [...filter, newValue],
      })
    } else {
      let newArray: FilterValueType[]
      switch (filterType) {
        case FilterType.CATEGORY:
          newArray = filters[FilterType.CATEGORY]!!.filter((v: Category) => v !== newValue)
          break
        case FilterType.CHAIN:
          newArray = filters[FilterType.CHAIN]!!.filter((v: Chain) => v !== newValue)
          break
        case FilterType.STATUS:
          newArray = filters[FilterType.STATUS]!!.filter((v: SaleStatus) => v !== newValue)
          break
        default:
          newArray = []
      }

      setFilters({
        ...filters,
        [filterType]: newArray,
      })
    }
  }

  const applyPriceFilter = (minPrice: number, maxPrice: number) => {
    setFilters({
      ...filters,
      minPrice,
      maxPrice,
    })
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

              <StatusFilter changeValueFilters={changeValueFilters} />

              <CategoryFilter changeValueFilters={changeValueFilters} />

              <ChainFilter changeValueFilters={changeValueFilters} />

              <PriceFilter applyPriceFilter={applyPriceFilter} />
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
