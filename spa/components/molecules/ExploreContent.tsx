import { useCallback } from "react"
import { useTranslation } from "next-export-i18n"
import Card from "../atoms/Card"
import Paginator from "./Paginator"
import SortDropdown, { SortOption } from "../atoms/SortDropdown"
import Nft from "../../types/Nft"
import { useFavoritedNfts } from "../../services/favorites"
import useSession from "../../hooks/useSession"

interface Props {
  nfts: Array<Nft>
  amountPages: number | undefined
  setPage: (page: number) => void
  sortDefaultValue: string
  setSort: (sort: string) => void
}

const ExploreContent = ({ nfts, amountPages = 0, setPage, sortDefaultValue, setSort }: Props) => {
  const { t } = useTranslation()
  const { userId, accessToken } = useSession()
  const { favorites, mutate } = useFavoritedNfts(
    userId,
    nfts.map((nft) => nft.id),
    accessToken,
  )
  const favoritesId = favorites?.nfts.map((nft) => nft.id)

  const handlePageChange = useCallback(
    (page: number) => {
      setPage(page)
    },
    [setPage],
  )

  const handleSortChange = useCallback(
    (sort: string) => {
      setSort(sort)
    },
    [setSort],
  )

  const sortOptions: SortOption[] = [
    { key: "name", value: t("explore.name") },
    { key: "priceAsc", value: t("explore.priceAsc") },
    { key: "priceDsc", value: t("explore.priceDsc") },
    { key: "collection", value: t("explore.collection") },
  ]
  return (
    <div className="w-[80%] min-w-[500px] grow flex flex-col">
      <div className="flex justify-between items-center h-16 mb-2 px-8">
        <Paginator amountPages={amountPages} handlePageChange={handlePageChange} />
        <SortDropdown
          sortDefaultValue={sortDefaultValue}
          options={sortOptions}
          handleSortChange={handleSortChange}
        />
      </div>

      <div className="px-8 pb-8 flex flex-wrap gap-8 overflow-y-scroll justify-center max-h-full">
        {nfts.length === 0 ? (
          <span suppressHydrationWarning className="pt-4 text-2xl">
            {t("explore.noNfts")}
          </span>
        ) : (
          nfts.map((nft) => (
            <div key={`${nft.contractAddr}#${nft.nftId}`}>
              <Card nft={nft} isFaved={favoritesId?.includes(nft.id)} mutateFavs={mutate} />
            </div>
          ))
        )}
      </div>
    </div>
  )
}

export default ExploreContent
