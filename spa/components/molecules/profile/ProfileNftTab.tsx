import { useCallback } from "react"
import { useTranslation } from "next-export-i18n"
import Card from "../../atoms/Card"
import { NftApi } from "../../../types/Nft"
import Paginator from "../Paginator"
import SortDropdown, { SortOption } from "../../atoms/SortDropdown"

interface Props {
  setPage: (page: number) => void
  sort: string
  setSort: (sort: string) => void
  nfts: NftApi
}

export default function ProfileNftTab({ setPage, sort, setSort, nfts }: Props) {
  const { t } = useTranslation()
  const handlePageChange = useCallback(
    (page: number) => {
      setPage(page)
    },
    [setPage],
  )

  const handleSortChange = useCallback(
    (newSort: string) => {
      setSort(newSort)
    },
    [setSort],
  )

  const sortOptions: SortOption[] = [
    { key: "name", value: t("common.name") },
    { key: "priceAsc", value: t("common.priceAsc") },
    { key: "priceDsc", value: t("common.priceDsc") },
  ]

  return (
    <>
      <div className="flex flex-row items-center justify-between">
        {/* Paginator */}
        <Paginator amountPages={nfts.totalPages} handlePageChange={handlePageChange} />

        {/* Dropdown menu */}
        <SortDropdown
          sortDefaultValue={sort}
          options={sortOptions}
          handleSortChange={handleSortChange}
        />
      </div>

      {/* Page items */}
      {nfts.total > 0 ? (
        <div className="flex flex-wrap justify-center gap-8 items-start">
          {nfts?.nfts.map((nft) => {
            return <Card nft={nft} key={nft.id} />
          })}
        </div>
      ) : (
        <span className="text-2xl text-center pt-10 xl:pt-20">{t("profile.noItems")}</span>
      )}
    </>
  )
}
