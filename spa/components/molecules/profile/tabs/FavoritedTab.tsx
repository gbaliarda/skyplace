import { useState } from "react"
import { useTranslation } from "next-export-i18n"
import ProfileNftTab from "../ProfileNftTab"
import ErrorBox from "../../../atoms/ErrorBox"
import Loader from "../../../atoms/Loader"
import { useFavorites } from "../../../../services/users"

interface Props {
  userId: number
}

export default function FavoritedTab({ userId }: Props) {
  const { t } = useTranslation()
  const [page, setPage] = useState(1)
  const [sort, setSort] = useState("")
  const { nfts, loading, error, mutate } = useFavorites(userId, page, sort)

  if (error) return <ErrorBox errorMessage={t("errors.errorLoadingTab")} retryAction={mutate} />
  if (loading) return <Loader className="grow flex items-center justify-center mb-32" />

  return <ProfileNftTab setPage={setPage} sort={sort} setSort={setSort} nfts={nfts!!} />
}
