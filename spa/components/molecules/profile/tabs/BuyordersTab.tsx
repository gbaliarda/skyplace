import { useState } from "react"
import { useTranslation } from "next-export-i18n"
import { useUserBuyorders } from "../../../../services/users"
import ProfileBuysTab from "../ProfileBuysTab"
import Loader from "../../../atoms/Loader"
import ErrorBox from "../../../atoms/ErrorBox"

interface Props {
  userId: number | undefined
}

export default function BuyordersTab({ userId }: Props) {
  const { t } = useTranslation()
  const [page, setPage] = useState(1)
  const [status, setStatus] = useState("ALL")
  const { buyorders, loading, error, mutate } = useUserBuyorders(userId, page, status)

  if (error) return <ErrorBox errorMessage={t("errors.errorLoadingTab")} retryAction={mutate} />
  if (loading) return <Loader className="grow flex items-center justify-center mb-32" />

  return (
    <ProfileBuysTab
      setPage={setPage}
      status={status}
      setStatus={setStatus}
      buyorders={buyorders!!}
    />
  )
}
