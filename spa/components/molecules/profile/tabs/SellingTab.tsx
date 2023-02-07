import { useState } from "react"
import { useTranslation } from "next-export-i18n"
import { useNfts } from "../../../../services/nfts"
import { SaleStatus } from "../../../../types/Filters"
import ProfileNftTab from "../ProfileNftTab"
import ErrorBox from "../../../atoms/ErrorBox"
import Loader from "../../../atoms/Loader"

interface Props {
  userId: number
}

export default function SellingTab({ userId }: Props) {
  const { t } = useTranslation()
  const [page, setPage] = useState(1)
  const [sort, setSort] = useState("")
  const { nfts, loading, error, mutate } = useNfts(
    page,
    { owner: userId, status: [SaleStatus.ONSALE] },
    sort,
  )

  if (error) return <ErrorBox errorMessage={t("errors.errorLoadingTab")} retryAction={mutate} />
  if (loading) return <Loader className="grow flex items-center justify-center mb-32" />

  return <ProfileNftTab nfts={nfts!!} setPage={setPage} sort={sort} setSort={setSort} />
}
