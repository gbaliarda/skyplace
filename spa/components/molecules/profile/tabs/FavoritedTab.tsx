import { useCallback, useEffect, useState } from "react"
import { useTranslation } from "next-export-i18n"
import ProfileNftTab from "../ProfileNftTab"
import ErrorBox from "../../../atoms/ErrorBox"
import Loader from "../../../atoms/Loader"
import { FavoritesURL, useFavorites } from "../../../../services/users"
import { api } from "../../../../services/endpoints"

interface Props {
  userId: number
}

export default function FavoritedTab({ userId }: Props) {
  const { t } = useTranslation()
  const defaultURL = {
    baseUrl: `${api}/users/${userId}/favorites?page=1`,
    sort: "",
  } as FavoritesURL
  const [url, setUrl] = useState<FavoritesURL>(defaultURL)
  const [sort, setSort] = useState("")
  const { favorites, links, totalPages, loading, error, refetchData } = useFavorites(url)

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
      baseUrl: `${api}/users/${userId}/favorites?page=1`,
      sort,
    })
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [sort])

  if (error)
    return (
      <ErrorBox errorMessage={t("errors.errorLoadingTab")} retryAction={() => refetchData(url)} />
    )
  if (loading || favorites === undefined)
    return <Loader className="grow flex items-center justify-center mb-32" />

  return (
    <ProfileNftTab
      updateUrl={updateUrl}
      sort={sort}
      setSort={setSort}
      nfts={favorites}
      totalPages={totalPages}
      links={links ?? undefined}
    />
  )
}
