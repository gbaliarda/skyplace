import { useCallback } from "react"
import { useTranslation } from "next-export-i18n"
import parse from "parse-link-header"
import Card from "../../atoms/Card"
import Paginator from "../Paginator"
import SortDropdown, { SortOption } from "../../atoms/SortDropdown"
import Nft from "../../../types/Nft"

interface Props {
  links?: parse.Links
  updateUrl: (_url: string) => void
  sort: string
  setSort: (sort: string) => void
  nfts: Nft[]
  totalPages: number
}

export default function ProfileNftTab({
  links,
  updateUrl,
  sort,
  setSort,
  nfts,
  totalPages,
}: Props) {
  const { t } = useTranslation()

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
        {links !== undefined && (
          <Paginator links={links} updateUrl={updateUrl} amountPages={totalPages} />
        )}

        {/* Dropdown menu */}
        <SortDropdown
          sortDefaultValue={sort}
          options={sortOptions}
          handleSortChange={handleSortChange}
        />
      </div>

      {/* Page items */}
      {nfts.length > 0 ? (
        <div className="flex flex-wrap justify-center gap-8 items-start">
          {nfts.map((nft) => {
            return <Card nft={nft} key={nft.id} />
          })}
        </div>
      ) : (
        <span className="text-2xl text-center pt-10 xl:pt-20">{t("profile.noItems")}</span>
      )}
    </>
  )
}
