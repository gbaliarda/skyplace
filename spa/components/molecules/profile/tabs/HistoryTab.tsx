import { useCallback, useState } from "react"
import { useTranslation } from "next-export-i18n"
import HistoryItem from "../../../atoms/HistoryItem"
import { usePurchases } from "../../../../services/purchases"
import Paginator from "../../Paginator"
import ErrorBox from "../../../atoms/ErrorBox"
import Loader from "../../../atoms/Loader"
import useSession from "../../../../hooks/useSession"

interface Props {
  userId: number
}

export default function HistoryTab({ userId }: Props) {
  const { t } = useTranslation()
  const [page, setPage] = useState(1)
  const { accessToken } = useSession()
  const { purchases, loading, error, mutate } = usePurchases(
    userId,
    page,
    accessToken === null ? undefined : accessToken,
  )

  const handlePageChange = useCallback(
    (newPage: number) => {
      setPage(newPage)
    },
    [setPage],
  )

  if (error) return <ErrorBox errorMessage={t("errors.errorLoadingTab")} retryAction={mutate} />
  if (loading) return <Loader className="grow flex items-center justify-center mb-32" />

  return (
    <>
      <Paginator amountPages={1} handlePageChange={handlePageChange} />
      <div className="flex flex-col gap-2 w-3/4 max-w-4xl self-center">
        {purchases !== undefined && purchases?.purchases.length > 0 ? (
          purchases?.purchases.map((purchase) => {
            return <HistoryItem userId={userId ?? 0} purchase={purchase} key={purchase.id} />
          })
        ) : (
          <span suppressHydrationWarning className="text-center text-slay-700">
            {t("profile.noActivity")}
          </span>
        )}
      </div>
    </>
  )
}
