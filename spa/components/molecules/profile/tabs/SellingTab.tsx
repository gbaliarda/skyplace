import { useCallback, useEffect, useState } from "react"
import { useTranslation } from "next-export-i18n"
import { NftsURL, useNfts } from "../../../../services/nfts"
import { SaleStatus } from "../../../../types/Filters"
import ProfileNftTab from "../ProfileNftTab"
import ErrorBox from "../../../atoms/ErrorBox"
import Loader from "../../../atoms/Loader"
import { api } from "../../../../services/endpoints"

interface Props {
  userId: number
}

export default function SellingTab({ userId }: Props) {
  const { t } = useTranslation()
  const defaultURL = {
    baseUrl: `${api}/nfts?page=1`,
    filters: { owner: userId, status: [SaleStatus.ONSALE] },
    search: {},
    sort: "",
  } as NftsURL
  const [url, setUrl] = useState<NftsURL>(defaultURL)
  const [sort, setSort] = useState("")
  const { nfts, links, loading, error, refetchData, totalPages } = useNfts(url)
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
      ...url,
      baseUrl: `${api}/nfts?page=1`,
      sort,
    })
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [sort])

  if (error)
    return (
      <ErrorBox errorMessage={t("errors.errorLoadingTab")} retryAction={() => refetchData(url)} />
    )
  if (loading || nfts === undefined)
    return <Loader className="grow flex items-center justify-center mb-32" />

  return (
    <ProfileNftTab
      links={links ?? undefined}
      updateUrl={updateUrl}
      sort={sort}
      setSort={setSort}
      nfts={nfts}
      totalPages={totalPages}
    />
  )
}
