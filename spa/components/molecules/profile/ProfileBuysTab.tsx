import { useCallback } from "react"
import { useTranslation } from "next-export-i18n"
import parse from "parse-link-header"
import BuyorderStatusFilter from "../../atoms/BuyorderStatusFilter"
import ProfileBuyorderCard from "../../atoms/ProfileBuyorderCard"
import Buyorder from "../../../types/Buyorder"
import Paginator from "../Paginator"

interface Props {
  links?: parse.Links
  updateUrl: (url: string) => void
  status: string
  setStatus: (status: string) => void
  buyorders: Buyorder[]
  updateBuyorders: () => void
  amountPages: number
}

export default function ProfileBuysTab({
  links,
  updateUrl,
  status,
  setStatus,
  buyorders,
  updateBuyorders,
  amountPages,
}: Props) {
  const { t } = useTranslation()

  const handleStatusChange = useCallback(
    (newStatus: string) => {
      setStatus(newStatus)
    },
    [setStatus],
  )

  return (
    <>
      <div className="flex flex-row items-center justify-between">
        {links !== undefined && (
          <Paginator amountPages={amountPages} links={links} updateUrl={updateUrl} />
        )}
        <BuyorderStatusFilter status={status} handleStatusChange={handleStatusChange} />
      </div>

      {buyorders.length > 0 ? (
        <div className="flex flex-col gap-2 w-3/4 max-w-4xl self-center">
          {buyorders.map((value) => {
            return (
              <ProfileBuyorderCard
                buyorder={value}
                updateBuyorders={updateBuyorders}
                key={value.self.toString()}
              />
            )
          })}
        </div>
      ) : (
        <span className="text-2xl text-center pt-10 xl:pt-20">{t("profile.noItems")}</span>
      )}
    </>
  )
}
