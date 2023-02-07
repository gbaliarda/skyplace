import { useCallback } from "react"
import { useTranslation } from "next-export-i18n"
import BuyorderStatusFilter from "../../atoms/BuyorderStatusFilter"
import ProfileBuyorderCard from "../../atoms/ProfileBuyorderCard"
import { BuyOrderApi } from "../../../types/Buyorder"
import Paginator from "../Paginator"

interface Props {
  setPage: (page: number) => void
  status: string
  setStatus: (status: string) => void
  buyorders: BuyOrderApi // TODO: Add history items type
}

export default function ProfileBuysTab({ setPage, status, setStatus, buyorders }: Props) {
  const { t } = useTranslation()
  const handlePageChange = useCallback(
    (page: number) => {
      setPage(page)
    },
    [setPage],
  )

  const handleStatusChange = useCallback(
    (newStatus: string) => {
      setStatus(newStatus)
    },
    [setStatus],
  )

  return (
    <>
      <div className="flex flex-row items-center justify-between">
        <Paginator amountPages={buyorders.totalPages} handlePageChange={handlePageChange} />

        <BuyorderStatusFilter status={status} handleStatusChange={handleStatusChange} />
      </div>

      {buyorders.buyorders.length > 0 ? (
        <div className="flex flex-col gap-2 w-3/4 max-w-4xl self-center">
          {buyorders?.buyorders.map((value) => {
            return (
              <ProfileBuyorderCard buyorder={value} status={status} key={value.self.toString()} />
            )
          })}
        </div>
      ) : (
        <span className="text-2xl text-center pt-10 xl:pt-20">{t("profile.noItems")}</span>
      )}
    </>
  )
}
