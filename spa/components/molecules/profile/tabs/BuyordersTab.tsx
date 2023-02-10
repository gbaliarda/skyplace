import { useCallback, useEffect, useState } from "react"
import { useTranslation } from "next-export-i18n"
import { BuyordersURL, useUserBuyorders } from "../../../../services/users"
import ProfileBuysTab from "../ProfileBuysTab"
import Loader from "../../../atoms/Loader"
import ErrorBox from "../../../atoms/ErrorBox"
import { api } from "../../../../services/endpoints"

interface Props {
  userId: number | undefined
}

export default function BuyordersTab({ userId }: Props) {
  const { t } = useTranslation()
  const [status, setStatus] = useState("ALL")
  const defaultURL = {
    baseUrl: `${api}/users/${userId}/buyorders?page=1`,
    status,
  } as BuyordersURL
  const [url, setUrl] = useState<BuyordersURL>(defaultURL)
  const { buyorders, links, totalPages, loading, error, refetchData } = useUserBuyorders(url)
  const updateUrl = useCallback(
    (_url: string) => {
      setUrl({
        ...url,
        baseUrl: _url,
      })
    },
    [url],
  )

  useEffect(() => {
    setUrl({
      baseUrl: `${api}/users/${userId}/buyorders?page=1`,
      status,
    })
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [status])

  if (error)
    return (
      <ErrorBox errorMessage={t("errors.errorLoadingTab")} retryAction={() => refetchData(url)} />
    )
  if (loading || buyorders === undefined)
    return <Loader className="grow flex items-center justify-center mb-32" />

  return (
    <ProfileBuysTab
      updateUrl={updateUrl}
      links={links ?? undefined}
      amountPages={totalPages}
      status={status}
      setStatus={setStatus}
      buyorders={buyorders}
    />
  )
}
